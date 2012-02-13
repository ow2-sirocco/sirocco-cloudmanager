package org.ow2.sirocco.cloudmanager.api.spec;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SystemTemplateSpec")
public class SystemTemplateSpec {
    private String cloudProviderAccountId;

    private String url;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getCloudProviderAccountId() {
        return this.cloudProviderAccountId;
    }

    public void setCloudProviderAccountId(final String cloudProviderAccountId) {
        this.cloudProviderAccountId = cloudProviderAccountId;
    }

    @Override
    public String toString() {
        return "SystemTemplateSpec [cloudProviderAccountId=" + this.cloudProviderAccountId + ", url=" + this.url + "]";
    }

}
