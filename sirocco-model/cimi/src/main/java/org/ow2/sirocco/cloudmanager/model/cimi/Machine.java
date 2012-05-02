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
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.CollectionOfElements;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;
import org.ow2.sirocco.cloudmanager.model.utils.FSM;

@Entity
@NamedQueries({@NamedQuery(name = "GET_MACHINE_BY_STATE", query = "SELECT v from Machine v WHERE v.state=:state")})
public class Machine extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private CloudProviderLocation location;

    public static final String GET_MACHINE_BY_STATE = "GET_MACHINE_BY_STATE";

    public static enum State {
        CREATING, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, DELETING, DELETED, ERROR
    }

    private State state;

    private Cpu cpu;

    private List<MachineVolume> volumes;
    private List<NetworkInterface>  networkInterfaces;

    private CloudProviderAccount cloudProviderAccount;

    private Memory 		memory;
    private List<Disk> disks;

    @Transient
    private FSM			fsm;

	

    public Machine() {
        this.disks = new ArrayList<Disk>();
        this.volumes = new ArrayList<MachineVolume>();
        this.networkInterfaces = new ArrayList<NetworkInterface>();
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

    @CollectionOfElements
    public List<Disk> getDisks() {
        return this.disks;
    }

    public void setDisks(List<Disk> disks) {
        disks = this.disks;
    }

    @OneToMany
    public List<MachineVolume> getVolumes() {
        return this.volumes;
    }

    public void setVolumes(final List<MachineVolume> volumes) {
        this.volumes = volumes;
    }


    @CollectionOfElements
    public List<NetworkInterface> getNetworkInterfaces() {
        return this.networkInterfaces;
    }

    public void setNetworkInterfaces(final List<NetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    @ManyToOne
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }
    
    /**
     * Ideally: create and persist an FSM per entity per provider.
     * Machines should then refer appropriate FSM.
     */
    public void initFSM() {
    	this.fsm = new FSM(State.CREATING.toString());
    	fsm.addAction(State.CREATING, "delete", State.DELETING);
		fsm.addAction(State.CREATING, "delete", State.ERROR);
		fsm.addAction(State.CREATING, "internal", State.STOPPED);
		fsm.addAction(State.CREATING, "internal", State.ERROR);
		fsm.addAction(State.ERROR, "start", State.STARTING);
		fsm.addAction(State.ERROR, "stop", State.STOPPING);
		fsm.addAction(State.ERROR, "restart", State.STARTING);
		fsm.addAction(State.ERROR, "delete", State.DELETING);


		fsm.addAction(State.STOPPED, "start", State.STARTING);
		fsm.addAction(State.STOPPED, "delete", State.DELETING);
		fsm.addAction(State.STOPPED, "capture", State.STOPPED);
		fsm.addAction(State.STOPPED, "restart", State.STARTING);

		fsm.addAction(State.STARTING, "internal", State.STARTED);
		fsm.addAction(State.STARTING, "start", State.STARTING);
		fsm.addAction(State.STARTING, "restart", State.STARTING);
		fsm.addAction(State.STARTING, "delete", State.DELETING);
		fsm.addAction(State.STARTING, "stop", State.STOPPING);

		fsm.addAction(State.STARTED, "restart", State.STARTING);
		fsm.addAction(State.STARTED, "stop", State.STOPPING);
		fsm.addAction(State.STARTED, "delete", State.DELETING);
		fsm.addAction(State.STARTED, "capture", State.STARTED);
		fsm.addAction(State.STARTED, "pause", State.PAUSING);
		fsm.addAction(State.STARTED, "suspend", State.SUSPENDING);

		fsm.addAction(State.STOPPING, "internal", State.STOPPED);
		fsm.addAction(State.STOPPING, "start", State.STARTING);
		fsm.addAction(State.STOPPING, "restart", State.STARTING);
		fsm.addAction(State.STOPPING, "delete", State.DELETING);

		fsm.addAction(State.STOPPED, "start", State.STARTING);
		fsm.addAction(State.STOPPED, "restart", State.STARTING);
		fsm.addAction(State.STOPPED, "capture", State.STOPPED);
		fsm.addAction(State.STOPPED, "delete", State.DELETING);

		fsm.addAction(State.PAUSING, "internal", State.PAUSED);
		fsm.addAction(State.PAUSING, "start", State.STARTING);
		fsm.addAction(State.PAUSING, "restart", State.STARTING);
		fsm.addAction(State.PAUSING, "delete", State.DELETING);

		fsm.addAction(State.PAUSED, "start", State.STARTING);
		fsm.addAction(State.PAUSED, "capture", State.PAUSED);
		fsm.addAction(State.PAUSED, "restart", State.STARTING);
		fsm.addAction(State.PAUSED, "stop", State.STOPPING);
		fsm.addAction(State.PAUSED, "delete", State.DELETING);

		fsm.addAction(State.SUSPENDING, "start", State.STARTING);
		fsm.addAction(State.SUSPENDING, "restart", State.STARTING);
		fsm.addAction(State.SUSPENDING, "delete", State.DELETING);
		fsm.addAction(State.SUSPENDING, "internal", State.SUSPENDED);

		fsm.addAction(State.SUSPENDED, "start", State.STARTING);
		fsm.addAction(State.SUSPENDED, "restart", State.STARTING);
		fsm.addAction(State.SUSPENDED, "capture", State.SUSPENDED);
		fsm.addAction(State.SUSPENDED, "delete", State.DELETING);

		fsm.addAction(State.DELETING, "delete", State.DELETING);
		fsm.addAction(State.DELETING, "internal", State.DELETED);
    }
    /** get operations allowed in this state */
    @Transient
    public Set<String> getOperations() {
    	Set<String> operations = fsm.getActionsAtState(state);
		operations.remove(new String("internal"));
    	return operations;
    }

    @ManyToOne
    public CloudProviderLocation getLocation() {
        return location;
    }

    public void setLocation(CloudProviderLocation location) {
        this.location = location;
    }
}
