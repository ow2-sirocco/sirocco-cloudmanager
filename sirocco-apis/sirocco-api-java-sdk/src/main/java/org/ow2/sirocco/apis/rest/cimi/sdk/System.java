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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemMachineCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class System extends Resource<CimiSystem> {
    public static enum State {
        CREATING, CREATED, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, MIXED, DELETING, DELETED, ERROR
    }

    System(final CimiClient cimiClient, final String id) {
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
        String startRef = Helper.findOperation(ActionType.START.getPath(), this.cimiObject);
        if (startRef == null) {
            throw new CimiException("Illegal operation");
        }
        CimiAction actionStart = new CimiAction();
        actionStart.setAction(ActionType.START.getPath());
        CimiJob cimiJob = this.cimiClient.actionRequest(startRef, actionStart);
        if (cimiJob != null) {
            return new Job(this.cimiClient, cimiJob);
        } else {
            return null;
        }
    }

    public Job stop() throws CimiException {
        String stopRef = Helper.findOperation(ActionType.STOP.getPath(), this.cimiObject);
        if (stopRef == null) {
            throw new CimiException("Illegal operation");
        }
        CimiAction actionStop = new CimiAction();
        actionStop.setAction(ActionType.STOP.getPath());
        CimiJob cimiJob = this.cimiClient.actionRequest(stopRef, actionStop);
        if (cimiJob != null) {
            return new Job(this.cimiClient, cimiJob);
        } else {
            return null;
        }
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

    public List<SystemMachine> getMachines() throws CimiException {
        String systemMachineCollection = this.cimiObject.getMachines().getHref();
        CimiSystemMachineCollectionRoot sysMachines = this.cimiClient.getRequest(this.cimiClient
            .extractPath(systemMachineCollection), CimiSystemMachineCollectionRoot.class,
            QueryParams.build().setExpand("machine"));
        this.cimiObject.getMachines().setArray(sysMachines.getArray());
        List<SystemMachine> machines = new ArrayList<SystemMachine>();
        if (this.cimiObject.getMachines().getArray() != null) {
            for (CimiSystemMachine cimiSystemMachine : this.cimiObject.getMachines().getArray()) {
                SystemMachine systemMachine = new SystemMachine(this.cimiClient, cimiSystemMachine);
                machines.add(systemMachine);
            }
        }
        return machines;

    }

    public static CreateResult<System> createSystem(final CimiClient client, final SystemCreate systemCreate)
        throws CimiException {
        if (client.cloudEntryPoint.getSystems() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiSystemCollection systemCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getSystems().getHref()), CimiSystemCollectionRoot.class, null);
        String addRef = Helper.findOperation("add", systemCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiSystem> result = client.postCreateRequest(addRef, systemCreate.cimiSystemCreate, CimiSystem.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        System system = result.getResource() != null ? new System(client, result.getResource()) : null;
        return new CreateResult<System>(job, system);
    }

    public static List<System> getSystems(final CimiClient client, final QueryParams queryParams) throws CimiException {
        if (client.cloudEntryPoint.getSystems() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiSystemCollection systemCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getSystems().getHref()), CimiSystemCollectionRoot.class, queryParams);
        List<System> result = new ArrayList<System>();

        if (systemCollection.getCollection() != null) {
            for (CimiSystem cimiSystem : systemCollection.getCollection().getArray()) {
                result.add(new System(client, cimiSystem));
            }
        }
        return result;
    }

    public static System getSystemByReference(final CimiClient client, final String ref) throws CimiException {
        System result = new System(client, client.getCimiObjectByReference(ref, CimiSystem.class, QueryParams.build()
            .setExpand("machines")));
        return result;
    }

    public static System getSystemByReference(final CimiClient client, final String ref, final QueryParams queryParams)
        throws CimiException {
        System result = new System(client, client.getCimiObjectByReference(ref, CimiSystem.class, queryParams));
        return result;
    }

}
