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

import java.util.Map;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.ComponentDescriptor;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.SystemTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.System;

/**
 * System management operations
 */
public interface ISystemManager extends IJobListener {

    static final String EJB_JNDI_NAME = "SystemManager";

    //create
    Job createSystem(SystemCreate systemCreate) throws CloudProviderException;    
    SystemTemplate createSystemTemplate(SystemTemplate systemTemplate) throws CloudProviderException;    
    
    //get by id
    System getSystemById(String systemId) throws CloudProviderException;
    SystemTemplate getSystemTemplateById(String systemTemplateId) throws CloudProviderException;
    
    // add/remove on system collections
    Job addVolumeToSystem(Volume volume,String systemId) throws CloudProviderException;
    Job removeVolumeFromSystem(String volumeId,String systemId) throws CloudProviderException;
    
    Job addSystemToSystem(System system,String systemId) throws CloudProviderException;
    Job removeSystemFromSystem(String systemToRemoveId,String systemId) throws CloudProviderException;    
    
    Job addMachineToSystem(Machine machine,String systemId) throws CloudProviderException;
    Job removeMachineFromSystem(String machineId,String systemId) throws CloudProviderException;    
    
    Job addCredentialToSystem(Credentials credential,String systemId) throws CloudProviderException;
    Job removeCredentialFromSystem(String credentialId,String systemId) throws CloudProviderException;      

    //management of SystemTemplate map
    boolean addComponentDescriptorToSystemTemplate(ComponentDescriptor componentDescriptor,String systemTemplateId) throws CloudProviderException;
    boolean removeComponentDescriptorFromSystemTemplate(String componentDescriptorId,String systemTemplateId) throws CloudProviderException;
    
    //management of compponent Descriptors
    System updateComponentDescriptor(final String id,
            Map<String, Object> updatedAttributes)
            throws CloudProviderException;    
    
    //global entity updates
    System updateSystem(final String id,
            Map<String, Object> updatedAttributes)
            throws CloudProviderException;
    SystemTemplate updateSystemTemplate(final String id,
            Map<String, Object> updatedAttributes)
            throws CloudProviderException;

    //operations on system
    Job startSystem(String systemId) throws CloudProviderException;
    Job stopSystem(String systemId) throws CloudProviderException;
    Job deleteSystem(String systemId) throws CloudProviderException;
    
}
