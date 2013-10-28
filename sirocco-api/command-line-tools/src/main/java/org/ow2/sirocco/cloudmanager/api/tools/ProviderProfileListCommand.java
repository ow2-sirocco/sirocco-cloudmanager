package org.ow2.sirocco.cloudmanager.api.tools;

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.cloudmanager.api.model.ProviderProfile;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "list provider profiles")
public class ProviderProfileListCommand implements Command {
    private static String COMMAND_NAME = "provider-profile-list";

    @Override
    public String getName() {
        return ProviderProfileListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        ProviderProfile.Collection profiles = restClient.getRequest("providers/profiles", ProviderProfile.Collection.class);

        Table table = new Table(3);
        table.addCell("Id");
        table.addCell("Type");
        table.addCell("Connector");

        for (ProviderProfile profile : profiles.getProviderProfiles()) {
            table.addCell(profile.getId());
            table.addCell(profile.getType());
            table.addCell(profile.getConnectorClass());
        }
        System.out.println(table.render());

    }

}
