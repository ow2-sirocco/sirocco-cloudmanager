package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.SystemTemplateInfo;
import org.ow2.sirocco.cloudmanager.api.spec.SystemTemplateInfos;
import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

import com.beust.jcommander.Parameter;

public class ListSystemTemplatesClient extends Client {
    @Parameter(names = "-project", description = "project Id")
    private String projectId;

    public ListSystemTemplatesClient() {
        this.commandName = "sirocco-systemtemplate-list";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        SystemTemplateInfos systemTemplates;
        if (this.projectId == null) {
            systemTemplates = proxy.listSystemTemplates();
        } else {
            systemTemplates = proxy.listSystemTemplates(this.projectId);
        }
        System.out.format("%-12s %-13s %-12s %-12s\n", "Id", "Name", "Status", "Project");
        for (SystemTemplateInfo systemTemplate : systemTemplates.getSystemTemplateInfo()) {
            System.out.format("%-12s %-13s %-12s %-12s\n", systemTemplate.getId(), systemTemplate.getName(),
                systemTemplate.getStatus(), systemTemplate.getProjectId());
        }
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    public static void main(final String[] args) {
        new ListSystemTemplatesClient().run(args);
    }

}