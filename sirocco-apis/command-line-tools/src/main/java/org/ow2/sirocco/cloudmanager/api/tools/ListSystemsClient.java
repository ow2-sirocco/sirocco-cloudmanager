package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.SystemInfo;
import org.ow2.sirocco.cloudmanager.api.spec.SystemInfos;
import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

import com.beust.jcommander.Parameter;

public class ListSystemsClient extends Client {
    @Parameter(names = "-project", description = "project Id")
    private String projectId;

    public ListSystemsClient() {
        this.commandName = "sirocco-system-list";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        SystemInfos systems;
        if (this.projectId == null) {
            systems = proxy.listSystems();
        } else {
            systems = proxy.listSystems(this.projectId);
        }
        System.out.format("%-12s %-13s %-12s %-12s\n", "Id", "Template", "Status", "Project");
        for (SystemInfo system : systems.getSystemInfo()) {
            System.out.format("%-12s %-13s %-12s %-12s\n", system.getId(), system.getSystemTemplateId(), system.getStatus(),
                system.getProjectId());
        }
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    public static void main(final String[] args) {
        new ListSystemsClient().run(args);
    }

}