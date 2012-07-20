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
 *  $Id: System.java 788 2012-04-17 11:49:55Z ycas7461 $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi.system;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.ICloudProviderResource;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

@Entity
@Table(name = "SYSTEMINSTANCE")
public class System extends CloudResource implements Serializable, ICloudProviderResource {

    private static final long serialVersionUID = 1L;

    private CloudProviderLocation location;

    public static enum State {
        CREATING, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, MIXED, DELETING, DELETED, ERROR
    }

    private List<SystemCredentials> credentials;

    private List<SystemMachine> machines;

    private List<SystemSystem> systems;

    private State state;

    private List<SystemVolume> volumes;

    private List<SystemNetwork> networks;

    private CloudProviderAccount cloudProviderAccount;

    public System() {
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    public List<SystemCredentials> getCredentials() {
        return this.credentials;
    }

    public void setCredentials(final List<SystemCredentials> credentials) {
        this.credentials = credentials;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    public List<SystemMachine> getMachines() {
        return this.machines;
    }

    public void setMachines(final List<SystemMachine> machines) {
        this.machines = machines;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    public List<SystemSystem> getSystems() {
        return this.systems;
    }

    public void setSystems(final List<SystemSystem> systems) {
        this.systems = systems;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    public List<SystemVolume> getVolumes() {
        return this.volumes;
    }

    public void setVolumes(final List<SystemVolume> volumes) {
        this.volumes = volumes;
    }

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
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

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    public List<SystemNetwork> getNetworks() {
        return this.networks;
    }

    public void setNetworks(final List<SystemNetwork> networks) {
        this.networks = networks;
    }

}
