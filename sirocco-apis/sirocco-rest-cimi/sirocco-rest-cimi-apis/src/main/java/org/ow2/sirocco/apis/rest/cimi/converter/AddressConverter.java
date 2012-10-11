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
import java.util.HashSet;
import java.util.Set;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiAddress;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetwork;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiAddress}</li>
 * <li>Service model: {@link Address}</li>
 * </ul>
 * </p>
 */
public class AddressConverter extends ObjectCommonConverter {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiAddress cimi = new CimiAddress();
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
        this.doCopyToCimi(context, (Address) dataService, (CimiAddress) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        Address service = new Address();
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
        this.doCopyToService(context, (CimiAddress) dataCimi, (Address) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final Address dataService, final CimiAddress dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.mustBeExpanded(dataCimi)) {
            dataCimi.setAllocation(dataService.getAllocation());
            dataCimi.setDefaultGateway(dataService.getDefaultGateway());
            if (dataService.getDns() != null && dataService.getDns().size() > 0) {
                dataCimi.setDns(dataService.getDns().toArray(new String[dataService.getDns().size()]));
            }
            dataCimi.setHostname(dataService.getHostName());
            dataCimi.setIp(dataService.getIp());
            dataCimi.setMask(dataService.getMask());
            dataCimi.setNetwork((CimiNetwork) context.convertNextCimi(dataService.getNetwork(), CimiNetwork.class));
            dataCimi.setProtocol(dataService.getProtocol());
            dataCimi.setResource(ConverterHelper.buildTargetResource(context, dataService.getResource()));
        }
    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    protected void doCopyToService(final CimiContext context, final CimiAddress dataCimi, final Address dataService) {
        this.fill(context, dataCimi, dataService);
        dataService.setDefaultGateway(dataCimi.getDefaultGateway());
        Set<String> dns = new HashSet<String>();
        if (dataCimi.getDns() != null) {
            for (String dnsValue : dataCimi.getDns()) {
                dns.add(dnsValue);
            }
        }
        dataService.setDns(dns);
        dataService.setHostName(dataCimi.getHostname());
        dataService.setIp(dataCimi.getIp());
        dataService.setMask(dataCimi.getMask());
        dataService.setNetwork((Network) context.convertNextService(dataCimi.getNetwork()));
        dataService.setProtocol(dataCimi.getProtocol());

        // Next Read only
        // dataService.setAllocation(dataCimi.getAllocation());
        // dataService.setResource((CloudResource)
        // context.convertNextService(dataCimi.getResource()));
    }
}
