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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.utils.Context;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;

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
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final Context context, final Object dataService) {
        CimiMachine cimi = new CimiMachine();
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
        this.doCopyToCimi(context, (Machine) dataService, (CimiMachine) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.Context,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final Context context, final Object dataCimi) {
        Machine service = new Machine();
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
        this.doCopyToService(context, (CimiMachine) dataCimi, (Machine) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final Context context, final Machine dataService, final CimiMachine dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.shouldBeExpanded(dataCimi)) {
            // TODO
        }
    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    protected void doCopyToService(final Context context, final CimiMachine dataCimi, final Machine dataService) {
        this.fill(dataCimi, dataService);
        // TODO
    }
}
