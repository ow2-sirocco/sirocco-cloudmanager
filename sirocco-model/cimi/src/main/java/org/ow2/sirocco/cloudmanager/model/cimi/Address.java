/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Address extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ip;

    private String hostName;

    private String allocation;

    private String defaultGateway;

    private String dns;

    private String protocol;

    private String mask;

    private Network network;

    private CloudResource resource;

    public String getIp() {
        return this.ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    public String getAllocation() {
        return this.allocation;
    }

    public void setAllocation(final String allocation) {
        this.allocation = allocation;
    }

    public String getDefaultGateway() {
        return this.defaultGateway;
    }

    public void setDefaultGateway(final String defaultGateway) {
        this.defaultGateway = defaultGateway;
    }

    public String getDns() {
        return this.dns;
    }

    public void setDns(final String dns) {
        this.dns = dns;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    public String getMask() {
        return this.mask;
    }

    public void setMask(final String mask) {
        this.mask = mask;
    }

    // TODO check this
    @ManyToOne
    public Network getNetwork() {
        return this.network;
    }

    public void setNetwork(final Network network) {
        this.network = network;
    }

    // TODO check this
    @OneToOne
    public CloudResource getResource() {
        return this.resource;
    }

    public void setResource(final CloudResource resource) {
        this.resource = resource;
    }

}
