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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiArray;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineTemplateCollection}</li>
 * <li>Service model: {@link MachineTemplateCollection}</li>
 * </ul>
 * </p>
 */
public class MachineTemplateCollectionConverter extends CollectionConverter {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiMachineTemplateCollection cimi = new CimiMachineTemplateCollection();
        this.copyToCimi(context, dataService, cimi);
        return cimi;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.ResourceConverter#copyToCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void copyToCimi(final CimiContext context, final Object dataService, final Object dataCimi) {
        MachineTemplateCollection use;
        if (dataService instanceof List<?>) {
            use = new MachineTemplateCollection();
            use.setMachineTemplates((List<MachineTemplate>) dataService);
        } else {
            use = (MachineTemplateCollection) dataService;
        }
        this.doCopyToCimi(context, use, (CimiMachineTemplateCollection) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        MachineTemplateCollection service = new MachineTemplateCollection();
        this.copyToService(context, dataCimi, service);
        return service;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.ResourceConverter#copyToService
     *      (org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void copyToService(final CimiContext context, final Object dataCimi, final Object dataService) {
        this.doCopyToService(context, (CimiMachineTemplateCollection) dataCimi, (MachineTemplateCollection) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final MachineTemplateCollection dataService,
        final CimiMachineTemplateCollection dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.mustBeExpanded(dataCimi)) {
            if ((null != dataService.getMachineTemplates()) && (dataService.getMachineTemplates().size() > 0)) {
                CimiConverter converter = context.getConverter(CimiMachineTemplate.class);
                CimiArray<CimiMachineTemplate> cimiList = dataCimi.newCollection();

                for (MachineTemplate serviceItem : dataService.getMachineTemplates()) {
                    cimiList.add((CimiMachineTemplate) converter.toCimi(context, serviceItem));
                }
                dataCimi.setCollection(cimiList);
            }
        }
    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    protected void doCopyToService(final CimiContext context, final CimiMachineTemplateCollection dataCimi,
        final MachineTemplateCollection dataService) {
        CimiArray<CimiMachineTemplate> cimiList = dataCimi.getCollection();
        if ((null != cimiList) && (cimiList.size() > 0)) {
            List<MachineTemplate> serviceList = new ArrayList<MachineTemplate>();
            dataService.setMachineTemplates(serviceList);

            CimiConverter converter = context.getConverter(CimiMachineTemplate.class);
            for (CimiMachineTemplate cimiItem : cimiList) {
                serviceList.add((MachineTemplate) converter.toService(context, cimiItem));
            }
        }
    }
}
