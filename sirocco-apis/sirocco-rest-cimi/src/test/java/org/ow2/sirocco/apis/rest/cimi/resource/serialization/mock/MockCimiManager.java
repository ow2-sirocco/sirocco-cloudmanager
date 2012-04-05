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

import org.ow2.sirocco.apis.rest.cimi.builder.CimiEntityBuilderHelper;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.utils.MethodType;
import org.ow2.sirocco.apis.rest.cimi.utils.PathType;

/**
 * Mock CimiManager.
 */
public abstract class MockCimiManager implements CimiManager {

    protected CimiData buildEntity(final CimiRequest request) {
        String pathname = this.extractPathname(request.getPath());
        PathType type = PathType.valueOfPathname(pathname);
        return this.buildEntity(request, type);
    }

    protected CimiData buildEntity(final CimiRequest request, final PathType type) {
        CimiData cimi = null;
        switch (type) {
        case Credentials:
            cimi = this.buildCredentials(request);
            break;
        case CredentialsTemplate:
            cimi = this.buildCredentialsTemplate(request);
            break;
        case MachineConfiguration:
            cimi = this.buildMachineConfiguration(request);
            break;
        case MachineImage:
            cimi = this.buildMachineImage(request);
            break;
        case Job:
            cimi = this.buildMachineJob(request);
            break;
        default:
            throw new UnsupportedOperationException(type + " for " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected CimiData buildCredentials(final CimiRequest request) {
        CimiData cimi = null;
        MethodType type = MethodType.valueOf(request.getMethod());

        switch (type) {
        case POST:
            cimi = CimiEntityBuilderHelper.buildCimiCredentialsCreate(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                // cimi =
                // CimiEntityBuilderHelper.buildCredentialsCollection(request.getHeader().getIntegerSiroccoInfoTestId(),
                // request.getHeader().getBooleanSiroccoInfoTestExpand());
                throw new UnsupportedOperationException(type + " for " + request.getPath() + " not implemented !");

            } else {
                cimi = CimiEntityBuilderHelper.buildCimiCredentials(Integer.valueOf(request.getId()));
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
            cimi = CimiEntityBuilderHelper.buildCimiCredentialsTemplate(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                // cimi =
                // CimiEntityBuilderHelper.buildCredentialsTemplateCollection(request.getHeader().getIntegerSiroccoInfoTestId(),
                // request.getHeader().getBooleanSiroccoInfoTestExpand());
                throw new UnsupportedOperationException(type + " for " + request.getPath() + " not implemented !");

            } else {
                cimi = CimiEntityBuilderHelper.buildCimiCredentialsTemplate(Integer.valueOf(request.getId()));
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
            cimi = CimiEntityBuilderHelper.buildCimiMachineConfiguration(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                cimi = CimiEntityBuilderHelper.buildCimiMachineConfigurationCollection(request.getHeader()
                    .getIntegerSiroccoInfoTestId(), request.getHeader().getBooleanSiroccoInfoTestExpand());

            } else {
                cimi = CimiEntityBuilderHelper.buildCimiMachineConfiguration(Integer.valueOf(request.getId()));
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
            cimi = CimiEntityBuilderHelper.buildCimiMachineImage(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                cimi = CimiEntityBuilderHelper.buildCimiMachineImageCollection(request.getHeader()
                    .getIntegerSiroccoInfoTestId(), request.getHeader().getBooleanSiroccoInfoTestExpand());

            } else {
                cimi = CimiEntityBuilderHelper.buildCimiMachineImage(Integer.valueOf(request.getId()));
            }
            break;
        case DELETE:
            break;
        default:
            throw new UnsupportedOperationException(type + " in " + request.getPath() + " not implemented !");
        }
        return cimi;
    }

    protected CimiData buildMachineJob(final CimiRequest request) {
        CimiData cimi = null;
        MethodType type = MethodType.valueOf(request.getMethod());

        switch (type) {
        case POST:
            cimi = CimiEntityBuilderHelper.buildCimiJob(1);
            break;
        case GET:
        case PUT:
            if (null == request.getId()) {
                throw new UnsupportedOperationException("Job Collection not implemented !");
            } else {
                cimi = CimiEntityBuilderHelper.buildCimiJob(Integer.valueOf(request.getId()));
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