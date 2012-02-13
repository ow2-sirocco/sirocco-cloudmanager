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
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class VolumeVO implements Serializable {

    private String volumeId;

    private String projectId;

    private String userName;

    private String status;

    private String name;

    private String description;

    private long capacityInMB;

    private boolean bootable;

    private String visibility;

    private List<String> attachedVirtualMachineIds;

    private Date creationDate;

    private String cloudProviderAccountId;

    private String cloudProviderId;

    private String location;

    public String getVolumeId() {
        return this.volumeId;
    }

    public void setVolumeId(final String volumeId) {
        this.volumeId = volumeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
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

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    public List<String> getAttachedVirtualMachineIds() {
        return this.attachedVirtualMachineIds;
    }

    public void setAttachedVirtualMachineIds(final List<String> attachedVirtualMachineIds) {
        this.attachedVirtualMachineIds = attachedVirtualMachineIds;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCloudProviderAccountId() {
        return this.cloudProviderAccountId;
    }

    public void setCloudProviderAccountId(final String cloudProviderAccountId) {
        this.cloudProviderAccountId = cloudProviderAccountId;
    }

    public String getCloudProviderId() {
        return this.cloudProviderId;
    }

    public void setCloudProviderId(final String cloudProviderId) {
        this.cloudProviderId = cloudProviderId;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }
}
