package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

public class PurgeMachinesClient extends Client {

    public PurgeMachinesClient() {
        this.commandName = "sirocco-admin-machine-purge";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        proxy.purgeAllMachines();
    }

    @Override
    protected Object getOptions() {
        return null;
    }

    public static void main(final String[] args) {
        new PurgeMachinesClient().run(args);
    }
}