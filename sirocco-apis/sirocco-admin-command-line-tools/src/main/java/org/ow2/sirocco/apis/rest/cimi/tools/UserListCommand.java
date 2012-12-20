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

package org.ow2.sirocco.apis.rest.cimi.tools;

import java.rmi.AccessException;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

import com.beust.jcommander.Parameters;
import com.sun.appserv.security.ProgrammaticLogin;

@Parameters(commandDescription = "list users")
public class UserListCommand implements Command {
    public static String COMMAND_NAME = "user-list";

    @Override
    public String getName() {
        return UserListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final Context context) throws Exception {
        IRemoteUserManager userManager = (IRemoteUserManager) context.lookup(IRemoteUserManager.EJB_JNDI_NAME);

        List<User> users = userManager.getUsers();

        Table table = new Table(3);
        table.addCell("User ID");
        table.addCell("Login");
        table.addCell("Password");

        for (User user : users) {
            table.addCell(user.getId().toString());
            table.addCell(user.getUsername());
            table.addCell(user.getPassword());
        }

        System.out.println(table.render());
    }

    public static void main(final String[] args) {
        try {
            Properties props = new Properties();
            props.setProperty("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
            props.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, AdminClient.GF_INITIAL_CONTEXT_FACTORY);

            ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
            programmaticLogin.login("admin", "admin");

            Context context = new InitialContext(props);

            new UserListCommand().execute(context);
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            while (cause != null) {
                if (cause instanceof AccessException) {
                    System.err.println("Access denied");
                    break;
                }
                cause = cause.getCause();
            }
            if (cause == null) {
                ex.printStackTrace();
            }
        }
    }
}