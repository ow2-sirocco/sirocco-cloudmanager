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

import javax.management.ObjectName;

import org.ow2.sirocco.vmm.api.ResourcePartitionMXBean;

public class ResourcePartitionInfo {
    public ResourcePartitionInfo() {
    }

    public ResourcePartitionInfo(final AllocationMode allocationMode, final ResourcePartitionMXBean mbean, final String path,
        final String hypervisor, final String location) {
        this.allocationMode = allocationMode;
        this.mbean = mbean;
        this.path = path;
        this.hypervisor = hypervisor;
        this.location = location == null ? "" : location;
        this.objectName = mbean.getObjectName();
    }

    private AllocationMode allocationMode;

    private ResourcePartitionMXBean mbean;

    private ObjectName objectName;

    private String path;

    private String hypervisor;

    private String location;

    public ResourcePartitionMXBean getMbean() {
        return this.mbean;
    }

    public void setMbean(final ResourcePartitionMXBean mbean) {
        this.mbean = mbean;
    }

    public ObjectName getObjectName() {
        return this.objectName;
    }

    public void setObjectName(final ObjectName objectName) {
        this.objectName = objectName;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getHypervisor() {
        return this.hypervisor;
    }

    public void setHypervisor(final String hypervisor) {
        this.hypervisor = hypervisor;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public AllocationMode getAllocationMode() {
        return this.allocationMode;
    }

    public void setAllocationMode(final AllocationMode allocationMode) {
        this.allocationMode = allocationMode;
    }

    @Override
    public String toString() {
        return "ResourcePartitionInfo [allocationMode=" + this.allocationMode + ", mbean=" + this.mbean + ", objectName="
            + this.objectName + ", path=" + this.path + ", hypervisor=" + this.hypervisor + ", location=" + this.location + "]";
    }

}
