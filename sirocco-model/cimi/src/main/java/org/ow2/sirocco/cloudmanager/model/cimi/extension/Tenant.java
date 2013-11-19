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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Container used to isolate resources at broker level
 */
@Entity
public class Tenant implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String uuid = UUID.randomUUID().toString();

    private String name;

    private String description;

    private Date created;

    private Date updated;

    private Map<String, String> properties;

    private List<User> users;

    private Set<CloudProviderAccount> cloudProviderAccounts;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    @ElementCollection(fetch = FetchType.EAGER, targetClass = java.lang.String.class)
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return this.created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdated() {
        return this.updated;
    }

    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    public void setUsers(final List<User> users) {
        this.users = users;
    }

    @ManyToMany(mappedBy = "tenants", fetch = FetchType.EAGER)
    public List<User> getUsers() {
        return this.users;
    }

    @ManyToMany(mappedBy = "tenants", fetch = FetchType.EAGER)
    public Set<CloudProviderAccount> getCloudProviderAccounts() {
        return this.cloudProviderAccounts;
    }

    public void setCloudProviderAccounts(final Set<CloudProviderAccount> cloudProviderAccounts) {
        this.cloudProviderAccounts = cloudProviderAccounts;
    }

}
