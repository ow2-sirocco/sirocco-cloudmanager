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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class Volume extends Resource<CimiVolume> {
    public static final String TYPE_URI = "http://schemas.dmtf.org/cimi/1/Volume";

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    Volume(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiVolume());
        this.cimiObject.setHref(id);
    }

    Volume(final CimiClient cimiClient, final CimiVolume cimiVolume) {
        super(cimiClient, cimiVolume);
    }

    public State getState() {
        return State.valueOf(this.cimiObject.getState());
    }

    public void setType(final String type) {
        this.cimiObject.setType(type);
    }

    public String getType() {
        return this.cimiObject.getType();
    }

    public int getCapacity() {
        return this.cimiObject.getCapacity();
    }

    public void setCapacity(final int capacity) {
        this.cimiObject.setCapacity(capacity);
    }

    public boolean getBootable() {
        return this.cimiObject.getBootable();
    }

    public void setBootable(final boolean bootable) {
        this.cimiObject.setBootable(bootable);
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

    public static CreateResult<Volume> createVolume(final CimiClient client, final VolumeCreate volumeCreate)
        throws CimiException {
        if (client.cloudEntryPoint.getVolumes() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiVolumeCollection volumeCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumes().getHref()), CimiVolumeCollectionRoot.class, null);
        String addRef = Helper.findOperation("add", volumeCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiVolume> result = client.postCreateRequest(addRef, volumeCreate.cimiVolumeCreate, CimiVolume.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        Volume volume = result.getResource() != null ? new Volume(client, result.getResource()) : null;
        return new CreateResult<Volume>(job, volume);
    }

    public static List<Volume> getVolumes(final CimiClient client, final QueryParams queryParams) throws CimiException {
        if (client.cloudEntryPoint.getVolumes() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiVolumeCollection volumeCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumes().getHref()), CimiVolumeCollectionRoot.class, queryParams);
        List<Volume> result = new ArrayList<Volume>();

        if (volumeCollection.getCollection() != null) {
            for (CimiVolume cimiVolume : volumeCollection.getCollection().getArray()) {
                result.add(new Volume(client, cimiVolume));
            }
        }
        return result;
    }

    public static Volume getVolumeByReference(final CimiClient client, final String ref) throws CimiException {
        return new Volume(client, client.getCimiObjectByReference(ref, CimiVolume.class));
    }

}
