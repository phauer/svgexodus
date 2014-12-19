package de.philipphauer.svgexodus.process;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import de.philipphauer.svgexodus.gui.event.ConsoleLogEvent;
import de.philipphauer.svgexodus.io.ErrorOutputTracker;
import de.philipphauer.svgexodus.io.ErrorOutputTracker.ErrorOutputPrintedExeception;
import de.philipphauer.svgexodus.io.ErrorOutputTracker.TrackedRunnable;
import de.philipphauer.svgexodus.model.Options;

/**
 * 
 * @author Philipp Hauer
 */
public class Converter {

	private static final Logger logger = LoggerFactory.getLogger(Converter.class);

	@Inject
	private EventBus eventBus;
	@Inject
	private ZeissSVGCorrector corrector;

	public void process(Path svgFile, Options options) {
		Preconditions.checkArgument(Files.exists(svgFile), "svg files doesn't exist: " + svgFile);
		try {
			svgFile = corrector.applyCorrectionFeatureIfNecessary(svgFile, options.isSvgMistakeCorrection());
			Path outputFile = FileHandleUtil.getOutputFilePath(svgFile, options);
			convert(svgFile, outputFile, options.getDestinationType(), options.getWeight(), options.getJpgQuality());
			eventBus.post(new ConsoleLogEvent(outputFile.toString()));
			openFileIfNecessary(outputFile, options);
		} catch (ErrorOutputPrintedExeception | SVGConverterException | IOException e) {
			eventBus.post(new ConsoleLogEvent("Couldn't convert: " + svgFile));
			JOptionPane.showMessageDialog(null, "Error appears while converting: " + e.getMessage());
			logger.error("Converting Error! Options:" + options + ". svgFile:" + svgFile, e);
		}
	}

	private void openFileIfNecessary(Path outputFile, Options options) {
		if (options.isAutomaticallyOpenResults()) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(outputFile.toFile());
			} catch (IOException e) {
				logger.error("Couldn't open produced file" + outputFile, e);
			}
		}
	}

	private void convert(Path svgFile, Path outputFile, DestinationType destinationType, float weight, float jpgQuality)
			throws ErrorOutputPrintedExeception, SVGConverterException {
		final SVGConverter svgConverter = new SVGConverter();
		svgConverter.setDestinationType(destinationType);
		svgConverter.setDst(outputFile.toFile());
		svgConverter.setSources(new String[] { svgFile.toFile().getAbsolutePath() });
		svgConverter.setWidth(weight);// applies for all destinatoinTypes
		svgConverter.setQuality(jpgQuality);// interval: (0,1)

		ErrorOutputTracker.executeWithOutputTracking(new TrackedRunnable() {
			public void run() throws SVGConverterException {
				svgConverter.execute();
			}
		});
	}
}
