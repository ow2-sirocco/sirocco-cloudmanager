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
 * $Id: HelperRequest.java 111 2012-03-06 00:20:18Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.request;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.ow2.sirocco.apis.rest.cimi.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelperResponse {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(HelperResponse.class);

    public static Response buildResponse(CimiResponse cimiResponse) {
        ResponseBuilder builder = null;

        if (null == cimiResponse.getStatus()) {
            builder = Response.serverError();
            LOGGER.error("CIMI Response Status is null");
        } else {
            switch (cimiResponse.getStatus()) {
                case OK:
                    builder = Response.ok(cimiResponse.getCimiData());
                    break;

                default:
                    builder = Response.status(cimiResponse.getStatus());
                    break;
            }
        }
        builder.header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI);
        return builder.build();

    }
}
