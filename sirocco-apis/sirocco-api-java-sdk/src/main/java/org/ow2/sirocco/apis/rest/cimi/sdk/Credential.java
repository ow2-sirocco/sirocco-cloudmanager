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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCollection;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class Credential extends Resource<CimiCredentials> {

    public Credential() {
        super(null, new CimiCredentials());
    }

    public Credential(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiCredentials());
        this.cimiObject.setHref(id);
        this.cimiObject.setId(id);
    }

    Credential(final CimiClient cimiClient, final CimiCredentials cimiObject) {
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

    public static Credential createCredential(final CimiClient client, final CredentialCreate credentialCreate)
        throws CimiException {
        CimiCredentials cimiObject = client.postRequest(ConstantsPath.CREDENTIALS_PATH, credentialCreate.cimiCredentialsCreate,
            CimiCredentials.class);
        return new Credential(client, cimiObject);
    }

    public static List<Credential> getCredentials(final CimiClient client) throws CimiException {
        CimiCredentialsCollection volumeConfigCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getCredentials().getHref()), CimiCredentialsCollection.class);

        List<Credential> result = new ArrayList<Credential>();

        if (volumeConfigCollection.getCollection() != null) {
            for (CimiCredentials cimiVolumeConfig : volumeConfigCollection.getCollection().getArray()) {
                result.add(Credential.getCredentialByReference(client, cimiVolumeConfig.getHref()));
            }
        }
        return result;
    }

    public static Credential getCredentialByReference(final CimiClient client, final String ref) throws CimiException {
        return new Credential(client, client.getCimiObjectByReference(ref, CimiCredentials.class));
    }

    public static Credential getCredentialById(final CimiClient client, final String id) throws CimiException {
        String path = client.getCredentialsPath() + "/" + id;
        return new Credential(client, client.getCimiObjectByReference(path, CimiCredentials.class));
    }

}
