package de.philipphauer.svgexodus.process;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.batik.apps.rasterizer.DestinationType;

import de.philipphauer.svgexodus.model.Options;

/**
 * 
 * @author Philipp Hauer
 */
public class FileHandleUtil {

    public static Path getOutputFilePath(Path inputSvgFile, Options options) {
        DestinationType destinationType = options.getDestinationType();
        if (options.isSeparateOutputFolder()) {
            String svgfileName = inputSvgFile.getFileName().toString();
            String targetFileName = replaceExtension(svgfileName, destinationType);
            if (options.isPrefixWithParentFoldername()) {//TODO GUI: only activate if the resultFolder is activated 
                String fileNamePrefix = getParentFolderName(inputSvgFile);
                String fileName = fileNamePrefix.isEmpty() ? targetFileName : fileNamePrefix + "_" + targetFileName;
                return Paths.get(options.getOutputFolder().toString(), fileName);
            } else {
                File resultFolder = options.getOutputFolder();
                return Paths.get(resultFolder.toString(), targetFileName);
            }
        } else {//place outputfile in same folder as inputSvgFile
            String outputPath = replaceExtension(inputSvgFile.toString(), destinationType);
            return Paths.get(outputPath);
        }
    }

    private static String replaceExtension(String svgFileOrPath, DestinationType destType) {
        String targetFileName = svgFileOrPath.replaceAll("\\.svg$", destType.getExtension());
        return targetFileName;
    }

    /**
     * input: C:/svgfolder/foo/file.svg 
     * output: foo
     */
    static String getParentFolderName(Path inputSvgFile) {
        Path parent = inputSvgFile.getParent();
        boolean isDirectUnderRoot = parent.getParent() == null;
        if (isDirectUnderRoot) {
            return "";
        }
        return parent.getFileName().toString();
    }

}
