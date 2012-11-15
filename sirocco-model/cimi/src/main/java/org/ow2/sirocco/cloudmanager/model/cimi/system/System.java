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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.ICloudProviderResource;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.utils.FSM;

@Entity
// @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
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

    private List<SystemNetworkPort> networkPorts;

    private List<SystemForwardingGroup> forwardingGroups;

    private CloudProviderAccount cloudProviderAccount;

    @Transient
    private static transient FSM<System.State, String> fsm = System.initFSM();

    public System() {
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    // @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<SystemCredentials> getCredentials() {
        return this.credentials;
    }

    public void setCredentials(final List<SystemCredentials> credentials) {
        this.credentials = credentials;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    // @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<SystemMachine> getMachines() {
        return this.machines;
    }

    public void setMachines(final List<SystemMachine> machines) {
        this.machines = machines;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    // @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<SystemSystem> getSystems() {
        return this.systems;
    }

    public void setSystems(final List<SystemSystem> systems) {
        this.systems = systems;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    // @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
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
    // @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    @ManyToOne
    // @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    // @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<SystemNetwork> getNetworks() {
        return this.networks;
    }

    public void setNetworks(final List<SystemNetwork> networks) {
        this.networks = networks;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    public List<SystemNetworkPort> getNetworkPorts() {
        return this.networkPorts;
    }

    public void setNetworkPorts(final List<SystemNetworkPort> networkPorts) {
        this.networkPorts = networkPorts;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "system_id")
    public List<SystemForwardingGroup> getForwardingGroups() {
        return this.forwardingGroups;
    }

    public void setForwardingGroups(final List<SystemForwardingGroup> forwardingGroups) {
        this.forwardingGroups = forwardingGroups;
    }

    @Transient
    public Set<String> getOperations() {
        Set<String> operations;
        // state may be null if not selected in the query that returns this
        // object
        if (this.state == null) {
            operations = Collections.emptySet();
        } else {
            operations = System.fsm.getActionsAtState(this.state);
            operations.remove(new String("internal"));
        }
        return operations;
    }

    private static FSM<System.State, String> initFSM() {
        System.fsm = new FSM<System.State, String>(State.CREATING);
        System.fsm.addAction(State.CREATING, "delete", State.DELETING);
        System.fsm.addAction(State.CREATING, "delete", State.ERROR);
        System.fsm.addAction(State.CREATING, "internal", State.STOPPED);
        System.fsm.addAction(State.CREATING, "internal", State.ERROR);
        System.fsm.addAction(State.ERROR, "start", State.STARTING);
        System.fsm.addAction(State.ERROR, "stop", State.STOPPING);
        System.fsm.addAction(State.ERROR, "restart", State.STARTING);
        System.fsm.addAction(State.ERROR, "delete", State.DELETING);

        System.fsm.addAction(State.STOPPED, "start", State.STARTING);
        System.fsm.addAction(State.STOPPED, "delete", State.DELETING);
        System.fsm.addAction(State.STOPPED, "restart", State.STARTING);

        System.fsm.addAction(State.STARTING, "internal", State.STARTED);
        System.fsm.addAction(State.STARTING, "start", State.STARTING);
        System.fsm.addAction(State.STARTING, "restart", State.STARTING);
        System.fsm.addAction(State.STARTING, "delete", State.DELETING);
        System.fsm.addAction(State.STARTING, "stop", State.STOPPING);

        System.fsm.addAction(State.STARTED, "restart", State.STARTING);
        System.fsm.addAction(State.STARTED, "stop", State.STOPPING);
        System.fsm.addAction(State.STARTED, "delete", State.DELETING);
        System.fsm.addAction(State.STARTED, "pause", State.PAUSING);
        System.fsm.addAction(State.STARTED, "suspend", State.SUSPENDING);

        System.fsm.addAction(State.STOPPING, "internal", State.STOPPED);
        System.fsm.addAction(State.STOPPING, "start", State.STARTING);
        System.fsm.addAction(State.STOPPING, "restart", State.STARTING);
        System.fsm.addAction(State.STOPPING, "delete", State.DELETING);

        System.fsm.addAction(State.STOPPED, "start", State.STARTING);
        System.fsm.addAction(State.STOPPED, "restart", State.STARTING);
        System.fsm.addAction(State.STOPPED, "delete", State.DELETING);

        System.fsm.addAction(State.PAUSING, "internal", State.PAUSED);
        System.fsm.addAction(State.PAUSING, "start", State.STARTING);
        System.fsm.addAction(State.PAUSING, "restart", State.STARTING);
        System.fsm.addAction(State.PAUSING, "delete", State.DELETING);

        System.fsm.addAction(State.PAUSED, "start", State.STARTING);
        System.fsm.addAction(State.PAUSED, "restart", State.STARTING);
        System.fsm.addAction(State.PAUSED, "stop", State.STOPPING);
        System.fsm.addAction(State.PAUSED, "delete", State.DELETING);

        System.fsm.addAction(State.SUSPENDING, "start", State.STARTING);
        System.fsm.addAction(State.SUSPENDING, "restart", State.STARTING);
        System.fsm.addAction(State.SUSPENDING, "delete", State.DELETING);
        System.fsm.addAction(State.SUSPENDING, "internal", State.SUSPENDED);

        System.fsm.addAction(State.SUSPENDED, "start", State.STARTING);
        System.fsm.addAction(State.SUSPENDED, "restart", State.STARTING);
        System.fsm.addAction(State.SUSPENDED, "delete", State.DELETING);

        System.fsm.addAction(State.DELETING, "delete", State.DELETING);
        System.fsm.addAction(State.DELETING, "internal", State.DELETED);
        return System.fsm;
    }

}
