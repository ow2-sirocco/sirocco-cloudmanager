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
 *  $Id: CloudEntity.java 897 2012-02-15 10:13:25Z ycas7461 $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@MappedSuperclass
// @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public abstract class CloudEntity implements Serializable, Resource {

    private static final long serialVersionUID = 1L;

    protected Integer id;

    protected User user;

    protected String name;

    protected String description;

    protected Date created;

    protected Date deleted;

    protected Date updated;

    protected String providerAssignedId;

    /*protected long versionNum;

    @Version
    @Column(name = "OPTLOCK")
    protected long getVersionNum() {
        return this.versionNum;
    }

    protected void setVersionNum(final long versionNum) {
        this.versionNum = versionNum;
    }*/

    public String getProviderAssignedId() {
        return this.providerAssignedId;
    }

    public void setProviderAssignedId(final String providerAssignedId) {
        this.providerAssignedId = providerAssignedId;
    }

    // @ManyToMany
    // public Collection<CloudProvider> getCloudProviders() {
    // return this.cloudProviders;
    // }

    // public void setCloudProviders(final Collection<CloudProvider>
    // cloudProviders) {
    // this.cloudProviders = cloudProviders;
    // }

    protected Map<String, String> properties;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return this.created;
    }

    public String getName() {
        return this.name;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    @OneToMany(fetch = FetchType.EAGER)
    // @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setDeleted(final Date deleted) {
        this.deleted = deleted;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getDeleted() {
        return this.deleted;
    }

    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdated() {
        return this.updated;
    }

    @ManyToOne
    // //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }
}
