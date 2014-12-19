package de.philipphauer.svgexodus.io.ser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    public void saveOptions(Options pOptions) throws IOException {
        logger.info("saveOptions... " + pOptions);
        try (ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(getTargetFile()))) {
            oout.writeObject(pOptions);
            logger.info("successfully saved options to " + getTargetFile());
        }
    }

    @Override
    public Options loadOptions() throws FileNotFoundException, IOException, ClassNotFoundException {
        logger.info("load Options from " + getTargetFile());
        File file = new File(getTargetFile());
        if (!file.exists()) {
            throw new FileNotFoundException("Options file not found at " + file);
        }
        try (ObjectInputStream oout = new ObjectInputStream(new FileInputStream(getTargetFile()))) {
            Options op = (Options)oout.readObject();
            return op;
        }
    }

    @Override
    protected String getExtension() {
        return ".ser";
    }

}
