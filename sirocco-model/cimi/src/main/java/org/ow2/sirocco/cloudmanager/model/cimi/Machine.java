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
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.utils.FSM;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({@NamedQuery(name = "GET_MACHINE_BY_STATE", query = "SELECT v from Machine v WHERE v.state=:state")})
public class Machine extends CloudResource implements Serializable, ICloudProviderResource {
    private static final long serialVersionUID = 1L;

    public static final String GET_MACHINE_BY_STATE = "GET_MACHINE_BY_STATE";

    private CloudProviderLocation location;

    public static enum State {
        CREATING, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, DELETING, DELETED, ERROR
    }

    private State state;

    private Integer cpu;

    private Integer memory;

    private List<MachineVolume> volumes;

    private List<MachineDisk> disks;

    private List<MachineNetworkInterface> networkInterfaces;

    private CloudProviderAccount cloudProviderAccount;

    @Transient
    private static transient FSM<Machine.State, String> fsm = Machine.initFSM();

    public Machine() {
        this.networkInterfaces = new ArrayList<MachineNetworkInterface>();
        this.disks = new ArrayList<MachineDisk>();
        this.volumes = new ArrayList<MachineVolume>();
    }

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public Integer getCpu() {
        return this.cpu;
    }

    public void setCpu(final Integer cpu) {
        this.cpu = cpu;
    }

    public Integer getMemory() {
        return this.memory;
    }

    public void setMemory(final Integer memory) {
        this.memory = memory;
    }

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<MachineDisk> getDisks() {
        return this.disks;
    }

    public void setDisks(final List<MachineDisk> disks) {
        this.disks = disks;
    }

    public void addMachineDisk(final MachineDisk disk) {
        if (!this.getDisks().contains(disk)) {
            this.getDisks().add(disk);
        }
    }

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<MachineVolume> getVolumes() {
        return this.volumes;
    }

    public void addMachineVolume(final MachineVolume mv) {
        if (!this.getVolumes().contains(mv)) {
            this.getVolumes().add(mv);
            mv.setOwner(this);
        }
    }

    public void removeMachineVolume(final MachineVolume mv) {
        if (this.getVolumes().contains(mv)) {
            mv.setOwner(null);
            this.getVolumes().remove(mv);
        }
    }

    public void setVolumes(final List<MachineVolume> volumes) {
        this.volumes = volumes;
    }

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "machine_id", referencedColumnName = "id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<MachineNetworkInterface> getNetworkInterfaces() {
        return this.networkInterfaces;
    }

    public void addNetworkInterface(final MachineNetworkInterface networkInterface) {
        if (!this.getNetworkInterfaces().contains(networkInterface)) {
            this.getNetworkInterfaces().add(networkInterface);
        }
    }

    public void setNetworkInterfaces(final List<MachineNetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    /**
     * Ideally: create and persist an FSM per entity per provider. Machines
     * should then refer appropriate FSM.
     */
    private static FSM<Machine.State, String> initFSM() {
        Machine.fsm = new FSM<Machine.State, String>(State.CREATING);
        Machine.fsm.addAction(State.CREATING, "delete", State.DELETING);
        Machine.fsm.addAction(State.CREATING, "delete", State.ERROR);
        Machine.fsm.addAction(State.CREATING, "internal", State.STOPPED);
        Machine.fsm.addAction(State.CREATING, "internal", State.ERROR);
        Machine.fsm.addAction(State.ERROR, "start", State.STARTING);
        Machine.fsm.addAction(State.ERROR, "stop", State.STOPPING);
        Machine.fsm.addAction(State.ERROR, "restart", State.STARTING);
        Machine.fsm.addAction(State.ERROR, "delete", State.DELETING);

        Machine.fsm.addAction(State.STOPPED, "start", State.STARTING);
        Machine.fsm.addAction(State.STOPPED, "delete", State.DELETING);
        Machine.fsm.addAction(State.STOPPED, "capture", State.STOPPED);
        Machine.fsm.addAction(State.STOPPED, "restart", State.STARTING);

        Machine.fsm.addAction(State.STARTING, "internal", State.STARTED);
        Machine.fsm.addAction(State.STARTING, "start", State.STARTING);
        Machine.fsm.addAction(State.STARTING, "restart", State.STARTING);
        Machine.fsm.addAction(State.STARTING, "delete", State.DELETING);
        Machine.fsm.addAction(State.STARTING, "stop", State.STOPPING);

        Machine.fsm.addAction(State.STARTED, "restart", State.STARTING);
        Machine.fsm.addAction(State.STARTED, "stop", State.STOPPING);
        Machine.fsm.addAction(State.STARTED, "delete", State.DELETING);
        Machine.fsm.addAction(State.STARTED, "capture", State.STARTED);
        Machine.fsm.addAction(State.STARTED, "pause", State.PAUSING);
        Machine.fsm.addAction(State.STARTED, "suspend", State.SUSPENDING);

        Machine.fsm.addAction(State.STOPPING, "internal", State.STOPPED);
        Machine.fsm.addAction(State.STOPPING, "start", State.STARTING);
        Machine.fsm.addAction(State.STOPPING, "restart", State.STARTING);
        Machine.fsm.addAction(State.STOPPING, "delete", State.DELETING);

        Machine.fsm.addAction(State.STOPPED, "start", State.STARTING);
        Machine.fsm.addAction(State.STOPPED, "restart", State.STARTING);
        Machine.fsm.addAction(State.STOPPED, "capture", State.STOPPED);
        Machine.fsm.addAction(State.STOPPED, "delete", State.DELETING);

        Machine.fsm.addAction(State.PAUSING, "internal", State.PAUSED);
        Machine.fsm.addAction(State.PAUSING, "start", State.STARTING);
        Machine.fsm.addAction(State.PAUSING, "restart", State.STARTING);
        Machine.fsm.addAction(State.PAUSING, "delete", State.DELETING);

        Machine.fsm.addAction(State.PAUSED, "start", State.STARTING);
        Machine.fsm.addAction(State.PAUSED, "capture", State.PAUSED);
        Machine.fsm.addAction(State.PAUSED, "restart", State.STARTING);
        Machine.fsm.addAction(State.PAUSED, "stop", State.STOPPING);
        Machine.fsm.addAction(State.PAUSED, "delete", State.DELETING);

        Machine.fsm.addAction(State.SUSPENDING, "start", State.STARTING);
        Machine.fsm.addAction(State.SUSPENDING, "restart", State.STARTING);
        Machine.fsm.addAction(State.SUSPENDING, "delete", State.DELETING);
        Machine.fsm.addAction(State.SUSPENDING, "internal", State.SUSPENDED);

        Machine.fsm.addAction(State.SUSPENDED, "start", State.STARTING);
        Machine.fsm.addAction(State.SUSPENDED, "restart", State.STARTING);
        Machine.fsm.addAction(State.SUSPENDED, "capture", State.SUSPENDED);
        Machine.fsm.addAction(State.SUSPENDED, "delete", State.DELETING);

        Machine.fsm.addAction(State.DELETING, "delete", State.DELETING);
        Machine.fsm.addAction(State.DELETING, "internal", State.DELETED);
        return Machine.fsm;
    }

    /** get operations allowed in this state */
    @Transient
    public Set<String> getOperations() {
        Set<String> operations = Machine.fsm.getActionsAtState(this.state);
        operations.remove(new String("internal"));
        return operations;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }
}
