package org.ow2.sirocco.cloudmanager.api.spec;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "system")
@XmlRootElement(name = "system")
public class SystemInfo {
    private String id;

    private String status;

    private String projectId;

    private String systemTemplateId;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
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

    public String getSystemTemplateId() {
        return this.systemTemplateId;
    }

    public void setSystemTemplateId(final String systemTemplateId) {
        this.systemTemplateId = systemTemplateId;
    }

    @Override
    public String toString() {
        return "SystemInfo [id=" + this.id + ", status=" + this.status + ", projectId=" + this.projectId
            + ", systemTemplateId=" + this.systemTemplateId + "]";
    }

}
