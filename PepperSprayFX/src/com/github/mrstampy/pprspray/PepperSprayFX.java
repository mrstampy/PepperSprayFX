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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.Stage;

import com.github.mrstampy.kitchensync.message.inbound.ByteArrayInboundMessageManager;
import com.github.mrstampy.pprspray.core.handler.MediaFooterHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationAckHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationHandler;
import com.github.mrstampy.pprspray.core.handler.WebcamMediaHandler;
import com.github.mrstampy.pprspray.core.receiver.MediaProcessor;
import com.github.mrstampy.pprspray.core.streamer.negotiation.AcceptingNegotationSubscriber;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationEventBus;
import com.github.mrstampy.pprspray.webcam.WebcamDemoButton;
import com.github.mrstampy.pprspray.webcam.WebcamDisplay;

// TODO: Auto-generated Javadoc
/**
 * The main class for PepperSprayFX.
 */
public class PepperSprayFX extends Application {

	private Stage stage;

	/**
	 * The Constructor initializes state and registers a
	 * {@link PepperSprayFXNegotiationSubscriber} with the
	 * {@link NegotiationEventBus}.
	 */
	public PepperSprayFX() {
		super();
		initSystem();
		NegotiationEventBus.register(new PepperSprayFXNegotiationSubscriber());
	}

	private void initSystem() {
		initInboundManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		Scene scene = new Scene(new WebcamDemoButton().getLayout());
		stage.setScene(scene);

		stage.centerOnScreen();
		stage.show();
	}

	/**
	 * Displays the webcam, invoked from
	 * {@link PepperSprayFXNegotiationSubscriber} when a video request has been
	 * made.
	 *
	 * @param display
	 *          the display
	 * 
	 * @see NegotiationEventBus
	 * @see AcceptingNegotationSubscriber
	 */
	public void displayWebcam(WebcamDisplay display) {
		Popup popup = new Popup();
		popup.getContent().add(display.getLayout());
		popup.centerOnScreen();
		popup.show(this.stage);
	}

	/**
	 * Inits the inbound manager.
	 */
	protected void initInboundManager() {
		//@formatter:off
		ByteArrayInboundMessageManager.INSTANCE.addMessageHandlers(
				new WebcamMediaHandler(),
				new NegotiationHandler(), 
				new NegotiationAckHandler(),
				new MediaFooterHandler());
		//@formatter:on
	}

	/**
	 * The Class PepperSprayFXNegotiationSubscriber.
	 */
	public class PepperSprayFXNegotiationSubscriber extends AcceptingNegotationSubscriber {

		/**
		 * The Constructor.
		 */
		public PepperSprayFXNegotiationSubscriber() {
			super(null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.github.mrstampy.pprspray.core.streamer.negotiation.
		 * AcceptingNegotationSubscriber
		 * #getMediaProcessor(com.github.mrstampy.pprspray
		 * .core.streamer.negotiation.NegotiationChunk)
		 */
		protected MediaProcessor getMediaProcessor(NegotiationChunk event) {
			switch (event.getRequestedType()) {
			case VIDEO:
				WebcamDisplay wd = new WebcamDisplay(event.getMediaHash(), event.getLocal(), event.getRemote());
				Platform.runLater(() -> displayWebcam(wd));

				return wd;
			default:
				return null;
			}
		}

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *          the args
	 * @throws Exception
	 *           the exception
	 */
	public static void main(String... args) throws Exception {
		launch(args);
	}

}
