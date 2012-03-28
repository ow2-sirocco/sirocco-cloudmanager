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

import java.util.HashMap;
import java.util.Map;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommon;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions. <p>Converted classes:<ul><li>CIMI model: {@link CimiCommon}
 * </li><li>Service model: {@link CloudEntity}</li></ul></p>
 */
public class CommonConverter {

    /**
     * Copy all data from a service object to a CIMI object.
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    private static void copyToCimi(CloudEntity dataService, CimiCommon dataCimi) {
        dataCimi.setCreated(dataService.getCreated());
        dataCimi.setDescription(dataService.getDescription());
        dataCimi.setName(dataService.getName());
        dataCimi.setUpdated(dataService.getUpdated());
        if (null != dataService.getProperties()) {
            Map<String, String> props = new HashMap<String, String>();
            dataCimi.setProperties(props);
            props.putAll(dataService.getProperties());
        }
    }

    /**
     * Copy all data from a service object to a CIMI object.
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    public static void copyToCimi(CloudEntity dataService, CimiCommon dataCimi, String urlBase, String urlConstant) {
        copyToCimi(dataService, dataCimi);
        dataCimi.setId(HrefHelper.makeHref(urlBase, urlConstant, dataService.getId()));
    }

    /**
     * Copy read/write data from a CIMI object to a service object.
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    public static void copyToService(CimiCommon dataCimi, CloudEntity dataService) {
        dataService.setDescription(dataCimi.getDescription());
        dataService.setName(dataCimi.getName());
        if (null != dataCimi.getProperties()) {
            Map<String, String> props = new HashMap<String, String>();
            dataService.setProperties(props);
            props.putAll(dataCimi.getProperties());
        }
    }

}
