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
package org.ow2.sirocco.apis.rest.cimi.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Resource;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineConfigurationCollection}</li>
 * <li>Service model: List of {@link MachineConfiguration}</li>
 * </ul>
 * </p>
 */
public class MachineConfigurationCollectionConverter extends CollectionConverterAbstract {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiMachineConfigurationCollection cimi = new CimiMachineConfigurationCollection();
        this.copyToCimi(context, dataService, cimi);
        return cimi;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#copyToCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void copyToCimi(final CimiContext context, final Object dataService, final Object dataCimi) {
        MachineConfigurationCollection use;
        if (dataService instanceof List<?>) {
            use = new MachineConfigurationCollection();
            use.setMachineConfigurations((List<MachineConfiguration>) dataService);
        } else {
            use = (MachineConfigurationCollection) dataService;
        }
        this.doCopyToCimi(context, use, (CimiMachineConfigurationCollection) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        MachineConfigurationCollection service = new MachineConfigurationCollection();
        this.copyToService(context, dataCimi, service);
        return service;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#copyToService
     *      (org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void copyToService(final CimiContext context, final Object dataCimi, final Object dataService) {
        this.doCopyToService(context, (CimiMachineConfigurationCollection) dataCimi,
            (MachineConfigurationCollection) dataService);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#getChildCollection(org.ow2.sirocco.cloudmanager.model.cimi.Resource)
     */
    @Override
    protected Collection<?> getChildCollection(final Resource resourceCollection) {
        MachineConfigurationCollection collect = (MachineConfigurationCollection) resourceCollection;
        return collect.getMachineConfigurations();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#setNewChildCollection(org.ow2.sirocco.cloudmanager.model.cimi.Resource)
     */
    @Override
    protected void setNewChildCollection(final Resource resourceCollection) {
        MachineConfigurationCollection collect = (MachineConfigurationCollection) resourceCollection;
        collect.setMachineConfigurations(new ArrayList<MachineConfiguration>());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#addItemChildCollection(org.ow2.sirocco.cloudmanager.model.cimi.Resource,
     *      java.lang.Object)
     */
    @Override
    protected void addItemChildCollection(final Resource resourceCollection, final Object itemService) {
        MachineConfigurationCollection collect = (MachineConfigurationCollection) resourceCollection;
        collect.getMachineConfigurations().add((MachineConfiguration) itemService);
    }
}
