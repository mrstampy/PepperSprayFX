/*
 * PepperSprayFX Copyright (C) 2014 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package com.github.mrstampy.pprspray;

import java.net.InetSocketAddress;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import javax.jmdns.ServiceInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class AvailableWindow.
 */
public class AvailableWindow extends Popup {

	private List<ServiceInfo> available;

	private AvailableWindowSelectionCallback callback;

	/**
	 * The Constructor.
	 *
	 * @param available
	 *          the available
	 * @param callback
	 *          the callback
	 */
	public AvailableWindow(List<ServiceInfo> available, AvailableWindowSelectionCallback callback) {
		this.available = available;
		this.callback = callback;
		init();
	}

	private void init() {
		setWidth(320);
		setHeight(180);
		getContent().add(createContent());
	}

	private Node createContent() {
		VBox box = new VBox(5);
		box.setMinHeight(180);
		box.setMinWidth(320);
		box.setAlignment(Pos.CENTER);

		for (ServiceInfo si : getAvailable()) {
			box.getChildren().add(createRow(si));
		}

		return box;
	}

	private Node createRow(ServiceInfo si) {
		Text text = new Text(si.getInetAddresses()[0] + ":" + si.getPort());

		text.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> evaluate(e, si));
		Font existing = text.getFont();
		text.setFont(Font.font(existing.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 30));
		text.setFill(Color.ORANGERED);
		text.setEffect(new DropShadow(10, Color.GREENYELLOW));

		return text;
	}

	private void evaluate(MouseEvent e, ServiceInfo si) {
		if (MouseButton.PRIMARY != e.getButton() || e.getClickCount() != 2) return;

		callback.selected(createAddress(si));

		hide();
	}

	private InetSocketAddress createAddress(ServiceInfo si) {
		return new InetSocketAddress(si.getInetAddresses()[0], si.getPort());
	}

	/**
	 * Gets the available.
	 *
	 * @return the available
	 */
	public List<ServiceInfo> getAvailable() {
		return available;
	}

	/**
	 * The Interface AvailableWindowSelectionCallback.
	 */
	public interface AvailableWindowSelectionCallback {

		/**
		 * Selected.
		 *
		 * @param address
		 *          the address
		 */
		void selected(InetSocketAddress address);
	}

}
