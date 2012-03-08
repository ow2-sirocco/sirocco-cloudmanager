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
 * $Id: HelperRequest.java 128 2012-03-08 00:27:00Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.request;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;
import org.ow2.sirocco.apis.rest.cimi.utils.Constants;

public class HelperRequest {

    public static CimiRequest buildRequest(HttpHeaders headers, UriInfo uri) {
        return buildRequest(headers, uri, null, null);
    }

    public static CimiRequest buildRequest(HttpHeaders headers, UriInfo uri, CimiData cimiData) {
        return buildRequest(headers, uri, null, cimiData);
    }

    public static CimiRequest buildRequest(HttpHeaders headers, UriInfo uri, String id) {
        return buildRequest(headers, uri, id, null);
    }

    public static CimiRequest buildRequest(HttpHeaders headers, UriInfo uri, String id, CimiData cimiData) {
        CimiRequest request = new CimiRequest();
        request.setHeader(buildRequestHeader(headers, uri));
        request.setId(id);
        request.setCimiData(cimiData);
        return request;
    }

    // TODO A passer en privé
    public static RequestHeader buildRequestHeader(HttpHeaders headers, UriInfo uri) {
        RequestHeader requestHeader = new RequestHeader();
        List<String> versions = headers.getRequestHeader(Constants.HEADER_CIMI_VERSION);
        if ((null != versions) && (versions.size() > 0)) {
            requestHeader.setVersion(versions.get(0));
        }

        requestHeader.setCimiSelect(new CimiSelect(transformQueryParamToList(uri.getQueryParameters())));
        requestHeader.setBaseUri(uri.getBaseUri().toString());
        requestHeader.setPath(uri.getPath());
        return requestHeader;
    }

    // TODO A passer en privé ou à supprimer
    public static RequestHeader buildRequestHeader(HttpHeaders headers, UriInfo uri, String id) {
        return buildRequestHeader(headers, uri);
    }

    // TODO A passer en privé ou à supprimer
    public static RequestHeader buildRequestHeader(HttpHeaders headers, UriInfo uri, CimiData cimiData) {
        return buildRequestHeader(headers, uri);
    } // TODO A passer en privé

    // TODO A passer en privé ou à supprimer
    public static RequestHeader buildRequestHeader(HttpHeaders headers, UriInfo uri, String id, CimiData cimiData) {
        return buildRequestHeader(headers, uri);
    }

    private static List<String> transformQueryParamToList(MultivaluedMap<String, String> queryParameters) {
        List<String> listSelect = new ArrayList<String>();
        listSelect = queryParameters.get(Constants.HEADER_CIMI_SELECT);
        return listSelect;
    }

}
