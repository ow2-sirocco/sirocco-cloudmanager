package org.ow2.sirocco.cloudmanager.api.spec;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SystemSpec")
public class SystemSpec {
    private String cloudProviderAccountId;

    private String systemTemplateId;

    public String getCloudProviderAccountId() {
        return this.cloudProviderAccountId;
    }

    public void setCloudProviderAccountId(final String cloudProviderAccountId) {
        this.cloudProviderAccountId = cloudProviderAccountId;
    }

    public String getSystemTemplateId() {
        return this.systemTemplateId;
    }

    public void setSystemTemplateId(final String systemTemplateId) {
        this.systemTemplateId = systemTemplateId;
    }

    @Override
    public String toString() {
        return "SystemSpec [cloudProviderAccountId=" + this.cloudProviderAccountId + ", systemTemplateId="
            + this.systemTemplateId + "]";
    }

}
