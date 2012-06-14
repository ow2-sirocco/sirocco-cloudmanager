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
package org.ow2.sirocco.apis.rest.cimi.builder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.ow2.sirocco.apis.rest.cimi.domain.ActionType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAction;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDataCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJobCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.domain.NestedJob;
import org.ow2.sirocco.apis.rest.cimi.domain.ParentJob;

/**
 * Helper to build Cimi Entities to test.
 */
public class CimiResourceBuilderHelper {

    public static Date buildDate(final Integer id) {
        Date ret = null;
        if (null != id) {
            int year = 2000 + (id % 100);
            int month = id % 12;
            int day = 1 + (id % 28);
            int hour = id % 24;
            int minute = id % 60;
            int second = id % 60;
            int milli = id % 1000;

            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.ZONE_OFFSET, 0);
            cal.set(year, month, day, hour, minute, second);
            cal.set(Calendar.MILLISECOND, milli);
            cal.setTimeZone(TimeZone.getTimeZone("Zulu"));
            ret = cal.getTime();
        }
        return ret;
    }

    public static Boolean buildBoolean(final Integer id) {
        Boolean ret = null;
        if (null != id) {
            ret = Boolean.valueOf(0 == id % 2);
        }
        return ret;
    }

    public static byte[] buildBytes(final Integer id) {
        byte[] ret = null;
        if ((null != id) && (id > 0)) {
            ret = new byte[13 + id * 13];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = (byte) i;
            }
        }
        return ret;
    }

    public static Map<String, String> buildProperties(final Integer id) {
        Map<String, String> properties = null;
        if ((null != id) && (id > 0)) {
            properties = new HashMap<String, String>();
            for (int i = 0; i < (id % 5); i++) {
                properties.put("keyProp_" + id + "_" + i, "valueProp_" + id + "_" + i);
            }
        }
        return properties;
    }

    public static CimiOperation[] buildOperations(final Integer id) {
        CimiOperation[] ret = null;
        if ((null != id) && (id > 0)) {
            List<CimiOperation> ops = new ArrayList<CimiOperation>();
            for (int i = 0; i < (id % 5); i++) {
                ops.add(new CimiOperation("relValue_" + id + "_" + i, "hrefValue_" + id + "_" + i));
            }
            ret = ops.toArray(new CimiOperation[ops.size()]);
        }
        return ret;
    }

    public static String buildPostfix(final Integer id, final Integer index) {
        StringBuilder sb = new StringBuilder();
        if (null != id) {
            sb.append('_').append(id);
            if (null != index) {
                sb.append('_').append(index);
            }
        }
        return sb.toString();
    }

    public static void fillCimiCommon(final CimiDataCommon common, final Integer id, final Integer index) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        common.setDescription("descriptionValue" + postfix);
        common.setName("nameValue" + postfix);
        common.setProperties(CimiResourceBuilderHelper.buildProperties(id));
    }

    public static void fillCimiObjectCommon(final CimiObjectCommon common, final Integer id, final Integer index,
        final Boolean expand) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        if ((null != expand) && (true == expand)) {
            CimiResourceBuilderHelper.fillCimiCommon(common, id, index);
            common.setCreated(CimiResourceBuilderHelper.buildDate(id));
            common.setId("idValue" + postfix);
            common.setResourceURI(common.getExchangeType().getResourceURI());
            common.setOperations(CimiResourceBuilderHelper.buildOperations(id));
            common.setUpdated(CimiResourceBuilderHelper.buildDate(id));
        }
        common.setHref("hrefValue" + postfix);
    }

    public static void fillCimiCollection(final CimiCollection<?> common, final Integer id, final Integer index,
        final Boolean expand) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        if ((null != expand) && (true == expand)) {
            common.setId("idValue" + postfix);
            common.setResourceURI(common.getExchangeType().getResourceURI());
            common.setOperations(CimiResourceBuilderHelper.buildOperations(id));
        }
        common.setHref("hrefValue" + postfix);
    }

    public static CimiCloudEntryPoint buildCimiCloudEntryPoint(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCloudEntryPoint(id, null, true);
    }

    public static CimiCloudEntryPoint buildCimiCloudEntryPoint(final Integer id, final Integer index, final Boolean expand) {
        CimiCloudEntryPoint cimi = new CimiCloudEntryPoint();
        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);
        cimi.setBaseURI("baseUriValue" + CimiResourceBuilderHelper.buildPostfix(id, index));
        cimi.setCredentials(CimiResourceBuilderHelper.buildCimiCredentialsCollection(id, expand));
        cimi.setCredentialsTemplates(CimiResourceBuilderHelper.buildCimiCredentialsTemplateCollection(id, expand));
        cimi.setJobs(CimiResourceBuilderHelper.buildCimiJobCollection(id, expand));
        cimi.setJobTime(id);
        cimi.setMachines(CimiResourceBuilderHelper.buildCimiMachineCollection(id, expand));
        cimi.setMachineConfigs(CimiResourceBuilderHelper.buildCimiMachineConfigurationCollection(id, expand));
        cimi.setMachineImages(CimiResourceBuilderHelper.buildCimiMachineImageCollection(id, expand));
        cimi.setMachineTemplates(CimiResourceBuilderHelper.buildCimiMachineTemplateCollection(id, expand));
        // TODO Volume, NetworkInterface, ...
        return cimi;
    }

    public static CimiCredentials buildCimiCredentials(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCredentials(id, null, true);
    }

    public static CimiCredentials buildCimiCredentials(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiCredentials cimi = new CimiCredentials();
        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setKey(CimiResourceBuilderHelper.buildBytes(id));
            cimi.setPassword("passwordValue" + postfix);
            cimi.setUserName("userNameValue" + postfix);
        }
        return cimi;
    }

    public static CimiCredentialsCreate buildCimiCredentialsCreate(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCredentialsCreate(id, null, true);
    }

    public static CimiCredentialsCreate buildCimiCredentialsCreate(final Integer id, final Integer index, final Boolean expand) {
        CimiCredentialsCreate cimi = new CimiCredentialsCreate();
        CimiResourceBuilderHelper.fillCimiCommon(cimi, id, index);
        cimi.setCredentialsTemplate(CimiResourceBuilderHelper.buildCimiCredentialsTemplate(id, index, expand));
        return cimi;
    }

    public static CimiCredentialsCollection buildCimiCredentialsCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCredentialsCollection(id, false);
    }

    public static CimiCredentialsCollection buildCimiCredentialsCollection(final Integer id, final Boolean expand) {
        CimiCredentialsCollection collec = new CimiCredentialsCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, true);

        if ((null != id) && (id > 0)) {
            List<CimiCredentials> cimis = new ArrayList<CimiCredentials>();
            for (int i = 0; i < id; i++) {
                cimis.add(CimiResourceBuilderHelper.buildCimiCredentials(id, i, expand));
            }
            collec.setArray(cimis.toArray(new CimiCredentials[cimis.size()]));
        }
        return collec;
    }

    public static CimiCredentialsTemplate buildCimiCredentialsTemplate(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCredentialsTemplate(id, null, true);
    }

    public static CimiCredentialsTemplate buildCimiCredentialsTemplate(final Integer id, final Integer index,
        final Boolean expand) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiCredentialsTemplate cimi = new CimiCredentialsTemplate();
        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setKey(CimiResourceBuilderHelper.buildBytes(id));
            cimi.setPassword("passwordValue" + postfix);
            cimi.setUserName("userNameValue" + postfix);
        }
        return cimi;
    }

    public static CimiCredentialsTemplateCollection buildCimiCredentialsTemplateCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCredentialsTemplateCollection(id, false);
    }

    public static CimiCredentialsTemplateCollection buildCimiCredentialsTemplateCollection(final Integer id,
        final Boolean expand) {
        CimiCredentialsTemplateCollection collec = new CimiCredentialsTemplateCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, true);

        if ((null != id) && (id > 0)) {
            List<CimiCredentialsTemplate> cimis = new ArrayList<CimiCredentialsTemplate>();
            for (int i = 0; i < id; i++) {
                cimis.add(CimiResourceBuilderHelper.buildCimiCredentialsTemplate(id, i, expand));
            }
            collec.setArray(cimis.toArray(new CimiCredentialsTemplate[cimis.size()]));
        }
        return collec;
    }

    public static CimiMachineImage buildCimiMachineImage(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachineImage(id, null, true);
    }

    public static CimiMachineImage buildCimiMachineImage(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiMachineImage cimi = new CimiMachineImage();
        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setImageLocation(new ImageLocation("hrefImageLocation" + postfix));
            cimi.setState("stateValue" + postfix);
            cimi.setType("typeValue" + postfix);
        }
        return cimi;
    }

    public static CimiMachineImageCollection buildCimiMachineImageCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachineImageCollection(id, false);
    }

    public static CimiMachineImageCollection buildCimiMachineImageCollection(final Integer id, final Boolean expand) {
        CimiMachineImageCollection collec = new CimiMachineImageCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, true);

        if ((null != id) && (id > 0)) {
            List<CimiMachineImage> cimis = new ArrayList<CimiMachineImage>();
            for (int i = 0; i < id; i++) {
                cimis.add(CimiResourceBuilderHelper.buildCimiMachineImage(id, i, expand));
            }
            collec.setArray(cimis.toArray(new CimiMachineImage[cimis.size()]));
        }
        return collec;
    }

    public static CimiJob buildCimiJob(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiJob(id, null, true);
    }

    public static CimiJob buildCimiJob(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiJob cimi = new CimiJob();

        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setAction("actionValue" + postfix);
            cimi.setIsCancellable(CimiResourceBuilderHelper.buildBoolean(id));
            cimi.setProgress(id);
            cimi.setReturnCode(id);
            cimi.setStatus("statusValue" + postfix);
            cimi.setStatusMessage("statusMessageValue" + postfix);
            cimi.setTargetResource("targetResourceValue" + postfix);
            cimi.setTimeOfStatusChange(CimiResourceBuilderHelper.buildDate(id));

            if ((null != id) && (id > 0)) {
                List<String> affectedResources = new ArrayList<String>();
                for (int i = 0; i < id; i++) {
                    affectedResources.add(new String("affectedResourcesValue" + postfix
                        + CimiResourceBuilderHelper.buildPostfix(id, i)));
                }
                cimi.setAffectedResources(affectedResources.toArray(new String[affectedResources.size()]));
                cimi.setParentJob(new ParentJob("hrefParentValue" + postfix));
                List<NestedJob> nesteds = new ArrayList<NestedJob>();
                for (int i = 0; i < id; i++) {
                    nesteds.add(new NestedJob("hrefNestedValue" + postfix + CimiResourceBuilderHelper.buildPostfix(id, i)));
                }
                cimi.setNestedJobs(nesteds.toArray(new NestedJob[nesteds.size()]));
            }
        }
        return cimi;
    }

    public static CimiJobCollection buildCimiJobCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiJobCollection(id, false);
    }

    public static CimiJobCollection buildCimiJobCollection(final Integer id, final Boolean expand) {
        CimiJobCollection collec = new CimiJobCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, true);

        if ((null != id) && (id > 0)) {
            List<CimiJob> cimis = new ArrayList<CimiJob>();
            for (int i = 0; i < id; i++) {
                cimis.add(CimiResourceBuilderHelper.buildCimiJob(id, i, expand));
            }
            collec.setArray(cimis.toArray(new CimiJob[cimis.size()]));
        }
        return collec;
    }

    public static CimiMemory buildCimiMemory(final Integer id, final Integer index) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiMemory cimi = new CimiMemory();
        cimi.setQuantity(id);
        cimi.setUnits("unitValue" + postfix);
        return cimi;
    }

    public static CimiCapacity buildCimiCapacity(final Integer id, final Integer index) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiCapacity cimi = new CimiCapacity();
        cimi.setQuantity(id);
        cimi.setUnits("unitValue" + postfix);
        return cimi;
    }

    public static CimiCpu buildCimiCpu(final Integer id, final Integer index) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiCpu cimi = new CimiCpu();
        cimi.setFrequency(new Float(id * 100.0));
        cimi.setNumberVirtualCpus(id);
        cimi.setUnits("unitValue" + postfix);
        return cimi;
    }

    public static CimiDisk buildCimiDisk(final Integer id, final Integer index) {
        CimiDisk cimi = new CimiDisk();
        cimi.setCapacity(CimiResourceBuilderHelper.buildCimiCapacity(id, index));
        return cimi;
    }

    public static CimiDiskConfiguration buildCimiDiskConfiguration(final Integer id, final Integer index) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiDiskConfiguration cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("attachmentPointValue" + postfix);
        cimi.setCapacity(CimiResourceBuilderHelper.buildCimiCapacity(id, index));
        cimi.setFormat("formatValue" + postfix);
        return cimi;
    }

    public static CimiAction buildCimiAction(final Integer id) {
        CimiAction cimi = new CimiAction();
        CimiResourceBuilderHelper.fillCimiCommon(cimi, id, null);
        cimi.setAction(ActionType.START.getPath());
        return cimi;
    }

    public static CimiMachine buildCimiMachine(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachine(id, null, true);
    }

    public static CimiMachine buildCimiMachine(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiMachine cimi = new CimiMachine();
        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);

        if ((null != expand) && (true == expand)) {
            cimi.setCpu(CimiResourceBuilderHelper.buildCimiCpu(id, index));
            if ((null != id) && (id > 0)) {
                List<CimiDisk> cimis = new ArrayList<CimiDisk>();
                for (int i = 0; i < id; i++) {
                    cimis.add(CimiResourceBuilderHelper.buildCimiDisk(id, i));
                }
                cimi.setDisks(cimis.toArray(new CimiDisk[cimis.size()]));
            }
            cimi.setMemory(CimiResourceBuilderHelper.buildCimiMemory(id, index));
            cimi.setState("stateValue" + postfix);
            // TODO Volume, NetworkInterface, ...
        }
        return cimi;
    }

    public static CimiMachineCollection buildCimiMachineCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachineCollection(id, false);
    }

    public static CimiMachineCollection buildCimiMachineCollection(final Integer id, final Boolean expand) {
        CimiMachineCollection collec = new CimiMachineCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, true);

        if ((null != id) && (id > 0)) {
            List<CimiMachine> cimis = new ArrayList<CimiMachine>();
            for (int i = 0; i < id; i++) {
                cimis.add(CimiResourceBuilderHelper.buildCimiMachine(id, i, expand));
            }
            collec.setArray(cimis.toArray(new CimiMachine[cimis.size()]));
        }
        return collec;
    }

    public static CimiMachineConfiguration buildCimiMachineConfiguration(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachineConfiguration(id, null, true);
    }

    public static CimiMachineConfiguration buildCimiMachineConfiguration(final Integer id, final Integer index,
        final Boolean expand) {
        CimiMachineConfiguration cimi = new CimiMachineConfiguration();
        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);

        if ((null != expand) && (true == expand)) {
            cimi.setCpu(CimiResourceBuilderHelper.buildCimiCpu(id, index));
            cimi.setMemory(CimiResourceBuilderHelper.buildCimiMemory(id, index));
            if ((null != id) && (id > 0)) {
                List<CimiDiskConfiguration> cimis = new ArrayList<CimiDiskConfiguration>();
                for (int i = 0; i < id; i++) {
                    cimis.add(CimiResourceBuilderHelper.buildCimiDiskConfiguration(id, i));
                }
                cimi.setDisks(cimis.toArray(new CimiDiskConfiguration[cimis.size()]));
            }
        }
        return cimi;
    }

    public static CimiMachineConfigurationCollection buildCimiMachineConfigurationCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachineConfigurationCollection(id, false);
    }

    public static CimiMachineConfigurationCollection buildCimiMachineConfigurationCollection(final Integer id,
        final Boolean expand) {
        CimiMachineConfigurationCollection collec = new CimiMachineConfigurationCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, true);

        if ((null != id) && (id > 0)) {
            List<CimiMachineConfiguration> cimis = new ArrayList<CimiMachineConfiguration>();
            for (int i = 0; i < id; i++) {
                cimis.add(CimiResourceBuilderHelper.buildCimiMachineConfiguration(id, i, expand));
            }
            collec.setArray(cimis.toArray(new CimiMachineConfiguration[cimis.size()]));
        }
        return collec;
    }

    public static CimiMachineCreate buildCimiMachineCreate(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachineCreate(id, null, true);
    }

    public static CimiMachineCreate buildCimiMachineCreate(final Integer id, final Integer index, final Boolean expand) {
        CimiMachineCreate cimi = new CimiMachineCreate();
        CimiResourceBuilderHelper.fillCimiCommon(cimi, id, index);
        cimi.setMachineTemplate(CimiResourceBuilderHelper.buildCimiMachineTemplate(id, index, expand));
        return cimi;
    }

    public static CimiMachineTemplate buildCimiMachineTemplate(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachineTemplate(id, null, true);
    }

    public static CimiMachineTemplate buildCimiMachineTemplate(final Integer id, final Integer index, final Boolean expand) {
        CimiMachineTemplate cimi = new CimiMachineTemplate();
        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);

        if ((null != expand) && (true == expand)) {
            cimi.setCredentials(CimiResourceBuilderHelper.buildCimiCredentials(id, index, expand));
            cimi.setMachineConfig(CimiResourceBuilderHelper.buildCimiMachineConfiguration(id, index, expand));
            cimi.setMachineImage(CimiResourceBuilderHelper.buildCimiMachineImage(id, index, expand));
            // TODO Volume, NetworkInterface, ...
        }
        return cimi;
    }

    public static CimiMachineTemplateCollection buildCimiMachineTemplateCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachineTemplateCollection(id, false);
    }

    public static CimiMachineTemplateCollection buildCimiMachineTemplateCollection(final Integer id, final Boolean expand) {
        CimiMachineTemplateCollection collec = new CimiMachineTemplateCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, true);

        if ((null != id) && (id > 0)) {
            List<CimiMachineTemplate> cimis = new ArrayList<CimiMachineTemplate>();
            for (int i = 0; i < id; i++) {
                cimis.add(CimiResourceBuilderHelper.buildCimiMachineTemplate(id, i, expand));
            }
            collec.setArray(cimis.toArray(new CimiMachineTemplate[cimis.size()]));
        }
        return collec;
    }

}