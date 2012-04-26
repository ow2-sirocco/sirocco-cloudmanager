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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachine}</li>
 * <li>Service model: {@link Machine}</li>
 * </ul>
 * </p>
 */
public class MachineConverter extends CommonIdConverter implements EntityConverter {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiMachine cimi = new CimiMachine();
        this.copyToCimi(context, dataService, cimi);
        return cimi;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#copyToCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void copyToCimi(final CimiContext context, final Object dataService, final Object dataCimi) {
        this.doCopyToCimi(context, (Machine) dataService, (CimiMachine) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        Machine service = new Machine();
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
        this.doCopyToService(context, (CimiMachine) dataCimi, (Machine) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final Machine dataService, final CimiMachine dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.mustBeExpanded(dataCimi)) {
            if (null != dataService.getCpu()) {
                dataCimi.setCpu((CimiCpu) context.getConverter(CimiCpu.class).toCimi(context, dataService.getCpu()));
            }
            if (null != dataService.getMemory()) {
                dataCimi
                    .setMemory((CimiMemory) context.getConverter(CimiMemory.class).toCimi(context, dataService.getMemory()));
            }
            if ((null != dataService.getDisks()) && (dataService.getDisks().size() > 0)) {
                List<CimiDisk> listCimis = new ArrayList<CimiDisk>();
                CimiConverter converter = context.getConverter(CimiDisk.class);
                for (Disk itemService : dataService.getDisks()) {
                    listCimis.add((CimiDisk) converter.toCimi(context, itemService));
                }
                dataCimi.setDisks(listCimis.toArray(new CimiDisk[listCimis.size()]));
            }
            if (null != dataService.getState()) {
                dataCimi.setState(dataService.getState().toString());
            }

            // TODO dataCimi.setNetworkInterfaces(???);
            // TODO dataCimi.setVolumes(???);
        }
    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    protected void doCopyToService(final CimiContext context, final CimiMachine dataCimi, final Machine dataService) {
        this.fill(context, dataCimi, dataService);
        if (null != dataCimi.getCpu()) {
            dataService.setCpu((Cpu) context.getConverter(CimiCpu.class).toService(context, dataCimi.getCpu()));
        }
        if (null != dataCimi.getMemory()) {
            dataService.setMemory((Memory) context.getConverter(CimiMemory.class).toService(context, dataCimi.getMemory()));
        }
        if ((null != dataCimi.getDisks()) && (dataCimi.getDisks().length > 0)) {
            List<Disk> listServices = new ArrayList<Disk>();
            CimiConverter converter = context.getConverter(CimiDisk.class);
            for (CimiDisk itemCimi : dataCimi.getDisks()) {
                listServices.add((Disk) converter.toService(context, itemCimi));
            }
        }
        // TODO dataService.setNetworkInterfaces(???);
        // TODO dataService.setVolumes(???);

        // Next Read only
        // dataService.setState(dataService.getState());
    }
}
