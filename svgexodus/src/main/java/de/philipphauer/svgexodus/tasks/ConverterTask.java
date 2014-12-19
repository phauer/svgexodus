package de.philipphauer.svgexodus.tasks;

import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.philipphauer.svgexodus.gui.event.ConverterStartEvent;
import de.philipphauer.svgexodus.gui.event.ConverterStopEvent;
import de.philipphauer.svgexodus.gui.event.UserCancelEvent;
import de.philipphauer.svgexodus.io.IOUtil;
import de.philipphauer.svgexodus.model.Options;
import de.philipphauer.svgexodus.process.Converter;

/**
 * @author Philipp Hauer
 */
public class ConverterTask extends StoppableTask {

	@Inject
	private Options options;

	private EventBus eventBus;

	@Inject
	private Converter converter;

	@Inject
	public ConverterTask(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	@Subscribe
	public void userCanceled(UserCancelEvent e) {
		stop();
	}

	@Override
	public void run() {
		final List<Path> pathList = IOUtil.getAllSVGFiles(options.getInputPath().toPath());
		eventBus.post(new ConverterStartEvent(pathList.size(), options.getInputPath().toString()));

		for (Path svgFile : pathList) {
			converter.process(svgFile, options);
			if (isStopSignal()) {
				eventBus.post(new ConverterStopEvent());
				break;
			}
		}

		eventBus.post(new ConverterStopEvent());
	}

}
