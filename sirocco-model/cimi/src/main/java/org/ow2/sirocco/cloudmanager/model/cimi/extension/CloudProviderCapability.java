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

package org.ow2.sirocco.cloudmanager.model.cimi.extension;



/**
 * A capability represents a feature or a feature set provided by an IaaS provider.
 */

public class CloudProviderCapability {
    
    
    public static final CloudProviderCapability MACHINE = new CloudProviderCapability(null, "machine");
   
    public static final CloudProviderCapability VOLUME = new CloudProviderCapability(null, "volume");

    public static final CloudProviderCapability NETWORK = new CloudProviderCapability(null, "network");
    
    public static final CloudProviderCapability SYSTEM = new CloudProviderCapability(null, "system");
    public static final CloudProviderCapability SYSTEMCOMPOSE = new CloudProviderCapability(SYSTEM, "compose");

    
    private final CloudProviderCapability parent;
    private final String     name;

    CloudProviderCapability(CloudProviderCapability parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public boolean hasParent() {
        if (parent != null) {
            return true;
        }
        return false;
    }

    public CloudProviderCapability getParent() {
        return parent;
    }
    public String getName() {
        return name;
    }
}
