package org.ow2.sirocco.cloudmanager.core.impl.command;

import java.io.Serializable;

import org.ow2.sirocco.cloudmanager.model.cimi.Job;

public class ResourceCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandType;

    private String resourceId;

    private Job job;

    public ResourceCommand(final String commandType) {
        this.commandType = commandType;
    }

    public Job getJob() {
        return this.job;
    }

    public ResourceCommand setJob(final Job job) {
        this.job = job;
        return this;
    }

    public String getCommandType() {
        return this.commandType;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public ResourceCommand setResourceId(final String resourceId) {
        this.resourceId = resourceId;
        return this;
    }

}
