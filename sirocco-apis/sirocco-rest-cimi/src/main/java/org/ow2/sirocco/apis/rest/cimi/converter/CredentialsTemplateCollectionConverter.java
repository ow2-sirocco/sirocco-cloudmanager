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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplateCollection;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiCredentialsTemplateCollection}</li>
 * <li>Service model: {@link CredentialsTemplateCollection}</li>
 * </ul>
 * </p>
 */
public class CredentialsTemplateCollectionConverter extends CommonIdConverter implements EntityConverter {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiCredentialsTemplateCollection cimi = new CimiCredentialsTemplateCollection();
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
        CredentialsTemplateCollection use;
        if (dataService instanceof List<?>) {
            use = new CredentialsTemplateCollection();
            use.setCredentialsTemplates((List<CredentialsTemplate>) dataService);
        } else {
            use = (CredentialsTemplateCollection) dataService;
        }
        this.doCopyToCimi(context, use, (CimiCredentialsTemplateCollection) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        CredentialsTemplateCollection service = new CredentialsTemplateCollection();
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
        this.doCopyToService(context, (CimiCredentialsTemplateCollection) dataCimi, (CredentialsTemplateCollection) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final CredentialsTemplateCollection dataService,
        final CimiCredentialsTemplateCollection dataCimi) {
        this.fill(context, dataService, dataCimi);
        CimiConverter converter = context.getConverter(CimiCredentialsTemplate.class);
        List<CimiCredentialsTemplate> cimiList = new ArrayList<CimiCredentialsTemplate>();
        for (CredentialsTemplate machineImage : dataService.getCredentialsTemplates()) {
            cimiList.add((CimiCredentialsTemplate) converter.toCimi(context, machineImage));
        }
        dataCimi.setCredentialsTemplates(cimiList.toArray(new CimiCredentialsTemplate[cimiList.size()]));

    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    protected void doCopyToService(final CimiContext context, final CimiCredentialsTemplateCollection dataCimi,
        final CredentialsTemplateCollection dataService) {
        List<CredentialsTemplate> listServicesImages = new ArrayList<CredentialsTemplate>();
        dataService.setCredentialsTemplates(listServicesImages);

        CimiConverter converter = context.getConverter(CimiCredentialsTemplate.class);
        CimiCredentialsTemplate[] images = dataCimi.getCredentialsTemplates();
        for (CimiCredentialsTemplate cimiImage : images) {
            listServicesImages.add((CredentialsTemplate) converter.toService(context, cimiImage));
        }
    }

}
