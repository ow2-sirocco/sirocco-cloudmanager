/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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
 */
package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

/**
 * Subnet abstraction
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Subnet.findByProviderAssignedId", query = "SELECT s FROM Subnet s WHERE s.providerAssignedId=:providerAssignedId"),
    @NamedQuery(name = "Subnet.findByUuid", query = "SELECT s from Subnet s WHERE s.uuid=:uuid")})
public class Subnet extends CloudResource implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    private CloudProviderAccount cloudProviderAccount;

    private CloudProviderLocation location;

    private State state;

    private Network owner;

    private String cidr;

    private String protocol = "IPv4";

    private boolean enableDhcp = true;

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

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    @ManyToOne
    public Network getOwner() {
        return this.owner;
    }

    public void setOwner(final Network owner) {
        this.owner = owner;
    }

    public String getCidr() {
        return this.cidr;
    }

    public void setCidr(final String cidr) {
        this.cidr = cidr;
    }

    public boolean isEnableDhcp() {
        return this.enableDhcp;
    }

    public void setEnableDhcp(final boolean enableDhcp) {
        this.enableDhcp = enableDhcp;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "Subnet [name=" + this.name + ", providerAssignedId=" + this.providerAssignedId + ", cidr=" + this.cidr
            + ", protocol=" + this.protocol + ", enableDhcp=" + this.enableDhcp + "]";
    }

}
