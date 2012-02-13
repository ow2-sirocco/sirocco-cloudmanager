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

/**
 * Business object representing a VM size. A VM size is a predefined combination
 * of CPU, disk and memory capacity. The Cloud service can be configured in two
 * alternate ways:
 * <ul>
 * <li>it can allow the user to specify specific values for the resource
 * capacity of virtual machine (disk size, memory size, number of CPUs)
 * <li>it can restrict the user to a predefined list of VM sizes
 * </ul>
 */
@SuppressWarnings("serial")
public class VMSizeVO implements Serializable {

    private String name;

    private int numCPUs;

    private long memorySizeMB;

    /**
     * Returns the symbolic name of the VM size
     * 
     * @return the symbolic name of the VM size
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the symbolic name of the VM size
     * 
     * @param name the symbolic name of the VM size
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the number of CPUs
     * 
     * @return the number of CPUs
     */
    public int getNumCPUs() {
        return this.numCPUs;
    }

    /**
     * Sets the number of CPUs
     * 
     * @param numCPUs the number of CPUs
     */
    public void setNumCPUs(final int numCPUs) {
        this.numCPUs = numCPUs;
    }

    /**
     * Returns the memory size in megabytes
     * 
     * @return the memory size in megabytes
     */
    public long getMemorySizeMB() {
        return this.memorySizeMB;
    }

    /**
     * Sets the memory size in megabytes
     * 
     * @param memorySizeMB the memory size in megabytes
     */
    public void setMemorySizeMB(final long memorySizeMB) {
        this.memorySizeMB = memorySizeMB;
    }

}
