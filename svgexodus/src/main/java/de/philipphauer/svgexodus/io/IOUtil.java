package de.philipphauer.svgexodus.io;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.philipphauer.svgexodus.Main;
import de.philipphauer.svgexodus.gui.GuiConst;

/**
 * @author Philipp Hauer
 */
public final class IOUtil {

    private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

    /**
     * Läuft rekursiv alle Unterordner ab und gibt Liste von SVG-Dateien zurück.
     */
    public static List<Path> getAllSVGFiles(Path path) {
        final ArrayList<Path> pathList = new ArrayList<>();
        try {
            Files.walkFileTree(path, new FileVisitorAdapter<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isRegularFile() && file.toString().endsWith(".svg")) {
                        pathList.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            String message = "Error while searching through the file system.";
            JOptionPane.showMessageDialog(null, message + GuiConst.LINE_SEPARATOR + e.getMessage());
            logger.error(message, e);
        }
        return pathList;
    }

    public static List<Path> getAllSubDirectorys(Path pBaseDir) {
        final ArrayList<Path> pathList = new ArrayList<>();
        try {
            Files.walkFileTree(pBaseDir, new FileVisitorAdapter<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path pDir, BasicFileAttributes pAttrs) throws IOException {
                    pathList.add(pDir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            String message = "Error while searching through the file system.";
            JOptionPane.showMessageDialog(null, message + GuiConst.LINE_SEPARATOR + e.getMessage());
            logger.error(message, e);
        }
        return pathList;
    }

    private static final Map<Path, Date> cMap = new HashMap<>();

    private static final int THRESHOLD_OBSERVING_TIME_ELAPSED_FOR_VALID_EVENT = 4500;

    /**
     * Kontext: Der WatchService erzeugt bei Änderungen nicht nur ein Event, sondern häufig zwei direkt hintereinander.
     * Da das Problem nicht zu lösen war (bzw. es sein kann, dass der Editor wirklich zweimal die Datei modifziert), wird eine 
     * Heuristik verwendet. Ein Event (ausgelöst von eine Änderung auf einem Path) ist dann gültig, wenn das letzte
     * Event auf den selben Path mehr als -THRESHOLD_OBSERVING_TIME_ELAPSED_FOR_VALID_EVENT- Sekunden her ist. --> Schwellwert
     */
    public static boolean isEventValid(Path pPath) {
        Date timeOfCurrentEvent = new Date();
        Date timeofLastEvent = cMap.get(pPath);
        if (timeofLastEvent == null
                || (timeOfCurrentEvent.getTime() - timeofLastEvent.getTime()) > THRESHOLD_OBSERVING_TIME_ELAPSED_FOR_VALID_EVENT) {
            //mehr als eine sec vergangen --> ok
            cMap.put(pPath, timeOfCurrentEvent);
            return true;
        }
        return false;
    }

    public static String detectCharset(Path pSvgFile) throws IOException {
        byte[] buf = new byte[4096];
        try (FileInputStream fis = new FileInputStream(pSvgFile.toFile())) {
            UniversalDetector detector = new UniversalDetector(null);

            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();

            String encoding = detector.getDetectedCharset();
            //null, wenn nicht erfolgreich

            detector.reset();
            return encoding;
        }
    }

    public static void openLogFile() {
        try {
            String logFilePath = getLogFilePath();
            Desktop.getDesktop().open(new File(logFilePath));
        }
        catch (IOException e) {
            String message = "Couln't open log file: " + e.getMessage();
            JOptionPane.showMessageDialog(null, message);
            logger.error(message, e);
        }

    }

    private static String getLogFilePath() throws FileNotFoundException, IOException {
        Properties logProps = new Properties();
        InputStream stream = Main.class.getResourceAsStream("/log4j.properties");
        logProps.load(stream);
        String logFilePath = logProps.getProperty("log4j.appender.file.File");
        logFilePath = logFilePath.replace("${java.io.tmpdir}/", System.getProperty("java.io.tmpdir"));
        return logFilePath;
    }

}
