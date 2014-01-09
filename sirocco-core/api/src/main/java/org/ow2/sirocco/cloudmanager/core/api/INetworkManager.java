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

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.AddressCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.AddressTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkNetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupRule;

/**
 * Network management operations
 */
public interface INetworkManager {
    static final String EJB_JNDI_NAME = "java:global/sirocco/sirocco-core/NetworkManager!org.ow2.sirocco.cloudmanager.core.api.IRemoteNetworkManager";

    // Network operations

    Job createNetwork(NetworkCreate networkCreate) throws InvalidRequestException, CloudProviderException;

    void syncNetwork(int networkId, Network network, int jobId);

    Job startNetwork(String networkId, Map<String, String> properties) throws ResourceNotFoundException, CloudProviderException;

    Job stopNetwork(String networkId, Map<String, String> properties) throws ResourceNotFoundException, CloudProviderException;

    Job startNetwork(String networkId) throws ResourceNotFoundException, CloudProviderException;

    Job stopNetwork(String networkId) throws ResourceNotFoundException, CloudProviderException;

    Network getNetworkById(int networkId) throws ResourceNotFoundException;

    Network getNetworkByUuid(String networkId) throws ResourceNotFoundException;

    Network getNetworkByProviderAssignedId(String providerAssignedId);

    Network getNetworkAttributes(final String networkId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    QueryResult<Network> getNetworks(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    QueryResult<Network> getNetworks(QueryParams... queryParams) throws InvalidRequestException, CloudProviderException;

    Job updateNetwork(Network network) throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job updateNetworkAttributes(String networkId, Map<String, Object> updatedAttributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    Job deleteNetwork(String networkId) throws ResourceNotFoundException, CloudProviderException;

    Job addNetworkPortToNetwork(String networkId, NetworkNetworkPort networkPort) throws ResourceNotFoundException,
        CloudProviderException;

    Job removeNetworkPortFromNetwork(String networkId, String networkNetworkPortId) throws ResourceNotFoundException,
        CloudProviderException;

    QueryResult<NetworkNetworkPort> getNetworkNetworkPorts(String networkId, int first, int last, List<String> filters,
        List<String> attributes) throws InvalidRequestException, CloudProviderException;

    List<NetworkNetworkPort> getNetworkNetworkPorts(String networkId) throws ResourceNotFoundException, CloudProviderException;

    NetworkNetworkPort getNetworkPortFromNetwork(String networkId, String networkNetworkPortId)
        throws ResourceNotFoundException, CloudProviderException;

    void updateNetworkPortInNetwork(String networkId, NetworkNetworkPort networkPort) throws ResourceNotFoundException,
        CloudProviderException;

    // NetworkConfiguration operations

    NetworkConfiguration createNetworkConfiguration(NetworkConfiguration networkConfig) throws InvalidRequestException,
        CloudProviderException;

    NetworkConfiguration getNetworkConfigurationById(int networkConfigId) throws ResourceNotFoundException;

    NetworkConfiguration getNetworkConfigurationByUuid(String networkConfigUuid) throws ResourceNotFoundException;

    NetworkConfiguration getNetworkConfigurationAttributes(final String networkConfigId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    QueryResult<NetworkConfiguration> getNetworkConfigurations(int first, int last, List<String> filters,
        List<String> attributes) throws InvalidRequestException, CloudProviderException;

    QueryResult<NetworkConfiguration> getNetworkConfigurations(QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException;

    void updateNetworkConfiguration(NetworkConfiguration networkConfig) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    void updateNetworkConfigurationAttributes(String networkConfigId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    void deleteNetworkConfiguration(String networkConfigId) throws ResourceNotFoundException, ResourceConflictException,
        CloudProviderException;

    // NetworkTemplate operations

    NetworkTemplate createNetworkTemplate(NetworkTemplate networkTemplate) throws InvalidRequestException,
        CloudProviderException;

    NetworkTemplate getNetworkTemplateById(int networkTemplateId) throws ResourceNotFoundException;

    NetworkTemplate getNetworkTemplateByUuid(String networkTemplateUuid) throws ResourceNotFoundException;

    NetworkTemplate getNetworkTemplateAttributes(final String networkTemplateId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    QueryResult<NetworkTemplate> getNetworkTemplates(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    QueryResult<NetworkTemplate> getNetworkTemplates(QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException;

    void updateNetworkTemplate(NetworkTemplate networkTemplate) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    void updateNetworkTemplateAttributes(String networkTemplateId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    void deleteNetworkTemplate(String networkConfigId) throws ResourceNotFoundException, CloudProviderException;

    // NetworkPort operations

    Job createNetworkPort(NetworkPortCreate networkPortCreate) throws InvalidRequestException, CloudProviderException;

    Job startNetworkPort(String networkPortId, Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException;

    Job stopNetworkPort(String networkPortId, Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException;

    Job startNetworkPort(String networkPortId) throws ResourceNotFoundException, CloudProviderException;

    Job stopNetworkPort(String networkPortId) throws ResourceNotFoundException, CloudProviderException;

    NetworkPort getNetworkPortById(int networkPortId) throws ResourceNotFoundException;

    NetworkPort getNetworkPortByUuid(String networkPortUuid) throws ResourceNotFoundException;

    NetworkPort getNetworkPortAttributes(final String networkPortId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    QueryResult<NetworkPort> getNetworkPorts(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    QueryResult<NetworkPort> getNetworkPorts(QueryParams... params) throws InvalidRequestException, CloudProviderException;

    Job updateNetworkPort(NetworkPort networkPort) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    Job updateNetworkPortAttributes(String networkPortId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job deleteNetworkPort(String networkPortId) throws ResourceNotFoundException, CloudProviderException;

    // NetworkPortConfiguration operations

    NetworkPortConfiguration createNetworkPortConfiguration(NetworkPortConfiguration networkPortConfiguration)
        throws InvalidRequestException, CloudProviderException;

    List<NetworkPortConfiguration> getNetworkPortConfigurations() throws CloudProviderException;

    NetworkPortConfiguration getNetworkPortConfigurationById(int networkPortConfigurationId) throws ResourceNotFoundException;

    NetworkPortConfiguration getNetworkPortConfigurationByUuid(String networkPortConfigurationUuid)
        throws ResourceNotFoundException;

    NetworkPortConfiguration getNetworkPortConfigurationAttributes(final String networkPortConfigurationId,
        List<String> attributes) throws ResourceNotFoundException, CloudProviderException;

    QueryResult<NetworkPortConfiguration> getNetworkPortConfigurations(int first, int last, List<String> filters,
        List<String> attributes) throws InvalidRequestException, CloudProviderException;

    void updateNetworkPortConfiguration(NetworkPortConfiguration networkPort) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    void updateNetworkPortConfigurationAttributes(String networkPortConfigurationId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    void deleteNetworkPortConfiguration(String networkPortConfigurationId) throws ResourceNotFoundException,
        CloudProviderException;

    // NetworkPortTemplate operations

    NetworkPortTemplate createNetworkPortTemplate(NetworkPortTemplate networkPortTemplate) throws InvalidRequestException,
        CloudProviderException;

    List<NetworkPortTemplate> getNetworkPortTemplates() throws CloudProviderException;

    NetworkPortTemplate getNetworkPortTemplateById(int networkPortTemplateId) throws ResourceNotFoundException;

    NetworkPortTemplate getNetworkPortTemplateByUuid(String networkPortTemplateUuid) throws ResourceNotFoundException;

    NetworkPortTemplate getNetworkPortTemplateAttributes(final String networkPortTemplateId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    QueryResult<NetworkPortTemplate> getNetworkPortTemplates(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    void updateNetworkPortTemplate(NetworkPortTemplate networkPort) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    void updateNetworkPortTemplateAttributes(String networkPortTemplateId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    void deleteNetworkPortTemplate(String networkPortTemplateId) throws ResourceNotFoundException, CloudProviderException;

    // ForwardingGroupTemplate operations

    ForwardingGroupTemplate createForwardingGroupTemplate(ForwardingGroupTemplate forwardingGroupTemplate)
        throws InvalidRequestException, CloudProviderException;

    List<ForwardingGroupTemplate> getForwardingGroupTemplates() throws CloudProviderException;

    ForwardingGroupTemplate getForwardingGroupTemplateById(int forwardingGroupTemplateId) throws ResourceNotFoundException;

    ForwardingGroupTemplate getForwardingGroupTemplateByUuid(String forwardingGroupTemplateUuid)
        throws ResourceNotFoundException;

    ForwardingGroupTemplate getForwardingGroupTemplateAttributes(final String forwardingGroupTemplateId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    QueryResult<ForwardingGroupTemplate> getForwardingGroupTemplates(int first, int last, List<String> filters,
        List<String> attributes) throws InvalidRequestException, CloudProviderException;

    void updateForwardingGroupTemplate(ForwardingGroupTemplate forwardingGroupTemplate) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    void updateForwardingGroupTemplateAttributes(String forwardingGroupTemplateId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    void deleteForwardingGroupTemplate(String forwardingGroupTemplateId) throws ResourceNotFoundException,
        CloudProviderException;

    // ForwardingGroup operations

    Job createForwardingGroup(ForwardingGroupCreate forwardingGroupCreate) throws InvalidRequestException,
        CloudProviderException;

    ForwardingGroup getForwardingGroupById(int forwardingGroupId) throws ResourceNotFoundException;

    ForwardingGroup getForwardingGroupByUuid(String forwardingGroupUuid) throws ResourceNotFoundException;

    ForwardingGroup getForwardingGroupAttributes(final String forwardingGroupId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    QueryResult<ForwardingGroup> getForwardingGroups(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    QueryResult<ForwardingGroup> getForwardingGroups(QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException;

    Job updateForwardingGroup(ForwardingGroup forwardingGroup) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    Job updateForwardingGroupAttributes(String forwardingGroupId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job deleteForwardingGroup(String forwardingGroupId) throws ResourceNotFoundException, CloudProviderException;

    Job addNetworkToForwardingGroup(String forwardingGroupId, ForwardingGroupNetwork forwardingGroupNetwork)
        throws ResourceNotFoundException, CloudProviderException;

    Job removeNetworkFromForwardingGroup(String forwardingGroupId, String networkId) throws ResourceNotFoundException,
        CloudProviderException;

    QueryResult<ForwardingGroupNetwork> getForwardingGroupNetworks(String forwardingGroupId, int first, int last,
        List<String> filters, List<String> attributes) throws InvalidRequestException, CloudProviderException;

    List<ForwardingGroupNetwork> getForwardingGroupNetworks(String forwardingGroupId) throws ResourceNotFoundException,
        CloudProviderException;

    ForwardingGroupNetwork getNetworkFromForwardingGroup(String forwardingGroupId, String networkId)
        throws ResourceNotFoundException, CloudProviderException;

    void updateNetworkInForwardingGroup(String forwardingGroupId, ForwardingGroupNetwork forwardingGroupNetwork)
        throws ResourceNotFoundException, CloudProviderException;

    // Address operations

    Job createAddress(AddressCreate addressCreate) throws InvalidRequestException, CloudProviderException;

    Address getAddressById(int addressId) throws ResourceNotFoundException;

    Address getAddressByUuid(String addressUuid) throws ResourceNotFoundException;

    Address getAddressAttributes(final String addressId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    QueryResult<Address> getAddresses(QueryParams... queryParams) throws InvalidRequestException, CloudProviderException;

    Job updateAddress(Address address) throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job updateAddressAttributes(String addressId, Map<String, Object> updatedAttributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    Job deleteAddress(String addressId) throws ResourceNotFoundException, CloudProviderException;

    Job addAddressToMachine(String machineUuid, String ip) throws ResourceNotFoundException, CloudProviderException;

    Job removeAddressFromMachine(String machineUuid, String ip) throws ResourceNotFoundException, CloudProviderException;

    // AddressTemplate operations

    AddressTemplate createAddressTemplate(AddressTemplate addressTemplate) throws InvalidRequestException,
        CloudProviderException;

    List<AddressTemplate> getAddressTemplates() throws CloudProviderException;

    AddressTemplate getAddressTemplateById(int addressTemplateId) throws ResourceNotFoundException;

    AddressTemplate getAddressTemplateByUuid(String addressTemplateUuid) throws ResourceNotFoundException;

    AddressTemplate getAddressTemplateAttributes(final String addressTemplateId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    QueryResult<AddressTemplate> getAddressTemplates(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    void updateAddressTemplate(AddressTemplate addressTemplate) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    void updateAddressTemplateAttributes(String addressTemplateId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    void deleteAddressTemplate(String addressTemplateId) throws ResourceNotFoundException, CloudProviderException;

    void updateNetworkState(int networkId, Network.State state) throws CloudProviderException;

    // Security group operations

    Job createSecurityGroup(SecurityGroupCreate securityGroupCreate) throws InvalidRequestException, CloudProviderException;

    Job deleteSecurityGroup(String securityGroupUuid) throws CloudProviderException;

    SecurityGroupRule addRuleToSecurityGroupUsingIpRange(String securityGroupUuid, String cidr, String ipProtocol,
        int fromPort, int toPort) throws CloudProviderException;

    SecurityGroupRule addRuleToSecurityGroupUsingSourceGroup(String securityGroupUuid, String sourceGroupUuid,
        String ipProtocol, int fromPort, int toPort) throws CloudProviderException;

    void deleteRuleFromSecurityGroup(String securityGroupUuid, String ruleUuid) throws CloudProviderException;

    SecurityGroup getSecurityGroupByUuid(String groupUuid) throws ResourceNotFoundException;

    SecurityGroup getSecurityGroupById(int groupId) throws ResourceNotFoundException;

    QueryResult<SecurityGroup> getSecurityGroups(QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException;

    void updateSecurityGroupState(int securityGroupId, SecurityGroup.State state) throws CloudProviderException;

}
