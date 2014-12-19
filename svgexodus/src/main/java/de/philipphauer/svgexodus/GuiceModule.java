package de.philipphauer.svgexodus;

import java.io.File;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import de.philipphauer.svgexodus.cli.CLIOptions;
import de.philipphauer.svgexodus.io.ser.JsonSerializer;
import de.philipphauer.svgexodus.io.ser.OptionsSerializer;
import de.philipphauer.svgexodus.model.FeatureSwitch;
import de.philipphauer.svgexodus.model.Options;

public class GuiceModule extends AbstractModule {

	private static final Logger logger = LoggerFactory.getLogger(GuiceModule.class);
	private CLIOptions cliOptions;

	public GuiceModule(CLIOptions cliOptions) {
		this.cliOptions = cliOptions;
	}

	@Override
	protected void configure() {
		bind(OptionsSerializer.class).to(JsonSerializer.class);
		bind(CLIOptions.class).toInstance(cliOptions);
	}

	@Provides
	@Singleton
	public FeatureSwitch provideFeatureSwitch() {
		return new FeatureSwitch(false);
	}

	@Provides
	@Singleton
	public Options provideOptions(OptionsSerializer serializer) {
		Options options;
		try {
			logger.info("loadOptions...");
			options = serializer.loadOptions();
			logger.info("successfully loaded options.");
		} catch (Exception e) {
			options = createDefaultOptions();
			logger.info("Couldn't load Options. Using default options. ", e);
		}
		logger.info("Using options " + options);
		return options;
	}

	private Options createDefaultOptions() {
		return Options.create()
				.withInputPath(new File(System.getProperty("user.home")))
				.withDestinationType(DestinationType.PDF)
				.withOutputFolder(null)
				.withAutomaticallyOpenResults(false)
				.withSvgMistakeCorrection(false)
				.withPrefixWithParentFoldername(false)
				.withDefaultWeight()
				.withJpgQuality(0.8f);
	}

	@Provides
	@Singleton
	public EventBus provideEventBus() {
		return new EventBus("svgExodusEventBus");
	}

}
