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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "create cloud provider")
public class CloudProviderCreateCommand implements Command {
    public static String COMMAND_NAME = "cloud-provider-create";

    @Parameter(names = "-type", description = "type", required = true)
    private String type;

    @Parameter(names = "-description", description = "description", required = false)
    private String description;

    @Parameter(names = "-endpoint", description = "endpoint", required = true)
    private String endpoint;

    @Parameter(names = "-properties", variableArity = true, description = "key value pairs", required = false)
    private List<String> properties;

    @Override
    public String getName() {
        return CloudProviderCreateCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final CimiClient cimiClient) throws Exception {
        Context context = new InitialContext();
        IRemoteCloudProviderManager cloudProviderManager = (IRemoteCloudProviderManager) context
            .lookup(ICloudProviderManager.EJB_JNDI_NAME);

        CloudProvider provider = new CloudProvider();
        provider.setCloudProviderType(this.type);
        provider.setDescription(this.description);
        provider.setEndpoint(this.endpoint);

        if (this.properties != null) {
            Map<String, String> props = new HashMap<String, String>();
            for (int i = 0; i < this.properties.size() / 2; i++) {
                props.put(this.properties.get(i * 2), this.properties.get(i * 2 + 1));
            }
            provider.setProperties(props);
        }

        provider = cloudProviderManager.createCloudProvider(provider);

        Table table = new Table(4);
        table.addCell("Cloud Provider ID");
        table.addCell("Type");
        table.addCell("Endpoint");
        table.addCell("Description");

        table.addCell(provider.getId().toString());
        table.addCell(provider.getCloudProviderType());
        table.addCell(provider.getEndpoint());
        table.addCell(provider.getDescription());

        System.out.println(table.render());
    }
}