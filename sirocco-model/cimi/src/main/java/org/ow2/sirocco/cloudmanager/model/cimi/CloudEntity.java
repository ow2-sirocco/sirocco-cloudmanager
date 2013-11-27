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
import java.util.UUID;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;

/**
 * Base class of CIMI entities that cannot be referenced in a Job
 */
@MappedSuperclass
public abstract class CloudEntity implements Serializable, Resource {

    private static final long serialVersionUID = 1L;

    protected Integer id;

    protected String uuid;

    protected Tenant tenant;

    @Size(max = 255, message = "{resource.name.size}")
    protected String name;

    @Size(max = 255)
    protected String description;

    protected Map<String, String> properties;

    protected Date created;

    protected Date deleted;

    protected Date updated;

    protected String providerAssignedId;

    public String getProviderAssignedId() {
        return this.providerAssignedId;
    }

    public void setProviderAssignedId(final String providerAssignedId) {
        this.providerAssignedId = providerAssignedId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    @PrePersist
    public void generateUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
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

    @ElementCollection(fetch = FetchType.EAGER, targetClass = java.lang.String.class)
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
    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(final Tenant tenant) {
        this.tenant = tenant;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }
}
