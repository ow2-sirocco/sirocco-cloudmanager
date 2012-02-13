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

package org.ow2.sirocco.cloudmanager.provider.api.entity.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Utility class containing extended information on a virtual machine
 */
@SuppressWarnings("serial")
public class VirtualMachineExtendedInfo implements Serializable {

    private int schedulingCap;

    private int schedulingWeight;

    private List<NICInfo> nics;

    /**
     * Returns the scheduling cap of the VM
     * 
     * @return the scheduling cap of the VM
     */
    public int getSchedulingCap() {
        return this.schedulingCap;
    }

    /**
     * Sets the scheduling cap of the VM
     * 
     * @param schedulingCap the scheduling cap of the VM
     */
    public void setSchedulingCap(final int schedulingCap) {
        this.schedulingCap = schedulingCap;
    }

    /**
     * Returns the scheduling weight of the VM
     * 
     * @return the scheduling weight of the VM
     */
    public int getSchedulingWeight() {
        return this.schedulingWeight;
    }

    /**
     * Sets the scheduling weight of the VM
     * 
     * @param schedulingWeight the scheduling weight of the VM
     */
    public void setSchedulingWeight(final int schedulingWeight) {
        this.schedulingWeight = schedulingWeight;
    }

    /**
     * Returns the network interfaces attached to the VM
     * 
     * @return a list of NICInfo objects, each one containing information on a
     *         distinct network interface attached to the VM
     */
    public List<NICInfo> getNics() {
        return this.nics;
    }

    /**
     * Sets the network interfaces attached to the VM
     * 
     * @param nics a list of NICInfo objects, each one containing information on
     *        a distinct network interface attached to the VM
     */
    public void setNics(final List<NICInfo> nics) {
        this.nics = nics;
    }

}
