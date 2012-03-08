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
 * $Id: CimiManagerUpdateCloudEntryPoint.java 123 2012-03-07 14:41:51Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.manager.cep;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class CimiManagerUpdateCloudEntryPoint {

    public CimiManagerUpdateCloudEntryPoint() {

    }

    public void execute(CimiRequest request, CimiResponse response) {
        // Status status = verifyRequest(request);
        // if (status.equals(Status.OK)) {
        // CloudEntryPoint cloudEntryPoint = getCloudEntryPoint();
        // CloudEntryPoint cloudEntryPointToUpdate = (CloudEntryPoint)
        // request.getHeader().getCimiData();
        // updateCloudEntryPoint(cloudEntryPoint, cloudEntryPointToUpdate,
        // request.getHeader().getListSelect());
        // // status = 202 Accepted
        // response.setStatusHttp(Status.ACCEPTED.getStatusCode());
        // } else {
        // // status = 400 BAD REQUEST
        // response.setStatusHttp(Status.BAD_REQUEST.getStatusCode());
        // }
    }

    private CimiCloudEntryPoint updateCloudEntryPoint(CimiCloudEntryPoint cloudEntryPoint,
            CimiCloudEntryPoint cloudEntryPointToUpdate, List<String> queryParam) {
        // FIXME IMachineManager.updateMachine(cloudEntryPoint,
        // cloudEntryPointToUpdate, queryParam);
        return null;

    }

    public Status verifyRequest(CimiRequest request) {
        // FIXME le path de la requete doit être au format http://example.com +
        // ConstantePath + / + id
        if (request.getHeader().getBaseUri().toString().equals("http://localhost:9998/")
                && request.getHeader().getPath().startsWith(ConstantsPath.MACHINE.substring(1))) {
            return Status.OK;
        } else {
            return Status.BAD_REQUEST;
        }
    }

    public CimiCloudEntryPoint getCloudEntryPoint() {
        // FIXME return IMachineManager.getCloudEnryPoint();
        return null;
    }
}
