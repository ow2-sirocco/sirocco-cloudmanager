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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineImageCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

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

    public MachineImage(final CimiClient cimiClient, final String id) {
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

    public void delete() throws CimiException {
        this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()));
    }

    public static Job createMachineImage(final CimiClient client, final MachineImage machineImage) throws CimiException {
        CimiJob cimiObject = client.postRequest(ConstantsPath.MACHINE_IMAGE_PATH, machineImage.cimiObject, CimiJob.class);
        return new Job(client, cimiObject);
    }

    public static List<MachineImage> getMachineImages(final CimiClient client, final int first, final int last,
        final String... filterExpression) throws CimiException {
        CimiMachineImageCollection machineImagesCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachineImages().getHref()), CimiMachineImageCollectionRoot.class,
            first, last, null, filterExpression);

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

    public static MachineImage getMachineImageById(final CimiClient client, final String id) throws CimiException {
        String path = client.getMachineImagesPath() + "/" + id;
        return new MachineImage(client, client.getCimiObjectByReference(path, CimiMachineImage.class));
    }

}
