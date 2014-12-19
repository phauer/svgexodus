package de.philipphauer.svgexodus.cli;

import com.lexicalscope.jewel.cli.ArgumentValidationException;

@SuppressWarnings("serial")
public class CLIException extends RuntimeException {

	public CLIException(String string, ArgumentValidationException e) {
		super(string, e);
	}

}
