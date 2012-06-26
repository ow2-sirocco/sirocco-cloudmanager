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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
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
            dataCimi.setCredentials((CimiCredentials) context.convertNextCimi(dataService.getCredentials(),
                CimiCredentials.class));
            dataCimi.setMachineConfig((CimiMachineConfiguration) context.convertNextCimi(dataService.getMachineConfiguration(),
                CimiMachineConfiguration.class));
            dataCimi.setMachineImage((CimiMachineImage) context.convertNextCimi(dataService.getMachineImage(),
                CimiMachineImage.class));

            // TODO NetworkInterfaces

            // Volume
            if ((null != dataService.getVolumes()) && (dataService.getVolumes().size() > 0)) {
                List<CimiMachineTemplateVolume> listCimis = new ArrayList<CimiMachineTemplateVolume>();
                for (MachineVolume itemService : dataService.getVolumes()) {
                    listCimis.add((CimiMachineTemplateVolume) context.convertNextCimi(itemService,
                        CimiMachineTemplateVolume.class));
                }
                dataCimi.setVolumes(listCimis.toArray(new CimiMachineTemplateVolume[listCimis.size()]));
            }

            // VolumeTemplate
            if ((null != dataService.getVolumeTemplates()) && (dataService.getVolumeTemplates().size() > 0)) {
                List<CimiMachineTemplateVolumeTemplate> listCimis = new ArrayList<CimiMachineTemplateVolumeTemplate>();
                for (MachineVolumeTemplate itemService : dataService.getVolumeTemplates()) {
                    listCimis.add((CimiMachineTemplateVolumeTemplate) context.convertNextCimi(itemService,
                        CimiMachineTemplateVolumeTemplate.class));
                }
                dataCimi.setVolumeTemplates(listCimis.toArray(new CimiMachineTemplateVolumeTemplate[listCimis.size()]));
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
        dataService.setCredentials((Credentials) context.convertNextService(dataCimi.getCredentials()));
        dataService.setMachineImage((MachineImage) context.convertNextService(dataCimi.getMachineImage()));
        dataService.setMachineConfiguration((MachineConfiguration) context.convertNextService(dataCimi.getMachineConfig()));
        // TODO NetworkInterfaces
        // dataService.setNetworkInterfaces(dataCimi.getUserName());

        // Volume
        if ((null != dataCimi.getVolumes()) && (dataCimi.getVolumes().length > 0)) {
            List<MachineVolume> listServices = new ArrayList<MachineVolume>();
            for (CimiMachineTemplateVolume itemCimi : dataCimi.getVolumes()) {
                listServices.add((MachineVolume) context.convertNextService(itemCimi));
            }
            dataService.setVolumes(listServices);
        }
        // VolumeTemplate
        if ((null != dataCimi.getVolumeTemplates()) && (dataCimi.getVolumeTemplates().length > 0)) {
            List<MachineVolumeTemplate> listServices = new ArrayList<MachineVolumeTemplate>();
            for (CimiMachineTemplateVolumeTemplate itemCimi : dataCimi.getVolumeTemplates()) {
                listServices.add((MachineVolumeTemplate) context.convertNextService(itemCimi));
            }
            dataService.setVolumeTemplates(listServices);
        }
    }

}
