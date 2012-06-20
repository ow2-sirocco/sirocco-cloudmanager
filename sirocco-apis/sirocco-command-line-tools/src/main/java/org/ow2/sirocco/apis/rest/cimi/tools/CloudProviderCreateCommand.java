package org.ow2.sirocco.apis.rest.cimi.tools;

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
        provider.setEndPoint(this.endpoint);

        provider = cloudProviderManager.createCloudProvider(provider);

        Table table = new Table(4);
        table.addCell("Cloud Provider ID");
        table.addCell("Type");
        table.addCell("Endpoint");
        table.addCell("Description");

        table.addCell(provider.getId().toString());
        table.addCell(provider.getCloudProviderType());
        table.addCell(provider.getEndPoint());
        table.addCell(provider.getDescription());

        System.out.println(table.render());
    }
}