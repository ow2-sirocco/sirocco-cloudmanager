package org.ow2.sirocco.apis.rest.cimi.tools;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "list cloud providers")
public class CloudProviderListCommand implements Command {
    public static String COMMAND_NAME = "cloud-provider-list";

    @Override
    public String getName() {
        return CloudProviderListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final CimiClient cimiClient) throws Exception {
        Context context = new InitialContext();
        IRemoteCloudProviderManager cloudProviderManager = (IRemoteCloudProviderManager) context
            .lookup(ICloudProviderManager.EJB_JNDI_NAME);

        List<CloudProvider> providers = cloudProviderManager.getCloudProviders();

        Table table = new Table(4);
        table.addCell("Cloud Provider ID");
        table.addCell("Type");
        table.addCell("Endpoint");
        table.addCell("Description");

        for (CloudProvider provider : providers) {
            table.addCell(provider.getId().toString());
            table.addCell(provider.getCloudProviderType());
            table.addCell(provider.getEndPoint());
            table.addCell(provider.getDescription());
        }

        System.out.println(table.render());
    }
}