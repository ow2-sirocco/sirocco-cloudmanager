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
package org.ow2.sirocco.apis.rest.cimi.manager;

import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.converter.InvalidConversionException;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.utils.Constants;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to manage the phases of validation, conversion and calling
 * services of requests.
 */
public abstract class CimiManagerAbstract implements CimiManager {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CimiManagerAbstract.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManager#execute(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse)
     */
    @Override
    public void execute(final CimiRequest request, final CimiResponse response) {
        if (this.doValidate(request, response)) {
            Object dataServiceIn = this.doConvertToDataService(request, response);
            if (false == response.isCommitted()) {
                Object dataServiceOut = this.doCallService(request, response, dataServiceIn);
                if (false == response.isCommitted()) {
                    this.doConvertToResponse(request, response, dataServiceOut);
                }
            }
        }
    }

    /**
     * Validate the request.
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @return True if the request is valid
     * @throws Exception In case of validation error
     */
    protected abstract boolean validate(CimiRequest request, CimiResponse response) throws Exception;

    /**
     * Convert the CIMI data from request to a service data.
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @return The input service data
     * @throws Exception In case of conversion error
     */
    protected abstract Object convertToDataService(CimiRequest request, CimiResponse response) throws Exception;

    /**
     * Call the service.
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param dataService The input service data
     * @return The output service data or null if none output
     * @throws Exception In case of error in service
     */
    protected abstract Object callService(CimiRequest request, CimiResponse response, Object dataService) throws Exception;

    /**
     * Convert the service data to a CIMI data from request.
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param dataService The output service data
     * @throws Exception In case of conversion error
     */
    protected abstract void convertToResponse(CimiRequest request, CimiResponse response, Object dataService) throws Exception;

    /**
     * Add operations to the response.
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param dataService The output service data
     */
    protected abstract void addOperations(CimiRequest request, CimiResponse response, Object dataService);

    /**
     * Manage the request validation.
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @return True if the request is valid
     */
    private boolean doValidate(final CimiRequest request, final CimiResponse response) {
        boolean valid = false;
        try {
            valid = this.validate(request, response);
            if (!valid) {
                response.setStatus(Response.Status.BAD_REQUEST);
            }
        } catch (Exception e) {
            this.convertToResponse(request, response, e);
        }
        return valid;
    }

    /**
     * Manage the conversion of the CIMI data from request to a service data.
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @return The input service data
     */
    private Object doConvertToDataService(final CimiRequest request, final CimiResponse response) {
        Object dataService = null;
        try {
            dataService = this.convertToDataService(request, response);
        } catch (InvalidConversionException e) {
            this.convertToResponse(request, response, e);
        } catch (Exception e) {
            this.convertToResponse(request, response, e);
        }
        return dataService;
    }

    /**
     * Manage the call to the service.
     * <p>
     * In case of catch exception, the state is directly updated in response as
     * shown in the table below:
     * <table>
     * <tr>
     * <th>Exception</th>
     * <th>HTTP status code</th>
     * </tr>
     * <tr>
     * <td>ResourceNotFoundException</td>
     * <td>404</td>
     * </tr>
     * <tr>
     * <td>InvalidRequestException</td>
     * <td>400</td>
     * </tr>
     * <tr>
     * <td>ResourceConflictException</td>
     * <td>409</td>
     * </tr>
     * <tr>
     * <td>ServiceUnavailableException</td>
     * <td>503</td>
     * </tr>
     * <tr>
     * <td>SecurityException</td>
     * <td>403</td>
     * </tr>
     * <tr>
     * <td>UnsupportedOperationException</td>
     * <td>501</td>
     * </tr>
     * <tr>
     * <td>CloudProviderException</td>
     * <td>500</td>
     * </tr>
     * <tr>
     * <td>Exception</td>
     * <td>500</td>
     * </tr>
     * </table>
     * </p>
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param dataServiceIn The input service data
     * @return The output service data or null if none output
     */
    private Object doCallService(final CimiRequest request, final CimiResponse response, final Object dataServiceIn) {
        Object dataServiceOut = null;
        try {
            dataServiceOut = this.callService(request, response, dataServiceIn);
        } catch (ResourceNotFoundException e) {
            this.convertToResponse(request, response, e);
        } catch (InvalidRequestException e) {
            this.convertToResponse(request, response, e);
        } catch (ResourceConflictException e) {
            this.convertToResponse(request, response, e);
        } catch (ServiceUnavailableException e) {
            this.convertToResponse(request, response, e);
        } catch (SecurityException e) {
            this.convertToResponse(request, response, e);
        } catch (UnsupportedOperationException e) {
            this.convertToResponse(request, response, e);
        } catch (CloudProviderException e) {
            this.convertToResponse(request, response, e);
        } catch (Exception e) {
            this.convertToResponse(request, response, e);
        }
        return dataServiceOut;
    }

