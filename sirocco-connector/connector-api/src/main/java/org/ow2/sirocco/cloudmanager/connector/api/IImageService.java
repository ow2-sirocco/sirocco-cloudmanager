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
package org.ow2.sirocco.cloudmanager.connector.api;

import java.util.List;
import java.util.Map;

import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;

/**
 * Lifecycle operations on MachineImages
 */
public interface IImageService {
    /**
     * Gets a machine image
     * 
     * @param machineImageId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    MachineImage getMachineImage(final String machineImageId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Gets all machine images
     * 
     * @param returnPublicImages if true include public images
     * @param searchCriteria placeholder for search criteria
     * @param target provider target
     * @return list of machine images available from the provider account
     */
    List<MachineImage> getMachineImages(boolean returnAccountImagesOnly, Map<String, String> searchCriteria,
        ProviderTarget target) throws ConnectorException;

    /**
     * Deletes a machine image
     * 
     * @param imageId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void deleteMachineImage(final String imageId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

}
