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
package com.github.mrstampy.pprspray.webcam;

import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.pprspray.channel.PepperSprayChannel;
import com.github.mrstampy.pprspray.core.streamer.webcam.WebcamStreamer;
import com.github.sarxos.webcam.Webcam;

// TODO: Auto-generated Javadoc
/**
 * The Class WebcamDemoButton encapsulates a toggle button to start & stop
 * streaming from the webcam.
 */
public class WebcamDemoButton {

	/** The Constant PEPPER_SPRAY_CORE_URI. */
	private static final String PEPPER_SPRAY_CORE_URI = "https://github.com/mrstampy/PepperSpray-core";

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(WebcamDemoButton.class);

	/** The start webcam. */
	private ToggleButton startWebcam = new ToggleButton("Start Webcam");

	/** The box. */
	private VBox box = new VBox(10);

	/** The label. */
	private Text label = new Text("PepperSprayFX");

	/** The info. */
	private Text info = new Text("Demo application for");

	/** The link. */
	private Hyperlink link = new Hyperlink("PepperSpray-core");

	/** The channel1. */
	private PepperSprayChannel channel1;

	/** The channel2. */
	private PepperSprayChannel channel2;

	/** The streamer. */
	private WebcamStreamer streamer;

	/** The svc. */
	private Scheduler svc = Schedulers.from(Executors.newSingleThreadExecutor());

	/**
	 * The Constructor.
	 */
	public WebcamDemoButton() {
		init();
	}

	/**
	 * Gets the layout.
	 *
	 * @return the layout
	 */
	public VBox getLayout() {
		return box;
	}

	/**
	 * Reset.
	 */
	public void reset() {
		startWebcam.fire();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		initLabel();
		initLink();
		startWebcam.addEventHandler(ActionEvent.ACTION, e -> buttonClicked());
		box.getChildren().addAll(label, startWebcam, getFooter());
		box.setAlignment(Pos.CENTER);
	}

	/**
	 * Inits the link.
	 */
	private void initLink() {
		link.addEventHandler(ActionEvent.ANY, e -> openBrowser());
		DropShadow ds = new DropShadow(10, Color.ORANGERED);
		Glow glow = new Glow(0.1);
		ds.setInput(glow);
		link.setEffect(ds);
		link.setTooltip(new Tooltip(PEPPER_SPRAY_CORE_URI));
	}

	/**
	 * Open browser.
	 */
	private void openBrowser() {
		try {
			Desktop.getDesktop().browse(new URI(PEPPER_SPRAY_CORE_URI));
		} catch (Exception e) {
			log.error("Could not open browser", e);
		}
	}

	/**
	 * Gets the footer.
	 *
	 * @return the footer
	 */
	private Node getFooter() {
		HBox box = new HBox();

		box.setAlignment(Pos.BASELINE_CENTER);
		box.getChildren().addAll(info, link);

		return box;
	}

	/**
	 * Inits the label.
	 */
	private void initLabel() {
		Font existing = label.getFont();
		Font newf = Font.font(existing.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 20);
		label.setFont(newf);
		label.setFill(Color.ALICEBLUE);
		label.setEffect(new DropShadow(10, Color.ORANGERED));
		info.setFill(Color.ALICEBLUE);
		info.setEffect(new DropShadow(10, Color.ALICEBLUE));
	}

	// Webcam acquisition must be off the JavaFX thread.
	/**
	 * Button clicked.
	 */
	private void buttonClicked() {
		if (startWebcam.isSelected()) {
			startWebcam();
		} else {
			stopWebcam();
		}
	}

	/**
	 * Stop webcam.
	 */
	private void stopWebcam() {
		svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				setButtonText("Start Webcam");
				streamer.destroy();
			}
		});
	}

	/**
	 * Start webcam.
	 */
	private void startWebcam() {
		svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				setButtonText("Starting...");

				channel1 = initChannel();
				channel2 = initChannel();

				initStreamer();

				setButtonText("Stop");
			}
		});
	}

	/**
	 * Inits the streamer.
	 */
	private void initStreamer() {
		Webcam webcam = Webcam.getDefault();
		streamer = new WebcamStreamer(webcam, channel1, channel2.localAddress());
		streamer.connect();
	}

	/**
	 * Sets the button text.
	 *
	 * @param text
	 *          the button text
	 */
	private void setButtonText(String text) {
		Platform.runLater(() -> startWebcam.setText(text));
	}

	/**
	 * Inits the channel.
	 *
	 * @return the pepper spray channel
	 */
	private PepperSprayChannel initChannel() {
		PepperSprayChannel channel = new PepperSprayChannel();

		channel.bind();

		return channel;
	}

}
