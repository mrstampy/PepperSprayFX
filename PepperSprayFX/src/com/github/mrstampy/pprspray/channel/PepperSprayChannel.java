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
package com.github.mrstampy.pprspray.channel;

import io.netty.channel.socket.nio.NioDatagramChannel;

import com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyChannel;
import com.github.mrstampy.kitchensync.netty.channel.initializer.ByteArrayMessageInitializer;
import com.github.mrstampy.kitchensync.netty.channel.payload.ByteArrayByteBufCreator;

// TODO: Auto-generated Javadoc
/**
 * Simple byte array channels.
 */
public class PepperSprayChannel extends
		AbstractKiSyChannel<ByteArrayByteBufCreator, ByteArrayMessageInitializer, NioDatagramChannel> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyChannel#initializer
	 * ()
	 */
	@Override
	protected ByteArrayMessageInitializer initializer() {
		return new ByteArrayMessageInitializer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyChannel#
	 * getChannelClass()
	 */
	@Override
	protected Class<NioDatagramChannel> getChannelClass() {
		return NioDatagramChannel.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyChannel#
	 * initByteBufCreator()
	 */
	@Override
	protected ByteArrayByteBufCreator initByteBufCreator() {
		return new ByteArrayByteBufCreator();
	}

}
