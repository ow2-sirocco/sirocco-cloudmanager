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

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.sun.appserv.security.ProgrammaticLogin;

public class AdminClient {
    private static String SIROCCO_ADMIN_USERNAME_ENV_NAME = "SIROCCO_ADMIN_USERNAME";

    private static String SIROCCO_ADMIN_PASSWORD_ENV_NAME = "SIROCCO_ADMIN_PASSWORD";

    private static String SIROCCO_ADMIN_HOST_ENV_NAME = "SIROCCO_ADMIN_HOST";

    private static String SIROCCO_ADMIN_PORT_ENV_NAME = "SIROCCO_ADMIN_PORT";

    public static final String GF_INITIAL_CONTEXT_FACTORY = "com.sun.enterprise.naming.SerialInitContextFactory";

    private static Command commands[] = {new UserCreateCommand(), new UserListCommand(), new CloudProviderCreateCommand(),
        new CloudProviderListCommand(), new CloudProviderAccountListCommand(), new CloudProviderAccountCreateCommand(),
        new CloudProviderAccountAddCommand(), new CloudProviderLocationCreateCommand(), new CloudProviderLocationListCommand(),
        new CloudProviderLocationAddCommand()};

    private AdminClient(final String[] args) {
        String login = System.getenv(AdminClient.SIROCCO_ADMIN_USERNAME_ENV_NAME);
        if (login == null) {
            System.err.println("SIROCCO_ADMIN_USERNAME environment variable not set");
            System.exit(1);
        }
        String password = System.getenv(AdminClient.SIROCCO_ADMIN_PASSWORD_ENV_NAME);
        if (password == null) {
            System.err.println("SIROCCO_ADMIN_PASSWORD environment variable not set");
            System.exit(1);
        }
        String host = System.getenv(AdminClient.SIROCCO_ADMIN_HOST_ENV_NAME);
        if (host == null) {
            System.err.println("SIROCCO_ADMIN_HOST environment variable not set");
            System.exit(1);
        }
        String port = System.getenv(AdminClient.SIROCCO_ADMIN_PORT_ENV_NAME);
        if (port == null) {
            System.err.println("SIROCCO_ADMIN_PORT environment variable not set");
            System.exit(1);
        }

        JCommander jCommander = new JCommander();
        for (Command command : AdminClient.commands) {
            jCommander.addCommand(command.getName(), command);
        }

        try {
            jCommander.parse(args);
            String commandName = jCommander.getParsedCommand();
            if (commandName == null) {
                this.printUsageAndExit(jCommander);
            }
            Command command = (Command) jCommander.getCommands().get(commandName).getObjects().get(0);

            Properties props = new Properties();

            props.setProperty("org.omg.CORBA.ORBInitialHost", host);
            props.setProperty("org.omg.CORBA.ORBInitialPort", port);
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, AdminClient.GF_INITIAL_CONTEXT_FACTORY);

            ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
            programmaticLogin.login(login, password);

            Context context = new InitialContext(props);

            command.execute(context);
        } catch (ParameterException ex) {
            this.printUsageAndExit(jCommander);
        } catch (CloudProviderException ex) {
            System.out.println("Request failed: " + ex.getMessage());
            System.exit(1);
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
            System.exit(1);
        }

    }

    private void printUsageAndExit(final JCommander jCommander) {
        StringBuilder sb = new StringBuilder();
        jCommander.usage(sb);
        System.out.println(sb.toString().replaceFirst("<main class>", "sirocco"));
        System.exit(1);
    }

    public static void main(final String[] args) {
        new AdminClient(args);
    }

}
