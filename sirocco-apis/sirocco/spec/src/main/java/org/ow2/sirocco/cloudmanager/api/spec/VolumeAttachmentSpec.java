package org.ow2.sirocco.cloudmanager.api.spec;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "VolumeAttachmentSpec")
public class VolumeAttachmentSpec {
    private String vmId;

    public String getVmId() {
        return this.vmId;
    }

    public void setVmId(final String vmId) {
        this.vmId = vmId;
    }

}
