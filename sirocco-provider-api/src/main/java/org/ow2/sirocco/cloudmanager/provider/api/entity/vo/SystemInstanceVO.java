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

@SuppressWarnings("serial")
public class SystemInstanceVO implements Serializable {

    private String id;

    private String status;

    private String userId;

    private String projectId;

    private String systemTemplateId;

    private Collection<String> virtualMachinesId;

    private Collection<String> volumesId;

    private Collection<String> networksId;

    public SystemInstanceVO() {

    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
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

    public String getSystemTemplateId() {
        return this.systemTemplateId;
    }

    public void setSystemTemplateId(final String systemTemplateId) {
        this.systemTemplateId = systemTemplateId;
    }

    public Collection<String> getVirtualMachinesId() {
        return this.virtualMachinesId;
    }

    public void setVirtualMachinesId(final Collection<String> virtualMachinesId) {
        this.virtualMachinesId = virtualMachinesId;
    }

    public Collection<String> getVolumesId() {
        return this.volumesId;
    }

    public void setVolumesId(final Collection<String> volumesId) {
        this.volumesId = volumesId;
    }

    public Collection<String> getNetworksId() {
        return this.networksId;
    }

    public void setNetworksId(final Collection<String> networksId) {
        this.networksId = networksId;
    }

}
