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

import javax.validation.groups.Default;
import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.converter.HrefHelper;
import org.ow2.sirocco.apis.rest.cimi.converter.JobConverter;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.utils.Constants;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHelper;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;

/**
 * Abstract class for manage CREATE request.
 */
public abstract class CimiManagerCreateAbstract extends CimiManagerAbstract {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#validate(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse)
     */
    @Override
    protected boolean validate(final CimiRequest request, final CimiResponse response) throws Exception {
        boolean valid = CimiValidatorHelper.getInstance().validate(request.getHeader());
        if (valid) {
            if (null == request.getCimiData()) {
                valid = false;
            } else {
                valid = CimiValidatorHelper.getInstance().validate(request.getCimiData(), Default.class, GroupCreate.class);
            }
        }
        return valid;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#convertToResponse(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse,
     *      java.lang.Object)
     */
    @Override
    protected void convertToResponse(final CimiRequest request, final CimiResponse response, final Object dataService)
        throws Exception {
        CimiJob cimi = new CimiJob();
        JobConverter.copyToCimi((Job) dataService, cimi, request.getHeader().getBaseUri(), true);
        response.setCimiData(cimi);
        response.putHeader(Constants.HEADER_CIMI_JOB_URI, cimi.getId());
        response.putHeader(Constants.HEADER_LOCATION,
            HrefHelper.makeHref(request.getHeader().getBaseUri(), this.getEntityPathname(), cimi.getTargetEntity()));
        response.setStatus(Response.Status.ACCEPTED);
    }

    /**
     * Get the pathname of the entity being created.
     * 
     * @return The name
     */
    protected abstract String getEntityPathname();

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#addOperations(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse,
     *      java.lang.Object)
     */
    @Override
    protected void addOperations(final CimiRequest request, final CimiResponse response, final Object dataService) {
        // Nothing to do
    }
}