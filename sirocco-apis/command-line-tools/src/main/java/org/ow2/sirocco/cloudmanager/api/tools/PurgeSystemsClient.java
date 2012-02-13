package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

public class PurgeSystemsClient extends Client {

    public PurgeSystemsClient() {
        this.commandName = "sirocco-admin-system-purge";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        proxy.purgeAllSystems();
    }

    @Override
    protected Object getOptions() {
        return null;
    }

    public static void main(final String[] args) {
        new PurgeSystemsClient().run(args);
    }
}