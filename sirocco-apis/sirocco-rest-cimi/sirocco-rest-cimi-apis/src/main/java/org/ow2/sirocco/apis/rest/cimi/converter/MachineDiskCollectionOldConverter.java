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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDiskCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineDiskCollection}</li>
 * <li>Service model: {@link DiskCollection}</li>
 * </ul>
 * </p>
 */
public class MachineDiskCollectionOldConverter extends
        CollectionOldConverterAbstract {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiMachineDiskCollection cimi = new CimiMachineDiskCollection();
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
    public void copyToCimi(final CimiContext context, final Object dataService,
            final Object dataCimi) {
        this.doCopyToCimi(context, (List<Object>) dataService,
                (CimiMachineDiskCollection) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        List<MachineDisk> service = new ArrayList<MachineDisk>();
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
    public void copyToService(final CimiContext context, final Object dataCimi,
            final Object dataService) {
        this.doCopyToService(context, (CimiMachineDiskCollection) dataCimi,
                (List<Object>) dataService);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#getChildCollection(java.lang.Object)
     */
    @Override
    protected Collection<?> getChildCollection(final Object resourceCollection) {
        // MachineDiskCollection collect = (MachineDiskCollection)
        // resourceCollection;
        // return collect.getItems();
        return (List<MachineDisk>) resourceCollection;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#setNewChildCollection(java.lang.Object)
     */
    @Override
    protected void setNewChildCollection(final Object resourceCollection) {
        // MachineDiskCollection collect = (MachineDiskCollection)
        // resourceCollection;
        // collect.setItems(new ArrayList<MachineDisk>());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CollectionConverterAbstract#addItemChildCollection(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    protected void addItemChildCollection(final Object resourceCollection,
            final Object itemService) {
        // MachineDiskCollection collect = (MachineDiskCollection)
        // resourceCollection;
        // collect.getItems().add((MachineDisk) itemService);
    }
}
