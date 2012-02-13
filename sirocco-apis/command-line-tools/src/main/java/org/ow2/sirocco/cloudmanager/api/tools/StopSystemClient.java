package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

import com.beust.jcommander.Parameter;

public class StopSystemClient extends Client {
    @Parameter(names = "-systemId", description = "System Id", required = true)
    private String systemId;

    public StopSystemClient() {
        this.commandName = "sirocco-system-stop";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        proxy.stopSystem(this.systemId);
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    public static void main(final String[] args) {
        new StopSystemClient().run(args);
    }
}