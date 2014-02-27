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
package org.ow2.sirocco.cloudmanager.model.cimi.extension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;

@Entity
@NamedQueries({@NamedQuery(name = "SecurityGroup.findByUuid", query = "SELECT s from SecurityGroup s WHERE s.uuid=:uuid"),
    @NamedQuery(name = "SecurityGroup.findByName", query = "SELECT s from SecurityGroup s WHERE s.name=:name")})
public class SecurityGroup extends CloudResource implements Serializable, ICloudProviderResource {
    private static final long serialVersionUID = 1L;

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    private CloudProviderAccount cloudProviderAccount;

    private CloudProviderLocation location;

    private State state;

    private List<SecurityGroupRule> rules;

    private List<Machine> members;

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

    @OneToMany(mappedBy = "parentGroup", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    public List<SecurityGroupRule> getRules() {
    	if (this.rules == null){
            this.rules = new ArrayList<SecurityGroupRule>();
    	}
    		return this.rules; 
    }

    public void setRules(final List<SecurityGroupRule> rules) {
        this.rules = rules;
    }

    @ManyToMany(mappedBy = "securityGroups")
    public List<Machine> getMembers() {
        if (this.members == null) {
            this.members = new ArrayList<Machine>();
        }
        return this.members;
    }

    public void setMembers(final List<Machine> members) {
        this.members = members;
    }

}
