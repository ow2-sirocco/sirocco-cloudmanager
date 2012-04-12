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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.utils.Context;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineConfiguration}</li>
 * <li>Service model: {@link MachineConfiguration}</li>
 * </ul>
 * </p>
 */
public class MachineConfigurationConverter extends CommonIdConverter implements EntityConverter {
    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final Context context, final Object dataService) {
        CimiMachineConfiguration cimi = new CimiMachineConfiguration();
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
        this.doCopyToCimi(context, (MachineConfiguration) dataService, (CimiMachineConfiguration) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final Context context, final Object dataCimi) {
        MachineConfiguration service = new MachineConfiguration();
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
        this.doCopyToService(context, (CimiMachineConfiguration) dataCimi, (MachineConfiguration) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final Context context, final MachineConfiguration dataService,
        final CimiMachineConfiguration dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.shouldBeExpanded(dataCimi)) {
            if (null != dataService.getCpu()) {
                dataCimi.setCpu((CimiCpu) context.getConverter(CimiCpu.class).toCimi(context, dataService.getCpu()));
            }
            if (null != dataService.getMemory()) {
                dataCimi
                    .setMemory((CimiMemory) context.getConverter(CimiMemory.class).toCimi(context, dataService.getMemory()));
            }
            if ((null != dataService.getDiskTemplates()) && (dataService.getDiskTemplates().size() > 0)) {
                List<CimiDiskConfiguration> listCimis = new ArrayList<CimiDiskConfiguration>();
                EntityConverter converter = context.getConverter(CimiDiskConfiguration.class);
                for (DiskTemplate itemService : dataService.getDiskTemplates()) {
                    listCimis.add((CimiDiskConfiguration) converter.toCimi(context, itemService));
                }
                dataCimi.setDisks(listCimis.toArray(new CimiDiskConfiguration[listCimis.size()]));
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
    protected void doCopyToService(final Context context, final CimiMachineConfiguration dataCimi,
        final MachineConfiguration dataService) {
        this.fill(dataCimi, dataService);
        if (null != dataCimi.getCpu()) {
            dataService.setCpu((Cpu) context.getConverter(CimiCpu.class).toService(context, dataCimi.getCpu()));
        }
        if (null != dataCimi.getMemory()) {
            dataService.setMemory((Memory) context.getConverter(CimiMemory.class).toService(context, dataCimi.getMemory()));
        }
        if ((null != dataCimi.getDisks()) && (dataCimi.getDisks().length > 0)) {
            List<DiskTemplate> listServices = new ArrayList<DiskTemplate>();
            EntityConverter converter = context.getConverter(CimiDiskConfiguration.class);
            for (CimiDiskConfiguration itemCimi : dataCimi.getDisks()) {
                listServices.add((DiskTemplate) converter.toService(context, itemCimi));
            }
        }
    }

}
