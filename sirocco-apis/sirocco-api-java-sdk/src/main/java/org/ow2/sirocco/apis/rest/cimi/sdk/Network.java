/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetwork;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiNetworkCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiNetworkCollectionRoot;

public class Network extends Resource<CimiNetwork> {
    public static final String TYPE_URI = "http://schemas.dmtf.org/cimi/1/Network";

    public static enum State {
        CREATING, STARTING, STARTED, STOPPING, STOPPED, DELETING, DELETED, ERROR
    }

    public Network(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiNetwork());
        this.cimiObject.setHref(id);
    }

    Network(final CimiClient cimiClient, final CimiNetwork cimiNetwork) {
        super(cimiClient, cimiNetwork);
    }

    public State getState() {
        return State.valueOf(this.cimiObject.getState());
    }

    public String getNetworkType() {
        return this.cimiObject.getNetworkType();
    }

    public void setNetworkType(final String networkType) {
        this.cimiObject.setNetworkType(networkType);
    }

    public static List<Network> getNetworks(final CimiClient client, final int first, final int last,
        final String... filterExpression) throws CimiException {
        if (client.cloudEntryPoint.getNetworks() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiNetworkCollection addressCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getNetworks().getHref()), CimiNetworkCollectionRoot.class, first, last,
            null, filterExpression);
        List<Network> result = new ArrayList<Network>();

        if (addressCollection.getCollection() != null) {
            for (CimiNetwork cimiNetwork : addressCollection.getCollection().getArray()) {
                result.add(new Network(client, cimiNetwork));
            }
        }
        return result;
    }

    public static Network getNetworkById(final CimiClient client, final String id) throws CimiException {
        return new Network(client, client.getCimiObjectByReference(id, CimiNetwork.class));
    }
}