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

import java.util.Map.Entry;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.ow2.sirocco.apis.rest.cimi.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to build REST response with datas of CIMI response.
 * 
 * @see CimiResponse
 */
public class HelperResponse {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(HelperResponse.class);

    /**
     * Build REST response with a CIMI response.
     * 
     * @param cimiResponse The CIMI response
     * @return The REST response
     */
    public static Response buildResponse(final CimiResponse cimiResponse) {
        ResponseBuilder builder = null;
        int status = cimiResponse.getStatus();

        // Make builder with stattus
        if (0 == status) {
            builder = Response.serverError();
            HelperResponse.LOGGER.error("CIMI Response Status is null");
        } else {
            builder = Response.status(cimiResponse.getStatus());
        }
        // Add entity (body)
        if (null != cimiResponse.getCimiData()) {
            builder.entity(cimiResponse.getCimiData());
        }
        // Add HEADER Response
        builder.header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI);
        if (null != cimiResponse.getHeaders()) {
            for (Entry<String, String> entry : cimiResponse.getHeaders().entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }
}
