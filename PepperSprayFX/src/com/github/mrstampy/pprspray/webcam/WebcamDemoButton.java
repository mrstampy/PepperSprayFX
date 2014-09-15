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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.jmdns.ServiceInfo;

import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.schedulers.Schedulers;

import com.github.mrstampy.pprspray.PepperSprayFX;
import com.github.mrstampy.pprspray.channel.PepperSprayChannel;
import com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.event.MediaStreamerEvent;
import com.github.mrstampy.pprspray.core.streamer.event.MediaStreamerEventBus;
import com.github.mrstampy.pprspray.core.streamer.webcam.WebcamStreamer;
import com.github.mrstampy.pprspray.discovery.jmdns.JmDNSDiscoveryEvent;
import com.github.mrstampy.pprspray.discovery.jmdns.JmDNSDiscoveryEventBus;
import com.github.mrstampy.pprspray.discovery.jmdns.JmDNSPepperSprayDiscovery;
import com.github.mrstampy.pprspray.jasypt.streamer.webcam.JasyptWebcamImageTransformer;
import com.github.sarxos.webcam.Webcam;
import com.google.common.eventbus.Subscribe;

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

	/** The box. */
	private VBox box = new VBox(10);

	/** The label. */
	private Text label = new Text("PepperSprayFX");

	/** The info. */
	private Text info = new Text("Demo application for");

	/** The link. */
	private Hyperlink link = new Hyperlink("PepperSpray-core");

	private Text ipAddress = new Text();

	/** The channel. */
	private PepperSprayChannel channel;

	/** The streamer. */
	private WebcamStreamer streamer;

	/** The svc. */
	private Scheduler svc = Schedulers.from(Executors.newFixedThreadPool(5));

	private CountDownLatch channelInit = new CountDownLatch(1);

	private PepperSprayDiscoveryService<ServiceInfo> discoveryService = new JmDNSPepperSprayDiscovery();

	/**
	 * The Constructor.
	 */
	public WebcamDemoButton() {
		init();
	}

	/**
	 * Discovery event.
	 *
	 * @param event
	 *          the event
	 */
	@Subscribe
	public void discoveryEvent(JmDNSDiscoveryEvent event) {
		switch (event.getType()) {
		case SHUTDOWN:
			Platform.runLater(() -> showShuttingDown());
			break;
		default:
			break;

		}
	}

	private void showShuttingDown() {
		disableControls(true);

		PopOver poptart = new PopOver(createShutdownContent());
		poptart.setDetachable(true);
		poptart.setDetachedTitle("PepperSpray DNS Shutting Down");
		poptart.setOpacity(0.5);
		poptart.show(box);

		svc.createWorker().schedule(() -> Platform.runLater(() -> fadePopOver(poptart)), 5, TimeUnit.SECONDS);
	}

	/**
	 * Gets the registered.
	 *
	 * @return the registered
	 */
	public List<ServiceInfo> getRegistered() {
		//@formatter:off
		return discoveryService
				.getRegisteredPepperSprayServices()
				.stream()
				.filter(si -> !isForChannel(si))
				.collect(Collectors.toList());
		//@formatter:on
	}

	private boolean isForChannel(ServiceInfo si) {
		return si.getPort() == channel.getPort()
				&& containsAddress(si.getInetAddresses(), channel.localAddress().getAddress());
	}

	private boolean containsAddress(InetAddress[] inetAddresses, InetAddress address) {
		//@formatter:off
		return Arrays
				.asList(inetAddresses)
				.stream()
				.anyMatch(ia -> ia.equals(address));
		//@formatter:on
	}

	/**
	 * Fade pop over.
	 *
	 * @param poptart
	 *          the poptart
	 */
	public void fadePopOver(PopOver poptart) {
		if (!poptart.isShowing()) return;
		Timeline timeline = new Timeline(getKeyFrame(poptart));
		timeline.setOnFinished(e -> poptart.hide());
		timeline.play();
	}

	private KeyFrame getKeyFrame(PopOver poptart) {
		return new KeyFrame(Duration.seconds(5), getKeyValues(poptart));
	}

	private KeyValue getKeyValues(PopOver poptart) {
		return new KeyValue(poptart.opacityProperty(), 0.0);
	}

	private Node createShutdownContent() {
		Text text = new Text(" Please wait while the PepperSpray discovery service shuts down ");

		text.setFill(Color.ROYALBLUE);

		return text;
	}

	/**
	 * Gets the layout.
	 *
	 * @return the layout
	 */
	public Parent getLayout() {
		return box;
	}

	/**
	 * Destroy.
	 */
	public void destroy() {
		svc.createWorker().schedule(() -> destroyImpl());
	}

	private void destroyImpl() {
		streamer.destroy();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		MediaStreamerEventBus.register(this);
		JmDNSDiscoveryEventBus.register(this);

		svc.createWorker().schedule(() -> initChannels());

		initLabel();
		initLink();
		box.getChildren().addAll(label, getFooter(), ipAddress);
		box.setAlignment(Pos.CENTER);
		box.setBackground(Background.EMPTY);
	}

	private void initChannels() {
		try {
			Platform.runLater(() -> showInitPopOver());
			Platform.runLater(() -> disableControls(true));
			channel = initChannel();

			ipAddress.setText(channel.localAddress().toString());

			discoveryService.registerPepperSprayServices(channel, MediaStreamType.VIDEO);
			Platform.runLater(() -> PepperSprayFX.getInstance().showHelp());
		} catch (Exception e) {
			log.error("Could not initialize channels", e);
		} finally {
			channelInit.countDown();
			Platform.runLater(() -> disableControls(false));
		}
	}

	private void showInitPopOver() {
		PopOver poptart = new PopOver(createInitContent());
		poptart.setDetachable(true);
		poptart.setDetachedTitle("PepperSpray DNS Starting Up");
		poptart.setOpacity(0.5);
		poptart.show(box);

		svc.createWorker().schedule(() -> Platform.runLater(() -> fadePopOver(poptart)), 3, TimeUnit.SECONDS);
	}

	private Node createInitContent() {
		Text text = new Text(" Please wait while the PepperSpray discovery service starts up ");

		text.setFill(Color.ROYALBLUE);

		return text;
	}

	/**
	 * Disable controls.
	 *
	 * @param disable
	 *          the disable
	 */
	public void disableControls(boolean disable) {
		PepperSprayFX.setCursor(disable ? Cursor.WAIT : Cursor.DEFAULT);
		box.setDisable(disable);
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
		Font newf = Font.font(existing.getFamily(), FontWeight.BOLD, 20);
		label.setFont(newf);
		label.setFill(Color.ALICEBLUE);
		label.setEffect(new DropShadow(10, Color.ORANGERED));
		info.setFill(Color.ALICEBLUE);
		info.setEffect(new DropShadow(10, Color.ALICEBLUE));
		ipAddress.setFill(Color.CORNFLOWERBLUE);
		ipAddress.setEffect(new DropShadow(10, Color.ORANGERED));
		ipAddress.setOpacity(0.5);
	}

	/**
	 * Inits the streamer.
	 *
	 * @param address
	 *          the address
	 */
	public void initStreamer(InetSocketAddress address) {
		final Webcam webcam = Webcam.getDefault();

		streamer = new WebcamStreamer(webcam, channel, address);
		streamer.setTransformer(new JasyptWebcamImageTransformer(PepperSprayFX.ENCRYPTOR));
		streamer.connect();
		svc.createWorker().schedule(() -> webcam.open());
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
		case DESTROYED:
			disableLink(true);
			break;
		case STARTED:
			disableLink(false);
			break;
		default:
			break;

		}
	}

	/**
	 * Disable link.
	 *
	 * @param disable
	 *          the disable
	 */
	public void disableLink(boolean disable) {
		Platform.runLater(() -> link.setDisable(disable));
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
