package de.philipphauer.svgexodus.cli;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;

public class CLIReader {

	public static CLIOptions readCLIOptions(String[] args) throws CLIException {
		Cli<CLIOptions> cli = CliFactory.createCli(CLIOptions.class);
		try {
			CLIOptions cliOptions = cli.parseArguments(args);
			return cliOptions;
		} catch (ArgumentValidationException e) {
			throw new CLIException("Invalid command line arguments! " + e.getMessage() + ". " + cli.getHelpMessage(), e);
		}
	}
}
