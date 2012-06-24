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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.utils.FSM;

@Entity
@NamedQueries({ @NamedQuery(name = "GET_MACHINE_BY_STATE", query = "SELECT v from Machine v WHERE v.state=:state") })
public class Machine extends CloudResource implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String GET_MACHINE_BY_STATE = "GET_MACHINE_BY_STATE";

    private CloudProviderLocation location;

    public static enum State {
        CREATING, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, DELETING, DELETED, ERROR
    }

    private State state;

    private Cpu cpu;

    private Memory memory;

    @OneToMany
    private List<MachineVolume> volumes;

    @OneToMany
    private List<MachineDisk> disks;

    @OneToMany
    @JoinColumn(name = "machine_id", referencedColumnName = "id")
    private List<MachineNetworkInterface> networkInterfaces;

    private CloudProviderAccount cloudProviderAccount;

    @Transient
    private FSM<Machine.State, String> fsm;

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

    @Embedded
    public Cpu getCpu() {
        return this.cpu;
    }

    public void setCpu(final Cpu cpu) {
        this.cpu = cpu;
    }

    @Embedded
    public Memory getMemory() {
        return this.memory;
    }

    public void setMemory(final Memory memory) {
        this.memory = memory;
    }

    @OneToMany
    public List<MachineDisk> getDisks() {
        return this.disks;
    }

    public void setDisks(final List<MachineDisk> disks) {
        this.disks = disks;
    }

    public void addMachineDisk(final MachineDisk disk) {
        if (!getDisks().contains(disk)) {
            getDisks().add(disk);
        }
    }

    @OneToMany
    public List<MachineVolume> getVolumes() {
        return this.volumes;
    }

    public void addMachineVolume(final MachineVolume mv) {
        if (!getVolumes().contains(mv)) {
            getVolumes().add(mv);
        }
    }

    public void removeMachineVolume(final MachineVolume mv) {
        if (getVolumes().contains(mv)) {
            getVolumes().remove(mv);
        }
    }

    public void setVolumes(final List<MachineVolume> volumes) {
        this.volumes = volumes;
    }

    @OneToMany
    @JoinColumn(name = "machine_id", referencedColumnName = "id")
    public List<MachineNetworkInterface> getNetworkInterfaces() {
        return this.networkInterfaces;
    }

    public void addNetworkInterface(final MachineNetworkInterface networkInterface) {
        if (!getNetworkInterfaces().contains(networkInterface)) {
            getNetworkInterfaces().add(networkInterface);
        }
    }

    public void setNetworkInterfaces(
            final List<MachineNetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    @ManyToOne
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(
            final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    /**
     * Ideally: create and persist an FSM per entity per provider. Machines
     * should then refer appropriate FSM.
     */
    public void initFSM() {
        this.fsm = new FSM<Machine.State, String>(State.CREATING);
        this.fsm.addAction(State.CREATING, "delete", State.DELETING);
        this.fsm.addAction(State.CREATING, "delete", State.ERROR);
        this.fsm.addAction(State.CREATING, "internal", State.STOPPED);
        this.fsm.addAction(State.CREATING, "internal", State.ERROR);
        this.fsm.addAction(State.ERROR, "start", State.STARTING);
        this.fsm.addAction(State.ERROR, "stop", State.STOPPING);
        this.fsm.addAction(State.ERROR, "restart", State.STARTING);
        this.fsm.addAction(State.ERROR, "delete", State.DELETING);

        this.fsm.addAction(State.STOPPED, "start", State.STARTING);
        this.fsm.addAction(State.STOPPED, "delete", State.DELETING);
        this.fsm.addAction(State.STOPPED, "capture", State.STOPPED);
        this.fsm.addAction(State.STOPPED, "restart", State.STARTING);

        this.fsm.addAction(State.STARTING, "internal", State.STARTED);
        this.fsm.addAction(State.STARTING, "start", State.STARTING);
        this.fsm.addAction(State.STARTING, "restart", State.STARTING);
        this.fsm.addAction(State.STARTING, "delete", State.DELETING);
        this.fsm.addAction(State.STARTING, "stop", State.STOPPING);

        this.fsm.addAction(State.STARTED, "restart", State.STARTING);
        this.fsm.addAction(State.STARTED, "stop", State.STOPPING);
        this.fsm.addAction(State.STARTED, "delete", State.DELETING);
        this.fsm.addAction(State.STARTED, "capture", State.STARTED);
        this.fsm.addAction(State.STARTED, "pause", State.PAUSING);
        this.fsm.addAction(State.STARTED, "suspend", State.SUSPENDING);

        this.fsm.addAction(State.STOPPING, "internal", State.STOPPED);
        this.fsm.addAction(State.STOPPING, "start", State.STARTING);
        this.fsm.addAction(State.STOPPING, "restart", State.STARTING);
        this.fsm.addAction(State.STOPPING, "delete", State.DELETING);

        this.fsm.addAction(State.STOPPED, "start", State.STARTING);
        this.fsm.addAction(State.STOPPED, "restart", State.STARTING);
        this.fsm.addAction(State.STOPPED, "capture", State.STOPPED);
        this.fsm.addAction(State.STOPPED, "delete", State.DELETING);

        this.fsm.addAction(State.PAUSING, "internal", State.PAUSED);
        this.fsm.addAction(State.PAUSING, "start", State.STARTING);
        this.fsm.addAction(State.PAUSING, "restart", State.STARTING);
        this.fsm.addAction(State.PAUSING, "delete", State.DELETING);

        this.fsm.addAction(State.PAUSED, "start", State.STARTING);
        this.fsm.addAction(State.PAUSED, "capture", State.PAUSED);
        this.fsm.addAction(State.PAUSED, "restart", State.STARTING);
        this.fsm.addAction(State.PAUSED, "stop", State.STOPPING);
        this.fsm.addAction(State.PAUSED, "delete", State.DELETING);

        this.fsm.addAction(State.SUSPENDING, "start", State.STARTING);
        this.fsm.addAction(State.SUSPENDING, "restart", State.STARTING);
        this.fsm.addAction(State.SUSPENDING, "delete", State.DELETING);
        this.fsm.addAction(State.SUSPENDING, "internal", State.SUSPENDED);

        this.fsm.addAction(State.SUSPENDED, "start", State.STARTING);
        this.fsm.addAction(State.SUSPENDED, "restart", State.STARTING);
        this.fsm.addAction(State.SUSPENDED, "capture", State.SUSPENDED);
        this.fsm.addAction(State.SUSPENDED, "delete", State.DELETING);

        this.fsm.addAction(State.DELETING, "delete", State.DELETING);
        this.fsm.addAction(State.DELETING, "internal", State.DELETED);
    }

    /** get operations allowed in this state */
    @Transient
    public Set<String> getOperations() {
        Set<String> operations = this.fsm.getActionsAtState(this.state);
        operations.remove(new String("internal"));
        return operations;
    }

    @ManyToOne
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }
}
