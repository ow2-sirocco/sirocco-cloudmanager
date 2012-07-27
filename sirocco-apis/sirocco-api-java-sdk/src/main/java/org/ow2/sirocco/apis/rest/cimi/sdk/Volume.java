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
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class Volume extends Resource<CimiVolume> {
    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    public Volume(final CimiClient cimiClient, final String id) {
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
        CimiJob cimiObject = this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()), CimiJob.class);
        return new Job(this.cimiClient, cimiObject);
    }

    public static Job createVolume(final CimiClient client, final VolumeCreate volumeCreate) throws CimiException {
        CimiJob cimiObject = client.postRequest(ConstantsPath.VOLUME_PATH, volumeCreate.cimiVolumeCreate, CimiJob.class);
        return new Job(client, cimiObject);
    }

    public static List<Volume> getVolumes(final CimiClient client, final int first, final int last,
        final String... filterExpression) throws CimiException {
        CimiVolumeCollection volumeCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumes().getHref()), CimiVolumeCollectionRoot.class, first, last,
            filterExpression);
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

    public static Volume getVolumeById(final CimiClient client, final String id) throws Exception {
        String path = client.getMachinesPath() + "/" + id;
        return new Volume(client, client.getCimiObjectByReference(path, CimiVolume.class));
    }

}
