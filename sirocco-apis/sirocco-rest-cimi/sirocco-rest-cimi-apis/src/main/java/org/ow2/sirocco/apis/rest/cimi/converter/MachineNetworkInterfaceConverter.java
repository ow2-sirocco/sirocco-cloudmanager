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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineNetworkInterface;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetwork;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetworkPort;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineNetworkInterfaceAddressCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineNetworkInterface}</li>
 * <li>Service model: {@link MachineNetworkInterface}</li>
 * </ul>
 * </p>
 */
public class MachineNetworkInterfaceConverter extends ObjectCommonConverter {
    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiMachineNetworkInterface cimi = new CimiMachineNetworkInterface();
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
        this.doCopyToCimi(context, (MachineNetworkInterface) dataService, (CimiMachineNetworkInterface) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        MachineNetworkInterface service = new MachineNetworkInterface();
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
        this.doCopyToService(context, (CimiMachineNetworkInterface) dataCimi, (MachineNetworkInterface) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final MachineNetworkInterface dataService,
        final CimiMachineNetworkInterface dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.mustBeExpanded(dataCimi)) {
            dataCimi.setAddresses((CimiMachineNetworkInterfaceAddressCollection) context.convertNextCimi(
                dataService.getAddresses(), CimiMachineNetworkInterfaceAddressCollection.class));
            dataCimi.setMacAddress(dataService.getMacAddress());
            dataCimi.setMtu(dataService.getMtu());
            dataCimi.setNetwork((CimiNetwork) context.convertNextCimi(dataService.getNetwork(), CimiNetwork.class));
            dataCimi.setNetworkPort((CimiNetworkPort) context.convertNextCimi(dataService.getNetworkPort(),
                CimiNetworkPort.class));
            dataCimi.setNetworkType(ConverterHelper.toString(dataService.getNetworkType()));
            dataCimi.setState(ConverterHelper.toString(dataService.getState()));
        }
    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    protected void doCopyToService(final CimiContext context, final CimiMachineNetworkInterface dataCimi,
        final MachineNetworkInterface dataService) {
        this.fill(context, dataCimi, dataService);
        dataService.setMacAddress(dataCimi.getMacAddress());
        dataService.setMtu(dataCimi.getMtu());
        dataService.setNetwork((Network) context.convertNextService(dataCimi.getNetwork()));
        dataService.setNetworkPort((NetworkPort) context.convertNextService(dataCimi.getNetworkPort()));
        dataService.setNetworkType(ConverterHelper.toNetworkType(dataCimi.getNetworkType()));
        dataService.setState(ConverterHelper.toMachineNetworkInterfaceState(dataCimi.getState()));

        // Next read-only
        // dataService.setAddresses((List<Address>)
        // context.convertNextService(dataCimi.getAddresses()));
    }
}
