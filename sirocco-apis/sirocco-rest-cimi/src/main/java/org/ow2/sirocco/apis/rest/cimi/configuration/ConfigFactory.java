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

import org.ow2.sirocco.apis.rest.cimi.converter.CloudEntryPointConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.CredentialsCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.CredentialsConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.CredentialsTemplateCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.CredentialsTemplateConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.JobCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.JobConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineConfigurationCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineConfigurationConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineImageCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineImageConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineTemplateCollectionConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.MachineTemplateConverter;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJobCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.utils.CimiEntityType;
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

    /**
     * Get the config.
     * 
     * @return The config
     */
    public Config getConfig() {
        Config config = new Config();
        config.setCimiEntityItems(this.buildEntityItems());
        return config;
    }

    /**
     * Build the configuration for CimiEntities.
     * 
     * @return A list of entity configs
     */
    protected List<ItemConfig> buildEntityItems() {
        List<ItemConfig> items = new ArrayList<ItemConfig>();
        for (CimiEntityType type : CimiEntityType.values()) {
            items.add(this.buildEntityItem(type));
        }
        return items;
    }

    /**
     * Build the configuration for the given CimiEntityType.
     * 
     * @return A entity config
     */
    protected ItemConfig buildEntityItem(final CimiEntityType type) {
        ItemConfig item = null;
        Map<String, CimiEntityType> associatedNames;

        switch (type) {

        case CloudEntryPoint:
            item = new ItemConfig(CimiEntityType.CloudEntryPoint, CimiCloudEntryPoint.class);
            associatedNames = new HashMap<String, CimiEntityType>();
            item.putData(ConfigFactory.NAMES, associatedNames);
            associatedNames.put("credentials", CimiEntityType.CredentialsCollection);
            associatedNames.put("credentialsTemplates", CimiEntityType.CredentialsTemplateCollection);
            associatedNames.put("machines", CimiEntityType.MachineCollection);
            associatedNames.put("machineTemplates", CimiEntityType.MachineTemplateCollection);
            associatedNames.put("machineConfigs", CimiEntityType.MachineConfigurationCollection);
            associatedNames.put("machineImages", CimiEntityType.MachineImageCollection);
            item.putData(ConfigFactory.CONVERTER, new CloudEntryPointConverter());
            break;

        case Credentials:
            item = new ItemConfig(CimiEntityType.Credentials, CimiCredentials.class);
            item.putData(ConfigFactory.CONVERTER, new CredentialsConverter());
            break;

        case CredentialsCollection:
            item = new ItemConfig(CimiEntityType.CredentialsCollection, CimiCredentialsCollection.class);
            associatedNames = new HashMap<String, CimiEntityType>();
            item.putData(ConfigFactory.NAMES, associatedNames);
            associatedNames.put("credentials", CimiEntityType.Credentials);
            item.putData(ConfigFactory.CONVERTER, new CredentialsCollectionConverter());
            break;

        case CredentialsTemplate:
            item = new ItemConfig(CimiEntityType.CredentialsTemplate, CimiCredentialsTemplate.class);
            item.putData(ConfigFactory.CONVERTER, new CredentialsTemplateConverter());
            break;

        case CredentialsTemplateCollection:
            item = new ItemConfig(CimiEntityType.CredentialsTemplateCollection, CimiCredentialsTemplateCollection.class);
            associatedNames = new HashMap<String, CimiEntityType>();
            item.putData(ConfigFactory.NAMES, associatedNames);
            associatedNames.put("credentialsTemplates", CimiEntityType.CredentialsTemplate);
            item.putData(ConfigFactory.CONVERTER, new CredentialsTemplateCollectionConverter());
            break;

        case Job:
            item = new ItemConfig(CimiEntityType.Job, CimiJob.class);
            item.putData(ConfigFactory.CONVERTER, new JobConverter());
            break;

        case JobCollection:
            item = new ItemConfig(CimiEntityType.JobCollection, CimiJobCollection.class);
            associatedNames = new HashMap<String, CimiEntityType>();
            item.putData(ConfigFactory.NAMES, associatedNames);
            associatedNames.put("jobs", CimiEntityType.Job);
            item.putData(ConfigFactory.CONVERTER, new JobCollectionConverter());
            break;

        case Machine:
            item = new ItemConfig(CimiEntityType.Machine, CimiMachine.class);
            item.putData(ConfigFactory.CONVERTER, new MachineConverter());
            break;

        case MachineCollection:
            item = new ItemConfig(CimiEntityType.MachineCollection, CimiMachine.class);
            associatedNames = new HashMap<String, CimiEntityType>();
            item.putData(ConfigFactory.NAMES, associatedNames);
            associatedNames.put("machines", CimiEntityType.Machine);
            item.putData(ConfigFactory.CONVERTER, new MachineCollectionConverter());
            break;

        case MachineConfiguration:
            item = new ItemConfig(CimiEntityType.MachineConfiguration, CimiMachineConfiguration.class);
            item.putData(ConfigFactory.CONVERTER, new MachineConfigurationConverter());
            break;

        case MachineConfigurationCollection:
            item = new ItemConfig(CimiEntityType.MachineConfigurationCollection, CimiMachineConfigurationCollection.class);
            associatedNames = new HashMap<String, CimiEntityType>();
            item.putData(ConfigFactory.NAMES, associatedNames);
            associatedNames.put("machineConfigurations", CimiEntityType.MachineConfiguration);
            item.putData(ConfigFactory.CONVERTER, new MachineConfigurationCollectionConverter());
            break;

        case MachineImage:
            item = new ItemConfig(CimiEntityType.MachineImage, CimiMachineImage.class);
            item.putData(ConfigFactory.CONVERTER, new MachineImageConverter());
            break;

        case MachineImageCollection:
            item = new ItemConfig(CimiEntityType.MachineImageCollection, CimiMachineImageCollection.class);
            associatedNames = new HashMap<String, CimiEntityType>();
            item.putData(ConfigFactory.NAMES, associatedNames);
            associatedNames.put("machineImages", CimiEntityType.MachineImage);
            item.putData(ConfigFactory.CONVERTER, new MachineImageCollectionConverter());
            break;

        case MachineTemplate:
            item = new ItemConfig(CimiEntityType.MachineTemplate, CimiMachineTemplate.class);
            item.putData(ConfigFactory.CONVERTER, new MachineTemplateConverter());
            break;

        case MachineTemplateCollection:
            item = new ItemConfig(CimiEntityType.MachineTemplateCollection, CimiMachineTemplateCollection.class);
            associatedNames = new HashMap<String, CimiEntityType>();
            item.putData(ConfigFactory.NAMES, associatedNames);
            associatedNames.put("machineTemplates", CimiEntityType.MachineTemplate);
            item.putData(ConfigFactory.CONVERTER, new MachineTemplateCollectionConverter());
            break;

        default:
            ConfigFactory.LOGGER.error("None configuration for {}", type);
            throw new RuntimeException("None configuration for " + type);
        }
        return item;
    }
}