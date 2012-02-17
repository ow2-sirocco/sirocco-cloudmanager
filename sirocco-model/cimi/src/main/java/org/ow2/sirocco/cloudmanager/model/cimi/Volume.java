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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Volume extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    private State state;

    private Capacity capacity;

    private Boolean bootable;

    private Boolean supportsSnapshots;

    private Collection<Machine> machines;

    private CloudProviderAccount cloudProviderAccount;

    public Volume() {
        this.machines = new ArrayList<Machine>();
    }

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    @Embedded
    public Capacity getCapacity() {
        return this.capacity;
    }

    public void setCapacity(final Capacity capacity) {
        this.capacity = capacity;
    }

    public Boolean getBootable() {
        return this.bootable;
    }

    public void setBootable(final Boolean bootable) {
        this.bootable = bootable;
    }

    @ManyToMany(mappedBy = "volumes", fetch = FetchType.EAGER)
    public Collection<Machine> getMachines() {
        return this.machines;
    }

    public void setMachines(final Collection<Machine> machines) {
        this.machines = machines;
    }

    public boolean isSupportsSnapshots() {
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
