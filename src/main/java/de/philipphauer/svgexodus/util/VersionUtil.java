package de.philipphauer.svgexodus.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.philipphauer.svgexodus.Main;

public class VersionUtil {

    private static final String UNKOWN_VERSION = "UnkownVersion";

    private static final Logger logger = LoggerFactory.getLogger(VersionUtil.class);

    public static String getVersion() {
        try {
            Properties properties = new Properties();
            InputStream stream = Main.class
                    .getResourceAsStream("/META-INF/maven/de.philipphauer/svgexodus/pom.properties");
            properties.load(stream);
            String version = properties.getProperty("version");
            return version;
        }
        catch (IOException | NullPointerException e) {
            logger.warn("Could not load version from pom.properties");
            return UNKOWN_VERSION;
        }
    }

    public static long getAppVersionAsVersionUID() {
        String version = getVersion();//1.2.2
        if (version.equals(UNKOWN_VERSION)) {
            return 0L;
        }
        String versionDigits = version.replace(".", "00");
        long uuid = Long.parseLong(versionDigits);
        return uuid;
    }

}
