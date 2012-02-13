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

import org.ow2.sirocco.vmm.api.VirtualMachineMXBean.PowerState;

public class VirtualMachinePowerStateChangeEvent implements Serializable {
    private static final long serialVersionUID = -3192011774166992076L;

    private String vmName;

    private String vmProviderId;

    private PowerState state;

    public VirtualMachinePowerStateChangeEvent() {
    }

    public VirtualMachinePowerStateChangeEvent(final String vmName, final String vmProviderId, final PowerState state) {
        super();
        this.vmName = vmName;
        this.vmProviderId = vmProviderId;
        this.state = state;
    }

    public String getVmProviderId() {
        return this.vmProviderId;
    }

    public void setVmProviderId(final String vmProviderId) {
        this.vmProviderId = vmProviderId;
    }

    public PowerState getState() {
        return this.state;
    }

    public void setState(final PowerState state) {
        this.state = state;
    }

    public String getVmName() {
        return this.vmName;
    }

    public void setVmName(final String vmName) {
        this.vmName = vmName;
    }

    @Override
    public String toString() {
        return "VirtualMachinePowerStateChangeEvent [vmName=" + this.vmName + ", vmProviderId=" + this.vmProviderId
            + ", state=" + this.state + "]";
    }

}
