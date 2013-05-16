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

import java.util.HashSet;
import java.util.Set;

import org.ow2.sirocco.cloudmanager.connector.api.IProviderCapability;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderCapability;

public class MockCloudProviderCapability implements IProviderCapability {

    private Set<CloudProviderCapability> capabilities = new HashSet<CloudProviderCapability>() {
        {
            this.add(CloudProviderCapability.MACHINE);
            this.add(CloudProviderCapability.SYSTEM);
            this.add(CloudProviderCapability.NETWORK);
            this.add(CloudProviderCapability.VOLUME);
        }
    };

    public MockCloudProviderCapability() {
    }

    @Override
    public boolean hasCapability(final CloudProviderCapability capability, final ProviderTarget target) {
        return this.capabilities.contains(capability);
    }

    @Override
    public void addCapability(final CloudProviderCapability capability, final ProviderTarget target) {
        // TODO Auto-generated method stub
        if (this.capabilities.contains(capability) == true) {
            return;
        }
        if (capability.hasParent() == true) {
            if (this.hasCapability(capability.getParent(), target) == false) {
                this.capabilities.add(capability.getParent());
            }
        }
        this.capabilities.add(capability);
    }

    @Override
    public void removeCapability(final CloudProviderCapability capability, final ProviderTarget target) {
        this.capabilities.remove(capability);
    }

}
