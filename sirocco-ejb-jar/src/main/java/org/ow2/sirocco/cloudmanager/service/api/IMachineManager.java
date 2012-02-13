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

import java.util.Date;
import java.util.List;

import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Visibility;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VirtualMachineSpec;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.DuplicateNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InsufficientResourceException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InternalErrorException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidHostIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineConfigurationException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineImageException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineStateException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidPowerStateException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidVolumeIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ResourceQuotaExceededException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.VolumeInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.service.MachineCreate;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobCompletionEvent;

/**
 * Machine management
 */
public interface IMachineManager {
    static final String EJB_JNDI_NAME = "MachineManagerBean";

    Machine createMachine(final String projectId, final String userName, final VirtualMachineSpec vmSpec,
        final String cloudProviderAccountId, final boolean startVm) throws InvalidUsernameException, InvalidProjectIdException,
        PermissionDeniedException, InvalidMachineImageException, InvalidNameException, DuplicateNameException,
        InvalidArgumentException, ResourceQuotaExceededException, InvalidMachineConfigurationException,
        InvalidProjectNameException, InternalErrorException, InsufficientResourceException;

    Machine createMachine(final String projectId, final String userName, final String cloudProviderAccountId,
        CloudProviderLocation cloudProviderLocation, final MachineCreate machineCreate) throws InvalidUsernameException,
        InvalidProjectIdException, PermissionDeniedException, InvalidMachineImageException, InvalidNameException,
        DuplicateNameException, InvalidArgumentException, ResourceQuotaExceededException, InvalidMachineConfigurationException,
        InvalidProjectNameException, InternalErrorException, InsufficientResourceException;

    void startMachine(final String userName, final String vmId) throws InvalidMachineIdException, InvalidProjectIdException,
        InvalidUsernameException, PermissionDeniedException, InternalErrorException, InvalidMachineStateException;

    void stopMachine(final String userName, final String vmId) throws InvalidUsernameException, InvalidProjectIdException,
        PermissionDeniedException, InvalidMachineIdException, InternalErrorException, InvalidMachineStateException;

    void deleteMachine(final String userName, final String vmId) throws InvalidMachineIdException, InvalidUsernameException,
        InvalidProjectIdException, PermissionDeniedException, InvalidMachineStateException, InvalidPowerStateException,
        InvalidArgumentException, InternalErrorException;

    void attachVolumeToMachine(final String username, final String volumeId, final String vmId)
        throws InvalidUsernameException, InvalidProjectIdException, InvalidVolumeIdException, InvalidMachineIdException,
        VolumeInUseException, PermissionDeniedException, CloudProviderException;

    void detachVolumeFromMachine(final String username, final String volumeId, final String vmId)
        throws InvalidUsernameException, InvalidProjectIdException, InvalidVolumeIdException, InvalidMachineIdException,
        PermissionDeniedException, CloudProviderException;

    void purgeDeletedMachines();

    String getMachineConsole(final String machineId) throws InvalidMachineIdException, CloudProviderException;

    List<Machine> getAllVirtualMachines();

    void pauseMachine(final String vmId, final String fromUsername) throws InvalidMachineIdException,
        InvalidPowerStateException, PermissionDeniedException;

    void unpauseMachine(final String vmId) throws InvalidMachineIdException, InvalidPowerStateException;

    void restartMachine(final String vmId, final String fromUsername) throws InvalidMachineIdException,
        InvalidPowerStateException, PermissionDeniedException;

    void migrateMachine(final String vmId, final String destinationHostId) throws InvalidMachineIdException,
        InvalidHostIdException, CloudProviderException;

    String getMachineConfigurationName(final String vmId) throws InvalidMachineIdException;

    List<MachineConfiguration> getMachineConfigurations();

    void captureImageFromMachine(final String projectId, final Visibility visibility, final String vmId, final String name,
        final String description, final String fromUsername) throws CloudProviderException, InvalidMachineIdException,
        InvalidNameException, DuplicateNameException, InvalidProjectIdException;

    Machine getMachineByProviderAssignedId(final String providerAssignedId);

    Machine getMachineById(final String vmId);

    void updateMachineExpirationDate(final String vmId, final Date expirationDate, final String fromUsername)
        throws InvalidMachineIdException, PermissionDeniedException;

    boolean checkMachineName(final String vmLabel);

    boolean doesMachineNameExistsInProject(final String vmName, final String projectId);

    MachineConfiguration getMachineConfigurationByName(final String name);

    boolean handleJobCompletion(JobCompletionEvent event);

}
