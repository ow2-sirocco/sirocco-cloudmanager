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
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class CredentialTemplate extends Resource<CimiCredentialTemplate> {
    public CredentialTemplate() {
        super(null, new CimiCredentialTemplate());
    }

    public CredentialTemplate(final CimiClient cimiClient, final String id) {
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

    public void delete() throws CimiException {
        this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()));
    }

    public static CredentialTemplate createCredentialTemplate(final CimiClient client,
        final CredentialTemplate credentialTemplate) throws CimiException {
        CimiCredentialTemplate cimiObject = client.postRequest(ConstantsPath.CREDENTIAL_TEMPLATE_PATH,
            credentialTemplate.cimiObject, CimiCredentialTemplate.class);
        return new CredentialTemplate(client, cimiObject);
    }

    public static List<CredentialTemplate> getCredentialTemplates(final CimiClient client, final int first, final int last,
        final String... filterExpression) throws CimiException {
        if (client.cloudEntryPoint.getCredentialTemplates() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiCredentialTemplateCollection credentialTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getCredentialTemplates().getHref()),
            CimiCredentialTemplateCollectionRoot.class, first, last, null, filterExpression);

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

    public static CredentialTemplate getCredentialTemplateById(final CimiClient client, final String id) throws CimiException {
        String path = client.getCredentialTemplatesPath() + "/" + id;
        return new CredentialTemplate(client, client.getCimiObjectByReference(path, CimiCredentialTemplate.class));
    }

}
