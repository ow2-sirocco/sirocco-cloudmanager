/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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
 */
package org.ow2.sirocco.cloudmanager.model.cimi.extension;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

/**
 * Information required to identify a resource on a provider
 */
@Embeddable
public class ProviderMapping implements Serializable {
    private static final long serialVersionUID = 1L;

    private String providerAssignedId;

    private CloudProviderAccount providerAccount;

    private CloudProviderLocation providerLocation;

    public String getProviderAssignedId() {
        return this.providerAssignedId;
    }

    public void setProviderAssignedId(final String providerAssignedId) {
        this.providerAssignedId = providerAssignedId;
    }

    @ManyToOne
    public CloudProviderAccount getProviderAccount() {
        return this.providerAccount;
    }

    public void setProviderAccount(final CloudProviderAccount providerAccount) {
        this.providerAccount = providerAccount;
    }

    @ManyToOne
    public CloudProviderLocation getProviderLocation() {
        return this.providerLocation;
    }

    public void setProviderLocation(final CloudProviderLocation providerLocation) {
        this.providerLocation = providerLocation;
    }

    public static ProviderMapping find(final IMultiCloudResource resource, final CloudProviderAccount providerAccount,
        final CloudProviderLocation location) {
        for (ProviderMapping mapping : resource.getProviderMappings()) {
            if (mapping.getProviderAccount().getId().equals(providerAccount.getId())) {
                if (mapping.getProviderLocation() == null) {
                    return mapping;
                }
                if (mapping.getProviderLocation().equals(location)) {
                    return mapping;
                }
            }
        }
        return null;
    }

    public static ProviderMapping find(final IMultiCloudResource resource, final String providerAccountId, final String country) {
        for (ProviderMapping mapping : resource.getProviderMappings()) {
            if (mapping.getProviderAccount().getId().toString().equals(providerAccountId)) {
                if (mapping.getProviderLocation() == null) {
                    return mapping;
                }
                if (mapping.getProviderLocation().getCountryName().equals(country)) {
                    return mapping;
                }
            }
        }
        return null;
    }
}
