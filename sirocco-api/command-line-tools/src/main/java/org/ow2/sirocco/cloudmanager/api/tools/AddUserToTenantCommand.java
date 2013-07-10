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
package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.model.UserTenantMembership;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "add user to tenant")
public class AddUserToTenantCommand implements Command {
    private static String COMMAND_NAME = "tenant-user-add";

    @Parameter(names = "-tenantId", description = "tenant id", required = true)
    private String tenantId;

    @Parameter(names = "-userId", description = "user id", required = true)
    private String userId;

    @Override
    public String getName() {
        return AddUserToTenantCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        UserTenantMembership membership = new UserTenantMembership();
        membership.setTenantId(this.tenantId);
        membership.setUserId(this.userId);

        membership = restClient
            .postCreateRequest("tenants/" + this.tenantId + "/users", membership, UserTenantMembership.class);
    }

}
