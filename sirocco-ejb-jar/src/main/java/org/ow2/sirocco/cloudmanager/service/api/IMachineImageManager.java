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

package org.ow2.sirocco.cloudmanager.service.api;

import java.util.List;

import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidCloudProviderAccountException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineImageException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.MachineImageInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ResourceQuotaExceededException;
import org.ow2.sirocco.cloudmanager.provider.api.service.ImageUpload;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobCompletionEvent;

/**
 * This interface provides public methods for managing Machine Images
 */
public interface IMachineImageManager {
    static final String EJB_JNDI_NAME = "MachineImageManagerBean";

    public void deleteMachineImage(final String machineImageId, final String fromUsername) throws InvalidMachineImageException,
        MachineImageInUseException, PermissionDeniedException;

    void uploadMachineImage(final String projectId, final String userName, final String cloudProviderAccountId,
        CloudProviderLocation location, ImageUpload imageUpload) throws InvalidUsernameException, InvalidProjectIdException,
        InvalidCloudProviderAccountException, PermissionDeniedException, ResourceQuotaExceededException, CloudProviderException;

    List<MachineImage> getAllMachineImages();

    void updateMachineImage(final MachineImage image) throws InvalidMachineImageException;

    MachineImage getMachineImageById(final String machineImageId);

    boolean handleJobCompletion(JobCompletionEvent event);

}
