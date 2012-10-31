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

import javax.ws.rs.core.MediaType;

import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.Options;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class Client {
    private static String SIROCCO_USERNAME_ENV_NAME = "SIROCCO_USERNAME";

    private static String SIROCCO_PASSWORD_ENV_NAME = "SIROCCO_PASSWORD";

    private static String SIROCCO_ENDPOINT_URL_ENV_NAME = "SIROCCO_ENDPOINT_URL";

    @Parameter(names = "-debug", description = "turn on debug mode", required = false)
    private boolean debug;

    @Parameter(names = "-xml", description = "xml serialization", required = false)
    private boolean xml;

    private static Command commands[] = {new MachineCreateCommand(), new MachineShowCommand(), new MachineListCommand(),
        new MachineDeleteCommand(), new MachineStartCommand(), new MachineStopCommand(), new MachineImageCreateCommand(),
        new MachineImageShowCommand(), new MachineImageListCommand(), new MachineImageDeleteCommand(),
        new MachineConfigCreateCommand(), new MachineConfigShowCommand(), new MachineConfigListCommand(),
        new MachineConfigDeleteCommand(), new MachineTemplateCreateCommand(), new MachineTemplateShowCommand(),
        new MachineTemplateListCommand(), new MachineTemplateDeleteCommand(), new JobListCommand(), new JobShowCommand(),
        new CredentialCreateCommand(), new CredentialShowCommand(), new CredentialListCommand(), new CredentialDeleteCommand(),
        new VolumeConfigCreateCommand(), new VolumeConfigShowCommand(), new VolumeConfigListCommand(),
        new VolumeConfigDeleteCommand(), new VolumeTemplateCreateCommand(), new VolumeTemplateShowCommand(),
        new VolumeTemplateListCommand(), new VolumeTemplateDeleteCommand(), new VolumeCreateCommand(), new VolumeShowCommand(),
        new VolumeListCommand(), new VolumeDeleteCommand(), new SystemCreateCommand(), new SystemDeleteCommand(),
        new SystemListCommand(), new SystemShowCommand(), new SystemStartCommand(), new SystemStopCommand(),
        new SystemTemplateListCommand(), new SystemTemplateShowCommand(), new AddressListCommand(), new AddressShowCommand(),
        new NetworkListCommand(), new NetworkShowCommand()};

    private Client(final String[] args) {
        String userName = System.getenv(Client.SIROCCO_USERNAME_ENV_NAME);
        if (userName == null) {
            System.err.println("SIROCCO_USERNAME environment variable not set");
            System.exit(1);
        }
        String password = System.getenv(Client.SIROCCO_PASSWORD_ENV_NAME);
        if (password == null) {
            System.err.println("SIROCCO_PASSWORD environment variable not set");
            System.exit(1);
        }
        String endpointUrl = System.getenv(Client.SIROCCO_ENDPOINT_URL_ENV_NAME);
        if (endpointUrl == null) {
            System.err.println("SIROCCO_ENDPOINT_URL environment variable not set");
            System.exit(1);
        }

        JCommander jCommander = new JCommander();
        jCommander.addObject(this);
        for (Command command : Client.commands) {
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
            if (this.xml) {
                options.setMediaType(MediaType.APPLICATION_XML_TYPE);
            } else {
                options.setMediaType(MediaType.APPLICATION_JSON_TYPE);
            }
            CimiClient cimiClient = CimiClient.login(endpointUrl, userName, password, options);

            command.execute(cimiClient);
        } catch (ParameterException ex) {
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
        } catch (CimiException ex) {
            System.out.println("Request failed: " + ex.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            ex.printStackTrace();
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
        new Client(args);
    }

}
