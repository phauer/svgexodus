/*
 * Copyright 2013 Philipp Hauer (philipphauer.de)
 */
package de.philipphauer.svgexodus.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.TeeOutputStream;

/**
 * Motivation dieser Klasse: SVGConverter schmeißt keine Exceptions, wenn er auf Fehler stößt (z.B. Fehlerhafte SVGs,
 * fehlende NS-Definitionen oder fehlende referenzierte Bidler), sondern catch die meisten selbst und schreibt sie auf
 * den Standard-Error-Output. Mein Prog läuft also weiter als ob nichts wäre und ich erhalte nicht die Fehlermeldung.
 * Dieses Objekt speichert den Error-Output zusätzlich und hält ihn abrufbar vor. Getrackt wird alles zwischen Aufruf
 * von start() und stop().
 * 
 * @author Philipp Hauer
 */
public class ErrorOutputTracker {

	private final PrintStream standardErr;

	private final ByteArrayOutputStream byteArrayOutputStream;

	public ErrorOutputTracker() {
		standardErr = System.err;
		byteArrayOutputStream = new ByteArrayOutputStream();
	}

	/**
	 * Standard-Error-Output wird (ZUSÄTZLICH zur Console) noch umgeleitet und gespeichert.
	 * 
	 * @throws FileNotFoundException
	 */
	private void start() {
		PrintStream filePrintStream = new PrintStream(byteArrayOutputStream);
		PrintStream branchedStandardErr = new PrintStream(new TeeOutputStream(filePrintStream, standardErr));
		System.setErr(branchedStandardErr);
	}

	/**
	 * Standard-Error-Output wiederhergestellt, d.h. es wird NUR noch auf der Console geloggt.
	 */
	private void stop() {
		System.setErr(standardErr);
		IOUtils.closeQuietly(byteArrayOutputStream);
	}

	/**
	 * Somit kann festgestellt werden, ob etwas auf Err ausgegeben wurde.
	 */
	private boolean hasOutputBeenPrinted() {
		return !byteArrayOutputStream.toString().isEmpty();
	}

	private String getOutput() {
		return byteArrayOutputStream.toString();
	}

	@SuppressWarnings("unused")
	private void saveOutputToFile(File pFile) throws IOException {
		FileUtils.writeStringToFile(pFile, byteArrayOutputStream.toString());
	}

	public static void executeWithOutputTracking(TrackedRunnable runnable) throws SVGConverterException,
			ErrorOutputPrintedExeception {
		ErrorOutputTracker errorOutputTracker = new ErrorOutputTracker();
		errorOutputTracker.start();
		runnable.run();
		errorOutputTracker.stop();
		if (errorOutputTracker.hasOutputBeenPrinted()) {
			String message = "Error appears while converting (invalid SVG-file?):\n" + errorOutputTracker.getOutput();
			throw new ErrorOutputPrintedExeception(message);
		}
	}

	public static interface TrackedRunnable {
		void run() throws SVGConverterException;
	}

	@SuppressWarnings("serial")
	public static class ErrorOutputPrintedExeception extends Exception {

		private String message;

		public ErrorOutputPrintedExeception(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

	}
}
