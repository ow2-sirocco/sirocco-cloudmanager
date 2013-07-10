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
 *
 */
package org.ow2.sirocco.cloudmanager.api.model;

import java.util.List;

public class UserTenantMembership {
    private String userId;

    private String tenantId;

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public static class Collection {
        List<UserTenantMembership> userTenantMemberships;

        public List<UserTenantMembership> getUserTenantMemberships() {
            return this.userTenantMemberships;
        }

        public void setUserTenantMemberships(final List<UserTenantMembership> userTenantMemberships) {
            this.userTenantMemberships = userTenantMemberships;
        }
    }
}
