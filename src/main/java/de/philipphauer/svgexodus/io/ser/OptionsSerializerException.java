package de.philipphauer.svgexodus.io.ser;

@SuppressWarnings("serial")
public class OptionsSerializerException extends RuntimeException {

	public OptionsSerializerException(String message, Exception ex) {
		super(message, ex);
	}

	public OptionsSerializerException(String string) {
		super(string);
	}
}
