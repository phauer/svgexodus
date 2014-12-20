package de.philipphauer.svgexodus.io.ser;

import java.io.File;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.philipphauer.svgexodus.model.Options;

public class OptionsSerializerTest {

	private final static String BASEDIR = "target/test-classes/options";

	private Options options;

	@Before
	public void init() {
		options = Options.create()
				.withAutomaticallyOpenResults(true)
				.withDestinationType(DestinationType.PDF)
				.withPrefixWithParentFoldername(false)
				.withOutputFolder(new File("C:\foo\bar"))
				.withInputPath(new File("D:\fooooo"))
				.withSvgMistakeCorrection(false);
	}

	@Test
	public void equalsCheck$stdSer() {
		testEqualityOfSavedAndLoadedObject(new StandardObjectSerializer(BASEDIR));
	}

	@Test
	public void equalsCheck$json() {
		testEqualityOfSavedAndLoadedObject(new JsonSerializer(BASEDIR));
	}

	@Test(expected = OptionsSerializerException.class)
	public void fileNotFound$stdSer() throws Exception {
		OptionsSerializer serializer = new StandardObjectSerializer("C:/thisPathDoesntExistXXX");
		serializer.loadOptions();
	}

	@Test(expected = OptionsSerializerException.class)
	public void fileNotFound$json() {
		OptionsSerializer serializer = new JsonSerializer("C:/thisPathDoesntExistXXX");
		serializer.loadOptions();
	}

	private void testEqualityOfSavedAndLoadedObject(OptionsSerializer seralizer) {
		seralizer.saveOptions(options);
		Options loadedOptions = seralizer.loadOptions();
		Assert.assertEquals(options, loadedOptions);
	}
}
