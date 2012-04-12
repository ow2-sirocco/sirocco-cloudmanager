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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.utils.Context;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;

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
public class MachineTemplateConverter extends CommonIdConverter implements EntityConverter {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final Context context, final Object dataService) {
        CimiMachineTemplate cimi = new CimiMachineTemplate();
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
        this.doCopyToCimi(context, (MachineTemplate) dataService, (CimiMachineTemplate) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final Context context, final Object dataCimi) {
        MachineTemplate service = new MachineTemplate();
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
        this.doCopyToService(context, (CimiMachineTemplate) dataCimi, (MachineTemplate) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final Context context, final MachineTemplate dataService, final CimiMachineTemplate dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.shouldBeExpanded(dataCimi)) {
            if (null != dataService.getCredentials()) {
                dataCimi.setCredentials((CimiCredentials) context.getConverter(CimiCredentials.class).toCimi(context,
                    dataService.getCredentials()));
            }
            if (null != dataService.getMachineConfiguration()) {
                dataCimi.setMachineConfig((CimiMachineConfiguration) context.getConverter(CimiMachineConfiguration.class)
                    .toCimi(context, dataService.getMachineConfiguration()));
            }
            if (null != dataService.getMachineImage()) {
                dataCimi.setMachineImage((CimiMachineImage) context.getConverter(CimiMachineImage.class).toCimi(context,
                    dataService.getMachineImage()));
            }
            // TODO NetworkInterfaces
            // if ((null != dataService.getNetworkInterfaces()) &&
            // (dataService.getNetworkInterfaces().size() > 0)) {
            // List<CimiNetworkInterface> listCimis = new
            // ArrayList<CimiNetworkInterface>();
            // EntityConverter converter =
            // context.getConverter(CimiNetworkInterface.class);
            // for (NetworkInterface itemService :
            // dataService.getNetworkInterfaces()) {
            // listCimis.add((CimiNetworkInterface) converter.toCimi(context,
            // itemService));
            // }
            // dataCimi.setNetworkInterfaces(listCimis.toArray(new
            // CimiNetworkInterface[listCimis.size()]));
            // }

            // TODO MachineVolume
            // if ((null != dataService.getVolumes()) &&
            // (dataService.getVolumes().size() > 0)) {
            // List<CimiMachineVolume> listCimis = new
            // ArrayList<CimiMachineVolume>();
            // EntityConverter converter =
            // context.getConverter(CimiMachineVolume.class);
            // for (MachineVolume itemService : dataService.getVolumes()) {
            // listCimis.add((CimiVolume) converter.toCimi(context,
            // itemService));
            // }
            // dataCimi.setVolumes(listCimis.toArray(new
            // CimiMachineVolume[listCimis.size()]));
            // }

            // TODO VolumeTemplate
            // if ((null != dataService.getVolumeTemplates()) &&
            // (dataService.getVolumeTemplates().size() > 0)) {
            // List<CimiVolumeTemplate> listCimis = new
            // ArrayList<CimiVolumeTemplate>();
            // EntityConverter converter =
            // context.getConverter(CimiVolumeTemplate.class);
            // for (VolumeTemplate itemService :
            // dataService.getVolumeTemplates()) {
            // listCimis.add((CimiVolumeTemplate) converter.toCimi(context,
            // itemService));
            // }
            // dataCimi.setVolumeTemplates(listCimis.toArray(new
            // CimiVolumeTemplate[listCimis.size()]));
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
    protected void doCopyToService(final Context context, final CimiMachineTemplate dataCimi, final MachineTemplate dataService) {
        this.fill(dataCimi, dataService);
        if (null != dataCimi.getCredentials()) {
            dataService.setCredentials((Credentials) context.getConverter(CimiCredentials.class).toService(context,
                dataCimi.getCredentials()));
        }
        if (null != dataCimi.getMachineImage()) {
            dataService.setMachineImage((MachineImage) context.getConverter(CimiMachineImage.class).toService(context,
                dataCimi.getMachineImage()));
        }
        if (null != dataCimi.getMachineConfig()) {
            dataService.setMachineConfiguration((MachineConfiguration) context.getConverter(CimiMachineConfiguration.class)
                .toService(context, dataCimi.getMachineConfig()));
        }
        // TODO NetworkInterfaces
        // dataService.setNetworkInterfaces(dataCimi.getUserName());
        // TODO MachineVolume
        // dataService.setVolumes(dataCimi.getUserName());
        // TODO VolumeTemplate
        // dataService.setVolumeTemplates(dataCimi.getUserName());
    }

}
