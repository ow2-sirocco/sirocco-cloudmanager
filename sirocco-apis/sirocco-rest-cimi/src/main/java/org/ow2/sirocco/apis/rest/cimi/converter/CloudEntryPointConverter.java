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
 * $Id: MachineImageCollectionConverter.java 1104 2012-03-28 16:09:36Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.converter;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiCloudEntryPoint}</li>
 * <li>Service model: List of {@link CloudEntryPoint}</li>
 * </ul>
 * </p>
 */
public class CloudEntryPointConverter {

    /**
     * Copy the data from the service object in the CIMI object.
     * 
     * @param dataService An instance of {@link CloudEntryPoint}
     * @param dataCimi An instance of List of {@link CimiCloudEntryPoint}
     * @param urlBase The URL base
     */
    public static void copyToCimi(final CloudEntryPoint dataService, final CimiCloudEntryPoint dataCimi, final String urlBase,
        final boolean expand) {
        CommonConverter.copyToCimi(dataService, dataCimi, urlBase, ConstantsPath.CLOUDENTRYPOINT);
        // FIXME Detail not implemented in CloudEntryPoint
    }

    /**
     * Copy the data from the CIMI object in the service object.
     * 
     * @param dataCimi An instance of {@link CimiCloudEntryPoint}
     * @param dataService An instance of {@link CloudEntryPoint}
     */
    public static void copyToService(final CimiCloudEntryPoint dataCimi, final CloudEntryPoint dataService) {
        // FIXME Not Implemented in EJB
    }

}
