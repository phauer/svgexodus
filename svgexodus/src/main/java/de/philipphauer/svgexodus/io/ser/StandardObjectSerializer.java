package de.philipphauer.svgexodus.io.ser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.philipphauer.svgexodus.model.Options;

public class StandardObjectSerializer extends AbstractOptionsSerializer {

	private static final Logger logger = LoggerFactory.getLogger(StandardObjectSerializer.class);

	public StandardObjectSerializer() {
		super();
	}

	StandardObjectSerializer(String targetFile) {
		super(targetFile);
	}

	@Override
	public void saveOptions(Options options) throws OptionsSerializerException {
		logger.info("saveOptions... " + options);
		try (ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(getTargetFile()))) {
			oout.writeObject(options);
			logger.info("successfully saved options to " + getTargetFile());
		} catch (IOException e) {
			throw new OptionsSerializerException("Error while writing object to file", e);
		}
	}

	@Override
	public Options loadOptions() throws OptionsSerializerException {
		logger.info("load Options from " + getTargetFile());
		try (ObjectInputStream oout = new ObjectInputStream(new FileInputStream(getTargetFile()))) {
			Options op = (Options) oout.readObject();
			return op;
		} catch (IOException | ClassNotFoundException e) {
			throw new OptionsSerializerException("Error while reading objects from file", e);
		}
	}

	@Override
	protected String getExtension() {
		return ".ser";
	}

}
