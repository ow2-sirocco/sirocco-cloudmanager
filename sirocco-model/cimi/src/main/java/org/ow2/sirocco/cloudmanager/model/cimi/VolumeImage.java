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

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

@NamedQueries(value = {@NamedQuery(name = "GET_VOLUMEIMAGE_BY_PROVIDER_ASSIGNED_ID", query = "SELECT v FROM VolumeImage v WHERE v.providerAssignedId=:providerAssignedId")})
@Entity
// @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class VolumeImage extends CloudResource implements Serializable, ICloudProviderResource {
    private static final long serialVersionUID = 1L;

    public static final String GET_VOLUMEIMAGE_BY_PROVIDER_ASSIGNED_ID = "GET_VOLUMEIMAGE_BY_PROVIDER_ASSIGNED_ID";

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    private State state;

    private String imageLocation;

    private Boolean bootable;

    private CloudProviderAccount cloudProviderAccount;

    private CloudProviderLocation location;

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public String getImageLocation() {
        return this.imageLocation;
    }

    public void setImageLocation(final String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public Boolean getBootable() {
        return this.bootable;
    }

    public void setBootable(final Boolean bootable) {
        this.bootable = bootable;
    }

    @ManyToOne
    // @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    public CloudProviderLocation getLocation() {
        return this.location;
    }

    @ManyToOne
    // @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.bootable == null) ? 0 : this.bootable.hashCode());
        result = prime * result + ((this.imageLocation == null) ? 0 : this.imageLocation.hashCode());
        result = prime * result + ((this.state == null) ? 0 : this.state.hashCode());
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
        VolumeImage other = (VolumeImage) obj;
        if (this.bootable == null) {
            if (other.bootable != null) {
                return false;
            }
        } else if (!this.bootable.equals(other.bootable)) {
            return false;
        }
        if (this.imageLocation == null) {
            if (other.imageLocation != null) {
                return false;
            }
        } else if (!this.imageLocation.equals(other.imageLocation)) {
            return false;
        }
        if (this.state != other.state) {
            return false;
        }
        return true;
    }

}
