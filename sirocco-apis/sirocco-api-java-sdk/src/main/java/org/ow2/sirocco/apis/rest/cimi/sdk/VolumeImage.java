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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class VolumeImage extends Resource<CimiVolumeImage> {
	public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

   public VolumeImage() {
       super(null, new CimiVolumeImage());
   }

   public VolumeImage(final CimiClient cimiClient, final String id) {
       super(cimiClient, new CimiVolumeImage());
       this.cimiObject.setHref(id);
       this.cimiObject.setId(id);
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
       return cimiObject.getBootable();
   }

   public void setBootable(final boolean bootable) {
	   cimiObject.setBootable(bootable);
   }

   public void delete() throws CimiException {
       this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()));
   }

   public static Job createVolumeImage(final CimiClient client, final VolumeImage volumeImage) throws CimiException {
       CimiJob cimiObject = client.postRequest(ConstantsPath.VOLUME_IMAGE_PATH, volumeImage.cimiObject, CimiJob.class);
       return new Job(client, cimiObject);
   }

   public static List<VolumeImage> getVolumeImages(final CimiClient client) throws CimiException {
       CimiVolumeImageCollection volumeImagesCollection = client.getRequest(
           client.extractPath(client.cloudEntryPoint.getVolumeImages().getHref()), CimiVolumeImageCollection.class);

       List<VolumeImage> result = new ArrayList<VolumeImage>();

       if (volumeImagesCollection.getCollection() != null) {
           for (CimiVolumeImage cimiVolumeImage : volumeImagesCollection.getCollection().getArray()) {
               result.add(VolumeImage.getVolumeImageByReference(client, cimiVolumeImage.getHref()));
           }
       }
       return result;
   }

   public static VolumeImage getVolumeImageByReference(final CimiClient client, final String ref) throws CimiException {
       return new VolumeImage(client, client.getCimiObjectByReference(ref, CimiVolumeImage.class));
   }

   public static VolumeImage getVolumeImageById(final CimiClient client, final String id) throws CimiException {
       String path = client.getVolumeImagesPath() + "/" + id;
       return new VolumeImage(client, client.getCimiObjectByReference(path, CimiVolumeImage.class));
   }

}
