/**
 *
 * SIROCCO
 * Copyright (C) 2014 Orange
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
public class PortForwardingRule implements Serializable {
    private static final long serialVersionUID = 1L;

    private String externalIp;

    private String internalIp;

    private int externalPort;

    private int internalPort;

    public String getExternalIp() {
        return this.externalIp;
    }

    public void setExternalIp(final String externalIp) {
        this.externalIp = externalIp;
    }

    public String getInternalIp() {
        return this.internalIp;
    }

    public void setInternalIp(final String internalIp) {
        this.internalIp = internalIp;
    }

    public int getExternalPort() {
        return this.externalPort;
    }

    public void setExternalPort(final int externalPort) {
        this.externalPort = externalPort;
    }

    public int getInternalPort() {
        return this.internalPort;
    }

    public void setInternalPort(final int internalPort) {
        this.internalPort = internalPort;
    }

}
