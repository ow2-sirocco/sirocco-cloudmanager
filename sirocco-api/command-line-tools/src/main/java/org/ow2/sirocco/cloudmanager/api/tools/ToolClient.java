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
package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.tools.RestClient.Options;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class ToolClient {
    private static String SIROCCO_USERNAME_ENV_NAME = "SIROCCO_USERNAME";

    private static String SIROCCO_PASSWORD_ENV_NAME = "SIROCCO_PASSWORD";

    private static String SIROCCO_TENANT_ID_ENV_NAME = "SIROCCO_TENANT_ID";

    private static String SIROCCO_ENDPOINT_URL_ENV_NAME = "SIROCCO_ENDPOINT_URL";

    @Parameter(names = "-debug", description = "turn on debug mode", required = false)
    private boolean debug;

    private static Command commands[] = {new ProviderListCommand(), new ProviderShowCommand(), new ProviderCreateCommand(),
        new ProviderDeleteCommand(), new ProviderLocationListCommand(), new AddLocationToProviderCommand(),
        new AccountAccessListCommand(), new AddAccountToTenantCommand(), new AddUserToTenantCommand(),
        new ProviderAccountCreateCommand(), new ProviderAccountDeleteCommand(), new ProviderAccountListCommand(),
        new RemoveAccountFromTenant(), new RemoveUserFromTenantCommand(), new TenantCreateCommand(), new TenantDeleteCommand(),
        new TenantListCommand(), new TenantShowCommand(), new UserCreateCommand(), new UserDeleteCommand(),
        new UserListCommand(), new UserShowCommand(), new UserTenantMembershipListCommand()};

    private ToolClient(final String[] args) {
        String userName = System.getenv(ToolClient.SIROCCO_USERNAME_ENV_NAME);
        if (userName == null) {
            System.err.println(ToolClient.SIROCCO_USERNAME_ENV_NAME + " environment variable not set");
            System.exit(1);
        }
        String password = System.getenv(ToolClient.SIROCCO_PASSWORD_ENV_NAME);
        if (password == null) {
            System.err.println(ToolClient.SIROCCO_PASSWORD_ENV_NAME + " environment variable not set");
            System.exit(1);
        }
        String endpointUrl = System.getenv(ToolClient.SIROCCO_ENDPOINT_URL_ENV_NAME);
        if (endpointUrl == null) {
            System.err.println(ToolClient.SIROCCO_ENDPOINT_URL_ENV_NAME + " environment variable not set");
            System.exit(1);
        }
        String tenantId = System.getenv(ToolClient.SIROCCO_TENANT_ID_ENV_NAME);

        JCommander jCommander = new JCommander();
        jCommander.addObject(this);
        for (Command command : ToolClient.commands) {
            jCommander.addCommand(command.getName(), command);
        }

        String commandName = null;
        try {
            jCommander.parse(args);
            commandName = jCommander.getParsedCommand();
            if (commandName == null) {
                // find command name if any
                for (String s : args) {
                    if (!s.startsWith("-")) {
                        commandName = s;
                        break;
                    }
                }
                if (commandName != null && jCommander.getCommands().get(commandName) == null) {
                    commandName = null;
                }
                this.printUsageAndExit(jCommander, commandName);
            }
            Command command = (Command) jCommander.getCommands().get(commandName).getObjects().get(0);

            Options options = Options.build();
            if (this.debug) {
                options.setDebug(true);
            }
            RestClient restClient = RestClient.login(endpointUrl, userName, password, tenantId, options);

            command.execute(restClient);
        } catch (ParameterException ex) {
            System.out.println(ex.getMessage());
            // find command name if any
            for (String s : args) {
                if (!s.startsWith("-")) {
                    commandName = s;
                    break;
                }
            }
            if (commandName != null && jCommander.getCommands().get(commandName) == null) {
                commandName = null;
            }
            this.printUsageAndExit(jCommander, commandName);
        } catch (ProviderException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    private void printUsageAndExit(final JCommander jCommander, final String commandName) {
        StringBuilder sb = new StringBuilder();
        if (commandName != null) {
            jCommander.usage(commandName, sb);
        } else {
            jCommander.usage(sb);
        }
        System.out.println(sb.toString().replaceFirst("<main class>", "sirocco"));
        System.exit(1);
    }

    public static void main(final String[] args) {
        new ToolClient(args);
    }

}
