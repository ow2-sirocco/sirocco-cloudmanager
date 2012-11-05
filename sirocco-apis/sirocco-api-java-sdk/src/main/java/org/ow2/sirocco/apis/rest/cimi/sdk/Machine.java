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
import java.util.Map;
import java.util.Map.Entry;

import org.ow2.sirocco.apis.rest.cimi.domain.ActionType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAction;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAddress;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineNetworkInterface;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineNetworkInterfaceAddress;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineNetworkInterfaceAddressCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineNetworkInterfaceCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class Machine extends Resource<CimiMachine> {
    public static final String TYPE_URI = "http://schemas.dmtf.org/cimi/1/Machine";

    public static enum State {
        CREATING, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, DELETING, DELETED, ERROR
    }

    Machine(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiMachine());
        this.cimiObject.setHref(id);
    }

    Machine(final CimiClient cimiClient, final CimiMachine cimiMachine) {
        super(cimiClient, cimiMachine);
    }

    public CimiMachine getCimiMachine() {
        return this.cimiObject;
    }

    public State getState() {
        return State.valueOf(this.cimiObject.getState());
    }

    public int getCpu() {
        return this.cimiObject.getCpu();
    }

    public int getMemory() {
        return this.cimiObject.getMemory();
    }

    public List<NetworkInterface> getNetworkInterface() throws CimiException {
        List<NetworkInterface> nics = new ArrayList<NetworkInterface>();
        if (this.cimiObject.getNetworkInterfaces() != null && this.cimiObject.getNetworkInterfaces().getArray() != null) {
            for (CimiMachineNetworkInterface cimiNic : this.cimiObject.getNetworkInterfaces().getArray()) {
                if (cimiNic.getAddresses().getArray() == null) {
                    CimiMachineNetworkInterfaceAddressCollectionRoot addresses = this.cimiClient.getRequest(
                        this.cimiClient.extractPath(cimiNic.getAddresses().getHref()),
                        CimiMachineNetworkInterfaceAddressCollectionRoot.class, null);
                    cimiNic.setAddresses(addresses);
                    if (addresses.getArray() != null) {
                        for (CimiMachineNetworkInterfaceAddress nicAddr : addresses.getArray()) {
                            CimiAddress cimiAddress = this.cimiClient.getCimiObjectByReference(nicAddr.getAddress().getHref(),
                                CimiAddress.class);
                            nicAddr.setAddress(cimiAddress);
                        }
                    }
                }
                String ip = "";
                if (cimiNic.getAddresses().getArray() != null && cimiNic.getAddresses().getArray().length > 0) {
                    ip = cimiNic.getAddresses().getArray()[0].getAddress().getIp();
                }
                NetworkInterface nic = new NetworkInterface(
                    cimiNic.getNetworkType().equalsIgnoreCase("public") ? NetworkInterface.Type.PUBLIC
                        : NetworkInterface.Type.PRIVATE, ip);
                nics.add(nic);
            }
        }
        return nics;
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

    public static CreateResult<Machine> createMachine(final CimiClient client, final MachineCreate machineCreate)
        throws CimiException {
        if (client.cloudEntryPoint.getMachines() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiMachineCollection machinesCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachines().getHref()), CimiMachineCollectionRoot.class, null);
        String addRef = Helper.findOperation("add", machinesCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiMachine> result = client.postCreateRequest(addRef, machineCreate.cimiMachineCreate, CimiMachine.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        Machine machine = result.getResource() != null ? new Machine(client, result.getResource()) : null;
        return new CreateResult<Machine>(job, machine);
    }

    public static UpdateResult<Machine> updateMachine(final CimiClient client, final String id,
        final Map<String, Object> attributeValues) throws CimiException {
        CimiMachine cimiObject = new CimiMachine();
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> entry : attributeValues.entrySet()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            String attribute = entry.getKey();
            sb.append(attribute);
            if (attribute.equals("name")) {
                cimiObject.setName((String) entry.getValue());
            } else if (attribute.equals("description")) {
                cimiObject.setDescription((String) entry.getValue());
            } else if (attribute.equals("properties")) {
                cimiObject.setProperties((Map<String, String>) entry.getValue());
            }
        }
        CimiResult<CimiMachine> cimiResult = client.partialUpdateRequest(id, cimiObject, sb.toString());
        Job job = cimiResult.getJob() != null ? new Job(client, cimiResult.getJob()) : null;
        Machine machineConfig = cimiResult.getResource() != null ? new Machine(client, cimiResult.getResource()) : null;
        return new UpdateResult<Machine>(job, machineConfig);
    }

    public static List<Machine> getMachines(final CimiClient client, final QueryParams queryParams) throws CimiException {
        if (client.cloudEntryPoint.getMachines() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiMachineCollection machinesCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachines().getHref()), CimiMachineCollectionRoot.class, queryParams);
        List<Machine> result = new ArrayList<Machine>();

        if (machinesCollection.getCollection() != null) {
            for (CimiMachine cimiMachine : machinesCollection.getCollection().getArray()) {
                result.add(new Machine(client, cimiMachine));
            }
        }
        return result;
    }

    public static Machine getMachineByReference(final CimiClient client, final String ref, final QueryParams queryParams)
        throws CimiException {
        Machine result = new Machine(client, client.getCimiObjectByReference(ref, CimiMachine.class, queryParams));
        if (result.cimiObject.getNetworkInterfaces() != null) {
            String machineNicsRef = result.cimiObject.getNetworkInterfaces().getHref();
            CimiMachineNetworkInterfaceCollectionRoot nics = client.getRequest(client.extractPath(machineNicsRef),
                CimiMachineNetworkInterfaceCollectionRoot.class, null);
            result.cimiObject.getNetworkInterfaces().setArray(nics.getArray());
        }
        return result;
    }

    public static Machine getMachineByReference(final CimiClient client, final String ref) throws CimiException {
        return Machine.getMachineByReference(client, ref, null);
    }

}
