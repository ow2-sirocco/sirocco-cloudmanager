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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineVolumeCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class MachineVolume extends Resource<CimiMachineVolume> {
    public static final String TYPE_URI = "http://schemas.dmtf.org/cimi/1/MachineVolume";

    public MachineVolume() {
        super(null, new CimiMachineVolume());
    }

    MachineVolume(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiMachineVolume());
        this.cimiObject.setHref(id);
    }

    MachineVolume(final CimiClient cimiClient, final CimiMachineVolume cimiMachineVolume) {
        super(cimiClient, cimiMachineVolume);
    }

    public Volume getVolume() {
        return new Volume(this.cimiClient, this.cimiObject.getVolume());
    }

    public String getInitialLocation() {
        return this.cimiObject.getInitialLocation();
    }

    public void setVolumeRef(final String volumeRef) {
        CimiVolume vol = new CimiVolume();
        vol.setHref(volumeRef);
        this.cimiObject.setVolume(vol);
    }

    public void setInitialLocation(final String initialLocation) {
        this.cimiObject.setInitialLocation(initialLocation);
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

    public static CreateResult<MachineVolume> createMachineVolume(final CimiClient client, final String machineId,
        final MachineVolume machineVolume) throws CimiException {
        Machine machine = Machine.getMachineByReference(client, machineId, QueryParams.build().setExpand("volumes"));
        String addRef = Helper.findOperation("add", machine.cimiObject.getVolumes());
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiMachineVolume> result = client.postCreateRequest(addRef, machineVolume.cimiObject,
            CimiMachineVolume.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        MachineVolume createdMachineVolume = result.getResource() != null ? new MachineVolume(client, result.getResource())
            : null;
        return new CreateResult<MachineVolume>(job, createdMachineVolume);
    }

    public static List<MachineVolume> getMachineVolumes(final CimiClient client, final String machineId,
        final QueryParams queryParams) throws CimiException {
        Machine machine = Machine.getMachineByReference(client, machineId);
        if (machine.cimiObject.getVolumes() == null) {
            throw new CimiException("Unsupported operation");
        }

        CimiMachineVolumeCollection machineVolumeCollection = client.getRequest(
            client.extractPath(machine.cimiObject.getVolumes().getHref()), CimiMachineVolumeCollectionRoot.class,
            queryParams.setExpand("volume"));
        List<MachineVolume> result = new ArrayList<MachineVolume>();

        if (machineVolumeCollection.getCollection() != null) {
            for (CimiMachineVolume cimiMachineVolume : machineVolumeCollection.getCollection().getArray()) {
                result.add(new MachineVolume(client, cimiMachineVolume));
            }
        }
        return result;
    }

    public static MachineVolume getMachineVolumeByReference(final CimiClient client, final String ref) throws CimiException {
        return new MachineVolume(client, client.getCimiObjectByReference(ref, CimiMachineVolume.class, QueryParams.build()
            .setExpand("volume")));
    }

}
