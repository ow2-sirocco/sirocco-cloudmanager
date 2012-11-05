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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class VolumeTemplate extends Resource<CimiVolumeTemplate> {
    private VolumeImage volumeImage;

    private VolumeConfiguration volumeConfig;

    public VolumeTemplate() {
        super(null, new CimiVolumeTemplate());
    }

    VolumeTemplate(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiVolumeTemplate());
        this.cimiObject.setHref(id);
    }

    VolumeTemplate(final CimiVolumeTemplate cimiObject) {
        super(null, cimiObject);
    }

    public VolumeTemplate(final CimiClient cimiClient, final CimiVolumeTemplate cimiObject) {
        super(cimiClient, cimiObject);
        if (cimiObject.getVolumeImage() != null) {
            this.volumeImage = new VolumeImage(cimiClient, cimiObject.getVolumeImage());
        }
        this.volumeConfig = new VolumeConfiguration(cimiClient, cimiObject.getVolumeConfig());
    }

    public VolumeImage getVolumeImage() {
        return this.volumeImage;
    }

    public void setVolumeImage(final VolumeImage volumeImage) {
        this.volumeImage = volumeImage;
        this.cimiObject.setVolumeImage(volumeImage.cimiObject);
    }

    public void setVolumeImageRef(final String volumeImageRef) {
        this.volumeImage = new VolumeImage(this.cimiClient, volumeImageRef);
        this.cimiObject.setVolumeImage(this.volumeImage.cimiObject);
    }

    public VolumeConfiguration getVolumeConfig() {
        return this.volumeConfig;
    }

    public void setVolumeConfig(final VolumeConfiguration volumeConfig) {
        this.volumeConfig = volumeConfig;
        this.cimiObject.setVolumeConfig(volumeConfig.cimiObject);
    }

    public void setVolumeConfigRef(final String volumeConfigRef) {
        this.volumeConfig = new VolumeConfiguration(this.cimiClient, volumeConfigRef);
        this.cimiObject.setVolumeConfig(this.volumeConfig.cimiObject);
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

    public static CreateResult<VolumeTemplate> createVolumeTemplate(final CimiClient client, final VolumeTemplate volumeTemplate)
        throws CimiException {
        if (client.cloudEntryPoint.getVolumeTemplates() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiVolumeTemplateCollection volumeTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumeTemplates().getHref()), CimiVolumeTemplateCollectionRoot.class,
            null);
        String addRef = Helper.findOperation("add", volumeTemplateCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiVolumeTemplate> result = client.postCreateRequest(addRef, volumeTemplate.cimiObject,
            CimiVolumeTemplate.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        VolumeTemplate createdVolumeTemplate = result.getResource() != null ? new VolumeTemplate(client, result.getResource())
            : null;
        return new CreateResult<VolumeTemplate>(job, createdVolumeTemplate);
    }

    public static List<VolumeTemplate> getVolumeTemplates(final CimiClient client) throws CimiException {
        return VolumeTemplate.getVolumeTemplates(client, null);
    }

    public static List<VolumeTemplate> getVolumeTemplates(final CimiClient client, final QueryParams queryParams)
        throws CimiException {
        if (client.cloudEntryPoint.getVolumeTemplates() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiVolumeTemplateCollection volumeTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumeTemplates().getHref()), CimiVolumeTemplateCollectionRoot.class,
            queryParams);

        List<VolumeTemplate> result = new ArrayList<VolumeTemplate>();

        if (volumeTemplateCollection.getCollection() != null) {
            for (CimiVolumeTemplate cimiVolumeTemplate : volumeTemplateCollection.getCollection().getArray()) {
                result.add(new VolumeTemplate(client, cimiVolumeTemplate));
            }
        }
        return result;
    }

    public static VolumeTemplate getVolumeTemplateByReference(final CimiClient client, final String ref) throws CimiException {
        return new VolumeTemplate(client, client.getCimiObjectByReference(ref, CimiVolumeTemplate.class));
    }

    public static VolumeTemplate getVolumeTemplateByReference(final CimiClient client, final String ref,
        final QueryParams queryParams) throws CimiException {
        return new VolumeTemplate(client, client.getCimiObjectByReference(ref, CimiVolumeTemplate.class, queryParams));
    }

}
