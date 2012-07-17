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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;

/**
 * Data container for a CIMI request.
 */
public class CimiRequest implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** Parameters of the request */
    private RequestParams params;

    /** IDs of the request */
    private IdRequest idRequest = new IdRequest();

    /** Data of the request */
    private CimiData cimiData;

    /** Base URI of the request */
    private String baseUri;

    /** Complete path of the request */
    private String path;

    /** Method used by the request. */
    private String method;

    /**
     * Get the parameters of the request.
     * 
     * @return The parameters
     */
    public RequestParams getParams() {
        return this.params;
    }

    /**
     * Set the parameters of the request.
     * 
     * @param params Parameters of the request
     */
    public void setParams(final RequestParams params) {
        this.params = params;
    }

    /**
     * Get the data of the request.
     * 
     * @return The data
     */
    public CimiData getCimiData() {
        return this.cimiData;
    }

    /**
     * Set the data of the request.
     * 
     * @param cimiData The data
     */
    public void setCimiData(final CimiData cimiData) {
        this.cimiData = cimiData;
    }

    /**
     * Get the base URI of the request.
     * 
     * @return The Base URI
     */
    public String getBaseUri() {
        return this.baseUri;
    }

    /**
     * Set the base URI of the request.
     * 
     * @param baseUri The base URI
     */
    public void setBaseUri(final String baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * Get the complete path of the request.
     * 
     * @return The path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Set the complete path of the request.
     * 
     * @param path The path
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * Get the method used by the request.
     * 
     * @return The method used
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * Set the method used by the request.
     * 
     * @param method The method used
     */
    public void setMethod(final String method) {
        this.method = method;
    }

    /**
     * Get all IDs.
     * 
     * @return the idRequest
     */
    public IdRequest getIds() {
        return this.idRequest;
    }

    /**
     * Set all IDs.
     * 
     * @param idRequest The idRequest to set
     */
    public void setIds(final IdRequest idRequest) {
        this.idRequest = idRequest;
    }

    /**
     * Get true if one of parent ID is passed on REST request.
     * 
     * @return True if one of parent ID is passed on REST request.
     * @see IdRequest
     */
    public boolean hasParentIds() {
        boolean has = false;
        if (null != this.idRequest) {
            has = has || (null != this.idRequest.getId(IdRequest.Type.RESOURCE_PARENT));
            has = has || (null != this.idRequest.getId(IdRequest.Type.RESOURCE_GRAND_PARENT));
        }
        return has;
    }

    /**
     * Get a resource ID passed on REST request by type.
     * 
     * @param type The type of the ID to get
     * @return The requested ID or null if none ID exist in the given type
     * @see IdRequest.Type
     */
    public String getId(final IdRequest.Type type) {
        String id = null;
        if (null != this.idRequest) {
            id = this.idRequest.getId(type);
        }
        return id;
    }

    /**
     * Get resource ID passed on REST request.
     * 
     * @return The resource ID or null if none ID passed on
     */
    public String getId() {
        return this.getId(IdRequest.Type.RESOURCE);
    }

    /**
     * Get parent resource ID passed on REST request.
     * 
     * @return The parent resource ID or null if none parent ID passed on
     */
    public String getIdParent() {
        return this.getId(IdRequest.Type.RESOURCE_PARENT);
    }

}
