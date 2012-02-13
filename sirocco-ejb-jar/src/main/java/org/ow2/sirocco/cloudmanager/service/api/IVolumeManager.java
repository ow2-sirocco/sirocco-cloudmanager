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
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Volume;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.DuplicateNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidCloudProviderAccountException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidVolumeIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ResourceQuotaExceededException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.VolumeInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.service.VolumeCreate;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobCompletionEvent;

public interface IVolumeManager {
    static final String EJB_JNDI_NAME = "VolumeManagerBean";

    Volume createVolume(final String projectId, final String username, final String cloudProviderAccountId,
        CloudProviderLocation location, final VolumeCreate volumeSpec) throws InvalidUsernameException, InvalidNameException,
        InvalidCloudProviderAccountException, ResourceQuotaExceededException, DuplicateNameException,
        InvalidProjectIdException, PermissionDeniedException, CloudProviderException;

    void deleteVolume(final String username, final String volumeId) throws InvalidUsernameException,
        InvalidProjectIdException, InvalidVolumeIdException, VolumeInUseException, PermissionDeniedException,
        CloudProviderException;

    List<Machine> getMachinesUsingVolume(final String volumeId) throws InvalidVolumeIdException;

    List<Volume> getAllVolumes();

    Volume getVolumeByName(final String volumeName);

    Volume getVolumeById(final String volumeId);

    Volume getVolumeByProviderAssignedId(final String volumeProviderAssignedId);

    boolean handleJobCompletion(JobCompletionEvent event);

}
