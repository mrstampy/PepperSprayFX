/*
 * 
 */
package com.github.mrstampy.pprspray.util;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

import com.google.common.util.concurrent.AtomicDouble;

// TODO: Auto-generated Javadoc
/**
 * The Class RepositioningDragListener.
 */
public class RepositioningDragListener implements EventHandler<MouseEvent> {

	/** The window. */
	private Window window;

	/** The last x. */
	private AtomicDouble lastX = new AtomicDouble();

	/** The last y. */
	private AtomicDouble lastY = new AtomicDouble();

	/**
	 * The Constructor.
	 *
	 * @param window
	 *          the window
	 */
	public RepositioningDragListener(Window window) {
		this.window = window;
		addEventHandlers();
	}

	/**
	 * Adds the event handlers.
	 */
	private void addEventHandlers() {
		window.addEventHandler(MouseEvent.DRAG_DETECTED, this);
		window.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(MouseEvent event) {
		EventType<? extends MouseEvent> type = event.getEventType();

		if (type == MouseEvent.DRAG_DETECTED) {
			dragDetected(event);
		} else if (type == MouseEvent.MOUSE_DRAGGED) {
			mouseDraggedDetected(event);
		}
	}

	/**
	 * Mouse dragged detected.
	 *
	 * @param e
	 *          the e
	 */
	private void mouseDraggedDetected(MouseEvent e) {
		window.setX(lastX.get() + e.getX());
		window.setY(lastY.get() + e.getY());
		setLast();
	}

	/**
	 * Drag detected.
	 *
	 * @param e
	 *          the e
	 */
	private void dragDetected(MouseEvent e) {
		setLast();
	}

	/**
	 * Sets the last.
	 */
	private void setLast() {
		lastX.set(window.getX());
		lastY.set(window.getY());
	}

}
