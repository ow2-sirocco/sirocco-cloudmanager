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
 * $Id: $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.converter;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineConfiguration}</li>
 * <li>Service model: {@link MachineConfiguration}</li>
 * </ul>
 * </p>
 */
public class MachineConfigurationConverter {

    /**
     * Copy the data from the service object in the CIMI object.
     * 
     * @param dataService An instance of {@link MachineConfiguration}
     * @param dataCimi An instance of {@link CimiMachineConfiguration}
     * @param urlBase The URL base
     */
    public static void copyToCimi(final MachineConfiguration dataService, final CimiMachineConfiguration dataCimi,
        final String urlBase, final boolean expand, final boolean href) {
        // TODO
        if (true == expand) {
            CommonConverter.copyToCimi(dataService, dataCimi, urlBase, ConstantsPath.MACHINE_CONFIGURATION);
            if (null != dataService.getCpu()) {
                // dataCimi.setConfigurationLocation(new
                // ConfigurationLocation(dataService.getConfigurationLocation()));
            }
            if (null != dataService.getMemory()) {
                // dataCimi.setConfigurationLocation(new
                // ConfigurationLocation(dataService.getConfigurationLocation()));
            }
            if (null != dataService.getDiskTemplates()) {
                // dataCimi.setConfigurationLocation(new
                // ConfigurationLocation(dataService.getConfigurationLocation()));
            }
        }
        if (true == href) {
            dataCimi.setHref(HrefHelper.makeHref(urlBase, ConstantsPath.MACHINE_CONFIGURATION, dataService.getId()));
        }
    }

    /**
     * Copy the data from the CIMI object in the service object.
     * 
     * @param dataCimi An instance of {@link CimiMachineConfiguration}
     * @param dataService An instance of {@link MachineConfiguration}
     */
    public static void copyToService(final CimiMachineConfiguration dataCimi, final MachineConfiguration dataService) {
        CommonConverter.copyToService(dataCimi, dataService);
        // TODO
        // dataService.setConfigurationLocation(dataCimi.getConfigurationLocation().getHref());
    }

}
