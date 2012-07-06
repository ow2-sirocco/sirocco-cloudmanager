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

import org.ow2.sirocco.apis.rest.cimi.builder.CimiResourceBuilderHelper;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;
import org.ow2.sirocco.apis.rest.cimi.domain.PathType;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.IdRequest;
import org.ow2.sirocco.apis.rest.cimi.request.MethodType;

/**
 * Mock CimiManager.
 */
public abstract class MockCimiManager implements CimiManager {

    protected CimiData buildEntity(final CimiRequest request) {
        String pathname = this.extractPathname(request.getPath());
        PathType type = PathType.valueOfPath(pathname);
        return this.buildResource(request, type);
    }

    protected CimiData buildResource(final CimiRequest request, final PathType type) {
        CimiData cimi = null;
        switch (type) {
        case CloudEntryPoint:
            cimi = this.buildCloudEntryPoint(request);
            break;
        case Credential:
            cimi = this.buildCredentials(request);
            break;
        case CredentialTemplate:
            cimi = this.buildCredentialsTemplate(request);
            break;
        case Job:
            cimi = this.buildJob(request);
            break;
        case Machine:
            cimi = this.buildMachine(request);
            break;
        case MachineConfiguration:
            cimi = this.buildMachineConfiguration(request);
            break;
        case MachineImage:
            cimi = this.buildMachineImage(request);
            break;
        case MachineTemplate:
            cimi = this.buildMachineTemplate(request);
            break;
        default:
            throw new UnsupportedOperationException(type + " for " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected CimiData buildCloudEntryPoint(final CimiRequest request) {
        CimiData cimi = null;
        MethodType type = MethodType.valueOf(request.getMethod());

        switch (type) {
        case GET:
        case PUT:
            request.setIds(new IdRequest("2"));
            cimi = CimiResourceBuilderHelper.buildCimiCloudEntryPoint(2);
            break;
        default:
            throw new UnsupportedOperationException(type + " in " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected CimiData buildCredentials(final CimiRequest request) {
        CimiData cimi = null;
        MethodType type = MethodType.valueOf(request.getMethod());

        switch (type) {
        case POST:
            cimi = CimiResourceBuilderHelper.buildCimiCredentialCreate(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                cimi = CimiResourceBuilderHelper.buildCimiCredentialCollection(request.getHeader()
                    .getIntegerSiroccoInfoTestId(), true, request.getHeader().getCimiExpand().hasExpandAll());
            } else {
                cimi = CimiResourceBuilderHelper.buildCimiCredential(Integer.valueOf(request.getId()));
            }
            break;
        case DELETE:
            break;
        default:
            throw new UnsupportedOperationException(type + " in " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected CimiData buildCredentialsTemplate(final CimiRequest request) {
        CimiData cimi = null;
        MethodType type = MethodType.valueOf(request.getMethod());

        switch (type) {
        case POST:
            cimi = CimiResourceBuilderHelper.buildCimiCredentialTemplate(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                cimi = CimiResourceBuilderHelper.buildCimiCredentialTemplateCollection(request.getHeader()
                    .getIntegerSiroccoInfoTestId(), true, request.getHeader().getCimiExpand().hasExpandAll());
            } else {
                cimi = CimiResourceBuilderHelper.buildCimiCredentialTemplate(Integer.valueOf(request.getId()));
            }
            break;
        case DELETE:
            break;
        default:
            throw new UnsupportedOperationException(type + " in " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected CimiData buildMachine(final CimiRequest request) {
        CimiData cimi = null;
        MethodType type = MethodType.valueOf(request.getMethod());

        switch (type) {
        case POST:
            if (null == request.getId()) {
                cimi = CimiResourceBuilderHelper.buildCimiMachineCreate(1);
            } else {
                cimi = CimiResourceBuilderHelper.buildCimiAction(Integer.valueOf(request.getId()));
            }
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                cimi = CimiResourceBuilderHelper.buildCimiMachineCollection(request.getHeader().getIntegerSiroccoInfoTestId(),
                    true, request.getHeader().getCimiExpand().hasExpandAll());
            } else {
                cimi = CimiResourceBuilderHelper.buildCimiMachine(Integer.valueOf(request.getId()));
            }
            break;
        case DELETE:
            break;
        default:
            throw new UnsupportedOperationException(type + " in " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected CimiData buildMachineConfiguration(final CimiRequest request) {
        CimiData cimi = null;
        MethodType type = MethodType.valueOf(request.getMethod());

        switch (type) {
        case POST:
            cimi = CimiResourceBuilderHelper.buildCimiMachineConfiguration(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                cimi = CimiResourceBuilderHelper.buildCimiMachineConfigurationCollection(request.getHeader()
                    .getIntegerSiroccoInfoTestId(), true, request.getHeader().getCimiExpand().hasExpandAll());
            } else {
                cimi = CimiResourceBuilderHelper.buildCimiMachineConfiguration(Integer.valueOf(request.getId()));
            }
            break;
        case DELETE:
            break;
        default:
            throw new UnsupportedOperationException(type + " in " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected CimiData buildMachineImage(final CimiRequest request) {
        CimiData cimi = null;
        MethodType type = MethodType.valueOf(request.getMethod());

        switch (type) {
        case POST:
            cimi = CimiResourceBuilderHelper.buildCimiMachineImage(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                cimi = CimiResourceBuilderHelper.buildCimiMachineImageCollection(request.getHeader()
                    .getIntegerSiroccoInfoTestId(), true, request.getHeader().getCimiExpand().hasExpandAll());
            } else {
                cimi = CimiResourceBuilderHelper.buildCimiMachineImage(Integer.valueOf(request.getId()));
            }
            break;
        case DELETE:
            break;
        default:
            throw new UnsupportedOperationException(type + " in " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected CimiData buildMachineTemplate(final CimiRequest request) {
        CimiData cimi = null;
        MethodType type = MethodType.valueOf(request.getMethod());

        switch (type) {
        case POST:
            cimi = CimiResourceBuilderHelper.buildCimiMachineTemplate(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                cimi = CimiResourceBuilderHelper.buildCimiMachineTemplateCollection(request.getHeader()
                    .getIntegerSiroccoInfoTestId(), true, request.getHeader().getCimiExpand().hasExpandAll());
            } else {
                cimi = CimiResourceBuilderHelper.buildCimiMachineTemplate(Integer.valueOf(request.getId()));
            }
            break;
        case DELETE:
            break;
        default:
            throw new UnsupportedOperationException(type + " in " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected CimiData buildJob(final CimiRequest request) {
        CimiData cimi = null;
        MethodType type = MethodType.valueOf(request.getMethod());

        switch (type) {
        case POST:
            cimi = CimiResourceBuilderHelper.buildCimiJob(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                cimi = CimiResourceBuilderHelper.buildCimiJobCollection(request.getHeader().getIntegerSiroccoInfoTestId(),
                    true, request.getHeader().getCimiExpand().hasExpandAll());
            } else {
                cimi = CimiResourceBuilderHelper.buildCimiJob(Integer.valueOf(request.getId()));
            }
            break;
        case DELETE:
            break;
        default:
            throw new UnsupportedOperationException(type + " in " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected String extractPathname(final String path) {
        String pathname = null;
        int index = path.indexOf('/');
        if (index > -1) {
            pathname = path.substring(0, index);
        } else {
            pathname = path;
        }
        return pathname;
    }
}