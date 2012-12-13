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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

@NamedQueries(value = {@NamedQuery(name = "GET_FORWARDINGGROUP_BY_PROVIDER_ASSIGNED_ID", query = "SELECT n FROM ForwardingGroup n WHERE n.providerAssignedId=:providerAssignedId")})
@Entity
//@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class ForwardingGroup extends CloudResource implements Serializable, ICloudProviderResource {
    private static final long serialVersionUID = 1L;

    public static final String GET_FORWARDINGGROUP_BY_PROVIDER_ASSIGNED_ID = "GET_FORWARDINGGROUP_BY_PROVIDER_ASSIGNED_ID";

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    private CloudProviderAccount cloudProviderAccount;

    private CloudProviderLocation location;

    private State state;

    private List<ForwardingGroupNetwork> networks;

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    @OneToMany
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<ForwardingGroupNetwork> getNetworks() {
        return this.networks;
    }

    public void setNetworks(final List<ForwardingGroupNetwork> networks) {
        this.networks = networks;
    }

    @ManyToOne
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    @ManyToOne
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

}
