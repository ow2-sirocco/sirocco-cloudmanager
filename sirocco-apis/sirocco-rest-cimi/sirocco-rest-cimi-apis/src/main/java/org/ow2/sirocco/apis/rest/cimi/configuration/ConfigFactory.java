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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ow2.sirocco.apis.rest.cimi.converter.AddressConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.AddressCreateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.AddressTemplateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.CloudEntryPointConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.ComponentDescriptorConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.CredentialConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.CredentialCreateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.CredentialTemplateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.DiskConfigurationConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.JobConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineConfigurationConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineCreateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineDiskConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineImageConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineNetworkInterfaceAddressConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineNetworkInterfaceConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineTemplateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineTemplateNetworkInterfaceConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineTemplateVolumeConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineTemplateVolumeTemplateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineVolumeConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.SystemConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.SystemCreateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.SystemCredentialConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.SystemMachineConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.SystemSystemConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.SystemTemplateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.SystemVolumeConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.VolumeConfigurationConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.VolumeConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.VolumeCreateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.VolumeImageConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.VolumeTemplateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.VolumeVolumeImageConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.AddressCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.AddressCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.AddressTemplateCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.AddressTemplateCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.CredentialCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.CredentialCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.CredentialTemplateCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.CredentialTemplateCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.JobCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.JobCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineConfigurationCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineConfigurationCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineDiskCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineDiskCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineImageCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineImageCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineNetworkInterfaceAddressCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineNetworkInterfaceAddressCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineNetworkInterfaceCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineNetworkInterfaceCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineTemplateCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineTemplateCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineVolumeCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.MachineVolumeCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemCredentialCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemCredentialCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemMachineCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemMachineCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemSystemCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemSystemCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemTemplateCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemTemplateCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemVolumeCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.SystemVolumeCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.VolumeCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.VolumeCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.VolumeConfigurationCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.VolumeConfigurationCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.VolumeImageCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.VolumeImageCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.VolumeTemplateCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.VolumeTemplateCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.VolumeVolumeImageCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.collection.VolumeVolumeImageCollectionRootConverter;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAction;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAddress;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAddressCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAddressTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiComponentDescriptor;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiExchange;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineNetworkInterface;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineNetworkInterfaceAddress;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateNetworkInterface;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetwork;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystem;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemSystem;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiAddressCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiAddressCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiAddressTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiAddressTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiJobCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiJobCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineDiskCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineDiskCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineImageCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineNetworkInterfaceAddressCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineNetworkInterfaceAddressCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineNetworkInterfaceCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineNetworkInterfaceCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineVolumeCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCredentialCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCredentialCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemMachineCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemSystemCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemSystemCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemVolumeCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeConfigurationCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeImageCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeTemplateCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeVolumeImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeVolumeImageCollectionRoot;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.AddressTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeVolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCredentials;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemMachine;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemSystem;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemVolume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default configuration factory.
 */
