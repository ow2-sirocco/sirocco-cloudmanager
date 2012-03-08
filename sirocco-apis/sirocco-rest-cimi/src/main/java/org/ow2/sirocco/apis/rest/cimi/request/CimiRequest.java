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
 * $Id: CimiRequest.java 108 2012-03-05 17:55:30Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.request;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;

public class CimiRequest {

    private RequestHeader headerData;

    private String id;

    private CimiData cimiData;

    public CimiRequest() {
    }

    public RequestHeader getHeader() {
        return headerData;
    }

    public void setHeader(RequestHeader headerData) {
        this.headerData = headerData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CimiData getCimiData() {
        return cimiData;
    }

    public void setCimiData(CimiData cimiData) {
        this.cimiData = cimiData;
    }

}
