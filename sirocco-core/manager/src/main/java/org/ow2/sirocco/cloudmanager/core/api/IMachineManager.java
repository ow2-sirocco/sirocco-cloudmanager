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

import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;

/**
 * Machine management operations
 */
public interface IMachineManager {

    static final String EJB_JNDI_NAME = "MachineManager";

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

    Job updateMachine(final String machineId, Map<String, Object> updatedAttributes) throws ResourceNotFoundException,
        CloudProviderException;

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

    void updateMachineCollection(Map<String, Object> attributes) throws InvalidRequestException, CloudProviderException;

    /**
     * Operations on MachineConfiguration
     */
    MachineConfiguration getMachineConfigurationById(final String MachineId) throws ResourceNotFoundException,
        CloudProviderException;;

    void updateMachineConfiguration(String machineConfigurationId, Map<String, Object> updatedAttributes)
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

    void updateMachineConfigurationCollection(Map<String, Object> attributes) throws InvalidRequestException,
        CloudProviderException;

    /**
     * Operations on MachineTemplate
     */
    MachineTemplate getMachineTemplateById(String machineTemplateId) throws ResourceNotFoundException, CloudProviderException;

    MachineTemplate updateMachineTemplate(String machineTemplateId, Map<String, Object> attributes)
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

    void updateMachineTemplateCollection(Map<String, Object> attributes) throws InvalidRequestException, CloudProviderException;

    boolean machineCompletionHandler(final Job job);

}
