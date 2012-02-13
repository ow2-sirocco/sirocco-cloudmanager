/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;


@Embeddable
public class NetworkInterface implements Serializable {
	private static final long serialVersionUID = 1L;
    public static enum InterfaceState {
		ACTIVE, STANDBY
    }

    public static enum IpAllocation {
		STATIC, DHCP
    }

    public static enum Protocol {
		IPv4, IPv6
    }


	private String			hostname;
	private String			macAddress;
	private InterfaceState	state;

	private Protocol		protocol;
	private IpAllocation		allocation;
	private String			address;
	private String			defaultGateway;


	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	@Enumerated(EnumType.STRING)
	public InterfaceState getState() {
		return state;
	}

	public void setState(InterfaceState state) {
		this.state = state;
	}


	@Enumerated(EnumType.STRING)
	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	@Enumerated(EnumType.STRING)
	public IpAllocation getAllocation() {
		return allocation;
	}

	public void setAllocation(IpAllocation allocation) {
		this.allocation = allocation;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDefaultGateway() {
		return defaultGateway;
	}

	public void setDefaultGateway(String defaultGateway) {
		this.defaultGateway = defaultGateway;
	}
}