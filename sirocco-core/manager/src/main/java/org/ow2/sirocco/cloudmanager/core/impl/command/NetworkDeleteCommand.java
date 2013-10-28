package org.ow2.sirocco.cloudmanager.core.impl.command;

public class NetworkDeleteCommand extends ResourceCommand {
    private static final long serialVersionUID = 1L;

    public static final String NETWORK_DELETE = "networkDelete";

    public NetworkDeleteCommand() {
        super(NetworkDeleteCommand.NETWORK_DELETE);
    }

}
