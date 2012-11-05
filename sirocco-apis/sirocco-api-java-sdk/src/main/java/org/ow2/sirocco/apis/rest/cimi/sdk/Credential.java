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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class Credential extends Resource<CimiCredential> {
    public static final String TYPE_URI = "http://schemas.dmtf.org/cimi/1/Credential";

    public Credential() {
        super(null, new CimiCredential());
    }

    Credential(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiCredential());
        this.cimiObject.setHref(id);
    }

    Credential(final CimiClient cimiClient, final CimiCredential cimiObject) {
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
        if (this.cimiObject.getKey() != null) {
            return new String(this.cimiObject.getKey());
        } else {
            return null;
        }
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

    public static CreateResult<Credential> createCredential(final CimiClient client, final CredentialCreate credentialCreate)
        throws CimiException {
        if (client.cloudEntryPoint.getCredentials() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiCredentialCollection credentialCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getCredentials().getHref()), CimiCredentialCollectionRoot.class, null);
        String addRef = Helper.findOperation("add", credentialCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiCredential> result = client.postCreateRequest(addRef, credentialCreate.cimiCredentialsCreate,
            CimiCredential.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        Credential cred = result.getResource() != null ? new Credential(client, result.getResource()) : null;
        return new CreateResult<Credential>(job, cred);
    }

    public static List<Credential> getCredentials(final CimiClient client, final QueryParams queryParams) throws CimiException {
        if (client.cloudEntryPoint.getCredentials() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiCredentialCollection credentialCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getCredentials().getHref()), CimiCredentialCollectionRoot.class,
            queryParams);

        List<Credential> result = new ArrayList<Credential>();

        if (credentialCollection.getCollection() != null) {
            for (CimiCredential cimiCrdential : credentialCollection.getCollection().getArray()) {
                result.add(new Credential(client, cimiCrdential));
            }
        }
        return result;
    }

    public static Credential getCredentialByReference(final CimiClient client, final String ref) throws CimiException {
        return new Credential(client, client.getCimiObjectByReference(ref, CimiCredential.class));
    }

}
