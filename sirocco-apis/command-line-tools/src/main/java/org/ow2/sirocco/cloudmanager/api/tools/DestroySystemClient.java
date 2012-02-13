package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

import com.beust.jcommander.Parameter;

public class DestroySystemClient extends Client {
    @Parameter(names = "-systemId", description = "System Id", required = true)
    private String systemId;

    public DestroySystemClient() {
        this.commandName = "sirocco-system-destroy";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        proxy.destroySystem(this.systemId);
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    public static void main(final String[] args) {
        new DestroySystemClient().run(args);
    }
}