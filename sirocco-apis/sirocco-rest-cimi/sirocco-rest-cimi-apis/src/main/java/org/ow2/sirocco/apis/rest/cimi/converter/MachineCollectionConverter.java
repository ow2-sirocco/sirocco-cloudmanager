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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Resource;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineCollection}</li>
 * <li>Service model: {@link MachineCollection}</li>
 * </ul>
 * </p>
 */
public class MachineCollectionConverter extends CollectionConverterAbstract {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiMachineCollection cimi = new CimiMachineCollection();
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
        MachineCollection use;
        if (dataService instanceof List<?>) {
            use = new MachineCollection();
            use.setMachines((List<Machine>) dataService);
        } else {
            use = (MachineCollection) dataService;
        }
        this.doCopyToCimi(context, use, (CimiMachineCollection) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        MachineCollection service = new MachineCollection();
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
        this.doCopyToService(context, (CimiMachineCollection) dataCimi, (MachineCollection) dataService);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#getChildCollection(org.ow2.sirocco.cloudmanager.model.cimi.Resource)
     */
    @Override
    protected Collection<?> getChildCollection(final Resource resourceCollection) {
        MachineCollection collect = (MachineCollection) resourceCollection;
        return collect.getMachines();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#setNewChildCollection(org.ow2.sirocco.cloudmanager.model.cimi.Resource)
     */
    @Override
    protected void setNewChildCollection(final Resource resourceCollection) {
        MachineCollection collect = (MachineCollection) resourceCollection;
        collect.setMachines(new ArrayList<Machine>());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#addItemChildCollection(org.ow2.sirocco.cloudmanager.model.cimi.Resource,
     *      java.lang.Object)
     */
    @Override
    protected void addItemChildCollection(final Resource resourceCollection, final Object itemService) {
        MachineCollection collect = (MachineCollection) resourceCollection;
        collect.getMachines().add((Machine) itemService);
    }
}
