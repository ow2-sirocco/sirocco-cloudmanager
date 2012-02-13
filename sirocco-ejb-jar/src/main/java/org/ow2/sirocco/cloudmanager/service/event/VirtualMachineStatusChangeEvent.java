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

import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VirtualMachineVO;

/**
 * Cloud administration event that indicates that the status of a virtual
 * machine has changed
 */
public class VirtualMachineStatusChangeEvent extends CloudAdminEvent implements Serializable {
    private static final long serialVersionUID = 1461226484940428467L;

    private VirtualMachineVO server;

    /**
     * Constructs a VirtualMachineStatusChangeEvent object
     */
    public VirtualMachineStatusChangeEvent() {
        super();
    }

    /**
     * Constructs a VirtualMachineStatusChangeEvent with virtual machine's value
     * object (includes id, and status).
     * 
     * @param server 's value object.
     */
    public VirtualMachineStatusChangeEvent(final VirtualMachineVO server) {
        this.server = server;
    }

    /**
     * Returns the Vm value object whose status has changed
     * 
     * @return the Vm value object whose status has changed
     */
    public VirtualMachineVO getServer() {
        return this.server;
    }

    /**
     * Sets the the Vm value object that whose status has changed
     * 
     * @param server 's value object whose status has changed
     */
    public void setServer(final VirtualMachineVO server) {
        this.server = server;
    }

}
