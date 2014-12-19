package de.philipphauer.svgexodus.gui.components;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.philipphauer.svgexodus.gui.GuiConst;
import de.philipphauer.svgexodus.io.IOUtil;

/**
 * @author Philipp Hauer
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

	private OptionsSideBar optionsSideBar;

	private JButton convertButton;

	private JButton observeButton;

	private JButton cancelButton;

	private JTextArea consoleTextArea;

	private JProgressBar progressBar;

	private TrayIcon trayIcon;

	public MainFrame() {
		super("SVGExodus");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 550);
		setLocationRelativeTo(null);
		setIconImages(GuiConst.getIconList());

		buildMenuBar();
		buildGUI();
		initTrayIconMinimizing();
	}

	private void initTrayIconMinimizing() {
		if (!SystemTray.isSupported()) {
			logger.warn("SystemTray is not supported");
			return;
		}
		trayIcon = new TrayIcon(GuiConst.IMAGE_LOGO, "SVGExodus");
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				maximizeFromTray();
			}
		});
		addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent event) {
				minimizeToTray();
			}
		});
	}

	public void minimizeToTray() {
		setVisible(false);
		addTrayIcon(trayIcon);
		trayIcon.displayMessage("SVGExodus", "is minimized here.", TrayIcon.MessageType.NONE);
	}

	public void maximizeFromTray() {
		setVisible(true);
		setExtendedState(JFrame.NORMAL);
		SystemTray.getSystemTray().remove(trayIcon);
	}

	private void addTrayIcon(final TrayIcon trayIcon) {
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			throw new IllegalStateException(
					"This should not happen, because I checked in advance if SystemTray is supported", e);
		}
	}

	private void buildGUI() {
		setLayout(new MigLayout("fill, wrap 1, ins 0", // Layout Constraints
				"", // Column constraints
				"[pref!][][pref!][pref!]")); // Row constraints

		optionsSideBar = new OptionsSideBar();
		add(optionsSideBar, "grow");

		consoleTextArea = new JTextArea();
		add(new JScrollPane(consoleTextArea), "grow");

		progressBar = new JProgressBar();
		add(progressBar, "grow");

		final JPanel pnlButtons = new JPanel(new MigLayout("ins 0, center", "", ""));
		convertButton = new JButton("Convert!", GuiConst.ICON_CONVERT);
		observeButton = new JButton("Observer folder", GuiConst.ICON_OBSERVE);
		cancelButton = new JButton("Cancel", GuiConst.ICON_CANCEL);
		cancelButton.setEnabled(false);
		pnlButtons.add(convertButton);
		pnlButtons.add(observeButton);
		pnlButtons.add(cancelButton);
		add(pnlButtons, "grow");

		observeButton.setToolTipText(GuiConst.TOOLTIP_OBSERVE);
		convertButton.setToolTipText(GuiConst.TOOLTIP_CONVERT);
	}

	private void buildMenuBar() {
		final JMenuBar menuBar = new JMenuBar();
		final JMenu aboutMenu = new JMenu("About");
		aboutMenu.addMenuListener(new MenuListener() {

			public void menuSelected(final MenuEvent arg0) {
				JOptionPane.showMessageDialog(MainFrame.this, GuiConst.ABOUT_TEXT, "About JavaCode4Web",
						JOptionPane.INFORMATION_MESSAGE, GuiConst.ICON_LOGO);
			}

			public void menuDeselected(final MenuEvent arg0) {
			}

			public void menuCanceled(final MenuEvent arg0) {
			}
		});
		menuBar.add(aboutMenu);
		final JMenu logMenu = new JMenu("Show Log");
		logMenu.addMenuListener(new MenuListener() {

			public void menuSelected(final MenuEvent arg0) {
				IOUtil.openLogFile();
			}

			public void menuDeselected(final MenuEvent arg0) {
			}

			public void menuCanceled(final MenuEvent arg0) {
			}
		});
		menuBar.add(logMenu);
		setJMenuBar(menuBar);
	}

	public void actionStarted() {
		enableComponents(false);
		optionsSideBar.actionStarted();
	}

	private void enableComponents(boolean state) {
		convertButton.setEnabled(state);
		observeButton.setEnabled(state);
		cancelButton.setEnabled(!state);
	}

	public void actionStopped() {
		enableComponents(true);
		optionsSideBar.actionStopped();
	}

	public JTextArea getConsoleTextArea() {
		return consoleTextArea;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public OptionsSideBar getOptionsSideBar() {
		return optionsSideBar;
	}

	public void addConvertListener(ActionListener actionListener) {
		convertButton.addActionListener(actionListener);
	}

	public void addObserveListener(ActionListener actionListener) {
		observeButton.addActionListener(actionListener);
	}

	public void addCancelListener(ActionListener actionListener) {
		cancelButton.addActionListener(actionListener);
	}

}
