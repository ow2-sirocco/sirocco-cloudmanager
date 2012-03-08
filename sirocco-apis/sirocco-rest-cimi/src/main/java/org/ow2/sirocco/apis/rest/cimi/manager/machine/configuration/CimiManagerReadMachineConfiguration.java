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

import javax.ws.rs.core.Response.Status;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class CimiManagerReadMachineConfiguration {

    public CimiManagerReadMachineConfiguration() {
    }

    public void execute(CimiRequest request, CimiResponse response) {
        // Status status = verifyRequest(request);
        // if (status.equals(Status.OK)) {
        // response.setCimiData(getMachineCongById(request.getHeader().getId()));
        // // status = 200 OK
        // response.setStatusHttp(status.getStatusCode());
        // } else {
        // // status = 400 BAD REQUEST
        // response.setStatusHttp(Status.BAD_REQUEST.getStatusCode());
        // }
    }

    private CimiMachineConfiguration getMachineCongById(String id) {
        // FIXME return IMachineManager.getMachineConfById(id);
        return null;
    }

    public Status verifyRequest(CimiRequest request) {
        // FIXME le path de la requete doit être au format http://example.com/
        if (request.getHeader().getBaseUri().toString().equals("http://localhost:9998/")
                && request.getHeader().getPath().startsWith(ConstantsPath.MACHINE_CONFIGURATION)) {
            return Status.OK;
        } else {
            return Status.BAD_REQUEST;
        }
    }

}
