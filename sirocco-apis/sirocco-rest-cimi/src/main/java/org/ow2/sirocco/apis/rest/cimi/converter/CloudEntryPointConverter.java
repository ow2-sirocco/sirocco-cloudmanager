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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CloudEntryPointAggregate;
import org.ow2.sirocco.apis.rest.cimi.utils.Context;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiCloudEntryPoint}</li>
 * <li>Service model: List of {@link CloudEntryPoint}</li>
 * </ul>
 * </p>
 */
public class CloudEntryPointConverter extends CommonIdConverter implements EntityConverter {
    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final Context context, final Object dataService) {
        CimiCloudEntryPoint cimi = new CimiCloudEntryPoint();
        this.copyToCimi(context, dataService, cimi);
        return cimi;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#copyToCimi(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void copyToCimi(final Context context, final Object dataService, final Object dataCimi) {
        this.doCopyToCimi(context, (CloudEntryPointAggregate) dataService, (CimiCloudEntryPoint) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final Context context, final Object dataCimi) {
        CloudEntryPoint service = new CloudEntryPoint();
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
        this.doCopyToService(context, (CimiCloudEntryPoint) dataCimi, (CloudEntryPoint) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final Context context, final CloudEntryPointAggregate dataService,
        final CimiCloudEntryPoint dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.shouldBeExpanded(dataCimi)) {
            if (null != dataService.getCredentials()) {
                dataCimi.setCredentials((CimiCredentialsCollection) context.getConverter(CimiCredentialsCollection.class)
                    .toCimi(context, dataService.getCredentials()));
            }
            if (null != dataService.getCredentialsTemplates()) {
                dataCimi.setCredentialsTemplates((CimiCredentialsTemplateCollection) context.getConverter(
                    CimiCredentialsTemplateCollection.class).toCimi(context, dataService.getCredentialsTemplates()));
            }
            if (null != dataService.getMachineConfigs()) {
                dataCimi.setMachineConfigs((CimiMachineConfigurationCollection) context.getConverter(
                    CimiMachineConfigurationCollection.class).toCimi(context, dataService.getMachineConfigs()));
            }
            if (null != dataService.getMachineImages()) {
                dataCimi.setMachineImages((CimiMachineImageCollection) context.getConverter(CimiMachineImageCollection.class)
                    .toCimi(context, dataService.getMachineImages()));
            }
            if (null != dataService.getMachines()) {
                dataCimi.setMachines((CimiMachineCollection) context.getConverter(CimiMachineCollection.class).toCimi(context,
                    dataService.getMachines()));
            }
            if (null != dataService.getMachineTemplates()) {
                dataCimi.setMachineTemplates((CimiMachineTemplateCollection) context.getConverter(
                    CimiMachineTemplateCollection.class).toCimi(context, dataService.getMachineTemplates()));
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
    protected void doCopyToService(final Context context, final CimiCloudEntryPoint dataCimi, final CloudEntryPoint dataService) {
        this.fill(dataCimi, dataService);
    }

}
