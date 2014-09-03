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

import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
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

	private ToggleButton startWebcam = new ToggleButton("Start Webcam");

	private VBox box = new VBox(10);

	private PepperSprayChannel channel1;
	private PepperSprayChannel channel2;
	private WebcamStreamer streamer;

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

	private void init() {
		startWebcam.addEventHandler(ActionEvent.ACTION, e -> buttonClicked());
		box.getChildren().add(startWebcam);
	}

	// Webcam acquisition must be off the JavaFX thread.
	private void buttonClicked() {
		if (startWebcam.isSelected()) {
			startWebcam();
		} else {
			stopWebcam();
		}
	}

	private void stopWebcam() {
		svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				streamer.destroy();
				setButtonText("Start Webcam");
			}
		});
	}

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

	private void initStreamer() {
		Webcam webcam = Webcam.getDefault();
		streamer = new WebcamStreamer(webcam, channel1, channel2.localAddress());
		streamer.connect();
	}

	private void setButtonText(String text) {
		Platform.runLater(() -> startWebcam.setText(text));
	}

	private PepperSprayChannel initChannel() {
		PepperSprayChannel channel = new PepperSprayChannel();

		channel.bind();

		return channel;
	}

}
