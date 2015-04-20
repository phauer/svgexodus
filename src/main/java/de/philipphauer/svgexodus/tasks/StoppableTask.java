package de.philipphauer.svgexodus.tasks;

/**
 * @author Philipp Hauer
 */
public abstract class StoppableTask implements Runnable {

	private boolean stopSignal = false;

	protected boolean isStopSignal() {
		return stopSignal;
	}

	public void stop() {
		stopSignal = true;
	}

}
