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

import org.ow2.sirocco.apis.rest.cimi.domain.ActionType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAction;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystem;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class System extends Resource<CimiSystem> {
    public static enum State {
        CREATING, CREATED, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, MIXED, DELETING, DELETED, ERROR
    }

    public System(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiSystem());
        this.cimiObject.setHref(id);
    }

    System(final CimiClient cimiClient, final CimiSystem cimiSystem) {
        super(cimiClient, cimiSystem);
    }

    public CimiSystem getCimiSystem() {
        return this.cimiObject;
    }

    public State getState() {
        return State.valueOf(this.cimiObject.getState());
    }

    public Job start() throws CimiException {
        CimiAction actionStart = new CimiAction();
        actionStart.setAction(ActionType.START.getPath());
        CimiJob cimiObject = this.cimiClient.postRequest(this.cimiClient.extractPath(this.getId()), actionStart, CimiJob.class);
        return new Job(this.cimiClient, cimiObject);
    }

    public Job stop() throws CimiException {
        CimiAction actionStart = new CimiAction();
        actionStart.setAction(ActionType.STOP.getPath());
        CimiJob cimiObject = this.cimiClient.postRequest(this.cimiClient.extractPath(this.getId()), actionStart, CimiJob.class);
        return new Job(this.cimiClient, cimiObject);
    }

    public Job delete() throws CimiException {
        CimiJob cimiObject = this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()), CimiJob.class);
        return new Job(this.cimiClient, cimiObject);
    }

    public static Job createSystem(final CimiClient client, final SystemCreate systemCreate) throws CimiException {
        CimiJob cimiObject = client.postRequest(ConstantsPath.SYSTEM_PATH, systemCreate.cimiSystemCreate, CimiJob.class);
        return new Job(client, cimiObject);
    }

    public static List<System> getSystems(final CimiClient client, final int first, final int last,
        final String... filterExpression) throws CimiException {
        CimiSystemCollection systemCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getSystems().getHref()), CimiSystemCollectionRoot.class, first, last,
            filterExpression);
        List<System> result = new ArrayList<System>();

        if (systemCollection.getCollection() != null) {
            for (CimiSystem cimiSystem : systemCollection.getCollection().getArray()) {
                result.add(new System(client, cimiSystem));
            }
        }
        return result;
    }

    public static System getSystemByReference(final CimiClient client, final String ref) throws CimiException {
        return new System(client, client.getCimiObjectByReference(ref, CimiSystem.class));
    }

    public static System getSystemById(final CimiClient client, final String id) throws Exception {
        String path = client.getSystemsPath() + "/" + id;
        return new System(client, client.getCimiObjectByReference(path, CimiSystem.class));
    }

}
