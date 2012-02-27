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
 *  $Id: CloudProviderAccount.java 788 2011-12-21 11:49:55Z dangtran $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class CloudProviderAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String login;

    private String password;
    
    private Set<User> users;

    private Set<Machine> machines;

    private Set<Volume> volumes;

    private Set<MachineImage> images;

    private CloudProvider cloudProvider;

    public CloudProviderAccount() {
        this.machines = new HashSet<Machine>();
        this.volumes = new HashSet<Volume>();
        this.images = new HashSet<MachineImage>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @OneToMany(mappedBy = "cloudProviderAccount")
    public Set<Machine> getMachines() {
        return this.machines;
    }

    public void setMachines(final Set<Machine> machines) {
        this.machines = machines;
    }

    @OneToMany(mappedBy = "cloudProviderAccount")
    public Set<Volume> getVolumes() {
        return this.volumes;
    }

    public void setVolumes(final Set<Volume> volumes) {
        this.volumes = volumes;
    }

    @OneToMany(mappedBy = "cloudProviderAccount")
    public Set<MachineImage> getImages() {
        return this.images;
    }

    public void setImages(final Set<MachineImage> images) {
        this.images = images;
    }

    @ManyToOne
    public CloudProvider getCloudProvider() {
        return this.cloudProvider;
    }

    public void setCloudProvider(final CloudProvider cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    @ManyToMany
	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
