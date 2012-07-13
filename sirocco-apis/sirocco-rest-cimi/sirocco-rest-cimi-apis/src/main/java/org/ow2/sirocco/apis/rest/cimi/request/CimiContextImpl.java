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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.configuration.AppConfig;
import org.ow2.sirocco.apis.rest.cimi.configuration.ConfigFactory;
import org.ow2.sirocco.apis.rest.cimi.configuration.ConfigurationException;
import org.ow2.sirocco.apis.rest.cimi.configuration.ItemConfig;
import org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiExchange;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.cloudmanager.model.cimi.Identifiable;
import org.ow2.sirocco.cloudmanager.model.cimi.Resource;

/**
 * The context used by a REST request during his processing.
 */
public class CimiContextImpl implements CimiContext {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** The current request */
    private CimiRequest request;

    /** The current response */
    private CimiResponse response;

    /** Indicator to force the write-only conversion */
    private boolean convertedWriteOnly;

    /** The stack of CIMI classes used during conversion */
    private LinkedList<Class<?>> stackConvertedCimiClass;

    /** The stack of resources IDs of service classes used during conversion */
    private LinkedList<Integer> stackConvertedIdService;

    /**
     * Set constructor.
     * 
     * @param request The current request
     * @param response The current response
     */
    public CimiContextImpl(final CimiRequest request, final CimiResponse response) {
        super();
        this.request = request;
        this.response = response;
        this.stackConvertedCimiClass = new LinkedList<Class<?>>();
        this.stackConvertedIdService = new LinkedList<Integer>();
        this.convertedWriteOnly = false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#getRequest()
     */
    @Override
    public CimiRequest getRequest() {
        return this.request;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#getResponse()
     */
    @Override
    public CimiResponse getResponse() {
        return this.response;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#getConverter(java.lang
     *      .Class)
     */
    @Override
    public CimiConverter getConverter(final Class<?> klass) {
        CimiConverter converter;
        try {
            ItemConfig item = AppConfig.getInstance().getConfig().find(klass);
            converter = (CimiConverter) item.getData(ConfigFactory.CONVERTER);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("CimiConverter not found in configuration for " + klass.getName());
        }
        return converter;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#convertNextCimi(org.ow2.sirocco.cloudmanager.model.cimi.Resource)
     */
    @Override
    public Object convertToCimi(final Resource service) {
        Object converted = null;
        if (null != service) {
            Class<? extends CimiResource> cimiAssociate = this.findAssociate(service.getClass());
            converted = this.convertToCimi(service, cimiAssociate);
        }
        return converted;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#convertToCimi(java.lang.Object,
     *      java.lang.Class)
     */
    @Override
    public Object convertToCimi(final Object service, final Class<?> cimiAssociate) {
        this.stackConvertedCimiClass.clear();
        this.stackConvertedIdService.clear();
        return this.convertNextCimi(service, cimiAssociate);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#convertNextCimi(org.ow2.sirocco.cloudmanager.model.cimi.Resource)
     */
    @Override
    public Object convertNextCimi(final Resource service) {
        Object converted = null;
        if (null != service) {
            Class<? extends CimiResource> cimiAssociate = this.findAssociate(service.getClass());
            converted = this.convertNextCimi(service, cimiAssociate);
        }
        return converted;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#convertNextCimi(java.lang.Object,
     *      java.lang.Class)
     */
    @Override
    public Object convertNextCimi(final Object service, final Class<?> cimiAssociate) {
        Object converted = null;
        if (null != service) {
            this.stackConvertedCimiClass.push(cimiAssociate);
            if (service instanceof Identifiable) {
                Identifiable idService = (Identifiable) service;
                this.stackConvertedIdService.push(idService.getId());
            } else {
                this.stackConvertedIdService.push(null);
            }
            converted = this.getConverter(cimiAssociate).toCimi(this, service);
            this.stackConvertedCimiClass.pop();
            this.stackConvertedIdService.pop();
        }
        return converted;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#convertToService(java.lang.Object)
     */
    @Override
    public Object convertToService(final Object cimi) {
        this.stackConvertedCimiClass.clear();
        return this.convertNextService(cimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#convertNextService(java.lang.Object)
     */
    @Override
    public Object convertNextService(final Object cimi) {
        Object converted = null;
        if (null != cimi) {
            converted = this.convertNextService(cimi, cimi.getClass());
        }
        return converted;
    }

    @Override
    public Object convertNextService(final Object cimi, final Class<?> cimiToUse) {
        Object converted = null;
        if (null != cimi) {
            this.stackConvertedCimiClass.push(cimiToUse);
            converted = this.getConverter(cimiToUse).toService(this, cimi);
            this.stackConvertedCimiClass.pop();
        }
        return converted;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#mustBeExpanded(org
     *      .ow2.sirocco.apis.rest.cimi.domain.CimiResource)
     */
    @Override
    public boolean mustBeExpanded(final CimiResource resource) {
        boolean expand = true;
        ExchangeType typeCurrent = this.getType(resource);
        ExchangeType typeRoot = this.getType(this.getRootConverting());
        if (typeRoot != typeCurrent) {
            switch (typeRoot) {
            case AddressCollection:
            case AddressTemplateCollection:
            case CloudEntryPoint:
            case CredentialCollection:
            case CredentialTemplateCollection:
            case DiskCollection:
            case JobCollection:
            case MachineCollection:
            case MachineConfigurationCollection:
            case MachineImageCollection:
            case MachineNetworkInterfaceAddressCollection:
            case MachineNetworkInterfaceCollection:
            case MachineTemplateCollection:
            case MachineVolumeCollection:
            case SystemCollection:
            case SystemCredentialCollection:
            case SystemMachineCollection:
            case SystemSystemCollection:
            case SystemTemplateCollection:
            case SystemVolumeCollection:
            case VolumeCollection:
            case VolumeConfigurationCollection:
            case VolumeImageCollection:
            case VolumeTemplateCollection:
            case VolumeVolumeImageCollection:
                expand = this.getRequest().getHeader().getCimiExpand().hasExpandAll();
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
     *      .ow2.CimiResource)
     */
    @Override
    public boolean mustBeReferenced(final CimiResource resource) {
        boolean reference = false;
        ExchangeType typeCurrent = this.getType(resource);
        ExchangeType typeRoot = this.getType(this.getRootConverting());
        if (typeRoot != typeCurrent) {
            switch (typeRoot) {
            case AddressCollection:
            case AddressTemplateCollection:
            case CloudEntryPoint:
            case CredentialCollection:
            case CredentialTemplateCollection:
            case DiskCollection:
            case JobCollection:
            case MachineCollection:
            case MachineConfigurationCollection:
            case MachineImageCollection:
            case MachineNetworkInterfaceAddressCollection:
            case MachineNetworkInterfaceCollection:
            case MachineTemplateCollection:
            case MachineVolumeCollection:
            case SystemCollection:
            case SystemCredentialCollection:
            case SystemMachineCollection:
            case SystemSystemCollection:
            case SystemTemplateCollection:
            case SystemVolumeCollection:
            case VolumeCollection:
            case VolumeConfigurationCollection:
            case VolumeImageCollection:
            case VolumeTemplateCollection:
            case VolumeVolumeImageCollection:
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
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#makeHref(CimiResource,
     *      java.lang.String)
     */
    @Override
    public String makeHrefBase(final CimiResource data) {
        return this.makeHref(data, (String) null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.utils.CimiContext#makeHref(CimiResource,
     *      java.lang.String)
     */
    @Override
    public String makeHref(final CimiResource data, final String id) {
        return this.makeHref(data.getClass(), id);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#makeHref(java.lang.Class,
     *      java.lang.String)
     */
    @Override
    public String makeHref(final Class<? extends CimiResource> classToUse, final String id) {
        String href = null;
        ExchangeType type = this.getType(classToUse);
        // Detects if type has parent
        if (true == type.hasParent()) {
            // Adds all IDs parent of the request if exists
            if (true == this.getRequest().hasParentIds()) {
                href = type.makeHref(this.getRequest().getBaseUri(), this.getRequest().getIds().makeArrayWithParents(id));
            } else {
                // Adds all IDs parent of the service if exists
                List<Integer> idsParent = this.findAllServiceIdParent();
                if (idsParent.size() > 0) {
                    // Reverse order : oldest to youngest
                    Collections.reverse(idsParent);
                    List<String> idsString = new ArrayList<String>();
                    for (Integer serviceId : idsParent) {
                        idsString.add(serviceId.toString());
                    }
                    // Add id
                    idsString.add(id);
                    // Make HREF
                    href = type.makeHref(this.getRequest().getBaseUri(), idsString.toArray(new String[idsString.size()]));
                }
            }
        } else {
            // None parent
            href = type.makeHref(this.getRequest().getBaseUri(), id);
        }
        return href;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#isConvertedWriteOnly()
     */
    @Override
    public boolean isConvertedWriteOnly() {
        return this.convertedWriteOnly;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#setConvertedWriteOnly(boolean)
     */
    @Override
    public void setConvertedWriteOnly(final boolean convertedWriteOnly) {
        this.convertedWriteOnly = convertedWriteOnly;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiContext#findAssociate(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends CimiResource> findAssociate(final Class<? extends Resource> klass) {
        Class<? extends CimiResource> cimi = null;
        try {
            ItemConfig item = AppConfig.getInstance().getConfig().find(klass);
            cimi = (Class<? extends CimiResource>) item.getData(ConfigFactory.ASSOCIATE_TO);
        } catch (Exception e) {
            throw new ConfigurationException("Associate class not found in configuration for " + klass.getName());
        }
        return cimi;
    }

    /**
     * Returns the root class being converted.
     * 
     * @return The current root converting
     */
    protected Class<?> getRootConverting() {
        return this.stackConvertedCimiClass.getLast();
    }

    /**
     * Finds all "ID parent" stored in the stack "ID service" for the current
     * object being converted.
     * 
     * @return A list of all IdParents, the list is empty if none idParent is
     *         found
     */
    protected List<Integer> findAllServiceIdParent() {
        List<Integer> idsParent = new ArrayList<Integer>();
        Integer idParent = null;
        for (int i = 1; i < this.stackConvertedIdService.size(); i++) {
            idParent = this.stackConvertedIdService.get(i);
            if (null != idParent) {
                idsParent.add(idParent);
            }
        }
        return idsParent;
    }

    /**
     * Get the exchange type for a CIMI instance.
     * 
     * @param exchange A CIMI Exchange instance
     * @return The exchange type or null if the instance's class is unknown
     */
    protected ExchangeType getType(final CimiExchange exchange) {
        ExchangeType type = null;
        if (null != exchange) {
            type = exchange.getExchangeType();
        }
        return type;
    }

    /**
     * Get the exchange type for a instance.
     * 
     * @param klass A class
     * @return The exchange type or null if the class is unknown
     */
    protected ExchangeType getType(final Class<?> klass) {
        ExchangeType type = null;
        ItemConfig item = AppConfig.getInstance().getConfig().find(klass);
        if (null != item) {
            type = item.getType();
        }
        return type;
    }

}
