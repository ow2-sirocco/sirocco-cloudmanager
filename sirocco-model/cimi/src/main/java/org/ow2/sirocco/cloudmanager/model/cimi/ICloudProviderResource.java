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
 *  $Id: Identifiable.java 1320 2012-06-18 13:39:45Z dangtran $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

/**
 * interface ICloudProvider, implemented by sirocco objects linked to resources
 * in providers, and so have a cloud provider account, location, id
 * 
 * @author ycas7461
 */
public interface ICloudProviderResource {

    // location
    CloudProviderLocation getLocation();

    void setLocation(final CloudProviderLocation location);

    // provider account
    CloudProviderAccount getCloudProviderAccount();

    void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount);

    // id of related provider resource
    String getProviderAssignedId();

    void setProviderAssignedId(final String providerAssignedId);

}
