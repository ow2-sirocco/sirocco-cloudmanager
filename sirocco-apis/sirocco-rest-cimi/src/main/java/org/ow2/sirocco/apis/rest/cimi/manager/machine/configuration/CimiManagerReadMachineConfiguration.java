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
package org.ow2.sirocco.apis.rest.cimi.manager.machine.configuration;

import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerReadAbstract;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.utils.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.utils.Context;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Manage READ request of Machine Configuration.
 */
@Component("CimiManagerReadMachineConfiguration")
public class CimiManagerReadMachineConfiguration extends CimiManagerReadAbstract {

    @Autowired
    @Qualifier("IMachineConfigurationManager")
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
        MachineConfiguration out = null;
        CimiSelect select = request.getHeader().getCimiSelect();
        if (true == select.isEmpty()) {
            out = this.manager.getMachineConfigurationById(request.getId());
        } else {
            // FIXME
            throw new UnsupportedOperationException();
            // out =
            // this.manager.getMachineConfigurationAttributes(request.getId(),
            // select.getAttributes());
        }
        if (null == out) {
            throw new ResourceNotFoundException();
        }
        return out;
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
        Context context = new Context(request, CimiEntityType.MachineConfiguration);
        CimiMachineConfiguration cimi = (CimiMachineConfiguration) context.getConverter().toCimi(context, dataService);
        response.setCimiData(cimi);
        response.setStatus(Response.Status.OK);
    }

}
