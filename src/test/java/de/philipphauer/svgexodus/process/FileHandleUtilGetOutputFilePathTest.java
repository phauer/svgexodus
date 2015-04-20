package de.philipphauer.svgexodus.process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.philipphauer.svgexodus.di.GuiceRunner;
import de.philipphauer.svgexodus.di.annotations.TestBaseDir;
import de.philipphauer.svgexodus.model.Options;

@RunWith(GuiceRunner.class)
public class FileHandleUtilGetOutputFilePathTest {

	private Options options;

	@Inject
	@TestBaseDir
	private Path baseDir;

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
	public void concreteSvgFile2SameFolder() throws IOException {
		Path inputSvgFile = baseDir.resolve("svgfiles/sample1.svg");

		Path outputFilePath = FileHandleUtil.getOutputFilePath(inputSvgFile, options);

		Assert.assertEquals(baseDir.resolve("svgfiles/sample1.pdf"), outputFilePath);
	}

	@Test
	public void concreteSvgFile2DifferentFolder() throws IOException {
		options.setOutputFolder(new File(baseDir + "/output/"));
		Path inputSvgFile = baseDir.resolve("svgfiles/sample1.svg");

		Path outputFilePath = FileHandleUtil.getOutputFilePath(inputSvgFile, options);

		Assert.assertEquals(baseDir.resolve("output/sample1.pdf"), outputFilePath);
	}

	@Test
	public void concreteSvgFile2DifferentFolderAndPrefixWithParentFoldername() throws IOException {
		configureOptionsForReflectionOfFolderStructure();
		Path inputSvgFile = baseDir.resolve("svgfiles/foo/sample1.svg");

		Path outputFilePath = FileHandleUtil.getOutputFilePath(inputSvgFile, options);

		Assert.assertEquals(baseDir.resolve("output/foo_sample1.pdf"), outputFilePath);
	}

	@Test
	public void dotSvgInPathButNotAtTheEnd() throws IOException {
		options.setOutputFolder(null);
		Path inputSvgFile = baseDir.resolve("de.philipphauer.svgexodus/sample1.svg");

		Path outputFilePath = FileHandleUtil.getOutputFilePath(inputSvgFile, options);

		Assert.assertEquals(baseDir.resolve("de.philipphauer.svgexodus/sample1.pdf"), outputFilePath);
	}

	private void configureOptionsForReflectionOfFolderStructure() {
		options.setInputPath(new File(baseDir + "/svgfiles/"));
		options.setOutputFolder(new File(baseDir + "/output/"));
		options.setPrefixWithParentFoldername(true);
	}

	@Test
	public void concreteSvgFile2DifferentFolderAndPrefixWithParentFoldername2() throws IOException {
		configureOptionsForReflectionOfFolderStructure();
		Path inputSvgFile = baseDir.resolve("svgfiles/foo/fuu/sample1.svg");

		Path outputFilePath = FileHandleUtil.getOutputFilePath(inputSvgFile, options);

		Assert.assertEquals(baseDir.resolve("output/fuu_sample1.pdf"), outputFilePath);
	}
}
