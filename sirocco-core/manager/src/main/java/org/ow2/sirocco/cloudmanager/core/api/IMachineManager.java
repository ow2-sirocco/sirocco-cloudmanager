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
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterfaceMachine;

/**
 * Machine management operations
 */
public interface IMachineManager {

    static final String EJB_JNDI_NAME = "org.ow2.sirocco.cloudmanager.core.impl.MachineManager_org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager@Remote";

    /**
     * Operations on CEP
     */
    CloudEntryPoint getCloudEntryPoint() throws CloudProviderException;

    /**
     * Operations on Machine
     */
    Job startMachine(final String machineId) throws ResourceNotFoundException, CloudProviderException;

    Job stopMachine(final String machineId) throws ResourceNotFoundException, CloudProviderException;

    Job deleteMachine(final String machineId) throws ResourceNotFoundException, CloudProviderException;

    Machine getMachineById(final String machineId) throws ResourceNotFoundException, CloudProviderException;

    Machine getMachineAttributes(final String machineId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    Job updateMachine(final Machine machine) throws ResourceNotFoundException, CloudProviderException;

    Job updateMachineAttributes(final String machineId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, CloudProviderException;

    /**
     * Operations on MachineCollection
     */
    MachineCollection getMachineCollection() throws CloudProviderException;

    Job createMachine(MachineCreate machineCreate) throws ResourceConflictException, InvalidRequestException,
        CloudProviderException;

    List<Machine> getMachines(int first, int last, List<String> attributes) throws InvalidRequestException,
        CloudProviderException;

    List<Machine> getMachines(List<String> attributes, String queryExpression) throws InvalidRequestException,
        CloudProviderException;

    /**
     * Operations on MachineConfiguration
     */
    MachineConfiguration getMachineConfigurationById(final String MachineId) throws ResourceNotFoundException,
        CloudProviderException;;

    void updateMachineConfiguration(MachineConfiguration machineConfiguration) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    void updateMachineConfigurationAttributes(String machineConfigurationId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    void deleteMachineConfiguration(final String machineConfigurationId) throws ResourceNotFoundException,
        CloudProviderException;

    /**
     * Operations on MachineConfigurationCollection
     */
    MachineConfigurationCollection getMachineConfigurationCollection() throws CloudProviderException;

    MachineConfiguration createMachineConfiguration(MachineConfiguration machineConfig) throws InvalidRequestException,
        CloudProviderException;

    List<MachineConfiguration> getMachineConfigurations(int first, int last, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    List<MachineConfiguration> getMachineConfigurations(List<String> attributes, String queryExpression)
        throws InvalidRequestException, CloudProviderException;

    /**
     * Operations on MachineTemplate
     */
    MachineTemplate getMachineTemplateById(String machineTemplateId) throws ResourceNotFoundException, CloudProviderException;

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

    MachineTemplateCollection getMachineTemplateCollection() throws CloudProviderException;

    List<MachineTemplate> getMachineTemplates(int first, int last, List<String> attributes) throws InvalidRequestException,
        CloudProviderException;

    List<MachineTemplate> getMachineTemplates(List<String> attributes, String queryExpression) throws InvalidRequestException,
        CloudProviderException;

    /**
     * Machine and Machine template volumes
     */

    List<MachineVolume> getMachineVolumes(final String machineId) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException;

    List<MachineVolumeTemplate> getMachineVolumeTemplates(final String mtId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    Job addVolumeToMachine(final String machineId, final String volumeId, final String initialLocation)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException;

    void addVolumeToMachineTemplate(final String mtId, final String volumeId, final String initialLocation)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException;

    void addVolumeTemplateToMachineTemplate(final String mtId, final String volumeId, final String initialLocation)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException;

    /** machineTemplateId and machineVolumeId */

    void removeVolumeFromMachineTemplate(String mtId, String mvId) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException;

    /** machineTemplateId and machineVolumeTemplateId */
    void removeVolumeTemplateFromMachineTemplate(String mtId, String mvtId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    Job removeVolumeFromMachine(String machineId, String mvId) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException;

    Job updateMachineVolume(String machineId, MachineVolume machineVolume) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    //
    // machine disk operations
    //

    List<MachineDisk> getMachineDisks(final String machineId) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException;

    Job addDiskToMachine(final String machineId, final MachineDisk disk) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    Job removeDiskFromMachine(String machineId, String machineDiskId) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException;

    //
    // machine network interface operations
    //

    List<NetworkInterfaceMachine> getMachineNetworkInterfaces(final String machineId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    Job addNetworkInterfaceToMachine(final String machineId, final NetworkInterfaceMachine nic)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException;

    Job removeNetworkInterfaceFromMachine(String machineId, String nicId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    boolean jobCompletionHandler(final String job);

}
