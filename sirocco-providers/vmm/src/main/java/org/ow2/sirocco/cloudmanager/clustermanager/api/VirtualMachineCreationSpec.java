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

import org.ow2.sirocco.vmm.api.CustomizationSpec;

public class VirtualMachineCreationSpec {
    private String name;

    private String vmImageId;

    private Integer diskCapacityMB;

    private int numVCPUs;

    private int memorySizeMB;

    private CustomizationSpec customizationSpec;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVmImageId() {
        return this.vmImageId;
    }

    public void setVmImageId(final String vmImageId) {
        this.vmImageId = vmImageId;
    }

    public Integer getDiskCapacityMB() {
        return this.diskCapacityMB;
    }

    public void setDiskCapacityMB(final Integer diskCapacityMB) {
        this.diskCapacityMB = diskCapacityMB;
    }

    public int getNumVCPUs() {
        return this.numVCPUs;
    }

    public void setNumVCPUs(final int numVCPUs) {
        this.numVCPUs = numVCPUs;
    }

    public int getMemorySizeMB() {
        return this.memorySizeMB;
    }

    public void setMemorySizeMB(final int memorySizeMB) {
        this.memorySizeMB = memorySizeMB;
    }

    public CustomizationSpec getCustomizationSpec() {
        return this.customizationSpec;
    }

    public void setCustomizationSpec(final CustomizationSpec customizationSpec) {
        this.customizationSpec = customizationSpec;
    }

}