public class ConfigFactory {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFactory.class);

    /** Associated names */
    public static final String NAMES = "names";

    /** Converter class */
    public static final String CONVERTER = "converter";

    /** Class associate to other class (Service class associate to CIMI Class) */
    public static final String ASSOCIATE_TO = "associate-to";

    /**
     * Get the config.
     * 
     * @return The config
     */
    public Config getConfig() {
        Config config = new Config();
        config.setItems(this.buildItems());
        return config;
    }

    /**
     * Build the configuration for CimiEntities.
     * 
     * @return A list of entity configs
     */
    protected List<ItemConfig> buildItems() {
        List<ItemConfig> items = this.buildExchangeItems();
        items.addAll(this.buildServiceResources());
        items.addAll(this.buildOtherItems());
        return items;
    }

    /**
     * Build the configuration for {@link CimiExchange}.
     * 
     * @return A list of items config
     */
    protected List<ItemConfig> buildExchangeItems() {
        List<ItemConfig> items = new ArrayList<ItemConfig>();
        ItemConfig item = null;
        ItemConfig itemCollectionRoot = null;
        for (ExchangeType type : ExchangeType.values()) {
            item = this.buildExchangeItem(type);
            items.add(item);
            itemCollectionRoot = this.buildExchangeItemCollectionRoot(type);
            if (null != itemCollectionRoot) {
                // Add names data
                itemCollectionRoot.putData(ConfigFactory.NAMES, item.getData(ConfigFactory.NAMES));
                items.add(itemCollectionRoot);
            }
        }
        return items;
    }

    /**
     * Build the configuration for the given {@link ExchangeType}.
     * 
     * @return A item config
     */
    protected ItemConfig buildExchangeItem(final ExchangeType type) {
        ItemConfig item = null;
        Map<ExchangeType, String> referenceNames;

        switch (type) {
        case Action:
            item = new ItemConfig(CimiAction.class, ExchangeType.Action);
            break;

        case Address:
            item = new ItemConfig(CimiAddress.class, ExchangeType.Address);
            item.putData(ConfigFactory.CONVERTER, new AddressConverter());
            break;

        case AddressCollection:
            item = new ItemConfig(CimiAddressCollection.class, ExchangeType.AddressCollection);
            item.putData(ConfigFactory.CONVERTER, new AddressCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.Address, "addresses");
            break;

        case AddressCreate:
            item = new ItemConfig(CimiAddressCreate.class, ExchangeType.AddressCreate);
            item.putData(ConfigFactory.CONVERTER, new AddressCreateConverter());
            break;

        case AddressTemplate:
            item = new ItemConfig(CimiAddressTemplate.class, ExchangeType.AddressTemplate);
            item.putData(ConfigFactory.CONVERTER, new AddressTemplateConverter());
            break;

        case AddressTemplateCollection:
            item = new ItemConfig(CimiAddressTemplateCollection.class, ExchangeType.AddressTemplateCollection);
            item.putData(ConfigFactory.CONVERTER, new AddressTemplateCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.AddressTemplate, "addressTemplates");
            break;

        case CloudEntryPoint:
            item = new ItemConfig(CimiCloudEntryPoint.class, ExchangeType.CloudEntryPoint);
            item.putData(ConfigFactory.CONVERTER, new CloudEntryPointConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.CredentialCollection, "credentials");
            referenceNames.put(ExchangeType.CredentialTemplateCollection, "credentialTemplates");
            referenceNames.put(ExchangeType.MachineCollection, "machines");
            referenceNames.put(ExchangeType.MachineTemplateCollection, "machineTemplates");
            referenceNames.put(ExchangeType.MachineConfigurationCollection, "machineConfigs");
            referenceNames.put(ExchangeType.MachineImageCollection, "machineImages");
            break;

        case Credential:
            item = new ItemConfig(CimiCredential.class, ExchangeType.Credential);
            item.putData(ConfigFactory.CONVERTER, new CredentialConverter());
            break;

        case CredentialCollection:
            item = new ItemConfig(CimiCredentialCollection.class, ExchangeType.CredentialCollection);
            item.putData(ConfigFactory.CONVERTER, new CredentialCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.Credential, "credentials");
            break;

        case CredentialCreate:
            item = new ItemConfig(CimiCredentialCreate.class, ExchangeType.CredentialCreate);
            item.putData(ConfigFactory.CONVERTER, new CredentialCreateConverter());
            break;

        case CredentialTemplate:
            item = new ItemConfig(CimiCredentialTemplate.class, ExchangeType.CredentialTemplate);
            item.putData(ConfigFactory.CONVERTER, new CredentialTemplateConverter());
            break;

        case CredentialTemplateCollection:
            item = new ItemConfig(CimiCredentialTemplateCollection.class, ExchangeType.CredentialTemplateCollection);
            item.putData(ConfigFactory.CONVERTER, new CredentialTemplateCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.CredentialTemplate, "credentialTemplates");
            break;

        case Disk:
            item = new ItemConfig(CimiMachineDisk.class, ExchangeType.Disk);
            item.putData(ConfigFactory.CONVERTER, new MachineDiskConverter());
            break;

        case DiskCollection:
            item = new ItemConfig(CimiMachineDiskCollection.class, ExchangeType.DiskCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineDiskCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.Disk, "disks");
            break;

        case Job:
            item = new ItemConfig(CimiJob.class, ExchangeType.Job);
            item.putData(ConfigFactory.CONVERTER, new JobConverter());
            break;

        case JobCollection:
            item = new ItemConfig(CimiJobCollection.class, ExchangeType.JobCollection);
            item.putData(ConfigFactory.CONVERTER, new JobCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.Job, "jobs");
            break;

        case Machine:
            item = new ItemConfig(CimiMachine.class, ExchangeType.Machine);
            item.putData(ConfigFactory.CONVERTER, new MachineConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.DiskCollection, "disks");
            referenceNames.put(ExchangeType.MachineVolumeCollection, "volumes");
            referenceNames.put(ExchangeType.MachineNetworkInterfaceCollection, "networkInterfaces");
            break;

        case MachineCollection:
            item = new ItemConfig(CimiMachineCollection.class, ExchangeType.MachineCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.Machine, "machines");
            break;

        case MachineConfiguration:
            item = new ItemConfig(CimiMachineConfiguration.class, ExchangeType.MachineConfiguration);
            item.putData(ConfigFactory.CONVERTER, new MachineConfigurationConverter());
            break;

        case MachineConfigurationCollection:
            item = new ItemConfig(CimiMachineConfigurationCollection.class, ExchangeType.MachineConfigurationCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineConfigurationCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.MachineConfiguration, "machineConfigurations");
            break;

        case MachineCreate:
            item = new ItemConfig(CimiMachineCreate.class, ExchangeType.MachineCreate);
            item.putData(ConfigFactory.CONVERTER, new MachineCreateConverter());
            break;

        case MachineImage:
            item = new ItemConfig(CimiMachineImage.class, ExchangeType.MachineImage);
            item.putData(ConfigFactory.CONVERTER, new MachineImageConverter());
            break;

        case MachineImageCollection:
            item = new ItemConfig(CimiMachineImageCollection.class, ExchangeType.MachineImageCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineImageCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.MachineImage, "machineImages");
            break;

        case MachineNetworkInterface:
            item = new ItemConfig(CimiMachineNetworkInterface.class, ExchangeType.MachineNetworkInterface);
            item.putData(ConfigFactory.CONVERTER, new MachineNetworkInterfaceConverter());
            break;

        case MachineNetworkInterfaceCollection:
            item = new ItemConfig(CimiMachineNetworkInterfaceCollection.class, ExchangeType.MachineNetworkInterfaceCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineNetworkInterfaceCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.MachineNetworkInterface, "networkInterfaces");
            break;

        case MachineNetworkInterfaceAddress:
            item = new ItemConfig(CimiMachineNetworkInterfaceAddress.class, ExchangeType.MachineNetworkInterfaceAddress);
            item.putData(ConfigFactory.CONVERTER, new MachineNetworkInterfaceAddressConverter());
            break;

        case MachineNetworkInterfaceAddressCollection:
            item = new ItemConfig(CimiMachineNetworkInterfaceAddressCollection.class,
                ExchangeType.MachineNetworkInterfaceAddressCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineNetworkInterfaceAddressCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.MachineNetworkInterfaceAddress, "addresses");
            break;

        case MachineTemplate:
            item = new ItemConfig(CimiMachineTemplate.class, ExchangeType.MachineTemplate);
            item.putData(ConfigFactory.CONVERTER, new MachineTemplateConverter());
            break;

        case MachineTemplateCollection:
            item = new ItemConfig(CimiMachineTemplateCollection.class, ExchangeType.MachineTemplateCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineTemplateCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.MachineTemplate, "machineTemplates");
            break;

        case MachineTemplateVolume:
            item = new ItemConfig(CimiMachineTemplateVolume.class, ExchangeType.MachineTemplateVolume);
            item.putData(ConfigFactory.CONVERTER, new MachineTemplateVolumeConverter());
            break;

        case MachineTemplateVolumeTemplate:
            item = new ItemConfig(CimiMachineTemplateVolumeTemplate.class, ExchangeType.MachineTemplateVolumeTemplate);
            item.putData(ConfigFactory.CONVERTER, new MachineTemplateVolumeTemplateConverter());
            break;

        case MachineVolume:
            item = new ItemConfig(CimiMachineVolume.class, ExchangeType.MachineVolume);
            item.putData(ConfigFactory.CONVERTER, new MachineVolumeConverter());
            break;

        case MachineVolumeCollection:
            item = new ItemConfig(CimiMachineVolumeCollection.class, ExchangeType.MachineVolumeCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineVolumeCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.MachineVolume, "volumes");
            break;

        case Volume:
            item = new ItemConfig(CimiVolume.class, ExchangeType.Volume);
            item.putData(ConfigFactory.CONVERTER, new VolumeConverter());
            break;

        case VolumeCollection:
            item = new ItemConfig(CimiVolumeCollection.class, ExchangeType.VolumeCollection);
            item.putData(ConfigFactory.CONVERTER, new VolumeCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.Volume, "volumes");
            break;

        case VolumeConfiguration:
            item = new ItemConfig(CimiVolumeConfiguration.class, ExchangeType.VolumeConfiguration);
            item.putData(ConfigFactory.CONVERTER, new VolumeConfigurationConverter());
            break;

        case VolumeConfigurationCollection:
            item = new ItemConfig(CimiVolumeConfigurationCollection.class, ExchangeType.VolumeConfigurationCollection);
            item.putData(ConfigFactory.CONVERTER, new VolumeConfigurationCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.VolumeConfiguration, "volumeConfigurations");
            break;

        case VolumeCreate:
            item = new ItemConfig(CimiVolumeCreate.class, ExchangeType.VolumeCreate);
            item.putData(ConfigFactory.CONVERTER, new VolumeCreateConverter());
            break;

        case VolumeImage:
            item = new ItemConfig(CimiVolumeImage.class, ExchangeType.VolumeImage);
            item.putData(ConfigFactory.CONVERTER, new VolumeImageConverter());
            break;

        case VolumeImageCollection:
            item = new ItemConfig(CimiVolumeImageCollection.class, ExchangeType.VolumeImageCollection);
            item.putData(ConfigFactory.CONVERTER, new VolumeImageCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.VolumeImage, "volumeImages");
            break;

        case VolumeTemplate:
            item = new ItemConfig(CimiVolumeTemplate.class, ExchangeType.VolumeTemplate);
            item.putData(ConfigFactory.CONVERTER, new VolumeTemplateConverter());
            break;

        case VolumeTemplateCollection:
            item = new ItemConfig(CimiVolumeTemplateCollection.class, ExchangeType.VolumeTemplateCollection);
            item.putData(ConfigFactory.CONVERTER, new VolumeTemplateCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.VolumeTemplate, "volumeTemplates");
            break;

        case VolumeVolumeImage:
            item = new ItemConfig(CimiVolumeVolumeImage.class, ExchangeType.VolumeVolumeImage);
            item.putData(ConfigFactory.CONVERTER, new VolumeVolumeImageConverter());
            break;

        case VolumeVolumeImageCollection:
            item = new ItemConfig(CimiVolumeVolumeImageCollection.class, ExchangeType.VolumeVolumeImageCollection);
            item.putData(ConfigFactory.CONVERTER, new VolumeVolumeImageCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.VolumeVolumeImage, "images");
            break;

        case System:
            item = new ItemConfig(CimiSystem.class, ExchangeType.System);
            item.putData(ConfigFactory.CONVERTER, new SystemConverter());
            break;

        case SystemCollection:
            item = new ItemConfig(CimiSystemCollection.class, ExchangeType.SystemCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.System, "systems");
            break;

        case SystemCreate:
            item = new ItemConfig(CimiSystemCreate.class, ExchangeType.SystemCreate);
            item.putData(ConfigFactory.CONVERTER, new SystemCreateConverter());
            break;

        case SystemCredential:
            item = new ItemConfig(CimiSystemCredential.class, ExchangeType.SystemCredential);
            item.putData(ConfigFactory.CONVERTER, new SystemCredentialConverter());
            break;

        case SystemCredentialCollection:
            item = new ItemConfig(CimiSystemCredentialCollection.class, ExchangeType.SystemCredentialCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemCredentialCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.SystemCredential, "credentials");
            break;

        case SystemMachine:
            item = new ItemConfig(CimiSystemMachine.class, ExchangeType.SystemMachine);
            item.putData(ConfigFactory.CONVERTER, new SystemMachineConverter());
            break;

        case SystemMachineCollection:
            item = new ItemConfig(CimiSystemMachineCollection.class, ExchangeType.SystemMachineCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemMachineCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.SystemMachine, "machines");
            break;

        case SystemSystem:
            item = new ItemConfig(CimiSystemSystem.class, ExchangeType.SystemSystem);
            item.putData(ConfigFactory.CONVERTER, new SystemSystemConverter());
            break;

        case SystemSystemCollection:
            item = new ItemConfig(CimiSystemSystemCollection.class, ExchangeType.SystemSystemCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemSystemCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.SystemSystem, "systems");
            break;

        case SystemTemplate:
            item = new ItemConfig(CimiSystemTemplate.class, ExchangeType.SystemTemplate);
            item.putData(ConfigFactory.CONVERTER, new SystemTemplateConverter());
            break;

        case SystemTemplateCollection:
            item = new ItemConfig(CimiSystemTemplateCollection.class, ExchangeType.SystemTemplateCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemTemplateCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.SystemTemplate, "systemTemplates");
            break;

        case SystemVolume:
            item = new ItemConfig(CimiSystemVolume.class, ExchangeType.SystemVolume);
            item.putData(ConfigFactory.CONVERTER, new SystemVolumeConverter());
            break;

        case SystemVolumeCollection:
            item = new ItemConfig(CimiSystemVolumeCollection.class, ExchangeType.SystemVolumeCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemVolumeCollectionConverter());
            referenceNames = new HashMap<ExchangeType, String>();
            item.putData(ConfigFactory.NAMES, referenceNames);
            referenceNames.put(ExchangeType.SystemVolume, "systemVolumes");
            break;

        default:
            ConfigFactory.LOGGER.error("Configuration not found : {}", type);
            throw new ConfigurationException("Configuration not found : " + type);
        }
        return item;
    }

    /**
     * Build the configuration for the given {@link ExchangeType}.
     * 
     * @return A item config
     */
    protected ItemConfig buildExchangeItemCollectionRoot(final ExchangeType type) {
        ItemConfig item = null;

        switch (type) {

        case AddressCollection:
            item = new ItemConfig(CimiAddressCollectionRoot.class, ExchangeType.AddressCollection);
            item.putData(ConfigFactory.CONVERTER, new AddressCollectionRootConverter());
            break;

        case AddressTemplateCollection:
            item = new ItemConfig(CimiAddressTemplateCollectionRoot.class, ExchangeType.AddressTemplateCollection);
            item.putData(ConfigFactory.CONVERTER, new AddressTemplateCollectionRootConverter());
            break;

        case CredentialCollection:
            item = new ItemConfig(CimiCredentialCollectionRoot.class, ExchangeType.CredentialCollection);
            item.putData(ConfigFactory.CONVERTER, new CredentialCollectionRootConverter());
            break;

        case CredentialTemplateCollection:
            item = new ItemConfig(CimiCredentialTemplateCollectionRoot.class, ExchangeType.CredentialTemplateCollection);
            item.putData(ConfigFactory.CONVERTER, new CredentialTemplateCollectionRootConverter());
            break;

        case DiskCollection:
            item = new ItemConfig(CimiMachineDiskCollectionRoot.class, ExchangeType.DiskCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineDiskCollectionRootConverter());
            break;

        case JobCollection:
            item = new ItemConfig(CimiJobCollectionRoot.class, ExchangeType.JobCollection);
            item.putData(ConfigFactory.CONVERTER, new JobCollectionRootConverter());
            break;

        case MachineCollection:
            item = new ItemConfig(CimiMachineCollectionRoot.class, ExchangeType.MachineCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineCollectionRootConverter());
            break;

        case MachineConfigurationCollection:
            item = new ItemConfig(CimiMachineConfigurationCollectionRoot.class, ExchangeType.MachineConfigurationCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineConfigurationCollectionRootConverter());
            break;

        case MachineImageCollection:
            item = new ItemConfig(CimiMachineImageCollectionRoot.class, ExchangeType.MachineImageCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineImageCollectionRootConverter());
            break;

        case MachineNetworkInterfaceCollection:
            item = new ItemConfig(CimiMachineNetworkInterfaceCollectionRoot.class,
                ExchangeType.MachineNetworkInterfaceCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineNetworkInterfaceCollectionRootConverter());
            break;

        case MachineNetworkInterfaceAddressCollection:
            item = new ItemConfig(CimiMachineNetworkInterfaceAddressCollectionRoot.class,
                ExchangeType.MachineNetworkInterfaceAddressCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineNetworkInterfaceAddressCollectionRootConverter());
            break;

        case MachineTemplateCollection:
            item = new ItemConfig(CimiMachineTemplateCollectionRoot.class, ExchangeType.MachineTemplateCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineTemplateCollectionRootConverter());
            break;

        case MachineVolumeCollection:
            item = new ItemConfig(CimiMachineVolumeCollectionRoot.class, ExchangeType.MachineVolumeCollection);
            item.putData(ConfigFactory.CONVERTER, new MachineVolumeCollectionRootConverter());
            break;

        case VolumeCollection:
            item = new ItemConfig(CimiVolumeCollectionRoot.class, ExchangeType.VolumeCollection);
            item.putData(ConfigFactory.CONVERTER, new VolumeCollectionRootConverter());
            break;

        case VolumeConfigurationCollection:
            item = new ItemConfig(CimiVolumeConfigurationCollectionRoot.class, ExchangeType.VolumeConfigurationCollection);
            item.putData(ConfigFactory.CONVERTER, new VolumeConfigurationCollectionRootConverter());
            break;

        case VolumeImageCollection:
            item = new ItemConfig(CimiVolumeImageCollectionRoot.class, ExchangeType.VolumeImageCollection);
            item.putData(ConfigFactory.CONVERTER, new VolumeImageCollectionRootConverter());
            break;

        case VolumeTemplateCollection:
            item = new ItemConfig(CimiVolumeTemplateCollectionRoot.class, ExchangeType.VolumeTemplateCollection);
            item.putData(ConfigFactory.CONVERTER, new VolumeTemplateCollectionRootConverter());
            break;

        case VolumeVolumeImageCollection:
            item = new ItemConfig(CimiVolumeVolumeImageCollectionRoot.class, ExchangeType.VolumeVolumeImageCollection);
            item.putData(ConfigFactory.CONVERTER, new VolumeVolumeImageCollectionRootConverter());
            break;

        case SystemCollection:
            item = new ItemConfig(CimiSystemCollectionRoot.class, ExchangeType.SystemCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemCollectionRootConverter());
            break;

        case SystemCredentialCollection:
            item = new ItemConfig(CimiSystemCredentialCollectionRoot.class, ExchangeType.SystemCredentialCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemCredentialCollectionRootConverter());
            break;

        case SystemMachineCollection:
            item = new ItemConfig(CimiSystemMachineCollectionRoot.class, ExchangeType.SystemMachineCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemMachineCollectionRootConverter());
            break;

        case SystemSystemCollection:
            item = new ItemConfig(CimiSystemSystemCollectionRoot.class, ExchangeType.SystemSystemCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemSystemCollectionRootConverter());
            break;

        case SystemTemplateCollection:
            item = new ItemConfig(CimiSystemTemplateCollectionRoot.class, ExchangeType.SystemTemplateCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemTemplateCollectionRootConverter());
            break;

        case SystemVolumeCollection:
            item = new ItemConfig(CimiSystemVolumeCollectionRoot.class, ExchangeType.SystemVolumeCollection);
            item.putData(ConfigFactory.CONVERTER, new SystemVolumeCollectionRootConverter());
            break;

        case Address:
        case AddressCreate:
        case AddressTemplate:
        case CloudEntryPoint:
        case Credential:
        case CredentialCreate:
        case CredentialTemplate:
        case Disk:
        case Job:
        case Machine:
        case Action:
        case MachineConfiguration:
        case MachineCreate:
        case MachineImage:
        case MachineNetworkInterface:
        case MachineNetworkInterfaceAddress:
        case MachineTemplate:
        case MachineTemplateVolume:
        case MachineTemplateVolumeTemplate:
        case MachineVolume:
        case System:
        case SystemCreate:
        case SystemCredential:
        case SystemMachine:
        case SystemSystem:
        case SystemTemplate:
        case SystemVolume:
        case Volume:
        case VolumeConfiguration:
        case VolumeCreate:
        case VolumeImage:
        case VolumeTemplate:
        case VolumeVolumeImage:
            break;

        default:
            ConfigFactory.LOGGER.error("Configuration not found : {}", type);
            throw new ConfigurationException("Configuration not found : " + type);
        }
        return item;
    }

    /**
     * Build the configuration for service resources classes.
     * 
     * @return A list of config items
     */
    protected List<ItemConfig> buildServiceResources() {
        List<ItemConfig> items = new ArrayList<ItemConfig>();

        // CloudEntity
        items.add(this.makeAssociate(Address.class, CimiAddress.class));
        items.add(this.makeAssociate(AddressTemplate.class, CimiAddressTemplate.class));
        items.add(this.makeAssociate(CloudEntryPoint.class, CimiCloudEntryPoint.class));
        // TODO ComponentDescriptor
        // TODO Event
        // TODO EventLog
        // TODO ForwardingGroupTemplate
        items.add(this.makeAssociate(Job.class, CimiJob.class));
        items.add(this.makeAssociate(MachineConfiguration.class, CimiMachineConfiguration.class));
        items.add(this.makeAssociate(MachineDisk.class, CimiMachineDisk.class));
        items.add(this.makeAssociate(MachineTemplateNetworkInterface.class, CimiMachineTemplateNetworkInterface.class));
        // TODO Meter
        // TODO MeterConfiguration
        // TODO MeterSample
        // TODO MeterTemplate
        // TODO NetworkConfiguration
        // TODO NetworkPortConfiguration
        // TODO NetworkPortTemplate
        items.add(this.makeAssociate(VolumeConfiguration.class, CimiVolumeConfiguration.class));
        items.add(this.makeAssociate(VolumeVolumeImage.class, CimiVolumeVolumeImage.class));

        // CloudCollectionItem
        items.add(this.makeAssociate(SystemCredentials.class, CimiSystemCredential.class));
        items.add(this.makeAssociate(SystemMachine.class, CimiSystemMachine.class));
        // TODO SystemNetwork
        items.add(this.makeAssociate(SystemSystem.class, CimiSystemSystem.class));
        items.add(this.makeAssociate(SystemVolume.class, CimiSystemVolume.class));

        // CloudTemplate
        items.add(this.makeAssociate(CredentialsTemplate.class, CimiCredentialTemplate.class));
        // TODO EventLogTemplate
        items.add(this.makeAssociate(MachineTemplate.class, CimiMachineTemplate.class));
        // TODO NetworkTemplate
        items.add(this.makeAssociate(SystemTemplate.class, CimiSystemTemplate.class));
        items.add(this.makeAssociate(VolumeTemplate.class, CimiVolumeTemplate.class));

        // CloudResource
        items.add(this.makeAssociate(Credentials.class, CimiCredential.class));
        // TODO ForwardingGroup
        items.add(this.makeAssociate(Machine.class, CimiMachine.class));
        items.add(this.makeAssociate(MachineImage.class, CimiMachineImage.class));
        items.add(this.makeAssociate(MachineNetworkInterface.class, CimiMachineNetworkInterface.class));
        items.add(this.makeAssociate(MachineVolume.class, CimiMachineVolume.class));
        items.add(this.makeAssociate(Network.class, CimiNetwork.class));
        // TODO NetworkPort
        items.add(this.makeAssociate(System.class, CimiSystem.class));
        items.add(this.makeAssociate(Volume.class, CimiVolume.class));
        items.add(this.makeAssociate(VolumeImage.class, CimiVolumeImage.class));

        return items;
    }

    /**
     * Make Associate Item Config.
     * 
     * @param classToConfig The class to add to the config
     * @param classToAssociate The CIMI class to associate
     * @return A associate config item
     */
    protected ItemConfig makeAssociate(final Class<?> classToConfig, final Class<? extends CimiData> classToAssociate) {
        ItemConfig item = new ItemConfig(classToConfig);
        item.putData(ConfigFactory.ASSOCIATE_TO, classToAssociate);
        return item;
    }

    /**
     * Build the configuration for other classes.
     * 
     * @return A list of config items
     */
    protected List<ItemConfig> buildOtherItems() {
        ItemConfig item;
        List<ItemConfig> items = new ArrayList<ItemConfig>();

        item = new ItemConfig(CimiDiskConfiguration.class);
        item.putData(ConfigFactory.CONVERTER, new DiskConfigurationConverter());
        items.add(item);

        item = new ItemConfig(CimiMachineTemplateNetworkInterface.class);
        item.putData(ConfigFactory.CONVERTER, new MachineTemplateNetworkInterfaceConverter());
        items.add(item);

        item = new ItemConfig(CimiComponentDescriptor.class);
        item.putData(ConfigFactory.CONVERTER, new ComponentDescriptorConverter());
        items.add(item);

        return items;
    }

}