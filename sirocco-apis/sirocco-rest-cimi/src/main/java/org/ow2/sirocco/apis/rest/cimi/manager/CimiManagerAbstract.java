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
 * $Id: CimiManagerAbstract.java 126 2012-03-07 17:25:59Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.manager;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CimiManagerAbstract implements CimiManager {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CimiManagerAbstract.class);

    /**
     * {@inheritDoc}
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManager#execute(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse)
     */
    @Override
    public void execute(CimiRequest request, CimiResponse response) {
        if (doValidate(request, response)) {
            Object dataServiceIn = doConvertToDataService(request, response);
            if (response.getStatus() == Status.OK) {
                Object dataServiceOut = doCallService(request, response, dataServiceIn);
                if (response.getStatus() == Status.OK) {
                    doConvertToResponse(request, response, dataServiceOut);
                }
            }
        }
    }

    /**
     * Validate the request.
     */
    protected boolean doValidate(CimiRequest request, CimiResponse response) {
        boolean valid = false;
        try {
            valid = validate(request, response);
            if (!valid) {
                response.setStatus(Response.Status.BAD_REQUEST);
            }
        } catch (Exception e) {
            convertToResponse(request, response, e);
        }
        return valid;
    }

    /**
     * .
     */
    private Object doConvertToDataService(CimiRequest request, CimiResponse response) {
        Object dataService = null;
        try {
            dataService = convertToDataService(request, response);
        } catch (Exception e) {
            convertToResponse(request, response, e);
        }
        return dataService;
    }

    /**
     * .
     */
    private Object doCallService(CimiRequest request, CimiResponse response, Object dataServiceIn) {
        Object dataServiceOut = null;
        try {
            dataServiceOut = callService(request, response, dataServiceIn);
        } catch (Exception e) {
            convertToResponse(request, response, e);
        }
        return dataServiceOut;
    }

    /**
     * .
     */
    private void doConvertToResponse(CimiRequest request, CimiResponse response, Object dataService) {
        try {
            convertToResponse(request, response, dataService);
        } catch (Exception e) {
            convertToResponse(request, response, e);
        }
    }

    /**
     * Validate the request.
     */
    protected abstract boolean validate(CimiRequest request, CimiResponse response) throws Exception;

    /**
     * .
     */
    protected abstract Object convertToDataService(CimiRequest request, CimiResponse response) throws Exception;

    /**
     * 
     */
    protected abstract Object callService(CimiRequest request, CimiResponse response, Object dataService)
            throws Exception;

    /**
     * 
     */
    protected abstract void convertToResponse(CimiRequest request, CimiResponse response, Object dataService)
            throws Exception;

    /**
     * 
     */
    protected void convertToResponse(CimiRequest request, CimiResponse response, Exception exception) {
        LOGGER.error("Internal error", exception);
        response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
    }

}
