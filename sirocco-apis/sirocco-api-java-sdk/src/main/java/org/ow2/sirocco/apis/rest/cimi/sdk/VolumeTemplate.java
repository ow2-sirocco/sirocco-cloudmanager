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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class VolumeTemplate extends Resource<CimiVolumeTemplate> {
    private VolumeImage volumeImage;

    private VolumeConfiguration volumeConfig;

    public VolumeTemplate() {
        super(null, new CimiVolumeTemplate());
    }

    public VolumeTemplate(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiVolumeTemplate());
        this.cimiObject.setHref(id);
    }

    public VolumeTemplate(final CimiVolumeTemplate cimiObject) {
        super(null, cimiObject);
    }

    public VolumeTemplate(final CimiClient cimiClient, final CimiVolumeTemplate cimiObject) {
        super(cimiClient, cimiObject);
        this.volumeImage = new VolumeImage(cimiClient, cimiObject.getVolumeImage());
        this.volumeConfig = new VolumeConfiguration(cimiClient, cimiObject.getVolumeConfig());
    }

    public VolumeImage getVolumeImage() {
        return this.volumeImage;
    }

    public void setVolumeImage(final VolumeImage volumeImage) {
        this.volumeImage = volumeImage;
        this.cimiObject.setVolumeImage(volumeImage.cimiObject);
    }

    public VolumeConfiguration getVolumeConfig() {
        return this.volumeConfig;
    }

    public void setVolumeConfig(final VolumeConfiguration volumeConfig) {
        this.volumeConfig = volumeConfig;
        this.cimiObject.setVolumeConfig(volumeConfig.cimiObject);
    }

    public void delete() throws CimiException {
        this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()));
    }

    public static VolumeTemplate createVolumeTemplate(final CimiClient client, final VolumeTemplate volumeTemplate)
        throws CimiException {
        CimiVolumeTemplate cimiObject = client.postRequest(ConstantsPath.VOLUME_TEMPLATE_PATH, volumeTemplate.cimiObject,
            CimiVolumeTemplate.class);
        return new VolumeTemplate(client, cimiObject);
    }

    public static List<VolumeTemplate> getVolumeTemplates(final CimiClient client, final int first, final int last,
        final String... filterExpression) throws CimiException {
        if (client.cloudEntryPoint.getVolumeTemplates() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiVolumeTemplateCollection volumeTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getVolumeTemplates().getHref()), CimiVolumeTemplateCollectionRoot.class,
            first, last, null, filterExpression);

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

    public static VolumeTemplate getVolumeTemplateById(final CimiClient client, final String id) throws CimiException {
        String path = client.getVolumeTemplatesPath() + "/" + id;
        return new VolumeTemplate(client, client.getCimiObjectByReference(path, CimiVolumeTemplate.class));
    }

}
