package de.philipphauer.svgexodus.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.eventbus.EventBus;

import de.philipphauer.svgexodus.gui.GuiConst;
import de.philipphauer.svgexodus.gui.event.ConsoleLogEvent;
import de.philipphauer.svgexodus.io.IOUtil;
import de.philipphauer.svgexodus.model.FeatureSwitch;

/**
 * @author Philipp Hauer
 */
public class ZeissSVGCorrector {

	private static final Logger logger = LoggerFactory.getLogger(ZeissSVGCorrector.class);

	@Inject
	private EventBus eventBus;

	@Inject
	private FeatureSwitch featureSwitch;

	public Path applyCorrectionFeatureIfNecessary(Path svgFile, boolean isSvgMistakeCorrection) throws IOException {
		boolean svgIsAlreadyCorrected = svgFile.toString().endsWith(FeatureSwitch.CORRECTION_FILE_POSTFIX + ".svg");
		if (featureSwitch.isCorrectionFeatureActived() && isSvgMistakeCorrection && !svgIsAlreadyCorrected) {
			eventBus.post(new ConsoleLogEvent("Start correction of: " + svgFile));
			Path correctedSvgFile = executeCorrection(svgFile);
			eventBus.post(new ConsoleLogEvent("Finished correction. New file: " + correctedSvgFile));
			return correctedSvgFile;
		}
		return svgFile;
	}

	/**
	 * Führt eine spezielle Korrektur von fehlerhaften SVG-Dateien durch. Diese Fehler sind: - Adding namespace
	 * declaration for svg and xlink if missing. - Removing non-standard style property 'text-anchor: left'. Erstellt
	 * einen neue korrigierte SVG-Datei mit dem Namen "*-corrected.svg" und gibt diese zurück Hintergrund: bestimmte
	 * Zeiss-Produkte (f Augenärzte) liefernt arg fehlerhafte svg-Dateien zurück. Hier werden sie korrigiert.
	 */
	private Path executeCorrection(Path svgFile) throws IOException {
		String encoding = IOUtil.detectCharset(svgFile);
		logger.info("detected encoding:" + encoding);// WINDOWS-1252 --> Cp1252
		encoding = correctEncodingIfNecessary(encoding);
		logger.info("used encoding:" + encoding);

		// File hat auch n komisches encoding. notepad++ sagt: "UCS-2 LE w/o BOM"... ... UnicodeLittleUnmarked funzt
		List<String> readAllLines = Files.readAllLines(svgFile, Charset.forName(encoding));
		// encoding siehe http://docs.oracle.com/javase/6/docs/technotes/guides/intl/encoding.doc.html
		// or http://stackoverflow.com/questions/879482/how-do-i-encode-decode-utf-16le-byte-arrays-with-a-bom

		// Namespace delaration hinzufügen. Quick&Dirty
		// <svg width="8.250000in" height="10.400000in">
		// -->
		// <svg width="8.250000in" height="10.400000in" xmlns:xlink="http://www.w3.org/1999/xlink"
		// xmlns="http://www.w3.org/2000/svg">
		int rootStartTagLineIndex = 0;
		String newString = null;
		for (String string : readAllLines) {
			// System.out.println(string);
			if (string.startsWith("<svg ") && !string.contains("xmlns") && !string.contains("xmlns:xlink")) {
				newString = string.replace(">",
						" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns=\"http://www.w3.org/2000/svg\">");
				rootStartTagLineIndex = readAllLines.indexOf(string);
				break;
			}
		}
		if (newString != null) {
			readAllLines.set(rootStartTagLineIndex, newString);
		}

		String svgText = Joiner.on(GuiConst.LINE_SEPARATOR).join(readAllLines);

		// remove text-anchor:left
		svgText = svgText.replace("text-anchor:left", "");

		// neues file erstellen
		File newFile = new File(svgFile.toString().replaceAll(".svg$", FeatureSwitch.CORRECTION_FILE_POSTFIX + ".svg"));
		IOUtils.write(svgText, new FileOutputStream(newFile), encoding);
		return Paths.get(newFile.toString());
	}

	private String correctEncodingIfNecessary(String encoding) {
		if (encoding == null) {
			return "UTF-8";// default... don't know if this is a good idea...
		} else if (encoding == "WINDOWS-1252") {
			// Cp1252 scheint aber auch nicht zu funzen --> einfach hart UnicodeLittleUnmarked nehmen,
			return "UnicodeLittleUnmarked";
		}
		// sonst der lib vertrauen
		return encoding;
	}

}
