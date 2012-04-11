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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJobCollection;
import org.ow2.sirocco.apis.rest.cimi.utils.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.utils.Context;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiJobCollection}</li>
 * <li>Service model: {@link JobCollection}</li>
 * </ul>
 * </p>
 */
public class JobCollectionConverter extends CommonIdConverter implements EntityConverter {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final Context context, final Object dataService) {
        CimiJobCollection cimi = new CimiJobCollection();
        this.copyToCimi(context, dataService, cimi);
        return cimi;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#copyToCimi(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void copyToCimi(final Context context, final Object dataService, final Object dataCimi) {
        JobCollection use;
        if (dataService instanceof List<?>) {
            use = new JobCollection();
            use.setJobs((List<Job>) dataService);
        } else {
            use = (JobCollection) dataService;
        }
        this.doCopyToCimi(context, use, (CimiJobCollection) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final Context context, final Object dataCimi) {
        JobCollection service = new JobCollection();
        this.copyToService(context, dataCimi, service);
        return service;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#copyToService
     *      (org.ow2.sirocco.apis.rest.cimi.utils.Context, java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public void copyToService(final Context context, final Object dataCimi, final Object dataService) {
        this.doCopyToService(context, (CimiJobCollection) dataCimi, (JobCollection) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final Context context, final JobCollection dataService, final CimiJobCollection dataCimi) {
        this.fill(context, dataService, dataCimi);
        EntityConverter converter = context.getConverter(CimiEntityType.Job);
        List<CimiJob> cimiList = new ArrayList<CimiJob>();
        for (Job machineImage : dataService.getJobs()) {
            cimiList.add((CimiJob) converter.toCimi(context, machineImage));
        }
        dataCimi.setJobs(cimiList.toArray(new CimiJob[cimiList.size()]));

    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    protected void doCopyToService(final Context context, final CimiJobCollection dataCimi, final JobCollection dataService) {
        List<Job> listServicesImages = new ArrayList<Job>();
        dataService.setJobs(listServicesImages);
        CimiJob[] images = dataCimi.getJobs();
        if (null != images) {
            EntityConverter converter = context.getConverter(CimiEntityType.Job);
            for (CimiJob cimiImage : images) {
                listServicesImages.add((Job) converter.toService(context, cimiImage));
            }
        }
    }

}
