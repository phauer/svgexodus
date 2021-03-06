package de.philipphauer.svgexodus.gui.presenter;

import java.awt.Desktop;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.event.MenuEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Provider;

import de.philipphauer.svgexodus.cli.CLIOptions;
import de.philipphauer.svgexodus.gui.components.MainFrame;
import de.philipphauer.svgexodus.gui.components.MenuListenerAdapter;
import de.philipphauer.svgexodus.gui.components.OptionsSideBar;
import de.philipphauer.svgexodus.gui.event.ApplicationClosingEvent;
import de.philipphauer.svgexodus.gui.event.ConsoleLogEvent;
import de.philipphauer.svgexodus.gui.event.StartEvent;
import de.philipphauer.svgexodus.gui.event.StopEvent;
import de.philipphauer.svgexodus.gui.event.UserCancelEvent;
import de.philipphauer.svgexodus.io.LogFile;
import de.philipphauer.svgexodus.io.SVGFileFilter;
import de.philipphauer.svgexodus.io.ser.OptionsSerializer;
import de.philipphauer.svgexodus.io.ser.OptionsSerializerException;
import de.philipphauer.svgexodus.model.FeatureSwitch;
import de.philipphauer.svgexodus.model.Options;
import de.philipphauer.svgexodus.tasks.ConsoleTask;
import de.philipphauer.svgexodus.tasks.ConverterTask;
import de.philipphauer.svgexodus.tasks.ObserveTask;

public class MainPresenter {

	private static final Logger logger = LoggerFactory.getLogger(MainPresenter.class);

	@Inject
	private Options options;

	@Inject
	private FeatureSwitch featureSwitch;

	@Inject
	private OptionsSerializer optionsSerializer;

	@Inject
	private EventBus eventBus;

	private MainFrame mainFrame;

	@Inject
	private Provider<ConverterTask> converterTaskProvider;

	@Inject
	private Provider<ObserveTask> observeTaskProvider;

	@Inject
	private CLIOptions cliOptions;

	@Inject
	@LogFile
	private File logFile;

	public void startApplication() {
		mainFrame = new MainFrame();
		if (cliOptions.isStartMinimized()) {
			mainFrame.minimizeToTray();
		} else {
			mainFrame.setVisible(true);
		}

		eventBus.register(this);

		ConsoleTask consoleTask = startConsoleTask(mainFrame.getConsoleTextArea(), mainFrame.getProgressBar());
		eventBus.register(consoleTask);

		initEventHandlingForMainFrame();

		final OptionsSideBar optionsSideBar = mainFrame.getOptionsSideBar();
		initEventHandlingForOptionsSideBar(optionsSideBar);
		optionsSideBar.setOption(options, featureSwitch);
		startObservingIfConfigured();
	}

	private void startObservingIfConfigured() {
		if (cliOptions.isStartObserving()) {
			try {
				tryToStartObserving();
			} catch (InvalidInputException ex) {
				String message = "Couldn't start observing automatically. ";
				eventBus.post(new ConsoleLogEvent(message + ex.getMessage()));
				JOptionPane.showMessageDialog(null, message);
				mainFrame.maximizeFromTray();
			}
		}
	}

	private void initEventHandlingForOptionsSideBar(final OptionsSideBar optionsSideBar) {
		optionsSideBar.addLockupSourceListener(event -> {
			JFileChooser fileChooser = new JFileChooser(optionsSideBar.getSelectedSourcePath());
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileChooser.setFileFilter(new SVGFileFilter());
			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				/*
				 * somehow the JFileChooser returns a Win32ShellFolder2 which caused a StackOverflow when trying to
				 * deserialize it again.
				 */
				File selectedFilePure = new File(selectedFile.getAbsolutePath());
				options.setInputPath(selectedFilePure);
			}
		});
		optionsSideBar.addLockupResultListener(event -> {
			JFileChooser fileChooser = new JFileChooser(optionsSideBar.getSelectedResultPath());
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				options.setOutputFolder(fileChooser.getSelectedFile());
			}
		});
	}

	private void initEventHandlingForMainFrame() {
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				eventBus.post(new ApplicationClosingEvent());
				try {
					optionsSerializer.saveOptions(options);
				} catch (OptionsSerializerException e) {
					logger.warn("exception occured during saving of the option file", e);
				}
			}
		});
		mainFrame.addConvertListener(event -> {
			try {
				checkInput();
				ConverterTask converterTask = converterTaskProvider.get();
				new Thread(converterTask).start();
			} catch (InvalidInputException e) {
				eventBus.post(new ConsoleLogEvent(e.getMessage()));
			}
		});
		mainFrame.addObserveListener(event -> {
			try {
				tryToStartObserving();
			} catch (InvalidInputException e) {
				eventBus.post(new ConsoleLogEvent(e.getMessage()));
			}
		});
		mainFrame.addCancelListener(event -> eventBus.post(new UserCancelEvent()));
		mainFrame.addOpenLogFileListener(new MenuListenerAdapter() {
			public void menuSelected(final MenuEvent event) {
				openLogFile();
			}
		});
	}

	public void openLogFile() {
		try {
			Desktop.getDesktop().open(logFile);
		} catch (IOException e) {
			String message = "Couln't open log file: " + e.getMessage();
			JOptionPane.showMessageDialog(null, message);
			logger.error(message, e);
		}

	}

	private void tryToStartObserving() throws InvalidInputException {
		checkInput();
		ObserveTask observeTask = observeTaskProvider.get();
		new Thread(observeTask).start();
	}

	private void checkInput() throws InvalidInputException {
		if (!options.getInputPath().exists()) {
			throw new InvalidInputException("Error: input file or folder doesn't exist: " + options.getInputPath());
		}
		if (options.isSeparateOutputFolder() && !options.getOutputFolder().isDirectory()) {
			throw new InvalidInputException("Error: output folder doesn't exist or isn't a directory: "
					+ options.getOutputFolder());
		}
	}

	private ConsoleTask startConsoleTask(final JTextArea consoleTextField, final JProgressBar consoleProgressBar) {
		final ConsoleTask consoleTask = new ConsoleTask(consoleTextField, consoleProgressBar);
		new Thread(consoleTask).start();
		return consoleTask;
	}

	@Subscribe
	public void actionStarted(StartEvent e) {
		mainFrame.actionStarted();
	}

	@Subscribe
	public void actionStopped(StopEvent e) {
		mainFrame.actionStopped();
	}

}
