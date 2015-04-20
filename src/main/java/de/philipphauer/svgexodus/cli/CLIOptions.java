package de.philipphauer.svgexodus.cli;

import com.lexicalscope.jewel.cli.Option;

public interface CLIOptions {

	@Option(description = "If true, SVGExodus starts minimized as a tray icon. Defaults to false")
	boolean isStartMinimized();

	@Option(description = "If true, SVGExodus starts the observe mode automatically. Defaults to false")
	boolean isStartObserving();
}
