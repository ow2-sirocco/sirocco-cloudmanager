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
package org.ow2.sirocco.apis.rest.cimi.resource.serialization.mock;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import junit.framework.ComparisonFailure;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ow2.sirocco.apis.rest.cimi.builder.CimiEntityBuilderHelper;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.resource.serialization.SerializationHelper;

/**
 * Mock CimiManagerUpdateMachineImage.
 */
public class MockCimiManagerUpdateMachineImage implements CimiManager {

    /**
     * {@inheritDoc}
     * <p>
     * Build a new MachineImage and compare it with the MachineImage in request.
     * </p>
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManager#execute(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse)
     */
    @Override
    public void execute(final CimiRequest request, final CimiResponse response) {
        try {
            // Build and compare
            Integer id = Integer.valueOf(request.getId());
            CimiMachineImage cimi = CimiEntityBuilderHelper.buildCimiMachineImage(id);
            Assert.assertEquals(ToStringBuilder.reflectionToString(cimi, new SerializationHelper.RecursiveToStringStyle()),
                ToStringBuilder.reflectionToString(request.getCimiData(), new SerializationHelper.RecursiveToStringStyle()));

            // Build response
            response.setCimiData(CimiEntityBuilderHelper.buildCimiJob(id));
            response.setStatus(Status.OK);
        } catch (ComparisonFailure e) {
            // Build assert error
            response.setCimiData(null);
            response.setErrorMessage(e.getMessage());
            response.setStatus(Status.NOT_ACCEPTABLE);
        } catch (Exception e) {
            response.setStatus(Status.BAD_REQUEST);
        }
    }

}