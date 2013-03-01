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

import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;

public interface INetworkService {
    //
    // Network operations
    //

    Job createNetwork(NetworkCreate networkCreate) throws ConnectorException;

    Network getNetwork(String networkId) throws ConnectorException;

    List<Network> getNetworks() throws ConnectorException;

    Job deleteNetwork(String networkId) throws ConnectorException;

    Job startNetwork(String networkId) throws ConnectorException;

    Job stopNetwork(String networkId) throws ConnectorException;

    //
    // NetworkPort operations
    //

    Job createNetworkPort(NetworkPortCreate networkPortCreate) throws ConnectorException;

    NetworkPort getNetworkPort(String networkPortId) throws ConnectorException;

    Job deleteNetworkPort(String networkPortId) throws ConnectorException;

    Job startNetworkPort(String networkPortId) throws ConnectorException;

    Job stopNetworkPort(String networkPortId) throws ConnectorException;

    //
    // ForwardingGroup operations
    //

    Job createForwardingGroup(ForwardingGroupCreate forwardingGroupCreate) throws ConnectorException;

    ForwardingGroup getForwardingGroup(String forwardingGroupId) throws ConnectorException;

    Job deleteForwardingGroup(String forwardingGroupId) throws ConnectorException;

    Job addNetworkToForwardingGroup(String forwardingGroupId, ForwardingGroupNetwork fgNetwork) throws ConnectorException;

    Job removeNetworkFromForwardingGroup(String forwardingGroupId, String networkId) throws ConnectorException;

}
