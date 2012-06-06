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
package org.ow2.sirocco.apis.rest.cimi.manager.cep;

import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.apis.rest.cimi.domain.CloudEntryPointAggregate;
import org.ow2.sirocco.apis.rest.cimi.domain.Operation;
import org.ow2.sirocco.apis.rest.cimi.domain.ResourceType;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerReadAbstract;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Manage READ request of Cloud Entry Point.
 */
@Component("CimiManagerReadCloudEntryPoint")
public class CimiManagerReadCloudEntryPoint extends CimiManagerReadAbstract {

    @Autowired
    @Qualifier("IMachineManager")
    private IMachineManager machineManager;

    @Autowired
    @Qualifier("ICredentialsManager")
    private ICredentialsManager credentialsManager;

    @Autowired
    @Qualifier("IMachineImageManager")
    private IMachineImageManager machineImageManager;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#callService(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      java.lang.Object)
     */
    @Override
    protected Object callService(final CimiContext context, final Object dataService) throws Exception {
        CloudEntryPointAggregate out = new CloudEntryPointAggregate(this.machineManager.getCloudEntryPoint());
        out.setMachineConfigs(this.machineManager.getMachineConfigurationCollection());
        out.setMachines(this.machineManager.getMachineCollection());
        out.setMachineTemplates(this.machineManager.getMachineTemplateCollection());
        out.setMachineImages(this.machineImageManager.getMachineImageCollection());
        // FIXME
        // out.setCredentials(this.credentialsManager.getCredentialsCollection());
        // FIXME
        // out.setCredentialsTemplates(this.credentialsManager.getCredentialsTemplateCollection());
        // TODO Volumes, ...
        // TODO CimiSelect
        return out;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#convertToResponse(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      java.lang.Object)
     */
    @Override
    protected void convertToResponse(final CimiContext context, final Object dataService) throws Exception {
        CimiCloudEntryPoint cimi = (CimiCloudEntryPoint) context.getRootConverter(ResourceType.CloudEntryPoint).toCimi(context,
            dataService);
        context.getResponse().setCimiData(cimi);
        context.getResponse().setStatus(Response.Status.OK);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerReadAbstract#afterConvertToResponse(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      java.lang.Object)
     */
    @Override
    protected void afterConvertToResponse(final CimiContext context, final Object dataService) {
        super.afterConvertToResponse(context, dataService);
        CimiResource resource = (CimiResource) context.getResponse().getCimiData();
        resource.add(new CimiOperation(Operation.EDIT.getRel(), resource.getId()));
    }
}
