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

import java.util.Set;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

/**
 * Performs the translation between the CIMI API and the IaaS provider-specific
 * API. A connector is specialized for a specific type of IaaS provider (e.g.
 * OpenStack or EC2). A connector is stateless. Information about the specific
 * IaaS instance to connect to (endpoint, credentials) is provided on a
 * per-operation basis (see {@link ProviderTarget}).
 * <p>
 * For convenience, operations on a connector are divided into the following
 * sub-services:
 * <p>
 * <ul>
 * <li>Compute Service: operations on machines
 * <li>Volume Service: operations on volumes
 * <li>System service: operations on systems
 * <li>Image service: operations on images
 * <li>Network service: operations of networks
 * </ul>
 * <p>
 * Note that a connector might raise an UnsupportedOperation exception for one
 * of these services if the underlying IaaS provider does not support the
 * corresponding IaaS resources.
 */
public interface ICloudProviderConnector {

    /**
     * Returns the locations supported by the provider associated with this
     * connector
     * 
     * @return set of locations supported by this provider associated with this
     *         connector
     */
    Set<CloudProviderLocation> getLocations();

    /**
     * Returns the Compute service of this connector
     * 
     * @return the Compute service of this connector
     * @throws ConnectorException raised if unsupported
     */
    IComputeService getComputeService() throws ConnectorException;

    /**
     * Returns the Volume service of this connector
     * 
     * @return the Volume service of this connector
     * @throws ConnectorException raised if unsupported
     */
    IVolumeService getVolumeService() throws ConnectorException;

    /**
     * Returns the Image service of this connector
     * 
     * @return the Image service of this connector
     * @throws ConnectorException raised if unsupported
     */
    IImageService getImageService() throws ConnectorException;

    /**
     * Return the Network Service of this connector
     * 
     * @return the Network Service of this connector
     * @throws ConnectorException raised if unsupported
     */
    INetworkService getNetworkService() throws ConnectorException;

    /**
     * Return the System service of this connector
     * 
     * @return the System service of this connector
     * @throws ConnectorException raised if unsupported
     */
    ISystemService getSystemService() throws ConnectorException;

}
