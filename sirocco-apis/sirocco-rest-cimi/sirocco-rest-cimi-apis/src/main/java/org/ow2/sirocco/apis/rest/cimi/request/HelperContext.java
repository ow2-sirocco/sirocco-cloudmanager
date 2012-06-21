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
import org.ow2.sirocco.apis.rest.cimi.resource.RestResourceAbstract;

/**
 * Utility to build CIMI Context with the data of REST request.
 */
public class HelperContext {

    public static CimiContext buildContext(final RestResourceAbstract.JaxRsRequestInfos infos) {
        return HelperContext.buildContext(infos, null, null, null);
    }

    public static CimiContext buildContext(final RestResourceAbstract.JaxRsRequestInfos infos, final CimiData cimiData) {
        return HelperContext.buildContext(infos, null, null, cimiData);
    }

    public static CimiContext buildContext(final RestResourceAbstract.JaxRsRequestInfos infos, final String id) {
        return HelperContext.buildContext(infos, id, null, null);
    }

    public static CimiContext buildContext(final RestResourceAbstract.JaxRsRequestInfos infos, final String id,
        final CimiData cimiData) {
        return HelperContext.buildContext(infos, id, null, cimiData);
    }

    public static CimiContext buildContext(final RestResourceAbstract.JaxRsRequestInfos infos, final String id,
        final String idParent) {
        return HelperContext.buildContext(infos, id, idParent, null);
    }

    public static CimiContext buildContext(final RestResourceAbstract.JaxRsRequestInfos infos, final String id,
        final String idParent, final CimiData cimiData) {
        CimiRequest request = HelperRequest.buildRequest(infos, id, idParent, cimiData);
        CimiResponse response = new CimiResponse();
        return new CimiContextImpl(request, response);
    }
}
