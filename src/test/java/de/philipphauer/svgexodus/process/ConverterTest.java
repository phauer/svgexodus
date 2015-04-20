package de.philipphauer.svgexodus.process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.philipphauer.svgexodus.di.GuiceRunner;
import de.philipphauer.svgexodus.di.annotations.TestBaseDir;
import de.philipphauer.svgexodus.di.annotations.TestOutputDir;
import de.philipphauer.svgexodus.model.FeatureSwitch;
import de.philipphauer.svgexodus.model.Options;

@RunWith(GuiceRunner.class)
public class ConverterTest {

	@Inject
	private Converter converter;

	private Options options;

	@Inject
	@TestBaseDir
	private Path baseDir;

	@Inject
	@TestOutputDir
	private Path outputDir;

	@Before
	public void initOptions() {
		options = Options.create()
				.withAutomaticallyOpenResults(false)
				.withDestinationType(DestinationType.PDF)
				.withPrefixWithParentFoldername(false)
				.withOutputFolder(null)
				.withInputPath(null)// set it in test
				.withSvgMistakeCorrection(false);
	}

	@Test
	public void process$SameFolder() throws IOException {
		options.setOutputFolder(null);
		Path svgFile = baseDir.resolve("svgfiles/sample1.svg");

		converter.process(svgFile, options);

		Path resultPDF = baseDir.resolve("svgfiles/sample1.pdf");
		assertExistanceAndNotEmpty(resultPDF);
	}

	@Test
	public void process$SeparateResultFolder() throws IOException {
		options.setOutputFolder(outputDir.toFile());
		Path svgFile = baseDir.resolve("svgfiles/sample1.svg");

		converter.process(svgFile, options);

		Path resultPDF = outputDir.resolve("sample1.pdf");
		assertExistanceAndNotEmpty(resultPDF);
	}

	@Test
	public void process$SeparateResultFolder$ReflectFolderStructure() throws IOException {
		options.setPrefixWithParentFoldername(true);
		options.setOutputFolder(outputDir.toFile());
		options.setInputPath(baseDir.resolve("svgfiles").toFile());
		Path svgFile = baseDir.resolve("svgfiles/foo/sub-sample1.svg");

		converter.process(svgFile, options);

		Path resultPDF = outputDir.resolve("foo_sub-sample1.pdf");
		assertExistanceAndNotEmpty(resultPDF);
	}

	@Test
	public void process$WithCorrection() throws IOException {
		options.setSvgMistakeCorrection(true);
		options.setOutputFolder(outputDir.toFile());
		Path svgFile = baseDir.resolve("invalid-files/Maria_6-21.svg");

		converter.process(svgFile, options);

		Path correctedSVG = baseDir
				.resolve("invalid-files/Maria_6-21" + FeatureSwitch.CORRECTION_FILE_POSTFIX + ".svg");
		assertExistanceAndNotEmpty(correctedSVG);

		Path resultPDF = outputDir.resolve("Maria_6-21" + FeatureSwitch.CORRECTION_FILE_POSTFIX + ".pdf");
		assertExistanceAndNotEmpty(resultPDF);
	}

	private void assertExistanceAndNotEmpty(Path resultPDF) throws IOException {
		Assert.assertTrue(Files.exists(resultPDF));
		Assert.assertTrue(Files.size(resultPDF) > 0);
	}

}