    /**
     * Manage the conversion of a service data to a CIMI data from request.
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param dataService The output service data
     */
    private void doConvertToResponse(final CimiRequest request, final CimiResponse response, final Object dataService) {
        try {
            // null
            if (null == dataService) {
                response.setCimiData(null);
                response.setStatus(Response.Status.OK);
            }
            // Job
            else if (dataService instanceof Job) {
                CimiJob cimi = (CimiJob) request.getContext().getRootConverter(CimiEntityType.Job)
                    .toCimi(request.getContext(), dataService);
                response.setCimiData(cimi);
                response.putHeader(Constants.HEADER_CIMI_JOB_URI, cimi.getId());
                response.putHeader(Constants.HEADER_LOCATION, cimi.getTargetEntity());
                response.setStatus(Response.Status.ACCEPTED);
            }
            // Other
            else {
                this.convertToResponse(request, response, dataService);
            }
            this.addOperations(request, response, dataService);
        } catch (InvalidConversionException e) {
            this.convertToResponse(request, response, e);
        } catch (Exception e) {
            this.convertToResponse(request, response, e);
        }
    }

    /**
     * Convert general exception to HTTP status "INTERNAL_SERVER_ERROR" (500).
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiRequest request, final CimiResponse response, final Exception exception) {
        CimiManagerAbstract.LOGGER.error("Internal Server Error", exception);
        response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Convert exception to HTTP status "NOT FOUND" (404).
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiRequest request, final CimiResponse response,
        final ResourceNotFoundException exception) {
        CimiManagerAbstract.LOGGER.debug("Resource not found : {}", request.getId());
        response.setStatus(Response.Status.NOT_FOUND);
    }

    /**
     * Convert exception to HTTP status "BAD REQUEST" (400).
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiRequest request, final CimiResponse response,
        final InvalidRequestException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        response.setStatus(Response.Status.BAD_REQUEST);
    }

    /**
     * Convert exception to HTTP status "BAD REQUEST" (400).
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiRequest request, final CimiResponse response,
        final InvalidConversionException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        response.setStatus(Response.Status.BAD_REQUEST);
    }

    /**
     * Convert exception to HTTP status "CONFLICT" (409).
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiRequest request, final CimiResponse response,
        final ResourceConflictException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        response.setStatus(Response.Status.CONFLICT);
    }

    /**
     * Convert exception to HTTP status "SERVICE UNAVAILABLE" (503).
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiRequest request, final CimiResponse response,
        final ServiceUnavailableException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        response.setStatus(Response.Status.SERVICE_UNAVAILABLE);
    }

    /**
     * Convert exception to HTTP status "FORBIDDEN" (403).
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiRequest request, final CimiResponse response, final SecurityException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        response.setStatus(Response.Status.FORBIDDEN);
    }

    /**
     * Convert exception to HTTP status "NOT IMPLEMENTED" (501).
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiRequest request, final CimiResponse response,
        final UnsupportedOperationException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        response.setStatus(501);
    }

    /**
     * Convert exception to HTTP status "INTERNAL_SERVER_ERROR" (500).
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiRequest request, final CimiResponse response,
        final CloudProviderException exception) {
        CimiManagerAbstract.LOGGER.error(exception.getMessage(), exception);
        response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
    }

}
