package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

public class PurgeSystemTemplatesClient extends Client {

    public PurgeSystemTemplatesClient() {
        this.commandName = "sirocco-admin-systemtemplate-purge";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        proxy.purgeAllSystemTemplates();
    }

    @Override
    protected Object getOptions() {
        return null;
    }

    public static void main(final String[] args) {
        new PurgeSystemTemplatesClient().run(args);
    }
}