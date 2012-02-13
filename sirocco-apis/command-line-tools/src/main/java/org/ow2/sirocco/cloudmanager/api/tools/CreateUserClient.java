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

package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;
import org.ow2.sirocco.cloudmanager.api.spec.UserInfo;
import org.ow2.sirocco.cloudmanager.api.spec.UserSpec;

import com.beust.jcommander.Parameter;

public class CreateUserClient extends Client {
    @Parameter(names = "-login", description = "username", required = true)
    private String username;

    @Parameter(names = "-userpassword", description = "user password", required = true)
    private String userPassword;

    @Parameter(names = "-firstname", description = "first name", required = true)
    private String firstName;

    @Parameter(names = "-lastname", description = "last name", required = true)
    private String lastName;

    @Parameter(names = "-email", description = "email address", required = true)
    private String email;

    public CreateUserClient() {
        this.commandName = "sirocco-admin-user-create";
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        UserSpec userSpec = new UserSpec();
        userSpec.setUsername(this.username);
        userSpec.setPassword(this.userPassword);
        userSpec.setUserFirstName(this.firstName);
        userSpec.setUserLastName(this.lastName);
        userSpec.setUserMail(this.email);

        UserInfo userInfo = proxy.createUser(userSpec);
        System.out.println("User: " + userInfo);
    }

    public static void main(final String[] args) {
        new CreateUserClient().run(args);
    }

}
