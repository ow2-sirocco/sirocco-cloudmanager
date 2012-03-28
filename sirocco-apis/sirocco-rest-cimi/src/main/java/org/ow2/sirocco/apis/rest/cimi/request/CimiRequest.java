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

    private RequestHeader headerData;

    private String id;

    private CimiData cimiData;

    public RequestHeader getHeader() {
        return this.headerData;
    }

    public void setHeader(final RequestHeader headerData) {
        this.headerData = headerData;
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

}
