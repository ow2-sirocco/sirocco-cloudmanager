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
package org.ow2.sirocco.apis.rest.cimi.manager.machine.image;

import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.converter.data.MachineImageConverter;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHeader;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("CimiManagerReadMachineImage")
public class CimiManagerReadMachineImage extends CimiManagerAbstract {

    @Autowired
    @Qualifier("IMachineImageManager")
    private IMachineImageManager manager;

    /**
     * {@inheritDoc}
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#validate(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse)
     */
    @Override
    protected boolean validate(CimiRequest request, CimiResponse response) throws Exception {
        return CimiValidatorHeader.getInstance().validate(request, response);
    }

    /**
     * {@inheritDoc}
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#convertToDataService(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse)
     */
    @Override
    protected Object convertToDataService(CimiRequest request, CimiResponse response) throws Exception {
        return null;
    }

    /**
     * {@inheritDoc}
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#callService(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse,
     *      java.lang.Object)
     */
    @Override
    protected Object callService(CimiRequest request, CimiResponse response, Object dataService) throws Exception {

        MachineImage image = this.manager.getMachineImageById(request.getId());

        return image;
    }

    @Override
    protected void convertToResponse(CimiRequest request, CimiResponse response, Object dataService) throws Exception {
        if (null == dataService) {
            throw new Exception("Data not found");
        }
        CimiMachineImage cimi = new CimiMachineImage();
        MachineImageConverter.copyToCimi((MachineImage) dataService, cimi, request.getHeader().getBaseUri().toString());
        response.setCimiData(cimi);
        response.setStatus(Response.Status.OK);
    }

}
