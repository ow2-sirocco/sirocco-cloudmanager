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

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

@NamedQueries(value = {@NamedQuery(name = "GET_NETWORKPORT_BY_PROVIDER_ASSIGNED_ID", query = "SELECT n FROM NetworkPort n WHERE n.providerAssignedId=:providerAssignedId")})
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NetworkPort extends CloudResource implements Serializable, ICloudProviderResource {
    private static final long serialVersionUID = 1L;

    public static final String GET_NETWORKPORT_BY_PROVIDER_ASSIGNED_ID = "GET_NETWORKPORT_BY_PROVIDER_ASSIGNED_ID";

    public static enum State {
        CREATING, STARTING, STARTED, STOPPING, STOPPED, DELETING, DELETED, ERROR
    }

    private CloudProviderAccount cloudProviderAccount;

    private CloudProviderLocation location;

    private State state;

    private Network network;

    private String portType;

    private String classOfService;

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public Network getNetwork() {
        return this.network;
    }

    public void setNetwork(final Network network) {
        this.network = network;
    }

    public String getPortType() {
        return this.portType;
    }

    public void setPortType(final String portType) {
        this.portType = portType;
    }

    public String getClassOfService() {
        return this.classOfService;
    }

    public void setClassOfService(final String classOfService) {
        this.classOfService = classOfService;
    }

}
