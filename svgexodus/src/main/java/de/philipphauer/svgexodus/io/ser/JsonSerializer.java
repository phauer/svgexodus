package de.philipphauer.svgexodus.io.ser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.google.common.base.Joiner;

import de.philipphauer.svgexodus.model.Options;

public class JsonSerializer extends AbstractOptionsSerializer {

	private static final Charset CHARSET = Charset.forName("UTF-8");

	private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

	public JsonSerializer() {
		super();
	}

	JsonSerializer(String targetFile) {
		super(targetFile);
	}

	@Override
	public void saveOptions(Options options) throws IOException {
		String json = JsonWriter.objectToJson(options);
		Path targetFile = Paths.get(getTargetFile());
		if (!Files.exists(targetFile)) {
			Files.createFile(targetFile);
		}
		Files.write(targetFile, json.getBytes(CHARSET), StandardOpenOption.WRITE);
		logger.info("successfully saved options to " + getTargetFile());
	}

	@Override
	public Options loadOptions() throws Exception {
		Path targetFile = Paths.get(getTargetFile());
		if (!Files.exists(targetFile)) {
			throw new FileNotFoundException("Options file not found at " + targetFile);
		}
		String json = Joiner.on("").join(Files.readAllLines(targetFile, CHARSET));
		Options opt = (Options) JsonReader.jsonToJava(json);
		logger.info("successfully loaded options from " + getTargetFile());
		return opt;
	}

	@Override
	protected String getExtension() {
		return ".js";
	}
}
