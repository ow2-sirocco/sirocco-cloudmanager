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

import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;

/**
 * Lifecycle operations on Volumes
 */
public interface IVolumeService {

    /**
     * Creates a volume
     * 
     * @param volumeCreate
     * @param target
     * @return
     * @throws ConnectorException
     */
    Volume createVolume(VolumeCreate volumeCreate, ProviderTarget target) throws ConnectorException;

    /**
     * Deletes a volume
     * 
     * @param volumeId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void deleteVolume(String volumeId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Gets a volume state
     * 
     * @param volumeId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    Volume.State getVolumeState(String volumeId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Gets a volume
     * 
     * @param volumeId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    Volume getVolume(String volumeId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Creates a volume image
     * 
     * @param volumeImage
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    VolumeImage createVolumeImage(VolumeImage volumeImage, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Creates a volume snapshot
     * 
     * @param volumeId
     * @param volumeImage
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    VolumeImage createVolumeSnapshot(String volumeId, VolumeImage volumeImage, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

    /**
     * Gets a volume image
     * 
     * @param volumeImageId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    VolumeImage getVolumeImage(String volumeImageId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Deletes a volume image
     * 
     * @param volumeImageId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void deleteVolumeImage(String volumeImageId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

}
