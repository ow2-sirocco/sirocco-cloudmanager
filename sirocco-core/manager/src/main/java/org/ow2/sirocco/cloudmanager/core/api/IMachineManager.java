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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;

import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.InvalidMachineIdException;

public interface IMachineManager {

	static final String EJB_JNDI_NAME = "MachineManager";

	/**
	 * Operations on MachineCollection
	 */
	Job createMachine(MachineCreate machineCreate) throws CloudProviderException;;
	List<Machine> getMachines(int first, int last, List<String> attributes) throws CloudProviderException;
	List<Machine> getMachinesAttributes(List<String> attributes, String queryExpression)  throws CloudProviderException;

	/**
	 * Operations on Machine
	 */
	Job startMachine(final String machineId) throws CloudProviderException;
	Job stopMachine(final String machineId) throws CloudProviderException;
	Job deleteMachine(final String machineId) throws CloudProviderException;

	Machine getMachineById(final String machineId) 
		throws InvalidMachineIdException, CloudProviderException;

	Machine getMachineAttributes(final String machineId, 
				     List<String> attributes) 
		 throws InvalidMachineIdException, CloudProviderException;

	Job updateMachine(final String machineId, 
			  Map<String, Object> updatedAttributes)
		 throws InvalidMachineIdException, CloudProviderException;

	/**
	 * Operations on MachineCollection
	 */
	MachineCollection	getMachineCollection()
	         throws CloudProviderException;

	void	updateMachineCollection(Map<String, Object> attributes)
	         throws CloudProviderException;

	/**
	 * Operations on MachineConfiguration
	 */
	MachineConfiguration getMachineConfiguration(final String MachineId) throws CloudProviderException;;
	void updateMachineConfiguration(String machineConfigurationId, 
			  					   Map<String, Object> updatedAttributes)
				      throws CloudProviderException;
	void deleteMachineConfiguration(final String machineConfigurationId)
				      throws CloudProviderException;

	/**
	 * Operations on MachineConfigurationCollection
	 */
	MachineConfigurationCollection getMachineConfigurationCollection();
	MachineConfiguration createMachineConfiguration(MachineConfiguration machineConfig)
						throws CloudProviderException;

	/**
	 * Operations on MachineTemplate
	 */
	MachineTemplate getMachineTemplate(String machineTemplateId) throws CloudProviderException;
	MachineTemplate updateMachineTemplate(String machineTemplateId, Map<String, Object> attributes)
		throws CloudProviderException;
	void deleteMachineTemplate(String machineTemplateId) throws CloudProviderException;

	/**
	 * Operations on MachineTemplateCollection
	 */
	MachineTemplate	createMachineTemplate(MachineTemplate machineTemplate) 
		throws CloudProviderException;
	MachineTemplateCollection getMachineTemplateCollection()
		throws CloudProviderException;
	void updateMachineTemplateCollection(Map<String, Object> attributes )
		throws CloudProviderException;
}
