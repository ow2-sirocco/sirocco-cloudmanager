package org.ow2.sirocco.cloudmanager.api.spec;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "systemTemplate")
@XmlRootElement(name = "systemTemplate")
public class SystemTemplateInfo {
    private String id;

    private String name;

    private String status;

    private String projectId;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "SystemTemplateInfo [id=" + this.id + ", name=" + this.name + ", status=" + this.status + ", projectId="
            + this.projectId + "]";
    }

}
