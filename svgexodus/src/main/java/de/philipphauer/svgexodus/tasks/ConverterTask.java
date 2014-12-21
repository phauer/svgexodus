package de.philipphauer.svgexodus.tasks;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.philipphauer.svgexodus.gui.GuiConst;
import de.philipphauer.svgexodus.gui.event.ConverterStartEvent;
import de.philipphauer.svgexodus.gui.event.ConverterStopEvent;
import de.philipphauer.svgexodus.gui.event.UserCancelEvent;
import de.philipphauer.svgexodus.model.Options;
import de.philipphauer.svgexodus.process.Converter;

/**
 * @author Philipp Hauer
 */
public class ConverterTask extends StoppableTask {

	private static final Logger logger = LoggerFactory.getLogger(ConverterTask.class);

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
		final List<Path> pathList = getAllSVGFiles(options.getInputPath().toPath());
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

	/**
	 * including sub directories.
	 */
	public List<Path> getAllSVGFiles(Path path) {
		final List<Path> pathList = new ArrayList<>();
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (attrs.isRegularFile() && file.toString().endsWith(".svg")) {
						pathList.add(file);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			String message = "Error while searching through the file system.";
			JOptionPane.showMessageDialog(null, message + GuiConst.LINE_SEPARATOR + e.getMessage());
			logger.error(message, e);
		}
		return pathList;
	}

}
