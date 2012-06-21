package org.ow2.sirocco.apis.rest.cimi.tools;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "add cloud provider account to user")
public class CloudProviderAccountAdd implements Command {
    public static String COMMAND_NAME = "cloud-provider-account-add";

    @Parameter(names = "-account", description = "account id", required = true)
    private String accountId;

    @Parameter(names = "-user", description = "user id", required = false)
    private String userId;

    @Override
    public String getName() {
        return CloudProviderAccountAdd.COMMAND_NAME;
    }

    @Override
    public void execute(final CimiClient cimiClient) throws Exception {
        Context context = new InitialContext();
        IRemoteCloudProviderManager cloudProviderManager = (IRemoteCloudProviderManager) context
            .lookup(ICloudProviderManager.EJB_JNDI_NAME);

        cloudProviderManager.addCloudProviderAccountToUser(this.userId, this.accountId);

    }
}