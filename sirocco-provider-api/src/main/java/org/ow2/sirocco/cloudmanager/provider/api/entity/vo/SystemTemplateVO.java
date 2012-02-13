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
import java.util.Collection;
import java.util.List;

@SuppressWarnings("serial")
public class SystemTemplateVO implements Serializable {

    private String id;

    private String name;

    private String status;

    private String userId;

    private String projectId;

    private List<String> virtualMachineTemplatesId;

    private Collection<String> networkTemplatesId;

    private Collection<String> volumeTemplatesId;

    private Collection<String> systemInstancesId;

    private Collection<String> customizationPropertiesId;

    public SystemTemplateVO() {

    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public List<String> getVirtualMachineTemplatesId() {
        return this.virtualMachineTemplatesId;
    }

    public void setVirtualMachineTemplatesId(final List<String> virtualMachineTemplatesId) {
        this.virtualMachineTemplatesId = virtualMachineTemplatesId;
    }

    public Collection<String> getNetworkTemplatesId() {
        return this.networkTemplatesId;
    }

    public void setNetworkTemplatesId(final Collection<String> networkTemplatesId) {
        this.networkTemplatesId = networkTemplatesId;
    }

    public Collection<String> getVolumeTemplatesId() {
        return this.volumeTemplatesId;
    }

    public void setVolumeTemplatesId(final Collection<String> volumeTemplatesId) {
        this.volumeTemplatesId = volumeTemplatesId;
    }

    public Collection<String> getSystemInstancesId() {
        return this.systemInstancesId;
    }

    public void setSystemInstancesId(final Collection<String> systemInstancesId) {
        this.systemInstancesId = systemInstancesId;
    }

    public Collection<String> getCustomizationPropertiesId() {
        return this.customizationPropertiesId;
    }

    public void setCustomizationPropertiesId(final Collection<String> customizationPropertiesId) {
        this.customizationPropertiesId = customizationPropertiesId;
    }

}
