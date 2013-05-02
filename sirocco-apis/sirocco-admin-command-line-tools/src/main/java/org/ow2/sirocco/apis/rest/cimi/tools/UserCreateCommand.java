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
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.sun.appserv.security.ProgrammaticLogin;

@Parameters(commandDescription = "create user")
public class UserCreateCommand implements Command {
    public static String COMMAND_NAME = "user-create";

    @Parameter(names = "-login", description = "login", required = true)
    private String userLogin;

    @Parameter(names = "-password", description = "password", required = true)
    private String userPassword;

    @Parameter(names = "-v", description = "verbose", required = false)
    private boolean verbose;

    @Override
    public String getName() {
        return UserCreateCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final Context context) throws Exception {
        IRemoteUserManager userManager = (IRemoteUserManager) context.lookup(IRemoteUserManager.EJB_JNDI_NAME);

        User user = new User();
        user.setUsername(this.userLogin);
        user.setPassword(this.userPassword);
        user = userManager.createUser(user);

        if (this.verbose) {
            Table table = new Table(3);
            table.addCell("User ID");
            table.addCell("Login");
            table.addCell("Password");

            table.addCell(user.getId().toString());
            table.addCell(user.getUsername());
            table.addCell(user.getPassword());

            System.out.println(table.render());
        } else {
            System.out.println(user.getId().toString());
        }
    }

    public static void main(final String[] args) {
        try {
            Properties props = new Properties();
            props.setProperty("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
            props.setProperty("org.omg.CORBA.ORBInitialPort", "3700");

            ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
            programmaticLogin.login("admin", "admin");

            Context context = new InitialContext(props);

            UserCreateCommand create = new UserCreateCommand();
            create.userLogin = "guest";
            create.userPassword = "guest";
            create.execute(context);
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