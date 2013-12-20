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
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupRule;

/**
 * Lifecycle operations on Network resources
 */
public interface INetworkService {
    //
    // Network operations
    //

    /**
     * Creates a network
     * 
     * @param networkCreate
     * @param target
     * @return
     * @throws ConnectorException
     */
    Network createNetwork(NetworkCreate networkCreate, ProviderTarget target) throws ConnectorException;

    /**
     * Gets a network
     * 
     * @param networkId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    Network getNetwork(String networkId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Gets a network states
     * 
     * @param networkId
     * @param target
     * @return
     * @throws ConnectorException
     */
    Network.State getNetworkState(String networkId, ProviderTarget target) throws ConnectorException;

    /**
     * Gets all networks
     * 
     * @param target
     * @return
     * @throws ConnectorException
     */
    List<Network> getNetworks(ProviderTarget target) throws ConnectorException;

    /**
     * Deletes a network
     * 
     * @param networkId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void deleteNetwork(String networkId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Starts a network
     * 
     * @param networkId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void startNetwork(String networkId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Stops a network
     * 
     * @param networkId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void stopNetwork(String networkId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    //
    // NetworkPort operations
    //

    /**
     * Creates a network port
     * 
     * @param networkPortCreate
     * @param target
     * @return
     * @throws ConnectorException
     */
    NetworkPort createNetworkPort(NetworkPortCreate networkPortCreate, ProviderTarget target) throws ConnectorException;

    /**
     * Gets a network port
     * 
     * @param networkPortId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    NetworkPort getNetworkPort(String networkPortId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Deletes a network port
     * 
     * @param networkPortId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void deleteNetworkPort(String networkPortId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Starts a network port
     * 
     * @param networkPortId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void startNetworkPort(String networkPortId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Stops a network port
     * 
     * @param networkPortId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void stopNetworkPort(String networkPortId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    //
    // ForwardingGroup operations
    //

    /**
     * Creates a forwarding group
     * 
     * @param forwardingGroupCreate
     * @param target
     * @return
     * @throws ConnectorException
     */
    ForwardingGroup createForwardingGroup(ForwardingGroupCreate forwardingGroupCreate, ProviderTarget target)
        throws ConnectorException;

    /**
     * Gets a forwarding group
     * 
     * @param forwardingGroupId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    ForwardingGroup getForwardingGroup(String forwardingGroupId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Deletes a forwarding group
     * 
     * @param forwardingGroupId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void deleteForwardingGroup(ForwardingGroup forwardingGroup, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Adds a network to a forwarding group
     * 
     * @param forwardingGroupId
     * @param fgNetwork
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void addNetworkToForwardingGroup(String forwardingGroupId, ForwardingGroupNetwork fgNetwork, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

    /**
     * Removes a network from a forwarding group
     * 
     * @param forwardingGroupId
     * @param networkId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void removeNetworkFromForwardingGroup(String forwardingGroupId, String networkId, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

    String createSecurityGroup(SecurityGroupCreate create, ProviderTarget target) throws ConnectorException;

    void deleteSecurityGroup(String groupId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    SecurityGroup getSecurityGroup(String groupId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    List<SecurityGroup> getSecurityGroups(ProviderTarget target) throws ConnectorException;

    String addRuleToSecurityGroup(String groupId, SecurityGroupRule rule, ProviderTarget target) throws ConnectorException;

    void deleteRuleFromSecurityGroup(String groupId, SecurityGroupRule rule, ProviderTarget target) throws ConnectorException;

}
