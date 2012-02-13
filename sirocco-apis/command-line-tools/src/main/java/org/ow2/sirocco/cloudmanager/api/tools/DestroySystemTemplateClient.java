package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

import com.beust.jcommander.Parameter;

public class DestroySystemTemplateClient extends Client {
    @Parameter(names = "-systemTemplateId", description = "SystemTemplate Id", required = true)
    private String systemTemplateId;

    public DestroySystemTemplateClient() {
        this.commandName = "sirocco-systemtemplate-destroy";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        proxy.destroySystemTemplate(this.systemTemplateId);
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    public static void main(final String[] args) {
        new DestroySystemTemplateClient().run(args);
    }
}