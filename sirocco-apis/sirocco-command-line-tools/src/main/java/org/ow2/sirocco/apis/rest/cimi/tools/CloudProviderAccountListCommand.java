package org.ow2.sirocco.apis.rest.cimi.tools;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "list cloud provider accounts")
public class CloudProviderAccountListCommand implements Command {
    public static String COMMAND_NAME = "cloud-provider-account-list";

    @Override
    public String getName() {
        return CloudProviderAccountListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final CimiClient cimiClient) throws Exception {
        Context context = new InitialContext();
        IRemoteCloudProviderManager cloudProviderManager = (IRemoteCloudProviderManager) context
            .lookup(ICloudProviderManager.EJB_JNDI_NAME);

        List<CloudProviderAccount> accounts = cloudProviderManager.getCloudProviderAccounts();

        Table table = new Table(4);
        table.addCell("Cloud Provider Account ID");
        table.addCell("Provider");
        table.addCell("Login");
        table.addCell("Password");

        for (CloudProviderAccount account : accounts) {
            table.addCell(account.getId().toString());
            table.addCell(account.getCloudProvider().getId().toString());
            table.addCell(account.getLogin());
            table.addCell(account.getPassword());
        }

        System.out.println(table.render());
    }
}