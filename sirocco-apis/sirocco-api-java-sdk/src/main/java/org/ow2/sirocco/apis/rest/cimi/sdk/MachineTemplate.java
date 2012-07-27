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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateNetworkInterface;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class MachineTemplate extends Resource<CimiMachineTemplate> {
    private MachineImage machineImage;

    private MachineConfiguration machineConfig;

    private Credential credential;

    public MachineTemplate() {
        super(null, new CimiMachineTemplate());
    }

    public MachineTemplate(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiMachineTemplate());
        this.cimiObject.setHref(id);
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

    public MachineConfiguration getMachineConfig() {
        return this.machineConfig;
    }

    public void setMachineConfig(final MachineConfiguration machineConfig) {
        this.machineConfig = machineConfig;
        this.cimiObject.setMachineConfig(machineConfig.cimiObject);
    }

    public Credential getCredential() {
        return this.credential;
    }

    public void setCredential(final Credential credential) {
        this.credential = credential;
        this.cimiObject.setCredential(credential.cimiObject);
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

    public void delete() throws CimiException {
        this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()));
    }

    public static MachineTemplate createMachineTemplate(final CimiClient client, final MachineTemplate machineTemplate)
        throws CimiException {
        CimiMachineTemplate cimiObject = client.postRequest(ConstantsPath.MACHINE_TEMPLATE_PATH, machineTemplate.cimiObject,
            CimiMachineTemplate.class);
        return new MachineTemplate(client, cimiObject);
    }

    public static List<MachineTemplate> getMachineTemplates(final CimiClient client, final int first, final int last,
        final String... filterExpression) throws CimiException {
        CimiMachineTemplateCollection machineTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachineTemplates().getHref()),
            CimiMachineTemplateCollectionRoot.class, first, last, filterExpression);

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

    public static MachineTemplate getMachineTemplateById(final CimiClient client, final String id) throws CimiException {
        String path = client.getMachineTemplatesPath() + "/" + id;
        return new MachineTemplate(client, client.getCimiObjectByReference(path, CimiMachineTemplate.class));
    }

}
