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
package com.github.mrstampy.pprspray.discovery;

import java.net.InetAddress;
import java.util.List;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;

// TODO: Auto-generated Javadoc
/**
 * The Class PepperSprayFXDiscoveryService.
 *
 * @param <PSDS>
 *          the generic type
 * @param <INFO>
 *          the generic type
 */
public abstract class PepperSprayFXDiscoveryService<PSDS extends PepperSprayDiscoveryService<INFO>, INFO> implements
		PepperSprayDiscoveryService<INFO> {

	private PSDS discoveryService;

	/**
	 * The Constructor.
	 *
	 * @param discoveryService
	 *          the discovery service
	 */
	protected PepperSprayFXDiscoveryService(PSDS discoveryService) {
		setDiscoveryService(discoveryService);
	}

	/**
	 * Gets the discovery service.
	 *
	 * @return the discovery service
	 */
	public PSDS getDiscoveryService() {
		return discoveryService;
	}

	/**
	 * Sets the discovery service.
	 *
	 * @param discoveryService
	 *          the discovery service
	 */
	protected void setDiscoveryService(PSDS discoveryService) {
		this.discoveryService = discoveryService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService
	 * #registerPepperSprayService
	 * (com.github.mrstampy.kitchensync.netty.channel.KiSyChannel)
	 */
	@Override
	public boolean registerPepperSprayService(KiSyChannel channel) {
		return getDiscoveryService().registerPepperSprayService(channel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService
	 * #registerPepperSprayService
	 * (com.github.mrstampy.kitchensync.netty.channel.KiSyChannel,
	 * java.lang.String)
	 */
	@Override
	public boolean registerPepperSprayService(KiSyChannel channel, String identifier) {
		return getDiscoveryService().registerPepperSprayService(channel, identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService
	 * #registerPepperSprayServices
	 * (com.github.mrstampy.kitchensync.netty.channel.KiSyChannel,
	 * com.github.mrstampy.pprspray.core.streamer.MediaStreamType[])
	 */
	@Override
	public boolean registerPepperSprayServices(KiSyChannel channel, MediaStreamType... types) {
		return getDiscoveryService().registerPepperSprayServices(channel, types);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService
	 * #unregisterPepperSprayService
	 * (com.github.mrstampy.kitchensync.netty.channel.KiSyChannel)
	 */
	@Override
	public boolean unregisterPepperSprayService(KiSyChannel channel) {
		return getDiscoveryService().unregisterPepperSprayService(channel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService
	 * #unregisterPepperSprayServices
	 * (com.github.mrstampy.kitchensync.netty.channel.KiSyChannel,
	 * com.github.mrstampy.pprspray.core.streamer.MediaStreamType[])
	 */
	@Override
	public boolean unregisterPepperSprayServices(KiSyChannel channel, MediaStreamType... types) {
		return getDiscoveryService().unregisterPepperSprayServices(channel, types);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService
	 * #unregisterPepperSprayService
	 * (com.github.mrstampy.kitchensync.netty.channel.KiSyChannel,
	 * java.lang.String)
	 */
	@Override
	public boolean unregisterPepperSprayService(KiSyChannel channel, String identifier) {
		return getDiscoveryService().unregisterPepperSprayService(channel, identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService
	 * #getRegisteredPepperSprayServices()
	 */
	@Override
	public List<INFO> getRegisteredPepperSprayServices() {
		return getDiscoveryService().getRegisteredPepperSprayServices();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService
	 * #getRegisteredPepperSprayServices(java.net.InetAddress)
	 */
	@Override
	public List<INFO> getRegisteredPepperSprayServices(InetAddress address) {
		return getDiscoveryService().getRegisteredPepperSprayServices(address);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService
	 * #getRegisteredPepperSprayServices(java.lang.String)
	 */
	@Override
	public List<INFO> getRegisteredPepperSprayServices(String identifier) {
		return getDiscoveryService().getRegisteredPepperSprayServices(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.discovery.PepperSprayDiscoveryService
	 * #createServiceName
	 * (com.github.mrstampy.pprspray.core.streamer.MediaStreamType)
	 */
	@Override
	public String createServiceName(MediaStreamType type) {
		return getDiscoveryService().createServiceName(type);
	}

}
