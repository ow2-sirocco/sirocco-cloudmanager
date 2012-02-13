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

package org.ow2.sirocco.cloudmanager.api.spec;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "volume", propOrder = {"id", "name", "description", "status", "user", "projectId", "capacityInMB", "bootable"})
@XmlRootElement(name = "volume")
public class VolumeInfo {
    private String name;

    private String id;

    private String user;

    private String projectId;

    private String status;

    private String description;

    private long capacityInMB;

    private boolean bootable;

    public VolumeInfo() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String instanceId) {
        this.id = instanceId;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String descrition) {
        this.description = descrition;
    }

    public long getCapacityInMB() {
        return this.capacityInMB;
    }

    public void setCapacityInMB(final long capacityInMB) {
        this.capacityInMB = capacityInMB;
    }

    public boolean isBootable() {
        return this.bootable;
    }

    public void setBootable(final boolean bootable) {
        this.bootable = bootable;
    }

    @Override
    public String toString() {
        return "VolumeInfo [name=" + this.name + ", id=" + this.id + ", user=" + this.user + ", projectId=" + this.projectId
            + ", status=" + this.status + ", description=" + this.description + ", capacityInMB=" + this.capacityInMB
            + ", bootable=" + this.bootable + "]";
    }

}
