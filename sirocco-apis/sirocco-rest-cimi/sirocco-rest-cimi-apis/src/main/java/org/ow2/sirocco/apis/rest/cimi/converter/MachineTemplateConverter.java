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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateNetworkInterface;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineTemplate}</li>
 * <li>Service model: {@link MachineTemplate}</li>
 * </ul>
 * </p>
 */
public class MachineTemplateConverter extends ObjectCommonConverter {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiMachineTemplate cimi = new CimiMachineTemplate();
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
        this.doCopyToCimi(context, (MachineTemplate) dataService, (CimiMachineTemplate) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        MachineTemplate service = new MachineTemplate();
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
        this.doCopyToService(context, (CimiMachineTemplate) dataCimi, (MachineTemplate) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final MachineTemplate dataService, final CimiMachineTemplate dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.mustBeExpanded(dataCimi)) {
            dataCimi
                .setCredential((CimiCredential) context.convertNextCimi(dataService.getCredentials(), CimiCredential.class));
            dataCimi.setInitialState(ConverterHelper.toString(dataService.getInitialState()));
            // FIXME EventLogTemplate
            // dataCimi.setEventLogTemplate((CimiEventLogTemplate)
            // context.convertNextCimi(dataService.getEventLogTemplate(),
            // CimiEventLogTemplate.class));
            dataCimi.setMachineConfig((CimiMachineConfiguration) context.convertNextCimi(dataService.getMachineConfiguration(),
                CimiMachineConfiguration.class));
            dataCimi.setMachineImage((CimiMachineImage) context.convertNextCimi(dataService.getMachineImage(),
                CimiMachineImage.class));
            // NetworkInterface
            if ((null != dataService.getNetworkInterfaces()) && (false == dataService.getNetworkInterfaces().isEmpty())) {
                List<CimiMachineTemplateNetworkInterface> listCimis = new ArrayList<CimiMachineTemplateNetworkInterface>();
                for (MachineTemplateNetworkInterface itemService : dataService.getNetworkInterfaces()) {
                    listCimis.add((CimiMachineTemplateNetworkInterface) context.convertNextCimi(itemService,
                        CimiMachineTemplateNetworkInterface.class));
                }
                dataCimi.setListNetworkInterfaces(listCimis);
            }
            dataCimi.setUserData(dataService.getUserData());
            // Volume
            if ((null != dataService.getVolumes()) && (false == dataService.getVolumes().isEmpty())) {
                List<CimiMachineTemplateVolume> listCimis = new ArrayList<CimiMachineTemplateVolume>();
                for (MachineVolume itemService : dataService.getVolumes()) {
                    listCimis.add((CimiMachineTemplateVolume) context.convertNextCimi(itemService,
                        CimiMachineTemplateVolume.class));
                }
                dataCimi.setListVolumes(listCimis);
            }
            // VolumeTemplate
            if ((null != dataService.getVolumeTemplates()) && (false == dataService.getVolumeTemplates().isEmpty())) {
                List<CimiMachineTemplateVolumeTemplate> listCimis = new ArrayList<CimiMachineTemplateVolumeTemplate>();
                for (MachineVolumeTemplate itemService : dataService.getVolumeTemplates()) {
                    listCimis.add((CimiMachineTemplateVolumeTemplate) context.convertNextCimi(itemService,
                        CimiMachineTemplateVolumeTemplate.class));
                }
                dataCimi.setListVolumeTemplates(listCimis);
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
    protected void doCopyToService(final CimiContext context, final CimiMachineTemplate dataCimi,
        final MachineTemplate dataService) {
        this.fill(context, dataCimi, dataService);
        dataService.setCredentials((Credentials) context.convertNextService(dataCimi.getCredential()));
        // FIXME EventLogTemplate
        // dataService.setEventLogTemplate((EventLogTemplate)
        // context.convertNextService(dataCimi.getEventLogTemplate()));
        dataService.setInitialState(ConverterHelper.toMachineState(dataCimi.getInitialState()));
        dataService.setMachineImage((MachineImage) context.convertNextService(dataCimi.getMachineImage()));
        dataService.setMachineConfiguration((MachineConfiguration) context.convertNextService(dataCimi.getMachineConfig()));
        // NetworkInterface
        if ((null != dataCimi.getListNetworkInterfaces()) && (false == dataCimi.getListNetworkInterfaces().isEmpty())) {
            List<MachineTemplateNetworkInterface> listServices = new ArrayList<MachineTemplateNetworkInterface>();
            for (CimiMachineTemplateNetworkInterface itemCimi : dataCimi.getListNetworkInterfaces()) {
                listServices.add((MachineTemplateNetworkInterface) context.convertNextService(itemCimi));
            }
            dataService.setNetworkInterfaces(listServices);
        }
        dataService.setUserData(dataCimi.getUserData());
        // Volume
        if ((null != dataCimi.getListVolumes()) && (false == dataCimi.getListVolumes().isEmpty())) {
            List<MachineVolume> listServices = new ArrayList<MachineVolume>();
            for (CimiMachineTemplateVolume itemCimi : dataCimi.getVolumes()) {
                listServices.add((MachineVolume) context.convertNextService(itemCimi));
            }
            dataService.setVolumes(listServices);
        }
        // VolumeTemplate
        if ((null != dataCimi.getListVolumeTemplates()) && (false == dataCimi.getListVolumeTemplates().isEmpty())) {
            List<MachineVolumeTemplate> listServices = new ArrayList<MachineVolumeTemplate>();
            for (CimiMachineTemplateVolumeTemplate itemCimi : dataCimi.getListVolumeTemplates()) {
                listServices.add((MachineVolumeTemplate) context.convertNextService(itemCimi));
            }
            dataService.setVolumeTemplates(listServices);
        }
    }

}
