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

@NamedQueries(value = {@NamedQuery(name = "GET_VOLUME_BY_PROVIDER_ASSIGNED_ID", query = "SELECT v FROM Volume v WHERE v.providerAssignedId=:providerAssignedId")})
@Entity
public class Volume extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String GET_VOLUME_BY_PROVIDER_ASSIGNED_ID = "GET_VOLUME_BY_PROVIDER_ASSIGNED_ID";

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    private State state;

    private Disk capacity;

    private Boolean bootable;

    private Boolean supportsSnapshots;

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

    @OneToMany(mappedBy = "volume", fetch = FetchType.EAGER)
    public Collection<MachineVolume> getMachineVolumes() {
        return this.machineVolumes;
    }

    public void setMachineVolumes(final Collection<MachineVolume> machineVs) {
        this.machineVolumes = machineVs;
    }

    public Boolean isSupportsSnapshots() {
        return this.supportsSnapshots;
    }

    public void setSupportsSnapshots(final boolean supportsSnapshots) {
        this.supportsSnapshots = supportsSnapshots;
    }

    @ManyToOne
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

}
