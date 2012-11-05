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
import java.util.Map;
import java.util.Map.Entry;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateNetworkInterface;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class MachineTemplate extends Resource<CimiMachineTemplate> {
    private MachineImage machineImage;

    private MachineConfiguration machineConfig;

    private Credential credential;

    public MachineTemplate() {
        super(null, new CimiMachineTemplate());
    }

    MachineTemplate(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiMachineTemplate());
        this.cimiObject.setHref(id);
    }

    MachineTemplate(final CimiMachineTemplate cimiObject) {
        super(null, cimiObject);
    }

    public MachineTemplate(final CimiClient cimiClient, final CimiMachineTemplate cimiObject) {
        super(cimiClient, cimiObject);
        this.machineImage = new MachineImage(cimiClient, cimiObject.getMachineImage());
        this.machineConfig = new MachineConfiguration(cimiClient, cimiObject.getMachineConfig());
        if (cimiObject.getCredential() != null) {
            this.credential = new Credential(cimiClient, cimiObject.getCredential());
        }
    }

    public MachineImage getMachineImage() {
        return this.machineImage;
    }

    public void setMachineImage(final MachineImage machineImage) {
        this.machineImage = machineImage;
        this.cimiObject.setMachineImage(machineImage.cimiObject);
    }

    public void setMachineImageRef(final String machineImageRef) {
        this.machineImage = new MachineImage(this.cimiClient, machineImageRef);
        this.cimiObject.setMachineImage(this.machineImage.cimiObject);
    }

    public MachineConfiguration getMachineConfig() {
        return this.machineConfig;
    }

    public void setMachineConfig(final MachineConfiguration machineConfig) {
        this.machineConfig = machineConfig;
        this.cimiObject.setMachineConfig(machineConfig.cimiObject);
    }

    public void setMachineConfigRef(final String machineConfigRef) {
        this.machineConfig = new MachineConfiguration(this.cimiClient, machineConfigRef);
        this.cimiObject.setMachineConfig(this.machineConfig.cimiObject);
    }

    public Credential getCredential() {
        return this.credential;
    }

    public void setCredential(final Credential credential) {
        this.credential = credential;
        this.cimiObject.setCredential(credential.cimiObject);
    }

    public void setCredentialRef(final String credentialRef) {
        this.credential = new Credential(this.cimiClient, credentialRef);
        this.cimiObject.setCredential(this.credential.cimiObject);
    }

    public List<NetworkInterface> getNetworkInterface() {
        List<NetworkInterface> nics = new ArrayList<NetworkInterface>();
        if (this.cimiObject.getListNetworkInterfaces() != null) {
            for (CimiMachineTemplateNetworkInterface cimiNic : this.cimiObject.getListNetworkInterfaces()) {
                String ip = "";
                if (cimiNic.getAddresses() != null && cimiNic.getAddresses().length > 0) {
                    ip = cimiNic.getAddresses()[0].getIp();
                }
                NetworkInterface nic = new NetworkInterface(NetworkInterface.Type.valueOf(cimiNic.getNetworkType()), ip);
                nics.add(nic);
            }
        }
        return nics;
    }

    public void setNetworkInterface(final List<NetworkInterface> nics) {
        List<CimiMachineTemplateNetworkInterface> templateNics = new ArrayList<CimiMachineTemplateNetworkInterface>();
        for (NetworkInterface nic : nics) {
            CimiMachineTemplateNetworkInterface templateNic = new CimiMachineTemplateNetworkInterface();
            templateNic.setNetworkType(nic.getType().toString());
            templateNics.add(templateNic);
        }
        this.cimiObject.setListNetworkInterfaces(templateNics);
    }

    public String getUserData() {
        return this.cimiObject.getUserData();
    }

    public void setUserData(final String userData) {
        this.cimiObject.setUserData(userData);
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

    public static CreateResult<MachineTemplate> createMachineTemplate(final CimiClient client,
        final MachineTemplate machineTemplate) throws CimiException {
        if (client.cloudEntryPoint.getMachineTemplates() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiMachineTemplateCollection machineTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachineTemplates().getHref()),
            CimiMachineTemplateCollectionRoot.class, null);
        String addRef = Helper.findOperation("add", machineTemplateCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiMachineTemplate> result = client.postCreateRequest(addRef, machineTemplate.cimiObject,
            CimiMachineTemplate.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        MachineTemplate createdMachineTemplate = result.getResource() != null ? new MachineTemplate(client,
            result.getResource()) : null;
        return new CreateResult<MachineTemplate>(job, createdMachineTemplate);
    }

    public static UpdateResult<MachineTemplate> updateMachineTemplate(final CimiClient client, final String id,
        final Map<String, Object> attributeValues) throws CimiException {
        CimiMachineTemplate cimiObject = new CimiMachineTemplate();
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> entry : attributeValues.entrySet()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            String attribute = entry.getKey();
            sb.append(attribute);
            if (attribute.equals("name")) {
                cimiObject.setName((String) entry.getValue());
            } else if (attribute.equals("description")) {
                cimiObject.setDescription((String) entry.getValue());
            } else if (attribute.equals("properties")) {
                cimiObject.setProperties((Map<String, String>) entry.getValue());
            } else if (attribute.equals("machineConfig")) {
                cimiObject.setMachineConfig(new CimiMachineConfiguration((String) entry.getValue()));
            } else if (attribute.equals("machineImage")) {
                cimiObject.setMachineImage(new CimiMachineImage((String) entry.getValue()));
            }
        }
        CimiResult<CimiMachineTemplate> cimiResult = client.partialUpdateRequest(id, cimiObject, sb.toString());
        Job job = cimiResult.getJob() != null ? new Job(client, cimiResult.getJob()) : null;
        MachineTemplate machineTemplate = cimiResult.getResource() != null ? new MachineTemplate(client,
            cimiResult.getResource()) : null;
        return new UpdateResult<MachineTemplate>(job, machineTemplate);
    }

    public static List<MachineTemplate> getMachineTemplates(final CimiClient client, final QueryParams queryParams)
        throws CimiException {
        if (client.cloudEntryPoint.getMachineTemplates() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiMachineTemplateCollection machineTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachineTemplates().getHref()),
            CimiMachineTemplateCollectionRoot.class, queryParams);

        List<MachineTemplate> result = new ArrayList<MachineTemplate>();

        if (machineTemplateCollection.getCollection() != null) {
            for (CimiMachineTemplate cimiMachineTemplate : machineTemplateCollection.getCollection().getArray()) {
                result.add(new MachineTemplate(client, cimiMachineTemplate));
            }
        }
        return result;
    }

    public static MachineTemplate getMachineTemplateByReference(final CimiClient client, final String ref) throws CimiException {
        return new MachineTemplate(client, client.getCimiObjectByReference(ref, CimiMachineTemplate.class));
    }

    public static MachineTemplate getMachineTemplateByReference(final CimiClient client, final String ref,
        final QueryParams queryParams) throws CimiException {
        return new MachineTemplate(client, client.getCimiObjectByReference(ref, CimiMachineTemplate.class, queryParams));
    }

}
