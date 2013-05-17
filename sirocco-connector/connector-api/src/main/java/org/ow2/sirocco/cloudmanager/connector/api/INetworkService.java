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
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;

public interface INetworkService {
    //
    // Network operations
    //

    Network createNetwork(NetworkCreate networkCreate, ProviderTarget target) throws ConnectorException;

    Network getNetwork(String networkId, ProviderTarget target) throws ConnectorException;

    Network.State getNetworkState(String networkId, ProviderTarget target) throws ConnectorException;

    List<Network> getNetworks(ProviderTarget target) throws ConnectorException;

    void deleteNetwork(String networkId, ProviderTarget target) throws ConnectorException;

    void startNetwork(String networkId, ProviderTarget target) throws ConnectorException;

    void stopNetwork(String networkId, ProviderTarget target) throws ConnectorException;

    //
    // NetworkPort operations
    //

    NetworkPort createNetworkPort(NetworkPortCreate networkPortCreate, ProviderTarget target) throws ConnectorException;

    NetworkPort getNetworkPort(String networkPortId, ProviderTarget target) throws ConnectorException;

    void deleteNetworkPort(String networkPortId, ProviderTarget target) throws ConnectorException;

    void startNetworkPort(String networkPortId, ProviderTarget target) throws ConnectorException;

    void stopNetworkPort(String networkPortId, ProviderTarget target) throws ConnectorException;

    //
    // ForwardingGroup operations
    //

    ForwardingGroup createForwardingGroup(ForwardingGroupCreate forwardingGroupCreate, ProviderTarget target)
        throws ConnectorException;

    ForwardingGroup getForwardingGroup(String forwardingGroupId, ProviderTarget target) throws ConnectorException;

    void deleteForwardingGroup(String forwardingGroupId, ProviderTarget target) throws ConnectorException;

    void addNetworkToForwardingGroup(String forwardingGroupId, ForwardingGroupNetwork fgNetwork, ProviderTarget target)
        throws ConnectorException;

    void removeNetworkFromForwardingGroup(String forwardingGroupId, String networkId, ProviderTarget target)
        throws ConnectorException;

}
