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
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;

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
public class MachineImageCollectionConverter extends CommonIdConverter implements EntityConverter {

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
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#copyToCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
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
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#copyToService
     *      (org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void copyToService(final CimiContext context, final Object dataCimi, final Object dataService) {
        this.doCopyToService(context, (CimiMachineImageCollection) dataCimi, (MachineImageCollection) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final MachineImageCollection dataService,
        final CimiMachineImageCollection dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (null != dataService.getImages()) {
            CimiConverter converter = context.getConverter(CimiMachineImage.class);
            List<CimiMachineImage> cimiList = new ArrayList<CimiMachineImage>();
            for (MachineImage machineImage : dataService.getImages()) {
                cimiList.add((CimiMachineImage) converter.toCimi(context, machineImage));
            }
            dataCimi.setMachineImages(cimiList.toArray(new CimiMachineImage[cimiList.size()]));
        }
    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    protected void doCopyToService(final CimiContext context, final CimiMachineImageCollection dataCimi,
        final MachineImageCollection dataService) {
        List<MachineImage> listServicesImages = new ArrayList<MachineImage>();
        dataService.setImages(listServicesImages);
        CimiMachineImage[] images = dataCimi.getMachineImages();
        if (null != images) {
            CimiConverter converter = context.getConverter(CimiMachineImage.class);
            for (CimiMachineImage cimiImage : images) {
                listServicesImages.add((MachineImage) converter.toService(context, cimiImage));
            }
        }
    }

}
