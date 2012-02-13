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

package org.ow2.sirocco.cloudmanager.clustermanager.api.event;

import java.io.Serializable;

public class VirtualMachineMigrationEvent implements Serializable {
    private static final long serialVersionUID = -4473034306905494568L;

    private String vmProviderId;

    private String fromHostId;

    private String toHostId;

    public VirtualMachineMigrationEvent(final String vmProviderId, final String fromHostId, final String toHostId) {
        super();
        this.vmProviderId = vmProviderId;
        this.fromHostId = fromHostId;
        this.toHostId = toHostId;
    }

    public String getVmProviderId() {
        return this.vmProviderId;
    }

    public void setVmProviderId(final String vmProviderId) {
        this.vmProviderId = vmProviderId;
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
        return "VirtualMachineMigrationEvent [vmProviderId=" + this.vmProviderId + ", fromHostId=" + this.fromHostId
            + ", toHostId=" + this.toHostId + "]";
    }

}
