package org.ow2.sirocco.apis.rest.cimi.request;

import java.io.Serializable;

public abstract class CimiParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Prepare the parameters.
     */
    protected abstract void prepare();

}