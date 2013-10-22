/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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
 */
package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class Subnet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private String providerAssignedId;

    private String cidr;

    private String protocol = "IPv4";

    private boolean enableDhcp = true;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getProviderAssignedId() {
        return this.providerAssignedId;
    }

    public void setProviderAssignedId(final String providerAssignedId) {
        this.providerAssignedId = providerAssignedId;
    }

    public String getCidr() {
        return this.cidr;
    }

    public void setCidr(final String cidr) {
        this.cidr = cidr;
    }

    public boolean isEnableDhcp() {
        return this.enableDhcp;
    }

    public void setEnableDhcp(final boolean enableDhcp) {
        this.enableDhcp = enableDhcp;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "Subnet [name=" + this.name + ", providerAssignedId=" + this.providerAssignedId + ", cidr=" + this.cidr
            + ", protocol=" + this.protocol + ", enableDhcp=" + this.enableDhcp + "]";
    }

}
