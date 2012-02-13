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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.api.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.message.Message;
import org.ow2.sirocco.cloudmanager.common.jndilocator.ServiceLocator;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteUserProjectManager;
import org.ow2.sirocco.cloudmanager.service.api.IUserProjectManager;

public class AuthenticationHandler implements RequestHandler {

    public Response handleRequest(final Message m, final ClassResourceInfo resourceClass) {
        AuthorizationPolicy policy = m.get(AuthorizationPolicy.class);
        String username = policy.getUserName();
        String suppliedPassword = policy.getPassword();

        IRemoteUserProjectManager userManager = (IRemoteUserProjectManager) ServiceLocator.getInstance().getRemoteObject(
            IUserProjectManager.EJB_JNDI_NAME);
        String password = userManager.getApiPasswordForUser(username);
        if (password == null || !suppliedPassword.equals(password)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        return null;
    }

}
