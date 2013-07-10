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
 *
 */
package org.ow2.sirocco.cloudmanager.api.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonPropertyOrder({"id", "href", "name", "description", "created", "updated", "properties", "apiType", "apiVersion",
    "endpoint", "locations"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class Provider extends CommonAttributes {

    private String api;

    private String apiVersion;

    private String endpoint;

    private List<Location> locations;

    public String getApi() {
        return this.api;
    }

    public void setApi(final String api) {
        this.api = api;
    }

    public String getApiVersion() {
        return this.apiVersion;
    }

    public void setApiVersion(final String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(final List<Location> locations) {
        this.locations = locations;
    }

    public static class Collection {
        private List<Provider> providers;

        public List<Provider> getProviders() {
            return this.providers;
        }

        public void setProviders(final List<Provider> providers) {
            this.providers = providers;
        }
    }
}
