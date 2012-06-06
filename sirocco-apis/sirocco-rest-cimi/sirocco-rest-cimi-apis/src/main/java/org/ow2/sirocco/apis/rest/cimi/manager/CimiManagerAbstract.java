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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.ResourceType;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
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
     * <td>InvalidConversionException</td>
     * <td>400</td>
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
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManager#execute(org.ow2.sirocco.apis.rest.cimi.request.CimiContext)
     */
    @Override
    public void execute(final CimiContext context) {
        try {
            if (this.doValidate(context)) {
                Object dataServiceIn = this.doConvertToDataService(context);
                if (false == context.getResponse().isCommitted()) {
                    Object dataServiceOut = this.doCallService(context, dataServiceIn);
                    if (false == context.getResponse().isCommitted()) {
                        this.doConvertToResponse(context, dataServiceOut);
                    }
                }
            }
        } catch (InvalidConversionException e) {
            this.convertToResponse(context, e);
        } catch (ResourceNotFoundException e) {
            this.convertToResponse(context, e);
        } catch (InvalidRequestException e) {
            this.convertToResponse(context, e);
        } catch (ResourceConflictException e) {
            this.convertToResponse(context, e);
        } catch (ServiceUnavailableException e) {
            this.convertToResponse(context, e);
        } catch (SecurityException e) {
            this.convertToResponse(context, e);
        } catch (UnsupportedOperationException e) {
            this.convertToResponse(context, e);
        } catch (CloudProviderException e) {
            this.convertToResponse(context, e);
        } catch (Exception e) {
            this.convertToResponse(context, e);
        }
    }

    /**
     * Validate the request.
     * 
     * @param context The CIMI context
     * @return True if the request is valid
     * @throws Exception In case of validation error
     */
    protected abstract boolean validate(CimiContext context) throws Exception;

    /**
     * Convert the CIMI data from request to a service data.
     * 
     * @param context The CIMI context
     * @return The input service data
     * @throws Exception In case of conversion error
     */
    protected abstract Object convertToDataService(CimiContext context) throws Exception;

    /**
     * Call the service.
     * 
     * @param context The CIMI context
     * @param dataService The input service data
     * @return The output service data or null if none output
     * @throws Exception In case of error in service
     */
    protected abstract Object callService(CimiContext context, Object dataService) throws Exception;

    /**
     * Convert the service data to a CIMI data from request.
     * 
     * @param context The CIMI context
     * @param dataService The output service data
     * @throws Exception In case of conversion error
     */
    protected abstract void convertToResponse(CimiContext context, Object dataService) throws Exception;

    /**
     * Call after the conversion.
     * 
     * @param context The CIMI context
     * @param dataService The output service data
     */
    protected void afterConvertToResponse(final CimiContext context, final Object dataService) {
        if (null == context.getResponse().getCimiData()) {
            // Job
            if (dataService instanceof Job) {
                CimiJob cimi = (CimiJob) context.getRootConverter(ResourceType.Job).toCimi(context, dataService);
                context.getResponse().setCimiData(cimi);
                context.getResponse().putHeader(Constants.HEADER_CIMI_JOB_URI, cimi.getId());
                context.getResponse().putHeader(Constants.HEADER_LOCATION, cimi.getTargetEntity());
                context.getResponse().setStatus(Response.Status.ACCEPTED);
            }
        }
    }

    /**
     * Call before the conversion.
     * 
     * @param context The CIMI context
     * @throws Exception In case of error
     */
    protected void beforeConvertToDataService(final CimiContext context) throws Exception {
        // Nothing to do
    }

    /**
     * Manage the request validation.
     * 
     * @param context The CIMI context
     * @return True if the request is valid
     * @throws Exception In case of validation error
     */
    private boolean doValidate(final CimiContext context) throws Exception {
        boolean valid = false;
        valid = this.validate(context);
        if (!valid) {
            context.getResponse().setStatus(Response.Status.BAD_REQUEST);
        }
        return valid;
    }

    /**
     * Manage the conversion of the CIMI data from request to a service data.
     * 
     * @param context The CIMI context
     * @return The input service data
     * @throws Exception In case of conversion error
     */
    private Object doConvertToDataService(final CimiContext context) throws Exception {
        Object dataService = null;
        this.beforeConvertToDataService(context);
        dataService = this.convertToDataService(context);
        return dataService;
    }

    /**
     * Manage the call to the service.
     * 
     * @param context The CIMI context
     * @param dataServiceIn The input service data
     * @return The output service data or null if none output
     * @throws Exception In case of service error
     */
    private Object doCallService(final CimiContext context, final Object dataServiceIn) throws Exception {
        Object dataServiceOut = null;
        dataServiceOut = this.callService(context, dataServiceIn);
        return dataServiceOut;
    }

    /**
     * Manage the conversion of a service data to a CIMI data from request.
     * 
     * @param context The CIMI context
     * @param dataService The output service data
     * @throws Exception In case of conversion error
     */
    private void doConvertToResponse(final CimiContext context, final Object dataService) throws Exception {
        this.convertToResponse(context, dataService);
        this.afterConvertToResponse(context, dataService);
    }

    /**
     * Convert general exception to HTTP status "INTERNAL_SERVER_ERROR" (500).
     * 
     * @param context The CIMI context
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiContext context, final Exception exception) {
        CimiManagerAbstract.LOGGER.error("Internal Server Error", exception);
        context.getResponse().setStatus(Response.Status.INTERNAL_SERVER_ERROR);
        context.getResponse().setErrorMessage(exception.getMessage());
    }

    /**
     * Convert exception to HTTP status "NOT FOUND" (404).
     * 
     * @param context The CIMI context
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiContext context, final ResourceNotFoundException exception) {
        CimiManagerAbstract.LOGGER.debug("Resource not found : {}", context.getRequest().getId());
        context.getResponse().setStatus(Response.Status.NOT_FOUND);
        context.getResponse().setErrorMessage(exception.getMessage());
    }

    /**
     * Convert exception to HTTP status "BAD REQUEST" (400).
     * 
     * @param context The CIMI context
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiContext context, final InvalidRequestException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        context.getResponse().setStatus(Response.Status.BAD_REQUEST);
        context.getResponse().setErrorMessage(exception.getMessage());
    }

    /**
     * Convert exception to HTTP status "BAD REQUEST" (400).
     * 
     * @param context The CIMI context
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiContext context, final InvalidConversionException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        context.getResponse().setStatus(Response.Status.BAD_REQUEST);
        context.getResponse().setErrorMessage(exception.getMessage());
    }

    /**
     * Convert exception to HTTP status "CONFLICT" (409).
     * 
     * @param context The CIMI context
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiContext context, final ResourceConflictException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        context.getResponse().setStatus(Response.Status.CONFLICT);
        context.getResponse().setErrorMessage(exception.getMessage());
    }

    /**
     * Convert exception to HTTP status "SERVICE UNAVAILABLE" (503).
     * 
     * @param context The CIMI context
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiContext context, final ServiceUnavailableException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        context.getResponse().setStatus(Response.Status.SERVICE_UNAVAILABLE);
        context.getResponse().setErrorMessage(exception.getMessage());
    }

    /**
     * Convert exception to HTTP status "FORBIDDEN" (403).
     * 
     * @param context The CIMI context
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiContext context, final SecurityException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        context.getResponse().setStatus(Response.Status.FORBIDDEN);
        context.getResponse().setErrorMessage(exception.getMessage());
    }

    /**
     * Convert exception to HTTP status "NOT IMPLEMENTED" (501).
     * 
     * @param context The CIMI context
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiContext context, final UnsupportedOperationException exception) {
        CimiManagerAbstract.LOGGER.debug(exception.getMessage(), exception);
        context.getResponse().setStatus(501);
        context.getResponse().setErrorMessage(exception.getMessage());
    }

    /**
     * Convert exception to HTTP status "INTERNAL_SERVER_ERROR" (500).
     * 
     * @param context The CIMI context
     * @param exception The exception to convert
     */
    private void convertToResponse(final CimiContext context, final CloudProviderException exception) {
        CimiManagerAbstract.LOGGER.error(exception.getMessage(), exception);
        context.getResponse().setStatus(Response.Status.INTERNAL_SERVER_ERROR);
        context.getResponse().setErrorMessage(exception.getMessage());
    }

}
