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

import io.netty.buffer.ByteBuf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;

import com.github.mrstampy.kitchensync.message.inbound.ByteArrayInboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.kitchensync.util.KiSyUtils;
import com.github.mrstampy.pprspray.core.handler.MediaFooterHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationAckHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationHandler;
import com.github.mrstampy.pprspray.core.handler.WebcamMediaHandler;
import com.github.mrstampy.pprspray.core.receiver.MediaProcessor;
import com.github.mrstampy.pprspray.core.streamer.event.MediaStreamerEvent;
import com.github.mrstampy.pprspray.core.streamer.event.MediaStreamerEventBus;
import com.github.mrstampy.pprspray.core.streamer.negotiation.AcceptingNegotationSubscriber;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationEventBus;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationMessageUtils;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;
import com.github.mrstampy.pprspray.util.RepositioningDragListener;
import com.github.mrstampy.pprspray.webcam.WebcamDemoButton;
import com.github.mrstampy.pprspray.webcam.WebcamDisplay;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The main class for PepperSprayFX.
 */
public class PepperSprayFX extends Application {

	/** The stage. */
	private Stage stage;

	/** The demo button. */
	private WebcamDemoButton demoButton;

	/** The drag listener. */
	@SuppressWarnings("unused")
	private RepositioningDragListener dragListener;

	private WebcamDisplay currentDisplay;

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

	/**
	 * Inits the system.
	 */
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
		this.demoButton = new WebcamDemoButton();

		dragListener = new RepositioningDragListener(stage);

		VBox layout = demoButton.getLayout();
		layout.setBackground(Background.EMPTY);
		layout.setEffect(new GaussianBlur(0.2));

		Color c = Color.BLACK;
		Scene scene = new Scene(layout, new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.25));

		stage.setScene(scene);
		stage.initStyle(StageStyle.TRANSPARENT);

		stage.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handleRightClick(e));

		setSize();

		stage.centerOnScreen();
		stage.show();
	}

	/**
	 * Handle right click.
	 *
	 * @param e
	 *          the e
	 */
	private void handleRightClick(MouseEvent e) {
		if (e.getButton() != MouseButton.SECONDARY) return;

		//@formatter:off
		Action action = Dialogs
				.create()
				.title("Exiting")
				.owner(stage)
				.message("Exiting, confirm?")
				.showConfirm();
		//@formatter:on

		if ("YES".equals(action.toString())) shutdown();
	}

	private void shutdown() {
		Thread t = new Thread() {
			public void run() {
				if (currentDisplay != null && currentDisplay.isOpen()) currentDisplay.destroy();
				System.exit(0);
			}
		};
		t.start();
	}

	/**
	 * Sets the size.
	 */
	private void setSize() {
		double width = 320;
		double height = 180;

		stage.setMinWidth(width);
		stage.setMinHeight(height);
		stage.setMaxWidth(width);
		stage.setMinHeight(height);
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
		this.currentDisplay = display;
		Popup popup = new Popup();
		popup.getContent().add(display.getLayout());
		display.setPopup(popup);
		popup.centerOnScreen();
		new RepositioningDragListener(popup);
		popup.setOpacity(0.75);
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
			MediaStreamerEventBus.register(this);
		}

		/**
		 * Media streamer event.
		 *
		 * @param event
		 *          the event
		 */
		@Subscribe
		public void mediaStreamerEvent(MediaStreamerEvent event) {
			switch (event.getType()) {
			case NEGOTIATION_FAILED:
				Platform.runLater(() -> showNegotiationsFailed(event));
				break;
			default:
				break;

			}
		}

		/**
		 * Show negotiations failed.
		 */
		private void showNegotiationsFailed(MediaStreamerEvent event) {
			demoButton.reset();

			//@formatter:off
			Dialogs
					.create()
					.title("Webcam request denied")
					.owner(stage)
					.message(event.getSource().getDestination() + " says no.")
					.showInformation();
			//@formatter:on
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.github.mrstampy.pprspray.core.streamer.negotiation.
		 * AcceptingNegotationSubscriber
		 * #negotiationRequestedImpl(com.github.mrstampy
		 * .pprspray.core.streamer.negotiation.NegotiationChunk)
		 */
		@Override
		protected void negotiationRequestedImpl(NegotiationChunk event) {
			boolean ok = requestConfirmation(event);

			if (ok) {
				super.negotiationRequestedImpl(event);
			} else {
				getStuffed(event);
			}
		}

		/**
		 * Gets the stuffed.
		 *
		 * @param event
		 *          the event
		 */
		private void getStuffed(NegotiationChunk event) {
			KiSyChannel channel = MediaStreamerUtils.getChannel(event.getLocal());

			ByteBuf ack = NegotiationMessageUtils.getNegotiationAckMessage(event.getMediaHash(), false);

			channel.send(ack.array(), event.getRemote());

			postNegotiationEvent(false, event);
		}

		/**
		 * Request confirmation.
		 *
		 * @param event
		 *          the event
		 * @return true, if request confirmation
		 */
		private boolean requestConfirmation(NegotiationChunk event) {
			CountDownLatch cdl = new CountDownLatch(1);
			AtomicBoolean ok = new AtomicBoolean(false);

			Platform.runLater(() -> requestConfirmation(event, cdl, ok));

			KiSyUtils.await(cdl, 30, TimeUnit.SECONDS);

			return ok.get();
		}

		/**
		 * Request confirmation.
		 *
		 * @param event
		 *          the event
		 * @param cdl
		 *          the cdl
		 * @param ok
		 *          the ok
		 */
		private void requestConfirmation(NegotiationChunk event, CountDownLatch cdl, AtomicBoolean ok) {
			//@formatter:off
			Action action = Dialogs
					.create()
					.title("Incoming " + event.getRequestedType() + " request")
					.owner(stage)
					.message(createConfirmationMessage(event))
					.showConfirm();
			//@formatter:on

			ok.set("YES".equals(action.toString()));

			cdl.countDown();
		}

		/**
		 * Creates the confirmation message.
		 *
		 * @param event
		 *          the event
		 * @return the string
		 */
		private String createConfirmationMessage(NegotiationChunk event) {
			StringBuilder builder = new StringBuilder();

			builder.append("Incoming " + event.getRequestedType() + " request from " + event.getRemote());
			builder.append("\nDo you accept the charges?");

			return builder.toString();
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
