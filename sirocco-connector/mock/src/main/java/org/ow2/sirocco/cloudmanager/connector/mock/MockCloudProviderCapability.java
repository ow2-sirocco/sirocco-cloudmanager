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
 *  $Id: $
 *
 */

package org.ow2.sirocco.cloudmanager.connector.mock;

import java.util.Set;
import java.util.HashSet;

import org.ow2.sirocco.cloudmanager.connector.api.IProviderCapability;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderCapability;

public class MockCloudProviderCapability implements IProviderCapability {
   
    private Set<CloudProviderCapability> capabilities = new HashSet<CloudProviderCapability>() {
    {
        add(CloudProviderCapability.MACHINE);
        add(CloudProviderCapability.SYSTEM);
        add(CloudProviderCapability.NETWORK);
        add(CloudProviderCapability.VOLUME);
    }};

    public MockCloudProviderCapability() {
    }

    
    @Override
    public boolean hasCapability(CloudProviderCapability capability) {
        return  capabilities.contains(capability);
    }

    @Override
    public void addCapability(CloudProviderCapability capability) {
        // TODO Auto-generated method stub
        if (capabilities.contains(capability) == true) {
            return;
        }
        if (capability.hasParent() == true) {
            if (hasCapability(capability.getParent()) == false) {
                capabilities.add(capability.getParent());
            }
        }
        capabilities.add(capability);
    }

    @Override
    public void removeCapability(CloudProviderCapability capability) {
        capabilities.remove(capability);
    }
    
}
