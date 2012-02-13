package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.SystemInfo;
import org.ow2.sirocco.cloudmanager.api.spec.SystemSpec;
import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

import com.beust.jcommander.Parameter;

public class CreateSystemClient extends Client {
    @Parameter(names = "-project", description = "project Id", required = true)
    private String projectId;

    @Parameter(names = "-templateId", description = "template Id", required = true)
    private String systemTemplateId;

    @Parameter(names = "-accountId", description = "Cloud Provider Account Id", required = true)
    private String cloudProviderAccountId;

    public CreateSystemClient() {
        this.commandName = "sirocco-system-create";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        SystemSpec systemSpec = new SystemSpec();
        systemSpec.setSystemTemplateId(this.systemTemplateId);
        systemSpec.setCloudProviderAccountId(this.cloudProviderAccountId);
        SystemInfo system = proxy.createSystem(this.projectId, systemSpec);

        System.out.format("%-12s %-13s %-12s %-12s\n", "Id", "Template", "Status", "Project");
        System.out.format("%-12s %-13s %-12s %-12s\n", system.getId(), system.getSystemTemplateId(), system.getStatus(),
            system.getProjectId());
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    public static void main(final String[] args) {
        new CreateSystemClient().run(args);
    }

}