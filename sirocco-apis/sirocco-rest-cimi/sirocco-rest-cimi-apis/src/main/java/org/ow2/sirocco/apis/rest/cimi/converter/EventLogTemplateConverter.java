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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiEventLogTemplate;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogTemplate;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiEventLogTemplate}</li>
 * <li>Service model: {@link EventLogTemplate}</li>
 * </ul>
 * </p>
 */
public class EventLogTemplateConverter extends ObjectCommonConverter {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiEventLogTemplate cimi = new CimiEventLogTemplate();
        this.copyToCimi(context, dataService, cimi);
        return cimi;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#copyToCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void copyToCimi(final CimiContext context, final Object dataService, final Object dataCimi) {
        this.doCopyToCimi(context, (EventLogTemplate) dataService, (CimiEventLogTemplate) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        EventLogTemplate service = new EventLogTemplate();
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
        this.doCopyToService(context, (CimiEventLogTemplate) dataCimi, (EventLogTemplate) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final EventLogTemplate dataService,
        final CimiEventLogTemplate dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.mustBeExpanded(dataCimi)) {
            // FIXME

            // dataCimi
            // .setCredential((CimiCredential)
            // context.convertNextCimi(dataService.getCredentials(),
            // CimiCredential.class));
            // dataCimi.setInitialState(ConverterHelper.toString(dataService.getInitialState()));
            // dataCimi.setEventLogConfig((CimiEventLogConfiguration)
            // context.convertNextCimi(
            // dataService.getEventLogConfiguration(),
            // CimiEventLogConfiguration.class));
            // dataCimi.setEventLogImage((CimiEventLogImage)
            // context.convertNextCimi(dataService.getEventLogImage(),
            // CimiEventLogImage.class));
            // // EventLogInterface
            // if ((null != dataService.getEventLogInterfaces()) && (false ==
            // dataService.getEventLogInterfaces().isEmpty())) {
            // List<CimiEventLogTemplateEventLogInterface> listCimis = new
            // ArrayList<CimiEventLogTemplateEventLogInterface>();
            // for (EventLogTemplateEventLogInterface itemService :
            // dataService.getEventLogInterfaces()) {
            // listCimis.add((CimiEventLogTemplateEventLogInterface)
            // context.convertNextCimi(itemService,
            // CimiEventLogTemplateEventLogInterface.class));
            // }
            // dataCimi.setListEventLogInterfaces(listCimis);
            // }
            // dataCimi.setUserData(dataService.getUserData());
            // // Volume
            // if ((null != dataService.getVolumes()) && (false ==
            // dataService.getVolumes().isEmpty())) {
            // List<CimiEventLogTemplateVolume> listCimis = new
            // ArrayList<CimiEventLogTemplateVolume>();
            // for (EventLogVolume itemService : dataService.getVolumes()) {
            // listCimis.add((CimiEventLogTemplateVolume)
            // context.convertNextCimi(itemService,
            // CimiEventLogTemplateVolume.class));
            // }
            // dataCimi.setListVolumes(listCimis);
            // }
            // // VolumeTemplate
            // if ((null != dataService.getVolumeTemplates()) && (false ==
            // dataService.getVolumeTemplates().isEmpty())) {
            // List<CimiEventLogTemplateVolumeTemplate> listCimis = new
            // ArrayList<CimiEventLogTemplateVolumeTemplate>();
            // for (EventLogVolumeTemplate itemService :
            // dataService.getVolumeTemplates()) {
            // listCimis.add((CimiEventLogTemplateVolumeTemplate)
            // context.convertNextCimi(itemService,
            // CimiEventLogTemplateVolumeTemplate.class));
            // }
            // dataCimi.setListVolumeTemplates(listCimis);
            // }
        }
    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    protected void doCopyToService(final CimiContext context, final CimiEventLogTemplate dataCimi,
        final EventLogTemplate dataService) {
        this.fill(context, dataCimi, dataService);
        // FIXME

        // dataService.setCredentials((Credentials)
        // context.convertNextService(dataCimi.getCredential()));
        // dataService.setInitialState(ConverterHelper.toEventLogState(dataCimi.getInitialState()));
        // dataService.setEventLogImage((EventLogImage)
        // context.convertNextService(dataCimi.getEventLogImage()));
        // dataService.setEventLogConfiguration((EventLogConfiguration)
        // context.convertNextService(dataCimi.getEventLogConfig()));
        // // EventLogInterface
        // if ((null != dataCimi.getListEventLogInterfaces()) && (false ==
        // dataCimi.getListEventLogInterfaces().isEmpty())) {
        // List<EventLogTemplateEventLogInterface> listServices = new
        // ArrayList<EventLogTemplateEventLogInterface>();
        // for (CimiEventLogTemplateEventLogInterface itemCimi :
        // dataCimi.getListEventLogInterfaces()) {
        // listServices.add((EventLogTemplateEventLogInterface)
        // context.convertNextService(itemCimi));
        // }
        // dataService.setEventLogInterfaces(listServices);
        // }
        // dataService.setUserData(dataCimi.getUserData());
        // // Volume
        // if ((null != dataCimi.getListVolumes()) && (false ==
        // dataCimi.getListVolumes().isEmpty())) {
        // List<EventLogVolume> listServices = new ArrayList<EventLogVolume>();
        // for (CimiEventLogTemplateVolume itemCimi : dataCimi.getVolumes()) {
        // listServices.add((EventLogVolume)
        // context.convertNextService(itemCimi));
        // }
        // dataService.setVolumes(listServices);
        // }
        // // VolumeTemplate
        // if ((null != dataCimi.getListVolumeTemplates()) && (false ==
        // dataCimi.getListVolumeTemplates().isEmpty())) {
        // List<EventLogVolumeTemplate> listServices = new
        // ArrayList<EventLogVolumeTemplate>();
        // for (CimiEventLogTemplateVolumeTemplate itemCimi :
        // dataCimi.getListVolumeTemplates()) {
        // listServices.add((EventLogVolumeTemplate)
        // context.convertNextService(itemCimi));
        // }
        // dataService.setVolumeTemplates(listServices);
        // }
    }

}
