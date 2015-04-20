package de.philipphauer.svgexodus.di;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mockito.Mockito;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import de.philipphauer.svgexodus.di.annotations.TestBaseDir;
import de.philipphauer.svgexodus.di.annotations.TestOutputDir;
import de.philipphauer.svgexodus.model.FeatureSwitch;

public class TestGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		Path baseDir = Paths.get("target/test-classes/");
		bind(Path.class).annotatedWith(TestBaseDir.class).toInstance(baseDir);
		Path outputDir = baseDir.resolve("output");
		bind(Path.class).annotatedWith(TestOutputDir.class).toInstance(outputDir);

		try {
			Files.createDirectories(outputDir);// TODO this is missplaced here
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create directory " + outputDir, e);
		}
	}

	@Provides
	@Singleton
	public FeatureSwitch provideFeatureSwitch() {
		return new FeatureSwitch(true);
	}

	@Provides
	@Singleton
	public EventBus provideEventBus() {
		EventBus eventBusStub = Mockito.mock(EventBus.class);
		Mockito.doNothing().when(eventBusStub).post(null);
		return eventBusStub;
	}
}
