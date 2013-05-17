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

public interface ISystemService {
    System createSystem(SystemCreate systemCreate, ProviderTarget target) throws ConnectorException;

    void deleteSystem(String systemId, ProviderTarget target) throws ConnectorException;

    void startSystem(String systemId, Map<String, String> properties, ProviderTarget target) throws ConnectorException;

    void stopSystem(String systemId, boolean force, Map<String, String> properties, ProviderTarget target)
        throws ConnectorException;

    void restartSystem(String systemId, boolean force, Map<String, String> properties, ProviderTarget target)
        throws ConnectorException;

    void pauseSystem(String systemId, Map<String, String> properties, ProviderTarget target) throws ConnectorException;

    void suspendSystem(String systemId, Map<String, String> properties, ProviderTarget target) throws ConnectorException;

    System getSystem(String systemId, ProviderTarget target) throws ConnectorException;

    System.State getSystemState(String systemId, ProviderTarget target) throws ConnectorException;

    List<? extends CloudCollectionItem> getEntityListFromSystem(String systemId, String entityType, ProviderTarget target)
        throws ConnectorException;

    void deleteEntityInSystem(String systemId, String entityId, String entityType, ProviderTarget target)
        throws ConnectorException;

    void removeEntityFromSystem(String systemId, String entityId, ProviderTarget target) throws ConnectorException;

    void addEntityToSystem(final String systemId, final String entityId, ProviderTarget target) throws ConnectorException;

}
