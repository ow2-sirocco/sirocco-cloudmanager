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
 * Cloud administration event that indicates that a new virtual machine has been
 * created.
 */
public class NewVirtualMachineEvent extends CloudAdminEvent implements Serializable {
    private static final long serialVersionUID = 5790692395509460880L;

    private VirtualMachineVO server;

    /**
     * Constructs a NewVirtualMachineEvent object
     */
    public NewVirtualMachineEvent() {
        super();
    }

    /**
     * Constructs a NewVirtualMachineEvent object with a specified virtual
     * machine description
     * 
     * @param server the virtual machine that has been created
     */
    public NewVirtualMachineEvent(final VirtualMachineVO server) {
        this.server = server;
    }

    /**
     * Returns the VM that has been created
     * 
     * @return the VM that has been created
     */
    public VirtualMachineVO getServer() {
        return this.server;
    }

    /**
     * Sets the the VM that has been created
     * 
     * @param server the VM that has been created
     */
    public void setServer(final VirtualMachineVO server) {
        this.server = server;
    }

}
