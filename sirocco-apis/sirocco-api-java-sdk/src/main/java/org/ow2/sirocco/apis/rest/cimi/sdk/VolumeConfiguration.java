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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeConfigurationCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class VolumeConfiguration extends Resource<CimiVolumeConfiguration> {

    public VolumeConfiguration() {
        super(null, new CimiVolumeConfiguration());
    }

    VolumeConfiguration(final CimiClient cimiClient, final String id) {
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

    public Job delete() throws CimiException {
        String deleteRef = Helper.findOperation("delete", this.cimiObject);
        if (deleteRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiJob job = this.cimiClient.deleteRequest(deleteRef);
        if (job != null) {
            return new Job(this.cimiClient, job);
        } else {
            return null;
        }
    }

    public static CreateResult<VolumeConfiguration> createVolumeConfiguration(final CimiClient client,
        final VolumeConfiguration volumeConfig) throws CimiException {
        if (client.cloudEntryPoint.getVolumeConfigs() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiVolumeConfigurationCollection volumeConfigCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumeConfigs().getHref()),
            CimiVolumeConfigurationCollectionRoot.class, null);
        String addRef = Helper.findOperation("add", volumeConfigCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiVolumeConfiguration> result = client.postCreateRequest(addRef, volumeConfig.cimiObject,
            CimiVolumeConfiguration.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        VolumeConfiguration createdVolumeConfiguration = result.getResource() != null ? new VolumeConfiguration(client,
            result.getResource()) : null;
        return new CreateResult<VolumeConfiguration>(job, createdVolumeConfiguration);
    }

    public static List<VolumeConfiguration> getVolumeConfigurations(final CimiClient client, final QueryParams queryParams)
        throws CimiException {
        if (client.cloudEntryPoint.getVolumeConfigs() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiVolumeConfigurationCollection volumeConfigCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumeConfigs().getHref()),
            CimiVolumeConfigurationCollectionRoot.class, queryParams);

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

}
