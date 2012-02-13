package org.ow2.sirocco.cloudmanager.api.spec;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ServerSpec")
public class VolumeSpec {
    private String name;

    private String description;

    private long capacityInMB;

    private String projectId;

    private String location;

    private String cloudProviderAccountId;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public long getCapacityInMB() {
        return this.capacityInMB;
    }

    public void setCapacityInMB(final long capacityInMB) {
        this.capacityInMB = capacityInMB;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getCloudProviderAccountId() {
        return this.cloudProviderAccountId;
    }

    public void setCloudProviderAccountId(final String cloudProviderAccountId) {
        this.cloudProviderAccountId = cloudProviderAccountId;
    }

    @Override
    public String toString() {
        return "VolumeSpec [name=" + this.name + ", description=" + this.description + ", capacityInMB=" + this.capacityInMB
            + ", projectId=" + this.projectId + ", location=" + this.location + ", cloudProviderAccountId="
            + this.cloudProviderAccountId + "]";
    }

}
