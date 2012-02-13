package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.SystemTemplateInfo;
import org.ow2.sirocco.cloudmanager.api.spec.SystemTemplateSpec;
import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

import com.beust.jcommander.Parameter;

public class CreateSystemTemplateFromOVFClient extends Client {
    @Parameter(names = "-project", description = "project Id", required = true)
    private String projectId;

    @Parameter(names = "-url", description = "OVF URL", required = true)
    private String url;

    @Parameter(names = "-accountId", description = "Cloud Provider Account Id", required = true)
    private String cloudProviderAccountId;

    public CreateSystemTemplateFromOVFClient() {
        this.commandName = "sirocco-systemtemplate-import-ovf";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        SystemTemplateSpec systemTemplateSpec = new SystemTemplateSpec();
        systemTemplateSpec.setUrl(this.url);
        systemTemplateSpec.setCloudProviderAccountId(this.cloudProviderAccountId);
        SystemTemplateInfo systemTemplate = proxy.importOVF(this.projectId, systemTemplateSpec);

        System.out.format("%-12s %-13s %-12s %-12s\n", "Id", "Name", "Status", "Project");
        System.out.format("%-12s %-13s %-12s %-12s\n", systemTemplate.getId(), systemTemplate.getName(),
            systemTemplate.getStatus(), systemTemplate.getProjectId());
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    public static void main(final String[] args) {
        new CreateSystemTemplateFromOVFClient().run(args);
    }

}