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
package org.ow2.sirocco.apis.rest.cimi.converter;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineImage}</li>
 * <li>Service model: {@link MachineImage}</li>
 * </ul>
 * </p>
 */
public class MachineImageConverter {

    /**
     * Copy the data from the service object in the CIMI object.
     * 
     * @param dataService An instance of {@link MachineImage}
     * @param dataCimi An instance of {@link CimiMachineImage}
     * @param urlBase The URL base
     */
    public static void copyToCimi(final MachineImage dataService, final CimiMachineImage dataCimi, final String urlBase,
        final boolean expand, final boolean href) {
        if (true == expand) {
            CommonConverter.copyToCimi(dataService, dataCimi, urlBase, ConstantsPath.MACHINE_IMAGE);
            if (null != dataService.getImageLocation()) {
                dataCimi.setImageLocation(new ImageLocation(dataService.getImageLocation()));
            }
            dataCimi.setState(dataService.getState().toString());
            dataCimi.setType(dataService.getType().toString());
        }
        if (true == href) {
            dataCimi.setHref(HrefHelper.makeHref(urlBase, ConstantsPath.MACHINE_IMAGE, dataService.getId()));
        }
    }

    /**
     * Copy the data from the CIMI object in the service object.
     * 
     * @param dataCimi An instance of {@link CimiMachineImage}
     * @param dataService An instance of {@link MachineImage}
     */
    public static void copyToService(final CimiMachineImage dataCimi, final MachineImage dataService) {
        CommonConverter.copyToService(dataCimi, dataService);
        dataService.setImageLocation(dataCimi.getImageLocation().getHref());
    }

}
