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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiCredentials}</li>
 * <li>Service model: {@link Credentials}</li>
 * </ul>
 * </p>
 */
public class CredentialsConverter {

    /**
     * Copy the data from the service object in the CIMI object.
     * 
     * @param dataService An instance of {@link Credentials}
     * @param dataCimi An instance of {@link CimiCredentials}
     * @param urlBase The URL base
     */
    public static void copyToCimi(final Credentials dataService, final CimiCredentials dataCimi, final String urlBase,
        final boolean expand, final boolean href) {
        // TODO
        if (true == expand) {
            CommonConverter.copyToCimi(dataService, dataCimi, urlBase, ConstantsPath.CREDENTIALS);
            // if (null != dataService.getCpu()) {
            // // dataCimi.setConfigurationLocation(new
            // //
            // ConfigurationLocation(dataService.getConfigurationLocation()));
            // }
            // if (null != dataService.getMemory()) {
            // // dataCimi.setConfigurationLocation(new
            // //
            // ConfigurationLocation(dataService.getConfigurationLocation()));
            // }
            // if (null != dataService.getDiskTemplates()) {
            // // dataCimi.setConfigurationLocation(new
            // //
            // ConfigurationLocation(dataService.getConfigurationLocation()));
            // }
        }
        if (true == href) {
            dataCimi.setHref(HrefHelper.makeHref(urlBase, ConstantsPath.CREDENTIALS, dataService.getId()));
        }
    }

    /**
     * Copy the data from the CIMI object in the service object.
     * 
     * @param dataCimi An instance of {@link CimiCredentials}
     * @param dataService An instance of {@link Credentials}
     */
    public static void copyToService(final CimiCredentials dataCimi, final Credentials dataService) {
        CommonConverter.copyToService(dataCimi, dataService);
        // TODO
        // dataService.setConfigurationLocation(dataCimi.getConfigurationLocation().getHref());
    }

    /**
     * Copy the data from the service object in the CIMI object.
     * 
     * @param dataService An instance of {@link Credentials}
     * @param dataCimi An instance of {@link CimiCredentials}
     * @param urlBase The URL base
     */
    public static void copyToCimi(final CredentialsCreate dataService, final CimiCredentialsCreate dataCimi,
        final String urlBase, final boolean expand, final boolean href) {
        // TODO
    }

    /**
     * Copy the data from the CIMI object in the service object.
     * 
     * @param dataCimi An instance of {@link CimiCredentialsCreate}
     * @param dataService An instance of {@link CredentialsCreate}
     */
    public static void copyToService(final CimiCredentialsCreate dataCimi, final CredentialsCreate dataService) {
        CommonConverter.copyToService(dataCimi, dataService);
        // TODO
        // dataService.setConfigurationLocation(dataCimi.getConfigurationLocation().getHref());
    }

}
