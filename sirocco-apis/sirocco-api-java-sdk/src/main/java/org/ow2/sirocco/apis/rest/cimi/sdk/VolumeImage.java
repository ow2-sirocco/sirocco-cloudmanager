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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeImageCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class VolumeImage extends Resource<CimiVolumeImage> {
    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    public VolumeImage() {
        super(null, new CimiVolumeImage());
    }

    VolumeImage(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiVolumeImage());
        this.cimiObject.setHref(id);
    }

    VolumeImage(final CimiClient cimiClient, final CimiVolumeImage cimiVolumeImage) {
        super(cimiClient, cimiVolumeImage);
    }

    public CimiVolumeImage getCimiVolumeImage() {
        return this.cimiObject;
    }

    public State getState() {
        return State.valueOf(this.cimiObject.getState());
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

    public static CreateResult<VolumeImage> createVolumeImage(final CimiClient client, final VolumeImage volumeImage)
        throws CimiException {
        if (client.cloudEntryPoint.getVolumeImages() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiVolumeImageCollection volumeImagesCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumeImages().getHref()), CimiVolumeImageCollectionRoot.class, null);
        String addRef = Helper.findOperation("add", volumeImagesCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiVolumeImage> result = client.postCreateRequest(addRef, volumeImage.cimiObject, CimiVolumeImage.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        VolumeImage createdVolumeConfiguration = result.getResource() != null ? new VolumeImage(client, result.getResource())
            : null;
        return new CreateResult<VolumeImage>(job, createdVolumeConfiguration);
    }

    public static List<VolumeImage> getVolumeImages(final CimiClient client, final QueryParams queryParams)
        throws CimiException {
        if (client.cloudEntryPoint.getVolumeImages() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiVolumeImageCollection volumeImagesCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumeImages().getHref()), CimiVolumeImageCollectionRoot.class,
            queryParams);

        List<VolumeImage> result = new ArrayList<VolumeImage>();

        if (volumeImagesCollection.getCollection() != null) {
            for (CimiVolumeImage cimiVolumeImage : volumeImagesCollection.getCollection().getArray()) {
                result.add(new VolumeImage(client, cimiVolumeImage));
            }
        }
        return result;
    }

    public static VolumeImage getVolumeImageByReference(final CimiClient client, final String ref) throws CimiException {
        return new VolumeImage(client, client.getCimiObjectByReference(ref, CimiVolumeImage.class));
    }

}
