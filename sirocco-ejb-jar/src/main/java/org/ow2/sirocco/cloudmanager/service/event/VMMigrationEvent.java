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

package org.ow2.sirocco.cloudmanager.service.event;

import java.io.Serializable;

public class VMMigrationEvent extends CloudAdminEvent implements Serializable {
    private static final long serialVersionUID = -1821104641145438119L;

    private String vmId;

    private String fromHostId;

    private String toHostId;

    public VMMigrationEvent() {
    }

    public VMMigrationEvent(final String vmId, final String fromHostId, final String toHostId) {
        super();
        this.vmId = vmId;
        this.fromHostId = fromHostId;
        this.toHostId = toHostId;
    }

    public String getVmId() {
        return this.vmId;
    }

    public void setVmId(final String vmId) {
        this.vmId = vmId;
    }

    public String getFromHostId() {
        return this.fromHostId;
    }

    public void setFromHostId(final String fromHostId) {
        this.fromHostId = fromHostId;
    }

    public String getToHostId() {
        return this.toHostId;
    }

    public void setToHostId(final String toHostId) {
        this.toHostId = toHostId;
    }

    @Override
    public String toString() {
        return "VMMigrationEvent [fromHostId=" + this.fromHostId + ",toHostId=" + this.toHostId + ", vmId=" + this.vmId + "]";
    }

}
