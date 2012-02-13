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

package org.ow2.sirocco.cloudmanager.provider.api.entity;

import java.io.Serializable;

/**
 * Represents the resource consumption of a project of the cloud service. The
 * resource consumption is measured in terms:
 * <ul>
 * <li>number of virtual machines
 * <li>aggregate storage usage in megabytes (consumed by VM disks and VM images
 * created by the project)
 * <li>aggregate memory usage in megabytes (consumed by the VMs created by the
 * project)
 * <li>aggregate number of CPU (virtual CPUs of the virtual machines created by
 * the project)
 * </ul>
 */
public class ResourceConsumption implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numVMs;

    private int storageMB;

    private int memoryMB;

    private int numCPUs;

    /**
     * Returns the number of machines owned by the project
     * 
     * @return the number of machines owned by the project
     */
    public int getNumVMs() {
        return this.numVMs;
    }

    /**
     * Sets the number of machines owned by the project
     * 
     * @param numVMs the number of machines owned by the project
     */
    public void setNumVMs(final int numVMs) {
        this.numVMs = numVMs;
    }

    /**
     * Returns the aggregate storage usage of the project in megabytes
     * 
     * @return the aggregate storage usage of the project in megabytes
     */
    public int getStorageMB() {
        return this.storageMB;
    }

    /**
     * Sets the aggregate storage usage of the project in megabytes
     * 
     * @param storageMB the aggregate storage usage of the project in megabytes
     */
    public void setStorageMB(final int storageMB) {
        this.storageMB = storageMB;
    }

    /**
     * Returns the aggregate memory usage of the project in megabytes
     * 
     * @return the aggregate memory usage of the project in megabytes
     */
    public int getMemoryMB() {
        return this.memoryMB;
    }

    /**
     * Sets the aggregate memory usage of the project in megabytes
     * 
     * @param memoryMB the aggregate memory usage of the project in megabytes
     */
    public void setMemoryMB(final int memoryMB) {
        this.memoryMB = memoryMB;
    }

    /**
     * Returns the aggregate number of CPUs allocated by the project
     * 
     * @return the aggregate number of CPUs allocated by the project
     */
    public int getNumCPUs() {
        return this.numCPUs;
    }

    /**
     * Sets the aggregate number of CPUs allocated by the project
     * 
     * @param numCPUs the aggregate number of CPUs allocated by the project
     */
    public void setNumCPUs(final int numCPUs) {
        this.numCPUs = numCPUs;
    }
}
