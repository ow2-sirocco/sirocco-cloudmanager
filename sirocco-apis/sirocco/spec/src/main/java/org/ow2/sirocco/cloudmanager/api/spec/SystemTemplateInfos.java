package org.ow2.sirocco.cloudmanager.api.spec;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "systemTemplates")
@XmlRootElement(name = "systemTemplates")
public class SystemTemplateInfos {

    private Collection<SystemTemplateInfo> systemTemplateInfos;

    public Collection<SystemTemplateInfo> getSystemTemplateInfo() {
        return this.systemTemplateInfos;
    }

    public void setSystemTemplateInfo(final Collection<SystemTemplateInfo> systemTemplateInfos) {
        this.systemTemplateInfos = systemTemplateInfos;
    }
}
