/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;

/**
 * Data container for a CIMI response.
 */
public class CimiResponse implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** CIMI Data */
    private CimiData cimiData;

    /** Error message */
    private String errorMessage;

    /** Response status */
    private int status;

    /** Committed response */
    private boolean committed = false;

    /** Item to add to HTTP response header. */
    private Map<String, String> headers;

    /**
     * Get the error message.
     * 
     * @return The error message
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Set the error message.
     * 
     * @param errorMessage The error message
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get the CIMI data.
     * 
     * @return The CIMI data
     */
    public CimiData getCimiData() {
        return this.cimiData;
    }

    /**
     * Set the CIMI data.
     * 
     * @param cimiData The CIMI data
     */
    public void setCimiData(final CimiData cimiData) {
        this.cimiData = cimiData;
    }

    /**
     * Get the status code.
     * 
     * @return The status code
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * Set the status code and commit the response.
     * 
     * @param status The status code
     */
    public void setStatus(final int status) {
        this.status = status;
        this.committed = true;
    }

    /**
     * Set the status code and commit the response.
     * 
     * @param status The status code
     */
    public void setStatus(final Status status) {
        this.setStatus(status.getStatusCode());
    }

    /**
     * Returns a boolean indicating if the response has been committed.
     * <p>
     * A committed response has already had its status code.
     * </p>
     * 
     * @return the committed
     */
    public boolean isCommitted() {
        return this.committed;
    }

    /**
     * Get the items to add to the HTTP header.
     * 
     * @return The items
     */
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * Set the items to add to the HTTP header.
     * 
     * @param headers The headers to set
     */
    public void setHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Put a item to add to the HTTP header.
     * 
     * @param key The key in HTTP Header response
     * @param value The value in HTTP Header response
     */
    public void putHeader(final String key, final String value) {
        if (null == this.headers) {
            this.headers = new HashMap<String, String>();
        }
        this.headers.put(key, value);
    }

}
