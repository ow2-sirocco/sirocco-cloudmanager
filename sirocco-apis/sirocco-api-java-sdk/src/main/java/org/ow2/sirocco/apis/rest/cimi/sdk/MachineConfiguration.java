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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

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

    public MachineConfiguration(final CimiClient cimiClient, final String id) {
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

    public void setDisks(final Disk[] disks) {
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
        this.cimiObject.setDisks(diskConfigs);
    }

    public void delete() throws CimiException {
        this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()));
    }

    public static MachineConfiguration createMachineConfiguration(final CimiClient client,
        final MachineConfiguration machineConfig) throws CimiException {
        CimiMachineConfiguration cimiObject = client.postRequest(ConstantsPath.MACHINE_CONFIGURATION_PATH,
            machineConfig.cimiObject, CimiMachineConfiguration.class);
        return new MachineConfiguration(client, cimiObject);
    }

    public static List<MachineConfiguration> getMachineConfigurations(final CimiClient client, final int first, final int last,
        final String... filterExpression) throws CimiException {
        CimiMachineConfigurationCollection machineConfigCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachineConfigs().getHref()),
            CimiMachineConfigurationCollectionRoot.class, first, last, null, filterExpression);

        List<MachineConfiguration> result = new ArrayList<MachineConfiguration>();

        if (machineConfigCollection.getCollection() != null) {
            for (CimiMachineConfiguration cimiMachineConfig : machineConfigCollection.getCollection().getArray()) {
                result.add(new MachineConfiguration(client, cimiMachineConfig));
            }
        }
        return result;
    }

    public static MachineConfiguration getMachineConfigurationByReference(final CimiClient client, final String ref)
        throws CimiException {
        return new MachineConfiguration(client, client.getCimiObjectByReference(ref, CimiMachineConfiguration.class));
    }

    public static MachineConfiguration getMachineConfigurationById(final CimiClient client, final String id)
        throws CimiException {
        String path = client.getMachineConfigurationsPath() + "/" + id;
        return new MachineConfiguration(client, client.getCimiObjectByReference(path, CimiMachineConfiguration.class));
    }

}
