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

package org.ow2.sirocco.cloudmanager.provider.api.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CloudProviderLocation implements Serializable {

    private String locationId;

    private String description;

    public CloudProviderLocation() {
    }

    public CloudProviderLocation(final String locationId) {
        this.locationId = locationId;
    }

    public CloudProviderLocation(final String locationId, final String description) {
        this.locationId = locationId;
        this.description = description;
    }

    public String getLocationId() {
        return this.locationId;
    }

    public void setLocationId(final String locationId) {
        this.locationId = locationId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.locationId == null) ? 0 : this.locationId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CloudProviderLocation other = (CloudProviderLocation) obj;
        if (this.locationId == null) {
            if (other.locationId != null) {
                return false;
            }
        } else if (!this.locationId.equals(other.locationId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CloudProviderLocation [locationId=" + this.locationId + ", description=" + this.description + "]";
    }

}
