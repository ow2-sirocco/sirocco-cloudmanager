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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class SystemTemplate extends Resource<CimiSystemTemplate> {
    public SystemTemplate() {
        super(null, new CimiSystemTemplate());
    }

    public SystemTemplate(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiSystemTemplate());
        this.cimiObject.setHref(id);
    }

    public SystemTemplate(final CimiClient cimiClient, final CimiSystemTemplate cimiObject) {
        super(cimiClient, cimiObject);
    }

    public void delete() throws CimiException {
        this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()));
    }

    public static SystemTemplate createSystemTemplate(final CimiClient client, final SystemTemplate systemTemplate)
        throws CimiException {
        CimiSystemTemplate cimiObject = client.postRequest(ConstantsPath.SYSTEM_TEMPLATE_PATH, systemTemplate.cimiObject,
            CimiSystemTemplate.class);
        return new SystemTemplate(client, cimiObject);
    }

    public static List<SystemTemplate> getSystemTemplates(final CimiClient client, final int first, final int last,
        final String... filterExpression) throws CimiException {
        CimiSystemTemplateCollection systemTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getSystemTemplates().getHref()), CimiSystemTemplateCollectionRoot.class,
            first, last, null, filterExpression);

        List<SystemTemplate> result = new ArrayList<SystemTemplate>();

        if (systemTemplateCollection.getCollection() != null) {
            for (CimiSystemTemplate cimiSystemTemplate : systemTemplateCollection.getCollection().getArray()) {
                result.add(new SystemTemplate(client, cimiSystemTemplate));
            }
        }
        return result;
    }

    public static SystemTemplate getSystemTemplateByReference(final CimiClient client, final String ref) throws CimiException {
        return new SystemTemplate(client, client.getCimiObjectByReference(ref, CimiSystemTemplate.class));
    }

    public static SystemTemplate getSystemTemplateById(final CimiClient client, final String id) throws CimiException {
        String path = client.getSystemTemplatesPath() + "/" + id;
        return new SystemTemplate(client, client.getCimiObjectByReference(path, CimiSystemTemplate.class));
    }

}
