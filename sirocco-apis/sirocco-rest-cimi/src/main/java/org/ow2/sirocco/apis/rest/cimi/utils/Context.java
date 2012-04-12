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
package org.ow2.sirocco.apis.rest.cimi.utils;

import org.ow2.sirocco.apis.rest.cimi.configuration.AppConfig;
import org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommonId;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;

/**
 * .
 */
public class Context {

    private CimiEntityType rootType;

    private CimiRequest request;

    // /**
    // * Default constructor.
    // */
    // public Context() {
    // super();
    // }

    /**
     * Set constructor.
     * 
     * @param rootType The root
     */
    public Context(final CimiRequest request, final CimiEntityType rootType) {
        super();
        this.request = request;
        this.rootType = rootType;
    }

    /**
     * @return the rootType
     */
    public CimiEntityType getRootType() {
        return this.rootType;
    }

    /**
     * @param rootType the rootType to set
     */
    public void setRootType(final CimiEntityType rootType) {
        this.rootType = rootType;
    }

    /**
     * Set the CimiRequest.
     * 
     * @param request The CimiRequest
     */
    public void setCimiRequest(final CimiRequest request) {
        this.request = request;
    }

    /**
     * Get the CimiRequest.
     * 
     * @return CimiRequest
     */
    public CimiRequest getCimiRequest() {
        return this.request;
    }

    public boolean shouldBeExpanded(final CimiCommon data) {
        boolean expand = true;
        CimiEntityType type = AppConfig.getType(data);
        if (this.rootType != type) {
            switch (this.rootType) {
            case CloudEntryPoint:
            case CredentialsCollection:
            case CredentialsTemplateCollection:
            case JobCollection:
            case MachineCollection:
            case MachineConfigurationCollection:
            case MachineImageCollection:
            case MachineTemplateCollection:
                expand = false;
                break;
            default:
                break;
            }
        }
        return expand;
    }

    public boolean shouldBeReferenced(final CimiCommon data) {
        boolean reference = false;
        CimiEntityType type = AppConfig.getType(data);
        if (this.rootType != type) {
            switch (this.rootType) {
            case CloudEntryPoint:
            case CredentialsCollection:
            case CredentialsTemplateCollection:
            case JobCollection:
            case MachineCollection:
            case MachineConfigurationCollection:
            case MachineImageCollection:
            case MachineTemplateCollection:
                reference = true;
                break;
            default:
                break;
            }
        }
        return reference;
    }

    public EntityConverter getConverter() {
        return AppConfig.getConverter(this.getRootType());
    }

    public EntityConverter getConverter(final Class<?> klass) {
        return AppConfig.getConverter(klass);
    }

    public EntityConverter getConverter(final CimiCommon data) {
        return AppConfig.getConverter(data);
    }

    public EntityConverter getConverter(final CimiEntityType type) {
        return AppConfig.getConverter(type);
    }

    /**
     * Make a HREF.
     * 
     * @param id Service ID
     * @return The HREF made
     */
    public String makeHref(final CimiCommonId data, final String id) {
        return this.makeHref(data, Integer.valueOf(id));
    }

    /**
     * Make a HREF.
     * 
     * @param id Service ID
     * @return The HREF made
     */
    public String makeHref(final CimiCommonId data, final Integer id) {
        StringBuilder sb = new StringBuilder();
        CimiEntityType type = AppConfig.getType(data);
        switch (type) {
        case CloudEntryPoint:
        case CredentialsCollection:
        case CredentialsTemplateCollection:
        case JobCollection:
        case MachineCollection:
        case MachineConfigurationCollection:
        case MachineImageCollection:
        case MachineTemplateCollection:
            sb.append(this.request.getBaseUri()).append(type.getPathType().pathname);
            break;
        default:
            sb.append(this.request.getBaseUri()).append(type.getPathType().pathname).append('/').append(id);
            break;
        }

        return sb.toString();
    }

}
