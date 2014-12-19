package de.philipphauer.svgexodus.gui.event;

public abstract class Event {

	private String message;

	public Event() {
	}

	public Event(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
