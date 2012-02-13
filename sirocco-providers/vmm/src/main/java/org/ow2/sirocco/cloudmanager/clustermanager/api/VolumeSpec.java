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
import java.util.Map;

public class VolumeSpec implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private long capacityInMB;

    private String visibility;

    private Map<String, String> constraints;

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

    public String getVisibility() {
        return this.visibility;
    }

    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    public Map<String, String> getConstraints() {
        return this.constraints;
    }

    public void setConstraints(final Map<String, String> constraints) {
        this.constraints = constraints;
    }

    @Override
    public String toString() {
        return "VolumeSpec [name=" + this.name + ", description=" + this.description + ", capacityInMB=" + this.capacityInMB
            + ", visibility=" + this.visibility + ", constraints=" + this.constraints + "]";
    }

}
