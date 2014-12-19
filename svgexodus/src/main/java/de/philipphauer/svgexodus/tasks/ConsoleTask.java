package de.philipphauer.svgexodus.tasks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.philipphauer.svgexodus.gui.GuiConst;
import de.philipphauer.svgexodus.gui.event.ApplicationClosingEvent;
import de.philipphauer.svgexodus.gui.event.ConsoleLogEvent;
import de.philipphauer.svgexodus.gui.event.ConvertedFileEvent;
import de.philipphauer.svgexodus.gui.event.ConverterStartEvent;
import de.philipphauer.svgexodus.gui.event.ConverterStopEvent;
import de.philipphauer.svgexodus.gui.event.ObserverStartEvent;
import de.philipphauer.svgexodus.gui.event.ObserverStopEvent;

/**
 * @author Philipp Hauer
 */
public class ConsoleTask extends StoppableTask {

	private static final Logger logger = LoggerFactory.getLogger(ConsoleTask.class);

	private JTextArea txaConsole;

	private final BlockingQueue<String> messageQueue;

	private JProgressBar pgbProgress;

	public ConsoleTask(JTextArea txaConsole, JProgressBar pPgbProgress, EventBus eventBus) {
		this.txaConsole = txaConsole;
		this.pgbProgress = pPgbProgress;
		messageQueue = new LinkedBlockingQueue<String>();
		eventBus.register(this);
	}

	@Subscribe
	public void converterStarted(ConsoleLogEvent e) {
		writeToConsole(e.getMessage());
	}

	@Subscribe
	public void converterStarted(ConverterStartEvent e) {
		writeToConsole("Convertion started.");
		writeToConsole("Found " + e.getSvgFilesCount() + " *.svg files in " + e.getSourcePath());
		resetProgressBar(e.getSvgFilesCount());
	}

	@Subscribe
	public void converterStopped(ConverterStopEvent e) {
		writeToConsole("Converting stopped.");
	}

	@Subscribe
	public void fileConverted(ConvertedFileEvent e) {
		writeToConsole("Converted: " + e.getConvertedFile(), true);

	}

	@Subscribe
	public void observationStarted(ObserverStartEvent e) {
		writeToConsole(e.getMessage());
	}

	@Subscribe
	public void observationStopped(ObserverStopEvent e) {
		writeToConsole("Observing stopped.");
	}

	@Subscribe
	public void applicationIsClosing(ApplicationClosingEvent e) {
		stop();
	}

	/**
	 * Nachricht wird auf Console ausgegeben.
	 * 
	 * @param message
	 */
	public void writeToConsole(String message) {
		writeToConsole(message, false);
	}

	/**
	 * Setzt Progressbar zurück
	 */
	public void resetProgressBar(int pMaxium) {
		pgbProgress.setValue(0);
		pgbProgress.setMaximum(pMaxium);
	}

	/**
	 * Nachricht wird auf Console ausgegeben.
	 * 
	 * @param pMessage
	 */
	public void writeToConsole(String pMessage, boolean pInkrementProgressbar) {
		logger.info(pMessage);
		try {
			messageQueue.put(pMessage);
			if (pInkrementProgressbar) {
				pgbProgress.setValue(pgbProgress.getValue() + 1);
			}
		} catch (InterruptedException e) {
			handleError(e);
		}
	}

	private void handleError(InterruptedException e) {
		String message = "Internal Error.";
		logger.error(message, e);
		JOptionPane.showMessageDialog(null, message + e.getMessage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!isStopSignal()) {
			try {
				String message = messageQueue.take();
				txaConsole.append(message + GuiConst.LINE_SEPARATOR);
				txaConsole.setCaretPosition(txaConsole.getDocument().getLength());
			} catch (InterruptedException e) {
				handleError(e);
			}
		}

	}

}
