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

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

@Entity
@Table(name = "SYSTEMINSTANCE")
public class System extends CloudResource implements Serializable {

    private static final long serialVersionUID = 1L;

    private CloudProviderLocation location;

    public static enum State {
        CREATING, CREATED, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, MIXED, DELETING, ERROR
    }

    private List<Credentials> credentials;

    private List<Machine> machines;

    private List<System> systems;

    private State state;

    private List<Volume> volumes;
    
    private List<Network> networks;

    private CloudProviderAccount cloudProviderAccount;

    public System() {
    }
    
    @OneToMany
    @JoinColumn(name="system_id")
    public List<Credentials> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<Credentials> credentials) {
        this.credentials = credentials;
    }

    @OneToMany
    @JoinColumn(name="system_id")
    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }

    @OneToMany
    @JoinColumn(name="system_id")
    public List<System> getSystems() {
        return systems;
    }

    public void setSystems(List<System> systems) {
        this.systems = systems;
    }

    @OneToMany
    @JoinColumn(name="system_id")
    public List<Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Volume> volumes) {
        this.volumes = volumes;
    }

    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

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
    
    @OneToMany
    @JoinColumn(name="system_id")
    public List<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(List<Network> networks) {
        this.networks = networks;
    }

}
