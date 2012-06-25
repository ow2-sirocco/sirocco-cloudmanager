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
 *  $Id$
 *
 */

package org.ow2.sirocco.apis.rest.cimi.sdk;

import java.util.HashMap;
import java.util.Map;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;

public class CredentialCreate {
    CimiCredentialsCreate cimiCredentialsCreate;

    private CredentialTemplate credentialTemplate;

    public CredentialCreate() {
        this.cimiCredentialsCreate = new CimiCredentialsCreate();
    }

    public String getName() {
        return this.cimiCredentialsCreate.getName();
    }

    public void setName(final String name) {
        this.cimiCredentialsCreate.setName(name);
    }

    public String getDescription() {
        return this.cimiCredentialsCreate.getDescription();
    }

    public void setDescription(final String description) {
        this.cimiCredentialsCreate.setDescription(description);
    }

    public Map<String, String> getProperties() {
        return this.cimiCredentialsCreate.getProperties();
    }

    public void setProperties(final Map<String, String> properties) {
        this.cimiCredentialsCreate.setProperties(properties);
    }

    public void addProperty(final String key, final String value) {
        if (this.cimiCredentialsCreate.getProperties() == null) {
            this.cimiCredentialsCreate.setProperties(new HashMap<String, String>());
        }
        this.cimiCredentialsCreate.getProperties().put(key, value);
    }

    public CredentialTemplate getCredentialTemplate() {
        return this.credentialTemplate;
    }

    public void setCredentialTemplate(final CredentialTemplate credentialTemplate) {
        this.credentialTemplate = credentialTemplate;
        this.cimiCredentialsCreate.setCredentialsTemplate(credentialTemplate.cimiObject);
    }

}
