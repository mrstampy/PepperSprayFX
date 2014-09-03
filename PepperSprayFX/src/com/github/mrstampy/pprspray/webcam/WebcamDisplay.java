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

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor;
import com.github.mrstampy.pprspray.core.receiver.MediaEvent;
import com.github.mrstampy.pprspray.core.receiver.MediaEventBus;

// TODO: Auto-generated Javadoc
/**
 * The Class WebcamDisplay shows images as they are received.
 * 
 * @see MediaEventBus
 * @see AbstractMediaProcessor
 */
public class WebcamDisplay extends AbstractMediaProcessor {
	private static final Logger log = LoggerFactory.getLogger(WebcamDisplay.class);

	private int size = 400;

	private ImageView imageView;
	private VBox vbox;

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @param local
	 *          the local
	 * @param remote
	 *          the remote
	 */
	public WebcamDisplay(int mediaHash, InetSocketAddress local, InetSocketAddress remote) {
		super(mediaHash, remote, remote);

		init();
	}

	/**
	 * Gets the layout.
	 *
	 * @return the layout
	 */
	public VBox getLayout() {
		return vbox;
	}

	private void init() {
		imageView = new ImageView();
		imageView.setFitWidth(getSize() * 1.2);
		imageView.setFitHeight(getSize());

		vbox = new VBox(10);
		vbox.setAlignment(Pos.CENTER);

		vbox.getChildren().add(imageView);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor#
	 * mediaEventImpl(com.github.mrstampy.pprspray.core.receiver.MediaEvent)
	 */
	@Override
	protected void mediaEventImpl(MediaEvent event) throws Exception {
		Image image = createImage(event);
		Platform.runLater(() -> displayImage(image));
	}

	private void displayImage(Image image) {
		try {
			imageView.setImage(image);
		} catch (Exception e) {
			log.error("Error processing image", e);
		}
	}

	private Image createImage(MediaEvent event) {
		ByteArrayInputStream bais = new ByteArrayInputStream(event.getProcessed());

		Image image = new Image(bais);

		return image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor#openImpl
	 * ()
	 */
	@Override
	protected boolean openImpl() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor#closeImpl
	 * ()
	 */
	@Override
	protected boolean closeImpl() {
		return true;
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 *
	 * @param size
	 *          the size
	 */
	public void setSize(int size) {
		this.size = size;
	}

}
