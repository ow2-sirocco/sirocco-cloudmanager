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

@XmlType(name = "image", propOrder = {"imageId", "name", "description", "projectId", "visibility"})
@XmlRootElement(name = "image")
public class VmImageInfo {

    private Integer imageId;

    private String name;

    private String description;

    private String projectId;

    private String visibility;

    public VmImageInfo() {
    }

    public Integer getImageId() {
        return this.imageId;
    }

    public void setImageId(final Integer imageId) {
        this.imageId = imageId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "VmImageInfo [imageId=" + this.imageId + ", name=" + this.name + ", description=" + this.description
            + ", projectId=" + this.projectId + ", visibility=" + this.visibility + "]";
    }

}
