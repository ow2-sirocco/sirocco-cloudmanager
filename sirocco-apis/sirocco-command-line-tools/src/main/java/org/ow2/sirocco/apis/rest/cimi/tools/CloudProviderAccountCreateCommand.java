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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "create cloud provider account")
public class CloudProviderAccountCreateCommand implements Command {
    public static String COMMAND_NAME = "cloud-provider-account-create";

    @Parameter(names = "-login", description = "login", required = true)
    private String login;

    @Parameter(names = "-password", description = "password", required = true)
    private String password;

    @Parameter(names = "-provider", description = "provider id", required = true)
    private String providerId;

    @Parameter(names = "-properties", variableArity = true, description = "key value pairs", required = false)
    private List<String> properties;

    @Override
    public String getName() {
        return CloudProviderAccountCreateCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final CimiClient cimiClient) throws Exception {
        Context context = new InitialContext();
        IRemoteCloudProviderManager cloudProviderManager = (IRemoteCloudProviderManager) context
            .lookup(ICloudProviderManager.EJB_JNDI_NAME);

        CloudProviderAccount account = new CloudProviderAccount();
        account.setLogin(this.login);
        account.setPassword(this.password);

        CloudProvider provider = cloudProviderManager.getCloudProviderById(this.providerId);

        account.setCloudProvider(provider);

        if (this.properties != null) {
            Map<String, String> props = new HashMap<String, String>();
            for (int i = 0; i < this.properties.size() / 2; i++) {
                props.put(this.properties.get(i * 2), this.properties.get(i * 2 + 1));
            }
            provider.setProperties(props);
        }

        account = cloudProviderManager.createCloudProviderAccount(account);

        Table table = new Table(4);
        table.addCell("Cloud Provider Account ID");
        table.addCell("Provider");
        table.addCell("Login");
        table.addCell("Password");

        table.addCell(account.getId().toString());
        table.addCell(account.getCloudProvider().getId().toString());
        table.addCell(account.getLogin());
        table.addCell(account.getPassword());

        System.out.println(table.render());
    }
}