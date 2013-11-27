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
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.core.api;

import java.util.List;
import java.util.Map;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;

/**
 * Machine management operations
 */
public interface IMachineManager {

    static final String EJB_JNDI_NAME = "java:global/sirocco/sirocco-core/MachineManager!org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager";

    /**
     * Operations on Machine
     */
    Job startMachine(final String machineId, Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException;

    Job stopMachine(final String machineId, boolean force, Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException;

    Job suspendMachine(final String machineId, Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException;

    Job restartMachine(final String machineId, boolean force, Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException;

    Job pauseMachine(final String machineId, Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException;

    Job startMachine(final String machineId) throws ResourceNotFoundException, CloudProviderException;

    Job stopMachine(final String machineId, boolean force) throws ResourceNotFoundException, CloudProviderException;

    Job stopMachine(final String machineId) throws ResourceNotFoundException, CloudProviderException;

    Job suspendMachine(final String machineId) throws ResourceNotFoundException, CloudProviderException;

    Job restartMachine(final String machineId, boolean force) throws ResourceNotFoundException, CloudProviderException;

    Job pauseMachine(final String machineId) throws ResourceNotFoundException, CloudProviderException;

    Job captureMachine(final String machineId, final MachineImage machineImage) throws CloudProviderException;

    Job deleteMachine(final String machineId) throws ResourceNotFoundException, CloudProviderException;

    Machine getMachineById(final int machineId) throws ResourceNotFoundException, CloudProviderException;

    Machine getMachineByUuid(final String machineUuid) throws ResourceNotFoundException, CloudProviderException;

    Machine getMachineAttributes(final String machineId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    Job updateMachine(final Machine machine) throws ResourceNotFoundException, CloudProviderException;

    void updateMachineState(int machineId, Machine.State state) throws CloudProviderException;

    void updateMachineVolumeState(int machineVolumeId, MachineVolume.State state) throws CloudProviderException;

    Job updateMachineAttributes(final String machineId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, CloudProviderException;

    Job createMachine(MachineCreate machineCreate) throws ResourceConflictException, InvalidRequestException,
        CloudProviderException;

    void syncMachine(int machineId, Machine machine, int jobId) throws CloudProviderException;

    void syncVolumeAttachment(int machineId, MachineVolume volumeAttachment, int jobId);

    QueryResult<Machine> getMachines(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    QueryResult<Machine> getMachines(QueryParams... queryParams) throws InvalidRequestException, CloudProviderException;

    /**
     * Operations on MachineConfiguration
     */
    MachineConfiguration getMachineConfigurationById(final int machineConfigurationId) throws ResourceNotFoundException,
        CloudProviderException;

    MachineConfiguration getMachineConfigurationByUuid(final String machineConfigurationId) throws ResourceNotFoundException,
        CloudProviderException;

    MachineConfiguration getMachineConfigurationAttributes(final String machineConfigurationId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    void updateMachineConfiguration(MachineConfiguration machineConfiguration) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    void updateMachineConfigurationAttributes(String machineConfigurationId, MachineConfiguration machineConfiguration,
        List<String> updatedAttributes) throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    void deleteMachineConfiguration(final String machineConfigurationId) throws ResourceNotFoundException,
        CloudProviderException;

    MachineConfiguration createMachineConfiguration(MachineConfiguration machineConfig) throws InvalidRequestException,
        CloudProviderException;

    QueryResult<MachineConfiguration> getMachineConfigurations(QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException;

    QueryResult<MachineConfiguration> getMachineConfigurations(int first, int last, List<String> filters,
        List<String> attributes) throws InvalidRequestException, CloudProviderException;

    /**
     * Operations on MachineTemplate
     */
    MachineTemplate getMachineTemplateById(int machineTemplateId) throws ResourceNotFoundException, CloudProviderException;

    MachineTemplate getMachineTemplateByUuid(String machineTemplateUuid) throws ResourceNotFoundException,
        CloudProviderException;

    MachineTemplate getMachineTemplateAttributes(final String machineTemplateId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    void updateMachineTemplate(MachineTemplate machineTemplate) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    void updateMachineTemplateAttributes(String machineTemplateId, Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    void deleteMachineTemplate(String machineTemplateId) throws ResourceNotFoundException, CloudProviderException;

    /**
     * Operations on MachineTemplateCollection
     */
    MachineTemplate createMachineTemplate(MachineTemplate machineTemplate) throws InvalidRequestException,
        CloudProviderException;

    QueryResult<MachineTemplate> getMachineTemplates(QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException;

    QueryResult<MachineTemplate> getMachineTemplates(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    /**
     * Machine and Machine template volumes
     */

    QueryResult<MachineVolume> getMachineVolumes(final String machineId, QueryParams... queryParams)
        throws InvalidRequestException, CloudProviderException;

    Job addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    MachineVolume getVolumeFromMachine(String machineId, String mvId) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException;

    Job removeVolumeFromMachine(String machineId, String mvId) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException;

    Job updateVolumeOnMachine(String machineId, MachineVolume machineVolume) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    Job updateVolumeAttributesInMachine(String machineId, String mvId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException;

    //
    // machine disk operations
    //

    // List<MachineDisk> getMachineDisks(final String machineId) throws
    // ResourceNotFoundException, CloudProviderException,
    // InvalidRequestException;

    QueryResult<MachineDisk> getMachineDisks(final String machineId, QueryParams... queryParams)
        throws InvalidRequestException, CloudProviderException;

    Job addDiskToMachine(final String machineId, final MachineDisk disk) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    MachineDisk getDiskFromMachine(String machineId, String machineDiskId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    Job removeDiskFromMachine(String machineId, String machineDiskId) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException;

    Job updateDiskInMachine(String machineId, MachineDisk disk) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException;

    Job updateDiskAttributesInMachine(String machineId, String machineDiskId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException;

    //
    // machine network interface operations
    //

    // List<MachineNetworkInterface> getMachineNetworkInterfaces(final String
    // machineId) throws ResourceNotFoundException,
    // CloudProviderException, InvalidRequestException;

    QueryResult<MachineNetworkInterface> getMachineNetworkInterfaces(final String machineId, QueryParams... queryParams)
        throws InvalidRequestException, CloudProviderException;

    Job addNetworkInterfaceToMachine(final String machineId, final MachineNetworkInterface nic)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException;

    Job removeNetworkInterfaceFromMachine(String machineId, String nicId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    MachineNetworkInterface getNetworkInterfaceFromMachine(String machineId, String nicId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    Job updateNetworkInterfaceInMachine(String machineId, final MachineNetworkInterface nic) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    Job updateNetworkInterfaceAttributesInMachine(String machineId, String nicId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException;

    QueryResult<MachineNetworkInterfaceAddress> getMachineNetworkInterfaceAddresses(final String machineId, final String nicId,
        QueryParams... queryParams) throws InvalidRequestException, CloudProviderException;

    Job addAddressToMachineNetworkInterface(final String machineId, final String nicId,
        MachineNetworkInterfaceAddress addressEntry) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException;

    Job removeAddressFromMachineNetworkInterface(final String machineId, final String nicId, final String addressEntryId)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException;

    Job updateMachineNetworkInterfaceAddress(final String machineId, final String nicId,
        final MachineNetworkInterfaceAddress addressEntry) throws InvalidRequestException, CloudProviderException;

    //
    // For system management
    //
    void persistMachineInSystem(Machine machine) throws CloudProviderException;

    void deleteMachineInSystem(Machine machine) throws CloudProviderException;

    void updateMachineInSystem(Machine machine) throws CloudProviderException;
}
