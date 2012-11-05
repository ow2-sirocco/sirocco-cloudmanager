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

import java.util.ArrayList;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class CredentialTemplate extends Resource<CimiCredentialTemplate> {
    public CredentialTemplate() {
        super(null, new CimiCredentialTemplate());
    }

    CredentialTemplate(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiCredentialTemplate());
        this.cimiObject.setHref(id);
    }

    public CredentialTemplate(final CimiCredentialTemplate cimiObject) {
        super(null, cimiObject);
    }

    public CredentialTemplate(final CimiClient cimiClient, final CimiCredentialTemplate cimiObject) {
        super(cimiClient, cimiObject);
    }

    public String getUserName() {
        return this.cimiObject.getUserName();
    }

    public void setUserName(final String userName) {
        this.cimiObject.setUserName(userName);
    }

    public String getPassword() {
        return this.cimiObject.getPassword();
    }

    public void setPassword(final String password) {
        this.cimiObject.setPassword(password);
    }

    public String getPublicKey() {
        return new String(this.cimiObject.getKey());
    }

    public void setPublicKey(final String key) {
        this.cimiObject.setKey(key.getBytes());
    }

    public Job delete() throws CimiException {
        String deleteRef = Helper.findOperation("delete", this.cimiObject);
        if (deleteRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiJob job = this.cimiClient.deleteRequest(deleteRef);
        if (job != null) {
            return new Job(this.cimiClient, job);
        } else {
            return null;
        }
    }

    public static CreateResult<CredentialTemplate> createCredentialTemplate(final CimiClient client,
        final CredentialTemplate credentialTemplate) throws CimiException {
        if (client.cloudEntryPoint.getCredentialTemplates() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiCredentialTemplateCollection credentialTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getCredentialTemplates().getHref()),
            CimiCredentialTemplateCollectionRoot.class, null);
        String addRef = Helper.findOperation("add", credentialTemplateCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiCredentialTemplate> result = client.postCreateRequest(addRef, credentialTemplate.cimiObject,
            CimiCredentialTemplate.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        CredentialTemplate credTemplate = result.getResource() != null ? new CredentialTemplate(client, result.getResource())
            : null;
        return new CreateResult<CredentialTemplate>(job, credTemplate);
    }

    public static List<CredentialTemplate> getCredentialTemplates(final CimiClient client, final QueryParams queryParams)
        throws CimiException {
        if (client.cloudEntryPoint.getCredentialTemplates() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiCredentialTemplateCollection credentialTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getCredentialTemplates().getHref()),
            CimiCredentialTemplateCollectionRoot.class, queryParams);

        List<CredentialTemplate> result = new ArrayList<CredentialTemplate>();

        if (credentialTemplateCollection.getCollection() != null) {
            for (CimiCredentialTemplate cimiCredentialTemplate : credentialTemplateCollection.getCollection().getArray()) {
                result.add(CredentialTemplate.getCredentialTemplateByReference(client, cimiCredentialTemplate.getHref()));
            }
        }
        return result;
    }

    public static CredentialTemplate getCredentialTemplateByReference(final CimiClient client, final String ref)
        throws CimiException {
        return new CredentialTemplate(client, client.getCimiObjectByReference(ref, CimiCredentialTemplate.class));
    }

}
