package de.philipphauer.svgexodus.io.ser;

import de.philipphauer.svgexodus.util.VersionUtil;

public abstract class AbstractOptionsSerializer implements OptionsSerializer {

    private static final String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir") + "svgexodus-"
            + VersionUtil.getVersion() + "-options";

    private String targetFile;

    public AbstractOptionsSerializer() {
        targetFile = TEMP_FILE_PATH + getExtension();
    }

    AbstractOptionsSerializer(String targetFile) {
        this.targetFile = targetFile + getExtension();
    }

    public String getTargetFile() {
        return targetFile;
    }

    protected abstract String getExtension();
}
