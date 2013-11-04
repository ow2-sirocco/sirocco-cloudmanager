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
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;

/**
 * System management operations
 */
public interface ISystemManager {

    static final String EJB_JNDI_NAME = "java:global/sirocco/sirocco-core/SystemManager!org.ow2.sirocco.cloudmanager.core.api.IRemoteSystemManager";

    // create
    Job createSystem(SystemCreate systemCreate) throws CloudProviderException;

    SystemTemplate createSystemTemplate(SystemTemplate systemTemplate) throws CloudProviderException;

    // get by id
    System getSystemById(String systemId) throws CloudProviderException;

    System getSystemAttributes(String systemId, List<String> attributes) throws CloudProviderException;

    SystemTemplate getSystemTemplateById(String systemTemplateId) throws CloudProviderException;

    SystemTemplate getSystemTemplateAttributes(String systemTemplateId, List<String> attributes) throws CloudProviderException;

    // CRUD on system collections
    Job addEntityToSystem(final String systemId, final CloudCollectionItem entity) throws CloudProviderException;

    Job removeEntityFromSystem(final String systemId, final String entityId) throws CloudProviderException;

    Job updateEntityInSystem(final String systemId, final CloudCollectionItem entity) throws CloudProviderException;

    Job updateEntityAttributesInSystem(String systemId, final String entityType, String entityId,
        Map<String, Object> updatedAttributes) throws InvalidRequestException, ResourceNotFoundException,
        CloudProviderException;

    CloudCollectionItem getEntityFromSystem(final String systemId, final String entityId) throws CloudProviderException;

    List<? extends CloudCollectionItem> getEntityListFromSystem(final String systemId,
        final Class<? extends CloudCollectionItem> entityType) throws CloudProviderException;

    QueryResult<CloudCollectionItem> getEntityListFromSystem(final String systemId,
        final Class<? extends CloudCollectionItem> entityType, int first, int last, List<String> filters,
        List<String> attributes) throws CloudProviderException;

    // management of SystemTemplate map
    boolean addComponentDescriptorToSystemTemplate(ComponentDescriptor componentDescriptor, String systemTemplateId)
        throws CloudProviderException;

    boolean removeComponentDescriptorFromSystemTemplate(String componentDescriptorId, String systemTemplateId)
        throws CloudProviderException;

    void deleteSystemTemplate(String systemTemplateId) throws ResourceNotFoundException, CloudProviderException;

    // management of component Descriptors
    System updateComponentDescriptor(final String id, Map<String, Object> updatedAttributes) throws CloudProviderException;

    // global entity updates
    System updateSystem(final System system) throws CloudProviderException;

    void updateSystemState(String systemId, System.State state) throws CloudProviderException;

    System updateAttributesInSystem(final String id, Map<String, Object> updatedAttributes) throws CloudProviderException;

    SystemTemplate updateSystemTemplate(final SystemTemplate systemTemplate) throws CloudProviderException;

    SystemTemplate updateAttributesInSystemTemplate(final String id, Map<String, Object> updatedAttributes)
        throws CloudProviderException;

    Job deleteSystem(String systemId) throws CloudProviderException;

    List<System> getSystems() throws CloudProviderException;

    QueryResult<System> getSystems(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    List<SystemTemplate> getSystemTemplates() throws CloudProviderException;

    QueryResult<SystemTemplate> getSystemTemplates(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    Job startSystem(String systemId, Map<String, String> properties) throws CloudProviderException;

    Job stopSystem(String systemId, boolean force, Map<String, String> properties) throws CloudProviderException;

    Job suspendSystem(String systemId, Map<String, String> properties) throws CloudProviderException;

    Job pauseSystem(String systemId, Map<String, String> properties) throws CloudProviderException;

    Job restartSystem(String systemId, boolean force, Map<String, String> properties) throws CloudProviderException;

    Job startSystem(String systemId) throws CloudProviderException;

    Job stopSystem(String systemId, boolean force) throws CloudProviderException;

    Job stopSystem(String systemId) throws CloudProviderException;

    Job suspendSystem(String systemId) throws CloudProviderException;

    Job pauseSystem(String systemId) throws CloudProviderException;

    Job restartSystem(String systemId, boolean force) throws CloudProviderException;

    Job exportSystem(String systemId, String format, String destination, Map<String, String> properties)
        throws CloudProviderException;

    Job importSystem(String source, Map<String, String> properties) throws CloudProviderException;

    Job exportSystemTemplate(String systemTemplateId, String format, String destination, Map<String, String> properties)
        throws CloudProviderException;

    Job importSystemTemplate(String source, Map<String, String> properties) throws CloudProviderException;

    void handleEntityStateChange(final Class<? extends CloudResource> entityType, final String entityId, final boolean deletion);

    void syncSystem(String systemId, System system, String jobId) throws CloudProviderException;

}
