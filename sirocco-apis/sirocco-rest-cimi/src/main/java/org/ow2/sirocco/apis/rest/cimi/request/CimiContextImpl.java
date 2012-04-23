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
package org.ow2.sirocco.apis.rest.cimi.request;

import org.ow2.sirocco.apis.rest.cimi.configuration.AppConfig;
import org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiHref;

/**
 * .
 */
public class CimiContextImpl implements CimiContext {

    private CimiEntityType currentRoot;

    private CimiRequest request;

    /**
     * Set constructor.
     * 
     * @param currentRoot The root
     */
    public CimiContextImpl(final CimiRequest request) {
        super();
        this.request = request;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#getType(org.ow2.sirocco.apis.rest.cimi.domain.CimiData)
     */
    public CimiEntityType getType(final CimiData data) {
        AppConfig.getInstance();
        return AppConfig.getType(data);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#getCurrentRootConverting()
     */
    @Override
    public CimiEntityType getCurrentRootConverting() {
        return this.currentRoot;
    }

    /**
     * Set the CimiRequest.
     * 
     * @param request The CimiRequest
     */
    public void setRequest(final CimiRequest request) {
        this.request = request;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#getRequest()
     */
    public CimiRequest getRequest() {
        return this.request;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#getRootConverter(CimiEntityType)
     */
    @Override
    public CimiConverter getRootConverter(final CimiEntityType type) {
        this.currentRoot = type;
        return AppConfig.getConverter(type);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#getConverter(java.lang
     *      .Class)
     */
    @Override
    public CimiConverter getConverter(final Class<?> klass) {
        return AppConfig.getConverter(klass);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#getEntityConverter(java.lang.Class)
     */
    @Override
    public EntityConverter getEntityConverter(final Class<?> klass) {
        return AppConfig.getEntityConverter(klass);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#mustBeExpanded(org
     *      .ow2.sirocco.apis.rest.cimi.domain.CimiData)
     */
    @Override
    public boolean mustBeExpanded(final CimiData data) {
        boolean expand = true;
        CimiEntityType type = AppConfig.getType(data);
        if (this.currentRoot != type) {
            switch (this.currentRoot) {
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

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#mustBeReferenced(org
     *      .ow2.CimiData)
     */
    @Override
    public boolean mustBeReferenced(final CimiData data) {
        boolean reference = false;
        CimiEntityType type = AppConfig.getType(data);
        if (this.currentRoot != type) {
            switch (this.currentRoot) {
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

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#mustHaveIdInReference(org.ow2.sirocco.apis.rest.cimi.domain.CimiHref)
     */
    @Override
    public boolean mustHaveIdInReference(final CimiHref data) {
        boolean withId = true;
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
            withId = false;
            break;
        default:
            break;
        }
        return withId;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#makeHref(org.ow2.sirocco
     *      .apis.rest.cimi.domain.CimiCommonId, java.lang.String)
     */
    @Override
    public String makeHrefBase(final CimiHref data) {
        return this.makeHref(data, (Integer) null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#makeHref(org.ow2.sirocco
     *      .apis.rest.cimi.domain.CimiCommonId, java.lang.String)
     */
    @Override
    public String makeHref(final CimiHref data, final String id) {
        return this.makeHref(data, Integer.valueOf(id));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#makeHref(org.ow2.sirocco
     *      .apis.rest.cimi.domain.CimiCommonId, java.lang.Integer)
     */
    @Override
    public String makeHref(final CimiHref data, final Integer id) {
        StringBuilder sb = new StringBuilder();
        CimiEntityType type = AppConfig.getType(data);
        sb.append(this.request.getBaseUri()).append(type.getPathType().getPathname());
        if (true == this.mustHaveIdInReference(data)) {
            sb.append('/');
            if (null != id) {
                sb.append(id);
            }
        }
        return sb.toString();
    }
}
