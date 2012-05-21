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
package org.ow2.sirocco.apis.rest.cimi.manager.machine;

import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerDeleteAbstract;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.utils.Constants;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Manage DELETE request of Machine.
 */
@Component("CimiManagerDeleteMachine")
public class CimiManagerDeleteMachine extends CimiManagerDeleteAbstract {

    @Autowired
    @Qualifier("IMachineManager")
    private IMachineManager manager;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#callService(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse,
     *      java.lang.Object)
     */
    @Override
    protected Object callService(final CimiRequest request, final CimiResponse response, final Object dataService)
        throws Exception {
        return this.manager.deleteMachine(request.getId());
    }

    /**
     * Call after the conversion.
     * 
     * @param request The CIMI request
     * @param response The CIMI response
     * @param dataService The output service data
     */
    @Override
    protected void afterConvertToResponse(final CimiRequest request, final CimiResponse response, final Object dataService) {
        if (null == response.getCimiData()) {
            // Job
            if (dataService instanceof Job) {
                CimiJob cimi = (CimiJob) request.getContext().getRootConverter(CimiEntityType.Job)
                    .toCimi(request.getContext(), dataService);
                response.setCimiData(cimi);
                response.putHeader(Constants.HEADER_CIMI_JOB_URI, cimi.getId());
                response.putHeader(Constants.HEADER_LOCATION, cimi.getTargetEntity());
                response.setStatus(Response.Status.ACCEPTED);
            }
        }
    }
}
