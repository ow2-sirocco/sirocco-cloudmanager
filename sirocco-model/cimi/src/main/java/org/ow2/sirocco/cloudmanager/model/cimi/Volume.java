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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

@NamedQueries(value = {@NamedQuery(name = "GET_VOLUME_BY_PROVIDER_ASSIGNED_ID", query = "SELECT v FROM Volume v WHERE v.providerAssignedId=:providerAssignedId")})
@Entity
public class Volume extends CloudResource implements Serializable {
    private static final long serialVersionUID = 1L;

    private CloudProviderLocation location;

    public static final String GET_VOLUME_BY_PROVIDER_ASSIGNED_ID = "GET_VOLUME_BY_PROVIDER_ASSIGNED_ID";

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    private State state;

    private String type;

    private Disk capacity;

    private Boolean bootable;

    private List<VolumeImage> images;

    private Collection<MachineVolume> machineVolumes;

    private CloudProviderAccount cloudProviderAccount;

    public Volume() {
        this.machineVolumes = new ArrayList<MachineVolume>();
    }

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    @Embedded
    public Disk getCapacity() {
        return this.capacity;
    }

    public void setCapacity(final Disk capacity) {
        this.capacity = capacity;
    }

    public Boolean getBootable() {
        return this.bootable;
    }

    public void setBootable(final Boolean bootable) {
        this.bootable = bootable;
    }

    @OneToMany(mappedBy = "volume")
    public Collection<MachineVolume> getMachineVolumes() {
        return this.machineVolumes;
    }

    public void setMachineVolumes(final Collection<MachineVolume> machineVs) {
        this.machineVolumes = machineVs;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    public List<VolumeImage> getImages() {
        return this.images;
    }

    public void setImages(final List<VolumeImage> images) {
        this.images = images;
    }

    @ManyToOne
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    @ManyToOne
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Volume [id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", created="
            + this.created + ", updated=" + this.updated + ", properties=" + this.properties + ", state=" + this.state
            + ", type=" + this.type + ", capacity=" + this.capacity + ", bootable=" + this.bootable + ", images=" + this.images
            + "]";
    }

}
