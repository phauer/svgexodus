package de.philipphauer.svgexodus.gui.event;

public class ConverterStartEvent extends StartEvent {

	private int svgFilesCount;
	private String sourcePath;

	public ConverterStartEvent(int svgFilesCount, String sourcePath) {
		this.svgFilesCount = svgFilesCount;
		this.sourcePath = sourcePath;
	}

	public int getSvgFilesCount() {
		return svgFilesCount;
	}

	public String getSourcePath() {
		return sourcePath;
	}

}
