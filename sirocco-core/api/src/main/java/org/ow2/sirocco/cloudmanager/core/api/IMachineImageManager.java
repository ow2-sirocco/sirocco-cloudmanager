/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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
 *  
 *
 */

package org.ow2.sirocco.cloudmanager.core.api;

import java.util.List;
import java.util.Map;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;

/**
 * Machine Image management operations
 */
public interface IMachineImageManager {

    static final String EJB_JNDI_NAME = "java:global/sirocco/sirocco-core/MachineImageManager!org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager";

    Job createMachineImage(MachineImage machineImage) throws CloudProviderException;

    List<MachineImage> getMachineImages() throws CloudProviderException;

    MachineImage getMachineImageById(int imageId) throws ResourceNotFoundException, CloudProviderException;

    MachineImage getMachineImageByUuid(String imageUuid) throws ResourceNotFoundException, CloudProviderException;

    MachineImage getMachineImageAttributes(String imageId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    void deleteMachineImage(String imageId) throws ResourceNotFoundException, CloudProviderException;

    void updateMachineImage(MachineImage machineImage) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    void updateMachineImageAttributes(String imageId, Map<String, Object> attributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    QueryResult<MachineImage> getMachineImages(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;
}
