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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineImageCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class MachineImage extends Resource<CimiMachineImage> {
    public static enum State {
        CREATING, AVAILABLE, DELETING, ERROR
    }

    public static enum Type {
        IMAGE, SNAPSHOT, PARTIAL_SNAPSHOT
    }

    public MachineImage() {
        super(null, new CimiMachineImage());
    }

    MachineImage(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiMachineImage());
        this.cimiObject.setHref(id);
    }

    MachineImage(final CimiClient cimiClient, final CimiMachineImage cimiMachineImage) {
        super(cimiClient, cimiMachineImage);
    }

    public CimiMachineImage getCimiMachineImage() {
        return this.cimiObject;
    }

    public State getState() {
        return State.valueOf(this.cimiObject.getState());
    }

    public Type getType() {
        return Type.valueOf(this.cimiObject.getType());
    }

    public String getImageLocation() {
        return this.cimiObject.getImageLocation().getHref();
    }

    public void setImageLocation(final String imageLocation) {
        ImageLocation loc = new ImageLocation();
        loc.setHref(imageLocation);
        this.cimiObject.setImageLocation(loc);
    }

    public void setState(final State state) {
        this.cimiObject.setState(state.toString());
    }

    public void setType(final Type type) {
        this.cimiObject.setType(type.toString());
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

    public static CreateResult<MachineImage> createMachineImage(final CimiClient client, final MachineImage machineImage)
        throws CimiException {
        if (client.cloudEntryPoint.getMachineImages() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiMachineImageCollection machineImagesCollection = client
            .getRequest(client.extractPath(client.cloudEntryPoint.getMachineImages().getHref()),
                CimiMachineImageCollectionRoot.class, null);
        String addRef = Helper.findOperation("add", machineImagesCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiMachineImage> result = client.postCreateRequest(addRef, machineImage.cimiObject, CimiMachineImage.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        MachineImage createdMachineImage = result.getResource() != null ? new MachineImage(client, result.getResource()) : null;
        return new CreateResult<MachineImage>(job, createdMachineImage);
    }

    public static UpdateResult<MachineImage> updateMachineImage(final CimiClient client, final String id,
        final Map<String, Object> attributeValues) throws CimiException {
        CimiMachineImage cimiObject = new CimiMachineImage();
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
            } else if (attribute.equals("imageLocation")) {
                ImageLocation location = new ImageLocation((String) entry.getValue());
                cimiObject.setImageLocation(location);
            }
        }
        CimiResult<CimiMachineImage> cimiResult = client.partialUpdateRequest(id, cimiObject, sb.toString());
        Job job = cimiResult.getJob() != null ? new Job(client, cimiResult.getJob()) : null;
        MachineImage machineImage = cimiResult.getResource() != null ? new MachineImage(client, cimiResult.getResource())
            : null;
        return new UpdateResult<MachineImage>(job, machineImage);
    }

    public static List<MachineImage> getMachineImages(final CimiClient client, final QueryParams queryParams)
        throws CimiException {
        if (client.cloudEntryPoint.getMachineImages() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiMachineImageCollection machineImagesCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachineImages().getHref()), CimiMachineImageCollectionRoot.class,
            queryParams);

        List<MachineImage> result = new ArrayList<MachineImage>();

        if (machineImagesCollection.getCollection() != null) {
            for (CimiMachineImage cimiMachineImage : machineImagesCollection.getCollection().getArray()) {
                result.add(new MachineImage(client, cimiMachineImage));
            }
        }
        return result;
    }

    public static MachineImage getMachineImageByReference(final CimiClient client, final String ref) throws CimiException {
        return new MachineImage(client, client.getCimiObjectByReference(ref, CimiMachineImage.class));
    }

}
