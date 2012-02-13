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

/**
 * Cloud administration event that indicates that a virtual machine has been
 * destroyed.
 */
public class DelVirtualMachineEvent extends CloudAdminEvent implements Serializable {
    private static final long serialVersionUID = -5300949456305612236L;

    private String vmId;

    private String hostId;

    /**
     * Constructs a DelVirtualMachineEvent object
     */
    public DelVirtualMachineEvent() {
        super();
    }

    /**
     * Constructs a DelVirtualMachineEvent object with a VM Id
     * 
     * @param vmId identifier of the virtual machine that has been destroyed
     */
    public DelVirtualMachineEvent(final String vmId, final String hostId) {
        this.vmId = vmId;
        this.hostId = hostId;
    }

    /**
     * Returns the identifier of the virtual machine that has been destroyed
     * 
     * @return VmId.
     */
    public String getVmId() {
        return this.vmId;
    }

    /**
     * Sets the identifier of the virtual machine that has been destroyed
     * 
     * @param vmId identifier of the virtual machine that has been destroyed
     */
    public void setVmId(final String vmId) {
        this.vmId = vmId;
    }

    /**
     * Returns the vmId of the host where the VM was located
     * 
     * @return HostId.
     */
    public String getHostId() {
        return this.hostId;
    }

    /**
     * Sets the vmId of the host where the VM was located
     * 
     * @param hostId vmId of the host where the VM was located
     */
    public void setHostId(final String hostId) {
        this.hostId = hostId;
    }

}
