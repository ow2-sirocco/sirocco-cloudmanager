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
public class VolumeImage extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String GET_VOLUMEIMAGE_BY_PROVIDER_ASSIGNED_ID = "GET_VOLUMEIMAGE_BY_PROVIDER_ASSIGNED_ID";

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    private State state;

    private String imageLocation;

    private Volume owner;

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

    @ManyToOne
    public Volume getOwner() {
        return this.owner;
    }

    public void setOwner(final Volume owner) {
        this.owner = owner;
    }

    public Boolean getBootable() {
        return this.bootable;
    }

    public void setBootable(final Boolean bootable) {
        this.bootable = bootable;
    }

    @ManyToOne
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
    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

}
