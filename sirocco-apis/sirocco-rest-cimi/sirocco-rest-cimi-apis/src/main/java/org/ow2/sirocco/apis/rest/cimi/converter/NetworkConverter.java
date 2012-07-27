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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiEventLog;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiForwardingGroup;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetwork;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiNetworkNetworkPortCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiNetwork}</li>
 * <li>Service model: {@link Network}</li>
 * </ul>
 * </p>
 */
public class NetworkConverter extends ObjectCommonConverter {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiNetwork cimi = new CimiNetwork();
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
        this.doCopyToCimi(context, (Network) dataService, (CimiNetwork) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        Network service = new Network();
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
        this.doCopyToService(context, (CimiNetwork) dataCimi, (Network) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final Network dataService, final CimiNetwork dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.mustBeExpanded(dataCimi)) {
            dataCimi.setClassOfService(dataService.getClassOfService());
            dataCimi.setEventLog((CimiEventLog) context.convertNextCimi(dataService.getEventLog(), CimiEventLog.class));
            dataCimi.setForwardingGroup((CimiForwardingGroup) context.convertNextCimi(dataService.getForwardingGroup(),
                CimiForwardingGroup.class));
            dataCimi.setMtu(dataService.getMtu());
            dataCimi.setNetworkPorts((CimiNetworkNetworkPortCollection) context.convertNextCimi(dataService.getNetworkPorts(),
                CimiNetworkNetworkPortCollection.class));
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
    protected void doCopyToService(final CimiContext context, final CimiNetwork dataCimi, final Network dataService) {
        this.fill(context, dataCimi, dataService);
        dataService.setClassOfService(dataCimi.getClassOfService());
        dataService.setMtu(dataCimi.getMtu());
        dataService.setNetworkType(ConverterHelper.toNetworkType(dataCimi.getNetworkType()));

        // Next Read only
        // dataService.setEventLog((EventLog)
        // context.convertNextService(dataCimi.getEventLog()));
        // dataService.setForwardingGroup((ForwardingGroup)
        // context.convertNextService(dataCimi.getForwardingGroup()));
        // dataService.setNetworkPorts((List<NetworkPort>)
        // context.convertNextService(dataCimi.getNetworkPorts()));
        // dataService.setState(dataCimi.getState());
    }
}
