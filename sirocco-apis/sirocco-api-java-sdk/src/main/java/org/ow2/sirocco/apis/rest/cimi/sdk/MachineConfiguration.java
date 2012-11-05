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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.CimiResult;

public class MachineConfiguration extends Resource<CimiMachineConfiguration> {
    public static class Disk {
        public int capacity;

        public String format;

        public String initialLocation;

        static Disk from(final CimiDiskConfiguration diskConfig) {
            Disk disk = new Disk();
            disk.capacity = diskConfig.getCapacity();
            disk.format = diskConfig.getFormat();
            disk.initialLocation = diskConfig.getInitialLocation();
            return disk;
        }
    }

    public MachineConfiguration() {
        super(null, new CimiMachineConfiguration());
    }

    MachineConfiguration(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiMachineConfiguration());
        this.cimiObject.setHref(id);
    }

    MachineConfiguration(final CimiClient cimiClient, final CimiMachineConfiguration cimiObject) {
        super(cimiClient, cimiObject);
    }

    public int getCpu() {
        return this.cimiObject.getCpu();
    }

    public void setCpu(final int cpu) {
        this.cimiObject.setCpu(cpu);
    }

    public int getMemory() {
        return this.cimiObject.getMemory();
    }

    public void setMemory(final int memory) {
        this.cimiObject.setMemory(memory);
    }

    public Disk[] getDisks() {
        Disk[] disks = new Disk[this.cimiObject.getDisks().length];
        for (int i = 0; i < disks.length; i++) {
            disks[i] = Disk.from(this.cimiObject.getDisks()[i]);
        }
        return disks;
    }

    private static CimiDiskConfiguration[] diskArrayToCimiDiskConfigurationArray(final Disk[] disks) {
        CimiDiskConfiguration diskConfigs[] = new CimiDiskConfiguration[disks.length];
        for (int i = 0; i < disks.length; i++) {
            diskConfigs[i] = new CimiDiskConfiguration();
            diskConfigs[i].setCapacity(disks[i].capacity);
            if (disks[i].format != null) {
                diskConfigs[i].setFormat(disks[i].format);
            } else {
                diskConfigs[i].setFormat("");
            }
            if (disks[i].initialLocation != null) {
                diskConfigs[i].setInitialLocation(disks[i].initialLocation);
            } else {
                diskConfigs[i].setInitialLocation("");
            }
        }
        return diskConfigs;
    }

    public void setDisks(final Disk[] disks) {
        this.cimiObject.setDisks(MachineConfiguration.diskArrayToCimiDiskConfigurationArray(disks));
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

    public static CreateResult<MachineConfiguration> createMachineConfiguration(final CimiClient client,
        final MachineConfiguration machineConfig) throws CimiException {
        if (client.cloudEntryPoint.getMachineConfigs() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiMachineConfigurationCollection machineConfigCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachineConfigs().getHref()),
            CimiMachineConfigurationCollectionRoot.class, null);
        String addRef = Helper.findOperation("add", machineConfigCollection);
        if (addRef == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiResult<CimiMachineConfiguration> result = client.postCreateRequest(addRef, machineConfig.cimiObject,
            CimiMachineConfiguration.class);
        Job job = result.getJob() != null ? new Job(client, result.getJob()) : null;
        MachineConfiguration createdMachineConfig = result.getResource() != null ? new MachineConfiguration(client,
            result.getResource()) : null;
        return new CreateResult<MachineConfiguration>(job, createdMachineConfig);
    }

    public static UpdateResult<MachineConfiguration> updateMachineConfiguration(final CimiClient client, final String id,
        final Map<String, Object> attributeValues) throws CimiException {
        CimiMachineConfiguration cimiObject = new CimiMachineConfiguration();
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
            } else if (attribute.equals("cpu")) {
                cimiObject.setCpu((Integer) entry.getValue());
            } else if (attribute.equals("memory")) {
                cimiObject.setMemory((Integer) entry.getValue());
            } else if (attribute.equals("disks")) {
                cimiObject.setDisks(MachineConfiguration.diskArrayToCimiDiskConfigurationArray((Disk[]) entry.getValue()));
            } else if (attribute.equals("cpuArch")) {
                cimiObject.setCpuArch((String) entry.getValue());
            }
        }
        CimiResult<CimiMachineConfiguration> cimiResult = client.partialUpdateRequest(id, cimiObject, sb.toString());
        Job job = cimiResult.getJob() != null ? new Job(client, cimiResult.getJob()) : null;
        MachineConfiguration machineConfig = cimiResult.getResource() != null ? new MachineConfiguration(client,
            cimiResult.getResource()) : null;
        return new UpdateResult<MachineConfiguration>(job, machineConfig);
    }

    public static List<MachineConfiguration> getMachineConfigurations(final CimiClient client, final QueryParams queryParams)
        throws CimiException {
        if (client.cloudEntryPoint.getMachineConfigs() == null) {
            throw new CimiException("Unsupported operation");
        }
        CimiMachineConfigurationCollection machineConfigCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachineConfigs().getHref()),
            CimiMachineConfigurationCollectionRoot.class, queryParams);

        List<MachineConfiguration> result = new ArrayList<MachineConfiguration>();

        if (machineConfigCollection.getCollection() != null) {
            for (CimiMachineConfiguration cimiMachineConfig : machineConfigCollection.getCollection().getArray()) {
                result.add(new MachineConfiguration(client, cimiMachineConfig));
            }
        }
        return result;
    }

    public static MachineConfiguration getMachineConfigurationByReference(final CimiClient client, final String ref,
        final QueryParams queryParams) throws CimiException {
        return new MachineConfiguration(client, client.getCimiObjectByReference(ref, CimiMachineConfiguration.class,
            queryParams));
    }

    public static MachineConfiguration getMachineConfigurationByReference(final CimiClient client, final String ref)
        throws CimiException {
        return MachineConfiguration.getMachineConfigurationByReference(client, ref, null);
    }

}
