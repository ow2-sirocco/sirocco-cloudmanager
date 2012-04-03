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
 * $Id: MachineConfigurationCollectionConverter.java 1104 2012-03-28 16:09:36Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.converter;

import java.util.ArrayList;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiMachineConfigurationCollection}</li>
 * <li>Service model: List of {@link MachineConfiguration}</li>
 * </ul>
 * </p>
 */
public class MachineConfigurationCollectionConverter {

    /**
     * Copy the data from the service object in the CIMI object.
     * 
     * @param dataService An instance of {@link MachineConfigurationCollection}
     * @param dataCimi An instance of List of {@link CimiMachineConfiguration}
     * @param urlBase The URL base
     */
    public static void copyToCimi(final List<MachineConfiguration> dataService,
        final CimiMachineConfigurationCollection dataCimi, final String urlBase) {

        dataCimi.setId(HrefHelper.makeHref(urlBase, ConstantsPath.MACHINE_IMAGE));
        CimiMachineConfiguration cimi = null;
        List<CimiMachineConfiguration> cimiList = new ArrayList<CimiMachineConfiguration>();
        for (MachineConfiguration machineConfiguration : dataService) {
            cimi = new CimiMachineConfiguration();
            cimiList.add(cimi);
            MachineConfigurationConverter.copyToCimi(machineConfiguration, cimi, urlBase, false, true);
        }
        dataCimi.setMachineConfigurations(cimiList.toArray(new CimiMachineConfiguration[cimiList.size()]));
    }

    /**
     * Copy the data from the CIMI object in the service object.
     * 
     * @param dataCimi An instance of {@link CimiMachineConfigurationCollection}
     * @param dataService An instance of {@link MachineConfiguration}
     */
    public static void copyToService(final CimiMachineConfigurationCollection dataCimi,
        final List<MachineConfiguration> dataService) {
        MachineConfiguration serviceConfiguration;
        CimiMachineConfiguration[] images = dataCimi.getMachineConfigurations();
        for (CimiMachineConfiguration cimiConfiguration : images) {
            serviceConfiguration = new MachineConfiguration();
            dataService.add(serviceConfiguration);
            MachineConfigurationConverter.copyToService(cimiConfiguration, serviceConfiguration);
        }
    }

}
