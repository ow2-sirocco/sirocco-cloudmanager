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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CollectionOfElements;

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

    private String cloudProviderType;

    private String description;

    private String endpoint;

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

    @CollectionOfElements(fetch = FetchType.EAGER)
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

    @OneToMany(mappedBy = "cloudProvider")
    public Set<CloudProviderAccount> getCloudProviderAccounts() {
        return this.cloudProviderAccounts;
    }

    public void setCloudProviderAccounts(final Set<CloudProviderAccount> cloudProviderAccounts) {
        this.cloudProviderAccounts = cloudProviderAccounts;
    }

    @ManyToMany(mappedBy = "cloudProviders")
    public Set<CloudProviderLocation> getCloudProviderLocations() {
        return this.cloudProviderLocations;
    }

    public void setCloudProviderLocations(final Set<CloudProviderLocation> cloudProviderLocations) {
        this.cloudProviderLocations = cloudProviderLocations;
    }

}
