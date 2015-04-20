package de.philipphauer.svgexodus;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.philipphauer.svgexodus.cli.CLIException;
import de.philipphauer.svgexodus.cli.CLIOptions;
import de.philipphauer.svgexodus.cli.CLIReader;
import de.philipphauer.svgexodus.gui.presenter.MainPresenter;
import de.philipphauer.svgexodus.util.VersionUtil;

/**
 * @author Philipp Hauer
 */
public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		try {
			logger.info("Starting SVGExodus " + VersionUtil.getVersion());
			CLIOptions cliOptions = CLIReader.readCLIOptions(args);
			logger.info("CLI arguments: " + cliOptions);
			setLookAndFeel();
			bootstrapApplication(cliOptions);
		} catch (CLIException e) {
			logger.error(e.getMessage());
		} catch (Exception ex) {
			logger.error("Unhandled Exception. ", ex);
			JOptionPane.showMessageDialog(null, "Fatal error: " + ex.getMessage());
		}
	}

	private static void bootstrapApplication(CLIOptions cliOptions) {
		Injector injector = Guice.createInjector(new GuiceModule(cliOptions));
		MainPresenter mainPresenter = injector.getInstance(MainPresenter.class);
		mainPresenter.startApplication();
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			logger.error("Couldn't set Look and Feel", e);
		}
	}

}
