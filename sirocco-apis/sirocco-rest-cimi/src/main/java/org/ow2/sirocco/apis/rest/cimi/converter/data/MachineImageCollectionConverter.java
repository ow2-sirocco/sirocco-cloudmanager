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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.converter.data;

import java.util.ArrayList;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineImageCollection}</li>
 * <li>Service model: List of {@link MachineImage}</li>
 * </ul>
 * </p>
 */
public class MachineImageCollectionConverter {

    /**
     * Copy the data from the service object in the CIMI object.
     * 
     * @param dataService An instance of {@link MachineImageCollection}
     * @param dataCimi An instance of List of {@link CimiMachineImage}
     * @param urlBase The URL base
     */
    public static void copyToCimi(final List<MachineImage> dataService, final CimiMachineImageCollection dataCimi,
        final String urlBase) {

        dataCimi.setId(HrefHelper.makeHref(urlBase, ConstantsPath.MACHINE_IMAGE));
        CimiMachineImage cimi = null;
        List<CimiMachineImage> cimiList = new ArrayList<CimiMachineImage>();
        for (MachineImage machineImage : dataService) {
            cimi = new CimiMachineImage();
            cimiList.add(cimi);
            MachineImageConverter.copyToCimi(machineImage, cimi, urlBase, false, true);
        }
        dataCimi.setMachineImages(cimiList.toArray(new CimiMachineImage[cimiList.size()]));
    }

    /**
     * Copy the data from the CIMI object in the service object.
     * 
     * @param dataCimi An instance of {@link CimiMachineImageCollection}
     * @param dataService An instance of {@link MachineImage}
     */
    public static void copyToService(final CimiMachineImageCollection dataCimi, final List<MachineImage> dataService) {
        MachineImage serviceImage;
        CimiMachineImage[] images = dataCimi.getMachineImages();
        for (CimiMachineImage cimiImage : images) {
            serviceImage = new MachineImage();
            dataService.add(serviceImage);
            MachineImageConverter.copyToService(cimiImage, serviceImage);
        }
    }

}
