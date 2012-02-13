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

package org.ow2.sirocco.cloudmanager.clustermanager.api;

import java.io.Serializable;

/**
 * Base class of all datacenter entities acting as a container of resources:
 * <ul>
 * <li>computing power (CPU cores and cycles)
 * <li>memory capacity
 * <li>storage capacity
 * </ul>
 * A ResourceContainer may aggregate resources from child ResourceContainers.
 */
public class ResourceContainer implements Serializable {
    private static final long serialVersionUID = 4121713117273314837L;

    private int cpuCapacityMHz;

    private int cpuAllocatedMHz;

    private int cpuCoreCapacity;

    private int cpuCoreAllocated;

    private int memoryCapacityMB;

    private int memoryAllocatedMB;

    private int storageCapacityMB;

    private int storageAllocatedMB;

    /**
     * Returns the CPU capacity in megaherz
     * 
     * @return the CPU capacity in megaherz
     */
    public int getCpuCapacityMHz() {
        return this.cpuCapacityMHz;
    }

    /**
     * Sets the CPU capacity in megaherz
     * 
     * @param cpuCapacityMHz the CPU capacity in megaherz
     */
    public void setCpuCapacityMHz(final int cpuCapacityMHz) {
        this.cpuCapacityMHz = cpuCapacityMHz;
    }

    /**
     * Returns the allocated CPU power in megaherz
     * 
     * @return the allocated CPU power in megaherz
     */
    public int getCpuAllocatedMHz() {
        return this.cpuAllocatedMHz;
    }

    /**
     * Sets the allocated CPU power in megaherz
     * 
     * @param cpuAllocatedMHz the allocated CPU power in megaherz
     */
    public void setCpuAllocatedMHz(final int cpuAllocatedMHz) {
        this.cpuAllocatedMHz = cpuAllocatedMHz;
    }

    /**
     * Returns the number of CPU cores of the container
     * 
     * @return the number of CPU cores of the container
     */
    public int getCpuCoreCapacity() {
        return this.cpuCoreCapacity;
    }

    /**
     * Sets the number of CPU cores of the container
     * 
     * @param cpuCoreCapacity the number of CPU cores of the container
     */
    public void setCpuCoreCapacity(final int cpuCoreCapacity) {
        this.cpuCoreCapacity = cpuCoreCapacity;
    }

    /**
     * Returns the number of allocated CPU cores in the container
     * 
     * @return the number of allocated CPU cores in the container
     */
    public int getCpuCoreAllocated() {
        return this.cpuCoreAllocated;
    }

    /**
     * Sets the number of allocated CPU cores in the container
     * 
     * @param cpuCoreAllocated the number of allocated CPU cores in the
     *        container
     */
    public void setCpuCoreAllocated(final int cpuCoreAllocated) {
        this.cpuCoreAllocated = cpuCoreAllocated;
    }

    /**
     * Returns the memory capacity of the container in megabytes
     * 
     * @return the memory capacity of the container in megabytes
     */
    public int getMemoryCapacityMB() {
        return this.memoryCapacityMB;
    }

    /**
     * Sets the memory capacity of the container in megabytes
     * 
     * @param memoryCapacityMB the memory capacity of the container in megabytes
     */
    public void setMemoryCapacityMB(final int memoryCapacityMB) {
        this.memoryCapacityMB = memoryCapacityMB;
    }

    /**
     * Returns the allocated memory size in megabytes
     * 
     * @return the allocated memory size in megabytes
     */
    public int getMemoryAllocatedMB() {
        return this.memoryAllocatedMB;
    }

    /**
     * Sets the allocated memory size in megabytes
     * 
     * @param memoryAllocatedMB the allocated memory size in megabytes
     */
    public void setMemoryAllocatedMB(final int memoryAllocatedMB) {
        this.memoryAllocatedMB = memoryAllocatedMB;
    }

    /**
     * Returns the storage capacity of the container in megabytes
     * 
     * @return the storage capacity of the container in megabytes
     */
    public int getStorageCapacityMB() {
        return this.storageCapacityMB;
    }

    /**
     * Sets the storage capacity of the container in megabytes
     * 
     * @param storageCapacityMB the storage capacity of the container in
     *        megabytes
     */
    public void setStorageCapacityMB(final int storageCapacityMB) {
        this.storageCapacityMB = storageCapacityMB;
    }

    /**
     * Returns the allocated storage space in megabytes
     * 
     * @return the allocated storage space in megabytes
     */
    public int getStorageAllocatedMB() {
        return this.storageAllocatedMB;
    }

    /**
     * Sets the allocated storage space in megabytes
     * 
     * @param storageAllocatedMB the allocated storage space in megabytes
     */
    public void setStorageAllocatedMB(final int storageAllocatedMB) {
        this.storageAllocatedMB = storageAllocatedMB;
    }

    @Override
    public String toString() {
        return "ResourceContainer(cpuMHz=" + this.cpuAllocatedMHz + "/" + this.cpuCapacityMHz + ",cpuCore="
            + this.cpuCoreAllocated + "/" + this.cpuCoreCapacity + ",memoryMB=" + this.memoryAllocatedMB + "/"
            + this.memoryCapacityMB + ",storageMB=" + this.storageAllocatedMB + "/" + this.storageCapacityMB + ")";
    }

}
