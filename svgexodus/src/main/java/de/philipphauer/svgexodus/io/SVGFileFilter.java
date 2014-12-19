/*
 * Copyright 2012 Philipp Hauer (philipphauer.de)
 */
package de.philipphauer.svgexodus.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Philipp Hauer
 */
public class SVGFileFilter extends FileFilter {
    
    @Override
    public String getDescription() {
        return "SVG file or folder";
    }


    @Override
    public boolean accept(File arg0) {
        if (arg0.isDirectory()) {
            return true;
        }
        if (arg0.isFile() && arg0.toString().endsWith(".svg")) {
            return true;
        }
        return false;
    }

}
