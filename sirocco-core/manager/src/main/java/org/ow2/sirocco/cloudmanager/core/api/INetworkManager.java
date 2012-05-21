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
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.AddressCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.AddressTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;

public interface INetworkManager {

    // Network operations

    Job createNetwork(NetworkCreate networkCreate) throws InvalidRequestException, CloudProviderException;

    Job startNetwork(String networkId) throws ResourceNotFoundException, CloudProviderException;

    Job stopNetwork(String networkId) throws ResourceNotFoundException, CloudProviderException;

    Network getNetworkById(String networkId) throws ResourceNotFoundException;

    Network getNetworkAttributes(final String networkId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    List<Network> getNetworks(int first, int last, List<String> attributes) throws InvalidRequestException,
        CloudProviderException;

    List<Network> getNetworks(List<String> attributes, String filterExpression) throws InvalidRequestException,
        CloudProviderException;

    Job updateNetwork(Network network) throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job updateNetworkAttributes(String networkId, Map<String, Object> updatedAttributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    Job deleteNetwork(String networkId) throws ResourceNotFoundException, CloudProviderException;

    // NetworkConfiguration operations

    Job createNetworkConfiguration(NetworkConfiguration networkConfig) throws InvalidRequestException, CloudProviderException;

    NetworkConfiguration getNetworkConfigurationById(String networkConfigId) throws ResourceNotFoundException;

    NetworkConfiguration getNetworkConfigurationAttributes(final String networkConfigId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    List<NetworkConfiguration> getNetworkConfigurations(int first, int last, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    List<NetworkConfiguration> getNetworkConfigurations(List<String> attributes, String filterExpression)
        throws InvalidRequestException, CloudProviderException;

    Job updateNetworkConfiguration(NetworkConfiguration networkConfig) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    Job updateNetworkConfigurationAttributes(String networkConfigId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job deleteNetworkConfiguration(String networkConfigId) throws ResourceNotFoundException, CloudProviderException;

    // NetworkTemplate operations

    Job createNetworkTemplate(NetworkTemplate networkTemplate) throws InvalidRequestException, CloudProviderException;

    NetworkTemplate getNetworkTemplateById(String networkTemplateId) throws ResourceNotFoundException;

    NetworkTemplate getNetworkTemplateAttributes(final String networkTemplateId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    List<NetworkTemplate> getNetworkTemplates(int first, int last, List<String> attributes) throws InvalidRequestException,
        CloudProviderException;

    List<NetworkTemplate> getNetworkTemplates(List<String> attributes, String filterExpression) throws InvalidRequestException,
        CloudProviderException;

    Job updateNetworkTemplate(NetworkTemplate networkTemplate) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    Job updateNetworkTemplateAttributes(String networkTemplateId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job deleteNetworkTemplate(String networkConfigId) throws ResourceNotFoundException, CloudProviderException;

    // NetworkPort operations

    Job createNetworkPort(NetworkPortCreate networkPortCreate) throws InvalidRequestException, CloudProviderException;

    Job startNetworkPort(String networkPortId) throws ResourceNotFoundException, CloudProviderException;

    Job stopNetworkPort(String networkPortId) throws ResourceNotFoundException, CloudProviderException;

    NetworkPort getNetworkPortById(String networkPortId) throws ResourceNotFoundException;

    NetworkPort getNetworkPortAttributes(final String networkPortId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    List<NetworkPort> getNetworkPorts(int first, int last, List<String> attributes) throws InvalidRequestException,
        CloudProviderException;

    List<NetworkPort> getNetworkPorts(List<String> attributes, String filterExpression) throws InvalidRequestException,
        CloudProviderException;

    Job updateNetworkPort(NetworkPort networkPort) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    Job updateNetworkPortAttributes(String networkPortId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job deleteNetworkPort(String networkPortId) throws ResourceNotFoundException, CloudProviderException;

    // NetworkPortConfiguration operations

    Job createNetworkPortConfiguration(NetworkPortConfiguration networkPortConfiguration) throws InvalidRequestException,
        CloudProviderException;

    NetworkPortConfiguration getNetworkPortConfigurationById(String networkPortConfigurationId)
        throws ResourceNotFoundException;

    NetworkPortConfiguration getNetworkPortConfigurationAttributes(final String networkPortConfigurationId,
        List<String> attributes) throws ResourceNotFoundException, CloudProviderException;

    List<NetworkPortConfiguration> getNetworkPortConfigurations(int first, int last, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    List<NetworkPortConfiguration> getNetworkPortConfigurations(List<String> attributes, String filterExpression)
        throws InvalidRequestException, CloudProviderException;

    Job updateNetworkPortConfiguration(NetworkPortConfiguration networkPort) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    Job updateNetworkPortConfigurationAttributes(String networkPortConfigurationId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job deleteNetworkPortConfiguration(String networkPortConfigurationId) throws ResourceNotFoundException,
        CloudProviderException;

    // NetworkPortTemplate operations

    Job createNetworkPortTemplate(NetworkPortTemplate networkPortTemplate) throws InvalidRequestException,
        CloudProviderException;

    NetworkPortTemplate getNetworkPortTemplateById(String networkPortTemplateId) throws ResourceNotFoundException;

    NetworkPortTemplate getNetworkPortTemplateAttributes(final String networkPortTemplateId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    List<NetworkPortTemplate> getNetworkPortTemplates(int first, int last, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    List<NetworkPortTemplate> getNetworkPortTemplates(List<String> attributes, String filterExpression)
        throws InvalidRequestException, CloudProviderException;

    Job updateNetworkPortTemplate(NetworkPortTemplate networkPort) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    Job updateNetworkPortTemplateAttributes(String networkPortTemplateId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job deleteNetworkPortTemplate(String networkPortTemplateId) throws ResourceNotFoundException, CloudProviderException;

    // ForwardingGroup template operations

    Job createForwardingGroupTemplate(ForwardingGroupTemplate forwardingGroupTemplate) throws InvalidRequestException,
        CloudProviderException;

    ForwardingGroupTemplate getForwardingGroupTemplateById(String forwardingGroupTemplateId) throws ResourceNotFoundException;

    ForwardingGroupTemplate getForwardingGroupTemplateAttributes(final String forwardingGroupTemplateId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    List<ForwardingGroupTemplate> getForwardingGroupTemplates(int first, int last, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    List<ForwardingGroupTemplate> getForwardingGroupTemplates(List<String> attributes, String filterExpression)
        throws InvalidRequestException, CloudProviderException;

    Job updateForwardingGroupTemplate(ForwardingGroupTemplate forwardingGroupTemplate) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    Job updateForwardingGroupTemplateAttributes(String forwardingGroupTemplateId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job deleteForwardingGroupTemplate(String forwardingGroupTemplateId) throws ResourceNotFoundException,
        CloudProviderException;

    // Address operations

    Job createAddress(AddressCreate addressCreate) throws InvalidRequestException, CloudProviderException;

    Address getAddressById(String addressId) throws ResourceNotFoundException;

    Address getAddressAttributes(final String addressId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    List<Address> getAddresses(int first, int last, List<String> attributes) throws InvalidRequestException,
        CloudProviderException;

    List<Address> getAddresses(List<String> attributes, String filterExpression) throws InvalidRequestException,
        CloudProviderException;

    Job updateAddress(Address address) throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job updateAddressAttributes(String addressId, Map<String, Object> updatedAttributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    Job deleteAddress(String addressId) throws ResourceNotFoundException, CloudProviderException;

    // AddressTemplate operations

    Job createAddressTemplate(AddressTemplate addressTemplate) throws InvalidRequestException, CloudProviderException;

    AddressTemplate getAddressTemplateById(String addressTemplateId) throws ResourceNotFoundException;

    AddressTemplate getAddressTemplateAttributes(final String addressTemplateId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    List<AddressTemplate> getAddressTemplatees(int first, int last, List<String> attributes) throws InvalidRequestException,
        CloudProviderException;

    List<AddressTemplate> getAddressTemplatees(List<String> attributes, String filterExpression)
        throws InvalidRequestException, CloudProviderException;

    Job updateAddressTemplate(AddressTemplate addressTemplate) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    Job updateAddressTemplateAttributes(String addressTemplateId, Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    Job deleteAddressTemplate(String addressTemplateId) throws ResourceNotFoundException, CloudProviderException;

}
