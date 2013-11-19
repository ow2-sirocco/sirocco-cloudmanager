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
 *  $Id: CloudProvider.java 788 2011-12-21 11:49:55Z dangtran $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi.extension;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A CloudProvider can provision compute resources (virtual machines along with
 * their network and storage resources). A CloudProvider can represent a private
 * cloud (e.g. an OpenStack instance available within a corporate network) or a
 * public cloud (e.g. Amazon EC2).
 */
@Entity
public class CloudProvider implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String uuid = UUID.randomUUID().toString();

    private Date created;

    private String cloudProviderType;

    private String description;

    private String endpoint;

    private Boolean enabled = true;

    private Map<String, String> properties;

    private Set<CloudProviderAccount> cloudProviderAccounts;

    private Set<CloudProviderLocation> cloudProviderLocations;

    public CloudProvider() {
        this.cloudProviderAccounts = new HashSet<CloudProviderAccount>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return this.created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public String getCloudProviderType() {
        return this.cloudProviderType;
    }

    public void setCloudProviderType(final String cloudProviderType) {
        this.cloudProviderType = cloudProviderType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    @ElementCollection(fetch = FetchType.EAGER, targetClass = java.lang.String.class)
    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(final String endPoint) {
        this.endpoint = endPoint;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    @OneToMany(mappedBy = "cloudProvider", fetch = FetchType.EAGER)
    public Set<CloudProviderAccount> getCloudProviderAccounts() {
        return this.cloudProviderAccounts;
    }

    public void setCloudProviderAccounts(final Set<CloudProviderAccount> cloudProviderAccounts) {
        this.cloudProviderAccounts = cloudProviderAccounts;
    }

    @ManyToMany(mappedBy = "cloudProviders", fetch = FetchType.EAGER)
    public Set<CloudProviderLocation> getCloudProviderLocations() {
        if (this.cloudProviderLocations == null) {
            this.cloudProviderLocations = new HashSet<>();
        }
        return this.cloudProviderLocations;
    }

    public void setCloudProviderLocations(final Set<CloudProviderLocation> cloudProviderLocations) {
        this.cloudProviderLocations = cloudProviderLocations;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

}
