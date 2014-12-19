package de.philipphauer.svgexodus.gui;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Philipp Hauer
 */
public class GuiConst {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static final String TOOLTIP_OBSERVE = "Starts observation of a folder. When detecting a change or a new file a conversation will automatically start.";

	public static final String TOOLTIP_CONVERT = "Convert selected file or all files in the selected folder (and recursively all subfolders).";

	public static final String TOOLTIP_CORRECTION = "Corrects misakes in the svg file. " + GuiConst.LINE_SEPARATOR
			+ "- Adding namespace declaration for svg and xlink if missing." + GuiConst.LINE_SEPARATOR
			+ "- Removing non-standard style property 'text-anchor: left'.";

	public static final String TOOLTIP_PREFIX = "Prefixes the output file's name with the name of the folder that contains the svg file";

	public static final String TOOLTIP_HEIGHT = "This only applies if you select JPGs. Enter a number between 0 and 1. e.g. '0.88'.";

	public static final String TOOLTIP_WIDTH = "By increasing the width you can increase the quality of the output. Keep it empty to use the input format. the height is set proportionally. e.g. '1500'.";

	// see http://www.w3.org/TR/SVG10/text.html#TextAnchorProperty

	public static final String ABOUT_TEXT = "Copyright 2012 by Philipp Hauer (philipphauer.de). License: GNU GPL v3."
			+ GuiConst.LINE_SEPARATOR
			+ "- Icons by Mark James (http://www.famfamfam.com/lab/icons/silk/). Creative Commons BY 2.5 License."
			+ GuiConst.LINE_SEPARATOR
			+ "- Apache Batik SVG Toolkit (http://xmlgraphics.apache.org/batik/). Apache License 2.0."
			+ GuiConst.LINE_SEPARATOR + "- MigLayout (http://migcalendar.com/miglayout/). BSD license."
			+ GuiConst.LINE_SEPARATOR
			+ "- Apache Commons VFS 2.0 (http://commons.apache.org/vfs/). Apache License 2.0."
			+ GuiConst.LINE_SEPARATOR + "- Apache Commons IO 2.4 (http://commons.apache.org/io/). Apache License 2.0."
			+ GuiConst.LINE_SEPARATOR
			+ "- JGoodies Common 1.6 and Binding 2.8 (http://www.jgoodies.com/). BSD license."
			+ GuiConst.LINE_SEPARATOR
			+ "- JUniversalchardet 1.0.3 (http://code.google.com/p/juniversalchardet/). Mozilla Public License 1.1.";

	public static List<? extends Image> getIconList() {
		List<Image> list = new ArrayList<Image>();
		list.add(Toolkit.getDefaultToolkit().getImage(GuiConst.class.getClass().getResource("/logo/logo_128.png")));
		list.add(Toolkit.getDefaultToolkit().getImage(GuiConst.class.getClass().getResource("/logo/logo_64.png")));
		list.add(Toolkit.getDefaultToolkit().getImage(GuiConst.class.getClass().getResource("/logo/logo_48.png")));
		list.add(Toolkit.getDefaultToolkit().getImage(GuiConst.class.getClass().getResource("/logo/logo_32.png")));
		list.add(Toolkit.getDefaultToolkit().getImage(GuiConst.class.getClass().getResource("/logo/logo_16.png")));
		return list;
	}

	public static Image IMAGE_LOGO = Toolkit.getDefaultToolkit().getImage(
			GuiConst.class.getClass().getResource("/logo/logo_64.png"));

	public static Icon ICON_LOGO = new ImageIcon(GuiConst.class.getClass().getResource("/logo/logo_64.png"));

	public static Icon ICON_FILE_SEARCH = new ImageIcon(GuiConst.class.getClass().getResource(
			"/icons/folder_explore.png"));

	public static Icon ICON_CANCEL = new ImageIcon(GuiConst.class.getClass().getResource("/icons/cancel.png"));

	public static Icon ICON_OBSERVE = new ImageIcon(GuiConst.class.getClass().getResource("/icons/eye.png"));

	public static Icon ICON_CONVERT = new ImageIcon(GuiConst.class.getClass().getResource("/icons/pictures.png"));
}
