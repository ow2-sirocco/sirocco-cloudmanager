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
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

@NamedQueries(value = {@NamedQuery(name = "GET_VOLUME_BY_PROVIDER_ASSIGNED_ID", query = "SELECT v FROM Volume v WHERE v.providerAssignedId=:providerAssignedId")})
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Volume extends CloudResource implements Serializable, ICloudProviderResource {
    private static final long serialVersionUID = 1L;

    private CloudProviderLocation location;

    public static final String GET_VOLUME_BY_PROVIDER_ASSIGNED_ID = "GET_VOLUME_BY_PROVIDER_ASSIGNED_ID";

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    private State state;

    private String type;

    private Integer capacity;

    private Boolean bootable;

    private List<VolumeVolumeImage> images;

    private CloudProviderAccount cloudProviderAccount;

    private MachineVolume attachment;

    public Volume() {
    }

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public void setCapacity(final Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getBootable() {
        return this.bootable;
    }

    public void setBootable(final Boolean bootable) {
        this.bootable = bootable;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<VolumeVolumeImage> getImages() {
        return this.images;
    }

    public void setImages(final List<VolumeVolumeImage> images) {
        this.images = images;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

    @OneToOne(mappedBy = "volume")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public MachineVolume getAttachment() {
        return this.attachment;
    }

    public void setAttachment(final MachineVolume attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Volume [id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", created="
            + this.created + ", updated=" + this.updated + ", properties=" + this.properties + ", state=" + this.state
            + ", type=" + this.type + ", capacity=" + this.capacity + ", bootable=" + this.bootable + ", images=" + this.images
            + "]";
    }

}
