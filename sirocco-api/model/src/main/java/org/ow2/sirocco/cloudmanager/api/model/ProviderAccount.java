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

@JsonPropertyOrder({"id", "href", "name", "description", "created", "updated", "properties", "providerId", "identity",
    "credential"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class ProviderAccount extends CommonAttributes {
    private String providerId;

    private String identity;

    private String credential;

    public String getProviderId() {
        return this.providerId;
    }

    public void setProviderId(final String providerId) {
        this.providerId = providerId;
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

    public static class Collection {
        private List<ProviderAccount> providerAccounts;

        public List<ProviderAccount> getProviderAccounts() {
            return this.providerAccounts;
        }

        public void setProviderAccounts(final List<ProviderAccount> providerAccounts) {
            this.providerAccounts = providerAccounts;
        }
    }
}
