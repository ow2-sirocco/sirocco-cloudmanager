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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiComponentDescriptor;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class SystemTemplate extends Resource<CimiSystemTemplate> {
    public SystemTemplate() {
        super(null, new CimiSystemTemplate());
    }

    SystemTemplate(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiSystemTemplate());
        this.cimiObject.setHref(id);
    }

    public SystemTemplate(final CimiClient cimiClient, final CimiSystemTemplate cimiObject) {
        super(cimiClient, cimiObject);
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

    public List<ComponentDescriptor> getComponentDescriptors() {
        List<ComponentDescriptor> result = new ArrayList<ComponentDescriptor>();
        if (this.cimiObject.getComponentDescriptors() != null) {
            for (CimiComponentDescriptor comp : this.cimiObject.getComponentDescriptors()) {
                result.add(new ComponentDescriptor(comp));
            }
        }
        return result;
    }

    public static CreateResult<SystemTemplate> createSystemTemplate(final CimiClient client, final SystemTemplate systemTemplate)
        throws CimiException {
        if (client.cloudEntryPoint.getSystemTemplates() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiSystemTemplateCollection systemTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getSystemTemplates().getHref()), CimiSystemTemplateCollectionRoot.class,
            null);
        String addRef = Helper.findOperation("add", systemTemplateCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiSystemTemplate> result = client.postCreateRequest(addRef, systemTemplate.cimiObject,
            CimiSystemTemplate.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        SystemTemplate createdSystemTemplate = result.getResource() != null ? new SystemTemplate(client, result.getResource())
            : null;
        return new CreateResult<SystemTemplate>(job, createdSystemTemplate);
    }

    public static List<SystemTemplate> getSystemTemplates(final CimiClient client, final QueryParams queryParams)
        throws CimiException {
        if (client.cloudEntryPoint.getSystemTemplates() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiSystemTemplateCollection systemTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getSystemTemplates().getHref()), CimiSystemTemplateCollectionRoot.class,
            queryParams);

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

}
