package de.philipphauer.svgexodus.tasks;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.philipphauer.svgexodus.gui.event.ConsoleLogEvent;
import de.philipphauer.svgexodus.gui.event.ObserverStartEvent;
import de.philipphauer.svgexodus.gui.event.ObserverStopEvent;
import de.philipphauer.svgexodus.gui.event.UserCancelEvent;
import de.philipphauer.svgexodus.model.Options;
import de.philipphauer.svgexodus.process.Converter;

/**
 * @author Philipp Hauer
 */
public class ObserveTask extends StoppableTask {

	private static final Logger logger = LoggerFactory.getLogger(ObserveTask.class);

	@Inject
	private Options options;

	private EventBus eventBus;

	@Inject
	private Converter converter;

	@Inject
	public ObserveTask(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	@Subscribe
	public void userChanceled(UserCancelEvent e) {
		stop();
	}

	@Override
	public void run() {
		eventBus.post(new ObserverStartEvent("Start observing *.svg files for changes in " + options.getInputPath()));
		try {
			FileSystemManager manager = VFS.getManager();
			FileObject path = manager.resolveFile(options.getInputPath().toString());

			DefaultFileMonitor fileMonitor = createFileMonitor(path);
			while (true) {
				Thread.sleep(1000);
				if (isStopSignal()) {
					fileMonitor.stop();
					eventBus.post(new ObserverStopEvent());
					break;
				}
			}
		} catch (FileSystemException | InterruptedException e) {
			String message = "Error while observing file system: ";
			JOptionPane.showMessageDialog(null, message + e.getMessage());
			logger.error(message, e);
		}
	}

	private DefaultFileMonitor createFileMonitor(FileObject path) {
		DefaultFileMonitor fileMonitor = new DefaultFileMonitor(new ConvertingFileListener());
		fileMonitor.setRecursive(true);
		fileMonitor.setDelay(1000);
		fileMonitor.addFile(path);
		fileMonitor.start();
		return fileMonitor;
	}

	public class ConvertingFileListener implements FileListener {

		@Override
		public void fileDeleted(FileChangeEvent pEvent) throws Exception {
		}

		@Override
		public void fileCreated(FileChangeEvent pEvent) throws Exception {
			fileChangedOrCreated(pEvent);
		}

		@Override
		public void fileChanged(FileChangeEvent pEvent) throws Exception {
			fileChangedOrCreated(pEvent);
		}

		private void fileChangedOrCreated(FileChangeEvent pEvent) throws Exception {
			FileObject fileObject = pEvent.getFile();
			File file = new File(fileObject.getURL().getPath());
			if (file.toString().endsWith(".svg")) {
				eventBus.post(new ConsoleLogEvent("Detected change in " + file));
				Path svgFile = Paths.get(file.toString());
				converter.process(svgFile, options);
			}
		}

	}
}
