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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;

/**
 * Data container for a CIMI request.
 */
public class CimiRequest {

    private RequestHeader header;

    private String id;

    private CimiData cimiData;

    private String baseUri;

    private String path;

    private String method;

    private CimiContext context;

    public RequestHeader getHeader() {
        return this.header;
    }

    public void setHeader(final RequestHeader header) {
        this.header = header;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public CimiData getCimiData() {
        return this.cimiData;
    }

    public void setCimiData(final CimiData cimiData) {
        this.cimiData = cimiData;
    }

    public String getBaseUri() {
        return this.baseUri;
    }

    public void setBaseUri(final String baseUri) {
        this.baseUri = baseUri;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    /**
     * @return the context
     */
    public CimiContext getContext() {
        return this.context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(final CimiContext context) {
        this.context = context;
    }

}
