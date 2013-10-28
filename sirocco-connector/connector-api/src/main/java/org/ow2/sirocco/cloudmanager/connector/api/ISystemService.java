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

package org.ow2.sirocco.cloudmanager.connector.api;

import java.util.List;
import java.util.Map;

import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;

/**
 * Lifecycle operations on Systems
 */
public interface ISystemService {
    /**
     * Creates a system
     * 
     * @param systemCreate
     * @param target
     * @return
     * @throws ConnectorException
     */
    System createSystem(SystemCreate systemCreate, ProviderTarget target) throws ConnectorException;

    /**
     * Deletes a system
     * 
     * @param systemId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void deleteSystem(String systemId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Starts a system
     * 
     * @param systemId
     * @param properties
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void startSystem(String systemId, Map<String, String> properties, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Stops a system
     * 
     * @param systemId
     * @param force
     * @param properties
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void stopSystem(String systemId, boolean force, Map<String, String> properties, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

    /**
     * Restarts a system
     * 
     * @param systemId
     * @param force
     * @param properties
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void restartSystem(String systemId, boolean force, Map<String, String> properties, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

    /**
     * Pauses a system
     * 
     * @param systemId
     * @param properties
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void pauseSystem(String systemId, Map<String, String> properties, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Suspends a system
     * 
     * @param systemId
     * @param properties
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void suspendSystem(String systemId, Map<String, String> properties, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

    /**
     * Gets a system
     * 
     * @param systemId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    System getSystem(String systemId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Gets a system state
     * 
     * @param systemId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    System.State getSystemState(String systemId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Gets the resources owned by this system
     * 
     * @param systemId provided-assigned id of the system
     * @param entityType type of the resource to return, e.g. "SystemMachine" or
     *        "SystemNetwork"
     * @param target provider target
     * @return list of resources owned by this system
     * @throws ResourceNotFoundException if system is not found
     * @throws ConnectorException
     */
    List<? extends CloudCollectionItem> getEntityListFromSystem(String systemId, String entityType, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

    /**
     * Deletes a resource owned by this system
     * 
     * @param systemId provided-assigned id of the system
     * @param entityId provided-assigned id of the resource
     * @param entityType type of the resource to delete
     * @param target provider target
     * @throws ResourceNotFoundException if system or resource is not found
     * @throws ConnectorException
     */
    void deleteEntityInSystem(String systemId, String entityId, String entityType, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

    /**
     * Removes a resource from this system
     * 
     * @param systemId provided-assigned id of the system
     * @param entityId provided-assigned id of the resource
     * @param target provider target
     * @throws ResourceNotFoundException if system or resource is not found
     * @throws ConnectorException
     */
    void removeEntityFromSystem(String systemId, String entityId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Adds a resource to a system
     * 
     * @param systemId provided-assigned id of the system
     * @param entityId provided-assigned id of the resource
     * @param target provider target
     * @throws ResourceNotFoundException if system or resource is not found
     * @throws ConnectorException
     */
    void addEntityToSystem(final String systemId, final String entityId, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

}
