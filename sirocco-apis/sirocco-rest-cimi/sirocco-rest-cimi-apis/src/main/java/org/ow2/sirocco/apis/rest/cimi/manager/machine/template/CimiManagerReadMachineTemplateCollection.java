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
package org.ow2.sirocco.apis.rest.cimi.manager.machine.template;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommonId;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation;
import org.ow2.sirocco.apis.rest.cimi.domain.Operation;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerReadAbstract;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Manage READ request of MachineTemplates collection.
 */
@Component("CimiManagerReadMachineTemplateCollection")
public class CimiManagerReadMachineTemplateCollection extends CimiManagerReadAbstract {

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
        Object out = null;
        CimiSelect select = request.getHeader().getCimiSelect();
        if (true == select.isEmpty()) {
            out = this.manager.getMachineTemplateCollection();
        } else {
            if (true == select.isNumericArrayPresent()) {
                List<Integer> numsArray = select.getNumericArray(select.getIndexFirstArray());
                out = this.manager.getMachineTemplates(numsArray.get(0).intValue(), numsArray.get(1).intValue(),
                    select.getAttributes());
            } else {
                out = this.manager.getMachineTemplates(select.getAttributes(),
                    select.getExpressionArray(select.getIndexFirstArray()));
            }
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
        CimiMachineTemplateCollection cimi = (CimiMachineTemplateCollection) request.getContext()
            .getRootConverter(CimiEntityType.MachineTemplateCollection).toCimi(request.getContext(), dataService);
        response.setCimiData(cimi);
        response.setStatus(Response.Status.OK);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#afterConvertToResponse(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse,
     *      java.lang.Object)
     */
    @Override
    protected void afterConvertToResponse(final CimiRequest request, final CimiResponse response, final Object dataService) {
        super.afterConvertToResponse(request, response, dataService);

        CimiCommonId common = (CimiCommonId) response.getCimiData();
        List<CimiOperation> ops = new ArrayList<CimiOperation>();
        ops.add(new CimiOperation(Operation.ADD.getRel(), common.getId()));
        common.setOperations(ops.toArray(new CimiOperation[ops.size()]));
    }
}
