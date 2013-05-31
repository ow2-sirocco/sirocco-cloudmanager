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

import org.ow2.sirocco.cloudmanager.api.model.MultiCloudUser;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "create user")
public class UserCreateCommand implements Command {
    private static String COMMAND_NAME = "user-create";

    @Parameter(names = "-username", description = "user name", required = true)
    private String userName;

    @Parameter(names = "-password", description = "user password", required = true)
    private String password;

    @Parameter(names = "-email", description = "user email", required = false)
    private String email;

    @Parameter(names = "-firstname", description = "user first name", required = false)
    private String firstName;

    @Parameter(names = "-lastname", description = "user last name", required = false)
    private String lastName;

    @Override
    public String getName() {
        return UserCreateCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        MultiCloudUser user = new MultiCloudUser();
        user.setUserName(this.userName);
        user.setPassword(this.password);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);

        user = restClient.postCreateRequest("users", user, MultiCloudUser.class);
        System.out.println(user.getId());
    }

}
