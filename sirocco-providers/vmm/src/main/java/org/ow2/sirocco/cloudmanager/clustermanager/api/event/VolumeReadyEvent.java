package org.ow2.sirocco.cloudmanager.clustermanager.api.event;

import java.io.Serializable;

public class VolumeReadyEvent implements Serializable {
    private static final long serialVersionUID = -5299373104843305793L;

    private String name;

    private String providerId;

    private Object handback;

    public VolumeReadyEvent(final String name, final String providerId, final Object handback) {
        super();
        this.name = name;
        this.providerId = providerId;
        this.handback = handback;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getProviderId() {
        return this.providerId;
    }

    public void setProviderId(final String providerId) {
        this.providerId = providerId;
    }

    public Object getHandback() {
        return this.handback;
    }

    public void setHandback(final Object handback) {
        this.handback = handback;
    }

    @Override
    public String toString() {
        return "VolumeReadyEvent [name=" + this.name + ", providerId=" + this.providerId + "]";
    }

}
