package de.philipphauer.svgexodus.gui.event;

public class ConvertedFileEvent extends Event {

	private String convertedFile;

	public ConvertedFileEvent(String convertedFile) {
		this.convertedFile = convertedFile;
	}

	public String getConvertedFile() {
		return convertedFile;
	}
}
