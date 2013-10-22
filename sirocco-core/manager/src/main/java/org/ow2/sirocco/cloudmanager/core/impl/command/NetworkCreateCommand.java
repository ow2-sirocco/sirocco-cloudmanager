package org.ow2.sirocco.cloudmanager.core.impl.command;

import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

public class NetworkCreateCommand extends ResourceCommand {
    private static final long serialVersionUID = 1L;

    public static final String NETWORK_CREATE = "networkCreate";

    private final NetworkCreate NetworkCreate;

    private CloudProviderAccount account;

    private CloudProviderLocation location;

    public NetworkCreateCommand(final NetworkCreate volumeCreate) {
        super(NetworkCreateCommand.NETWORK_CREATE);
        this.NetworkCreate = volumeCreate;
    }

    public NetworkCreate getNetworkCreate() {
        return this.NetworkCreate;
    }

    public CloudProviderAccount getAccount() {
        return this.account;
    }

    public NetworkCreateCommand setAccount(final CloudProviderAccount account) {
        this.account = account;
        return this;
    }

    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public NetworkCreateCommand setLocation(final CloudProviderLocation location) {
        this.location = location;
        return this;
    }

}
