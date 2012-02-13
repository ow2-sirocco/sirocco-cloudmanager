package org.ow2.sirocco.cloudmanager.api.spec;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "systems")
@XmlRootElement(name = "systems")
public class SystemInfos {

    private Collection<SystemInfo> systemInfos;

    public Collection<SystemInfo> getSystemInfo() {
        return this.systemInfos;
    }

    public void setSystemInfo(final Collection<SystemInfo> systemInfos) {
        this.systemInfos = systemInfos;
    }
}
