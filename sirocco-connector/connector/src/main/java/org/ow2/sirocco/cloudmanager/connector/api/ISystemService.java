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
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;

public interface ISystemService {
    Job createSystem(SystemCreate systemCreate) throws ConnectorException;

    Job deleteSystem(String systemId) throws ConnectorException;

    Job startSystem(String systemId, Map<String, String> properties) throws ConnectorException;

    Job stopSystem(String systemId, boolean force, Map<String, String> properties) throws ConnectorException;

    Job restartSystem(String systemId, boolean force, Map<String, String> properties) throws ConnectorException;

    Job pauseSystem(String systemId, Map<String, String> properties) throws ConnectorException;

    Job suspendSystem(String systemId, Map<String, String> properties) throws ConnectorException;

    System getSystem(String systemId) throws ConnectorException;

    List<? extends CloudCollectionItem> getEntityListFromSystem(String systemId, String entityType) throws ConnectorException;

    Job removeEntityFromSystem(String systemId, String entityId) throws ConnectorException;

    Job addEntityToSystem(final String systemId, final String entityId) throws ConnectorException;

}
