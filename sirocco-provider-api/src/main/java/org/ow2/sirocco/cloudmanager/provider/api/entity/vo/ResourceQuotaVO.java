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
 * Represents the resource quota of a user of the cloud service. This quota is
 * quantified by
 * <ul>
 * <li>the maximum aggregate number of CPUs
 * <li>the maximum aggregate storage capacity
 * <li>the maximum aggregate memory capacity
 * <li>the maximum number of virtual machines
 * </ul>
 * A user is allowed to create a new virtual machine or a new VM image only if
 * his/her quota is not exceeded.
 */
@SuppressWarnings("serial")
public class ResourceQuotaVO implements Serializable {

    private int cpuQuota;

    private int ramQuotaInMB;

    private int diskQuotaInMB;

    private int maxNumberOfVMs;

    /**
     * Returns the CPU quota (maximum number of CPUs)
     * 
     * @return the CPU quota (maximum number of CPUs)
     */
    public int getCpuQuota() {
        return this.cpuQuota;
    }

    /**
     * Sets the the CPU quota (maximum number of CPUs)
     * 
     * @param cpuQuota the CPU quota (maximum number of CPUs)
     */
    public void setCpuQuota(final int cpuQuota) {
        this.cpuQuota = cpuQuota;
    }

    /**
     * Returns the memory quota (maximum aggregate memory capacity in megabytes)
     * 
     * @return the memory quota (maximum aggregate memory capacity in megabytes)
     */
    public int getRamQuotaInMB() {
        return this.ramQuotaInMB;
    }

    /**
     * Sets the memory quota (maximum aggregate memory capacity in megabytes)
     * 
     * @param ramQuotaInMB the memory quota (maximum aggregate memory capacity
     *        in megabytes)
     */
    public void setRamQuotaInMB(final int ramQuotaInMB) {
        this.ramQuotaInMB = ramQuotaInMB;
    }

    /**
     * Returns the disk quota (maximum aggregate disk capacity in megabytes)
     * 
     * @return the disk quota (maximum aggregate disk capacity in megabytes)
     */
    public int getDiskQuotaInMB() {
        return this.diskQuotaInMB;
    }

    /**
     * Sets the disk quota (maximum aggregate disk capacity in megabytes)
     * 
     * @param diskQuotaInMB the disk quota (maximum aggregate disk capacity in
     *        megabytes)
     */
    public void setDiskQuotaInMB(final int diskQuotaInMB) {
        this.diskQuotaInMB = diskQuotaInMB;
    }

    /**
     * Returns the maximum number of virtual machines
     * 
     * @return the maximum number of virtual machines
     */
    public int getMaxNumberOfVMs() {
        return this.maxNumberOfVMs;
    }

    /**
     * Sets the maximum number of virtual machines
     * 
     * @param maxNumberOfVMs the maximum number of virtual machines
     */
    public void setMaxNumberOfVMs(final int maxNumberOfVMs) {
        this.maxNumberOfVMs = maxNumberOfVMs;
    }
}
