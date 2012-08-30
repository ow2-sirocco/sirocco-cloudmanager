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

import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiEventLog;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystem;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCredentialCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemForwardingGroupCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemNetworkCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemNetworkPortCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemSystemCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCredentials;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemMachine;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemNetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemSystem;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemVolume;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiSystem}</li>
 * <li>Service model: {@link System}</li>
 * </ul>
 * </p>
 */
public class SystemConverter extends ObjectCommonConverter {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiSystem cimi = new CimiSystem();
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
        this.doCopyToCimi(context, (System) dataService, (CimiSystem) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        System service = new System();
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
        this.doCopyToService(context, (CimiSystem) dataCimi, (System) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final System dataService, final CimiSystem dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.mustBeExpanded(dataCimi)) {
            // FIXME Adresses
            // dataCimi.setAddresses((CimiSystemAddressCollection)
            // context.convertNextCimi(dataService.getAddresses(),
            // CimiSystemAddressCollection.class));
            dataCimi.setForwardingGroups((CimiSystemForwardingGroupCollection) context.convertNextCimi(
                dataService.getForwardingGroups(), CimiSystemForwardingGroupCollection.class));
            dataCimi.setCredentials((CimiSystemCredentialCollection) context.convertNextCimi(dataService.getCredentials(),
                CimiSystemCredentialCollection.class));
            dataCimi.setEventLog((CimiEventLog) context.convertNextCimi(dataService.getEventLog(), CimiEventLog.class));
            dataCimi.setMachines((CimiSystemMachineCollection) context.convertNextCimi(dataService.getMachines(),
                CimiSystemMachineCollection.class));
            dataCimi.setNetworks((CimiSystemNetworkCollection) context.convertNextCimi(dataService.getNetworks(),
                CimiSystemNetworkCollection.class));
            dataCimi.setNetworkPorts((CimiSystemNetworkPortCollection) context.convertNextCimi(dataService.getNetworkPorts(),
                CimiSystemNetworkPortCollection.class));
            dataCimi.setState(ConverterHelper.toString(dataService.getState()));
            dataCimi.setSystems((CimiSystemSystemCollection) context.convertNextCimi(dataService.getSystems(),
                CimiSystemSystemCollection.class));
            dataCimi.setVolumes((CimiSystemVolumeCollection) context.convertNextCimi(dataService.getVolumes(),
                CimiSystemVolumeCollection.class));

        }
    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    @SuppressWarnings("unchecked")
    protected void doCopyToService(final CimiContext context, final CimiSystem dataCimi, final System dataService) {
        this.fill(context, dataCimi, dataService);
        // FIXME Adresses
        // dataService.setAddresses((List<SystemAddresses>)
        // context.convertNextService(dataCimi.getAddresses()));
        dataService
            .setForwardingGroups((List<SystemForwardingGroup>) context.convertNextService(dataCimi.getForwardingGroups()));
        dataService.setCredentials((List<SystemCredentials>) context.convertNextService(dataCimi.getCredentials()));
        dataService.setMachines((List<SystemMachine>) context.convertNextService(dataCimi.getMachines()));
        dataService.setNetworks((List<SystemNetwork>) context.convertNextService(dataCimi.getNetworks()));
        dataService.setNetworkPorts((List<SystemNetworkPort>) context.convertNextService(dataCimi.getNetworkPorts()));
        dataService.setSystems((List<SystemSystem>) context.convertNextService(dataCimi.getSystems()));
        dataService.setVolumes((List<SystemVolume>) context.convertNextService(dataCimi.getVolumes()));

        // Next Read only
        // dataService.setState(dataService.getState());
        // dataService.setEventLog((EventLog)
        // context.convertNextService(dataCimi.getEventLog()));
    }
}
