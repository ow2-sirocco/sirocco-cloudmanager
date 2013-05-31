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

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.cloudmanager.api.model.MultiCloudUser;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "list users")
public class UserListCommand implements Command {
    private static String COMMAND_NAME = "user-list";

    @Override
    public String getName() {
        return UserListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        MultiCloudUser.Collection users = restClient.getRequest("users", MultiCloudUser.Collection.class);

        Table table = new Table(5);
        table.addCell("Id");
        table.addCell("UserName");
        table.addCell("Email");
        table.addCell("First Name");
        table.addCell("Last Name");

        for (MultiCloudUser user : users.getUsers()) {
            table.addCell(user.getId());
            table.addCell(user.getUserName());
            table.addCell(user.getEmail());
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
        }
        System.out.println(table.render());

    }

}
