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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;
import org.ow2.sirocco.apis.rest.cimi.resource.CimiResourceAbstract;
import org.ow2.sirocco.apis.rest.cimi.utils.Constants;

public class HelperRequest {

    public static CimiRequest buildRequest(final CimiResourceAbstract.JaxRsRequestInfos infos) {
        return HelperRequest.buildRequest(infos, null, null);
    }

    public static CimiRequest buildRequest(final CimiResourceAbstract.JaxRsRequestInfos infos, final CimiData cimiData) {
        return HelperRequest.buildRequest(infos, null, cimiData);
    }

    public static CimiRequest buildRequest(final CimiResourceAbstract.JaxRsRequestInfos infos, final String id) {
        return HelperRequest.buildRequest(infos, id, null);
    }

    public static CimiRequest buildRequest(final CimiResourceAbstract.JaxRsRequestInfos infos, final String id,
        final CimiData cimiData) {
        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(infos));
        request.setId(id);
        request.setCimiData(cimiData);
        request.setBaseUri(infos.getUriInfo().getBaseUri().toString());
        request.setPath(infos.getUriInfo().getPath());
        request.setMethod(infos.getRequest().getMethod());
        return request;
    }

    private static RequestHeader buildRequestHeader(final CimiResourceAbstract.JaxRsRequestInfos infos) {
        RequestHeader requestHeader = new RequestHeader();
        List<String> versions = infos.getHeaders().getRequestHeader(Constants.HEADER_CIMI_VERSION);
        if ((null != versions) && (versions.size() > 0)) {
            requestHeader.setVersion(versions.get(0));
        }

        requestHeader.setCimiSelect(new CimiSelect(HelperRequest.transformQueryParamToList(infos.getUriInfo()
            .getQueryParameters())));

        List<String> siroccoInfoTestsId = infos.getHeaders().getRequestHeader(Constants.HEADER_SIROCCO_INFO_TEST_ID);
        if ((null != siroccoInfoTestsId) && (siroccoInfoTestsId.size() > 0)) {
            requestHeader.setSiroccoInfoTestId(siroccoInfoTestsId.get(0));
        }
        List<String> siroccoInfoTestExpand = infos.getHeaders().getRequestHeader(Constants.HEADER_SIROCCO_INFO_TEST_EXPAND);
        if ((null != siroccoInfoTestExpand) && (siroccoInfoTestExpand.size() > 0)) {
            requestHeader.setSiroccoInfoTestExpand(siroccoInfoTestExpand.get(0));
        }

        return requestHeader;
    }

    public static CimiRequest buildRequest(final HttpHeaders headers, final UriInfo uri) {
        return HelperRequest.buildRequest(headers, uri, null, null);
    }

    public static CimiRequest buildRequest(final HttpHeaders headers, final UriInfo uri, final CimiData cimiData) {
        return HelperRequest.buildRequest(headers, uri, null, cimiData);
    }

    public static CimiRequest buildRequest(final HttpHeaders headers, final UriInfo uri, final String id) {
        return HelperRequest.buildRequest(headers, uri, id, null);
    }

    public static CimiRequest buildRequest(final HttpHeaders headers, final UriInfo uri, final String id,
        final CimiData cimiData) {
        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uri));
        request.setId(id);
        request.setCimiData(cimiData);
        request.setBaseUri(uri.getBaseUri().toString());
        request.setPath(uri.getPath());

        return request;
    }

    // TODO A passer en privé
    public static RequestHeader buildRequestHeader(final HttpHeaders headers, final UriInfo uri) {
        RequestHeader requestHeader = new RequestHeader();
        List<String> versions = headers.getRequestHeader(Constants.HEADER_CIMI_VERSION);
        if ((null != versions) && (versions.size() > 0)) {
            requestHeader.setVersion(versions.get(0));
        }

        requestHeader.setCimiSelect(new CimiSelect(HelperRequest.transformQueryParamToList(uri.getQueryParameters())));
        List<String> siroccoInfoTestsId = headers.getRequestHeader(Constants.HEADER_SIROCCO_INFO_TEST_ID);
        if ((null != siroccoInfoTestsId) && (siroccoInfoTestsId.size() > 0)) {
            requestHeader.setSiroccoInfoTestId(siroccoInfoTestsId.get(0));
        }
        List<String> siroccoInfoTestExpand = headers.getRequestHeader(Constants.HEADER_SIROCCO_INFO_TEST_EXPAND);
        if ((null != siroccoInfoTestExpand) && (siroccoInfoTestExpand.size() > 0)) {
            requestHeader.setSiroccoInfoTestExpand(siroccoInfoTestExpand.get(0));
        }

        return requestHeader;
    }

    // TODO A passer en privé ou à supprimer
    public static RequestHeader buildRequestHeader(final HttpHeaders headers, final UriInfo uri, final String id) {
        return HelperRequest.buildRequestHeader(headers, uri);
    }

    // TODO A passer en privé ou à supprimer
    public static RequestHeader buildRequestHeader(final HttpHeaders headers, final UriInfo uri, final CimiData cimiData) {
        return HelperRequest.buildRequestHeader(headers, uri);
    } // TODO A passer en privé

    // TODO A passer en privé ou à supprimer
    public static RequestHeader buildRequestHeader(final HttpHeaders headers, final UriInfo uri, final String id,
        final CimiData cimiData) {
        return HelperRequest.buildRequestHeader(headers, uri);
    }

    private static List<String> transformQueryParamToList(final MultivaluedMap<String, String> queryParameters) {
        List<String> listSelect = new ArrayList<String>();
        listSelect = queryParameters.get(Constants.PARAM_CIMI_SELECT);
        return listSelect;
    }

}
