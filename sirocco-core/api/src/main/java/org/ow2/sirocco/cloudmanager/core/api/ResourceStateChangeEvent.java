package org.ow2.sirocco.cloudmanager.core.api;

import java.io.Serializable;
import java.util.Date;

import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;

/**
 * Indicates that a resource state has changed
 */
public class ResourceStateChangeEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CloudResource resource;

    private final Date timeStamp;

    public ResourceStateChangeEvent(final CloudResource resource) {
        super();
        this.resource = resource;
        this.timeStamp = new Date();
    }

    /**
     * Resource whose state has changed
     */
    public CloudResource getResource() {
        return this.resource;
    }

    /**
     * Time of the change
     */
    public Date getTimeStamp() {
        return this.timeStamp;
    }
}
