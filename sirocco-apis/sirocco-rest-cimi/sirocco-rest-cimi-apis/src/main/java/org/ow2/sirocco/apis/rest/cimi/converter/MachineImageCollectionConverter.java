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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Resource;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineImageCollection}</li>
 * <li>Service model: {@link MachineImageCollection}</li>
 * </ul>
 * </p>
 */
public class MachineImageCollectionConverter extends CollectionConverterAbstract {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiMachineImageCollection cimi = new CimiMachineImageCollection();
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
        MachineImageCollection use;
        if (dataService instanceof List<?>) {
            use = new MachineImageCollection();
            use.setImages((List<MachineImage>) dataService);
        } else {
            use = (MachineImageCollection) dataService;
        }
        this.doCopyToCimi(context, use, (CimiMachineImageCollection) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        MachineImageCollection service = new MachineImageCollection();
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
        this.doCopyToService(context, (CimiMachineImageCollection) dataCimi, (MachineImageCollection) dataService);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#getChildCollection(org.ow2.sirocco.cloudmanager.model.cimi.Resource)
     */
    @Override
    protected Collection<?> getChildCollection(final Resource resourceCollection) {
        MachineImageCollection collect = (MachineImageCollection) resourceCollection;
        return collect.getImages();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#setNewChildCollection(org.ow2.sirocco.cloudmanager.model.cimi.Resource)
     */
    @Override
    protected void setNewChildCollection(final Resource resourceCollection) {
        MachineImageCollection collect = (MachineImageCollection) resourceCollection;
        collect.setImages(new ArrayList<MachineImage>());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#addItemChildCollection(org.ow2.sirocco.cloudmanager.model.cimi.Resource,
     *      java.lang.Object)
     */
    @Override
    protected void addItemChildCollection(final Resource resourceCollection, final Object itemService) {
        MachineImageCollection collect = (MachineImageCollection) resourceCollection;
        collect.getImages().add((MachineImage) itemService);
    }
}
