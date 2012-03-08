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

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class CimiManagerUpdateMachineCollection {

    public CimiManagerUpdateMachineCollection() {
    }

    public void execute(CimiRequest request, CimiResponse response) {
        // Status status = verifyRequest(request);
        // if (status.equals(Status.OK)) {
        // MachineCollection machineCoToUpdate = getMachineCollection();
        // MachineCollection machinePropertiesToUpdate = (MachineCollection)
        // request.getHeader().getCimiData();
        // updateMachineCo(machineCoToUpdate, machinePropertiesToUpdate,
        // request.getHeader().getListSelect());
        // // status = 202 Accepted
        // response.setStatusHttp(Status.ACCEPTED.getStatusCode());
        // } else {
        // // status = 400 BAD REQUEST
        // response.setStatusHttp(Status.BAD_REQUEST.getStatusCode());
        // }
    }

    private CimiMachineCollection updateMachineCo(CimiMachineCollection machineToUpdate,
            CimiMachineCollection machinePropertiesToUpdate, List<String> queryParam) {
        // FIXME return IMachineManager.updateMachine(machineToUpdate,
        // machinePropertiesToUpdate, queryParam);
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

    public CimiMachineCollection getMachineCollection() {
        // FIXME return IMachineManager.getMachineCollection();
        return null;
    }
}
