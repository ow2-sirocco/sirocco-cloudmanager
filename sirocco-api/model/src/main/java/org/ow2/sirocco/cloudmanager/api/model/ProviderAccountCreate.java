/**
 *
 * SIROCCO
 * Copyright (C) 2014 Orange
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
package org.ow2.sirocco.cloudmanager.api.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class ProviderAccountCreate implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private String type;

    private String endpoint;

    private Location location;

    private String identity;

    private String credential;

    @JsonProperty
    private Map<String, String> properties;

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

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public String getIdentity() {
        return this.identity;
    }

    public void setIdentity(final String identity) {
        this.identity = identity;
    }

    public String getCredential() {
        return this.credential;
    }

    public void setCredential(final String credential) {
        this.credential = credential;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    public void addProperty(final String key, final String value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, String>();
        }
        this.properties.put(key, value);
    }

}
