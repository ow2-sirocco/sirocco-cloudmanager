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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id$
 *
 */

package org.ow2.sirocco.apis.rest.cimi.sdk;

import java.util.ArrayList;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeConfigurationCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class VolumeConfiguration extends Resource<CimiVolumeConfiguration> {

    public VolumeConfiguration() {
        super(null, new CimiVolumeConfiguration());
    }

    public VolumeConfiguration(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiVolumeConfiguration());
        this.cimiObject.setHref(id);
    }

    VolumeConfiguration(final CimiClient cimiClient, final CimiVolumeConfiguration cimiObject) {
        super(cimiClient, cimiObject);
    }

    public void setType(final String type) {
        this.cimiObject.setType(type);
    }

    public String getType() {
        return this.cimiObject.getType();
    }

    public int getCapacity() {
        return this.cimiObject.getCapacity();
    }

    public void setCapacity(final int capacity) {
        this.cimiObject.setCapacity(capacity);
    }

    public String getFormat() {
        return this.cimiObject.getFormat();
    }

    public void setFormat(final String format) {
        this.cimiObject.setFormat(format);
    }

    public void delete() throws CimiException {
        this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()));
    }

    public static VolumeConfiguration createVolumeConfiguration(final CimiClient client, final VolumeConfiguration volumeConfig)
        throws CimiException {
        CimiVolumeConfiguration cimiObject = client.postRequest(ConstantsPath.VOLUME_CONFIGURATION_PATH,
            volumeConfig.cimiObject, CimiVolumeConfiguration.class);
        return new VolumeConfiguration(client, cimiObject);
    }

    public static List<VolumeConfiguration> getVolumeConfigurations(final CimiClient client, final int first, final int last,
        final String... filterExpression) throws CimiException {
        CimiVolumeConfigurationCollection volumeConfigCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumeConfigs().getHref()),
            CimiVolumeConfigurationCollectionRoot.class, first, last, null, filterExpression);

        List<VolumeConfiguration> result = new ArrayList<VolumeConfiguration>();

        if (volumeConfigCollection.getCollection() != null) {
            for (CimiVolumeConfiguration cimiVolumeConfig : volumeConfigCollection.getCollection().getArray()) {
                result.add(new VolumeConfiguration(client, cimiVolumeConfig));
            }
        }
        return result;
    }

    public static VolumeConfiguration getVolumeConfigurationByReference(final CimiClient client, final String ref)
        throws CimiException {
        return new VolumeConfiguration(client, client.getCimiObjectByReference(ref, CimiVolumeConfiguration.class));
    }

    public static VolumeConfiguration getVolumeConfigurationById(final CimiClient client, final String id) throws CimiException {
        String path = client.getVolumeConfigurationsPath() + "/" + id;
        return new VolumeConfiguration(client, client.getCimiObjectByReference(path, CimiVolumeConfiguration.class));
    }

}
