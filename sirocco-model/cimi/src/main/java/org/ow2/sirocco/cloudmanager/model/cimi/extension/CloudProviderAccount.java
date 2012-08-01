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

package org.ow2.sirocco.cloudmanager.model.cimi.extension;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CollectionOfElements;

@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CloudProviderAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String login;

    private String password;

    private Map<String, String> properties;

    private Set<User> users;

    private CloudProvider cloudProvider;

    public CloudProviderAccount() {
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

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    @CollectionOfElements(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public CloudProvider getCloudProvider() {
        return this.cloudProvider;
    }

    public void setCloudProvider(final CloudProvider cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(final Set<User> users) {
        this.users = users;
    }

}
