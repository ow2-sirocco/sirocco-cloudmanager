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

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
public class ProviderProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String type;

    private String connectorClass;

    private List<AccountParameter> accountParameters;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getConnectorClass() {
        return this.connectorClass;
    }

    public void setConnectorClass(final String connectorClass) {
        this.connectorClass = connectorClass;
    }

    public List<AccountParameter> getAccountParameters() {
        return this.accountParameters;
    }

    public void setAccountParameters(final List<AccountParameter> accountParameters) {
        this.accountParameters = accountParameters;
    }

    public static class AccountParameter implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;

        private String alias = "";

        private String description = "";

        private String type = "String";

        private boolean required = true;

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getAlias() {
            return this.alias;
        }

        public void setAlias(final String alias) {
            this.alias = alias;
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

        public boolean isRequired() {
            return this.required;
        }

        public void setRequired(final boolean required) {
            this.required = required;
        }
    }

    public static class Collection {
        private List<ProviderProfile> providerProfiles;

        public List<ProviderProfile> getProviderProfiles() {
            return this.providerProfiles;
        }

        public void setProviderProfiles(final List<ProviderProfile> providerProfiles) {
            this.providerProfiles = providerProfiles;
        }
    }

}
