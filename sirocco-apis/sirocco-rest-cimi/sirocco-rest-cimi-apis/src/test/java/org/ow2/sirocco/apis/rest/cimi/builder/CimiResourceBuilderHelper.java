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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDataCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.domain.NestedJob;
import org.ow2.sirocco.apis.rest.cimi.domain.ParentJob;
import org.ow2.sirocco.apis.rest.cimi.domain.TargetResource;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiJobCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineDiskCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineImageCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineTemplateCollection;

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
        cimi.setCredentials(CimiResourceBuilderHelper.buildCimiCredentialCollection(id, false, false));
        cimi.setCredentialTemplates(CimiResourceBuilderHelper.buildCimiCredentialTemplateCollection(id, false, false));
        cimi.setJobs(CimiResourceBuilderHelper.buildCimiJobCollection(id, false, false));
        cimi.setJobTime(id);
        cimi.setMachines(CimiResourceBuilderHelper.buildCimiMachineCollection(id, false, false));
        cimi.setMachineConfigs(CimiResourceBuilderHelper.buildCimiMachineConfigurationCollection(id, false, false));
        cimi.setMachineImages(CimiResourceBuilderHelper.buildCimiMachineImageCollection(id, false, false));
        cimi.setMachineTemplates(CimiResourceBuilderHelper.buildCimiMachineTemplateCollection(id, false, false));
        // TODO Volume, NetworkInterface, ...
        return cimi;
    }

    public static CimiCredential buildCimiCredential(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCredential(id, null, true);
    }

    public static CimiCredential buildCimiCredential(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiCredential cimi = new CimiCredential();
        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setKey(CimiResourceBuilderHelper.buildBytes(id));
            cimi.setPassword("passwordValue" + postfix);
            cimi.setUserName("userNameValue" + postfix);
        }
        return cimi;
    }

    public static CimiCredentialCreate buildCimiCredentialCreate(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCredentialCreate(id, null, true);
    }

    public static CimiCredentialCreate buildCimiCredentialCreate(final Integer id, final Integer index, final Boolean expand) {
        CimiCredentialCreate cimi = new CimiCredentialCreate();
        CimiResourceBuilderHelper.fillCimiCommon(cimi, id, index);
        cimi.setCredentialTemplate(CimiResourceBuilderHelper.buildCimiCredentialTemplate(id, index, expand));
        return cimi;
    }

    public static CimiCredentialCollection buildCimiCredentialCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCredentialCollection(id, true, false);
    }

    public static CimiCredentialCollection buildCimiCredentialCollection(final Integer id, final Boolean expand,
        final Boolean expandItems) {
        CimiCredentialCollection collec = new CimiCredentialCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, expand);
        if ((null != expand) && (true == expand)) {
            if ((null != id) && (id > 0)) {
                List<CimiCredential> cimis = new ArrayList<CimiCredential>();
                for (int i = 0; i < id; i++) {
                    cimis.add(CimiResourceBuilderHelper.buildCimiCredential(id, i, expandItems));
                }
                collec.setArray(cimis.toArray(new CimiCredential[cimis.size()]));
            }
        }
        return collec;
    }

    public static CimiCredentialTemplate buildCimiCredentialTemplate(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCredentialTemplate(id, null, true);
    }

    public static CimiCredentialTemplate buildCimiCredentialTemplate(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiCredentialTemplate cimi = new CimiCredentialTemplate();
        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setKey(CimiResourceBuilderHelper.buildBytes(id));
            cimi.setPassword("passwordValue" + postfix);
            cimi.setUserName("userNameValue" + postfix);
        }
        return cimi;
    }

    public static CimiCredentialTemplateCollection buildCimiCredentialTemplateCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiCredentialTemplateCollection(id, true, false);
    }

    public static CimiCredentialTemplateCollection buildCimiCredentialTemplateCollection(final Integer id,
        final Boolean expand, final Boolean expandItems) {
        CimiCredentialTemplateCollection collec = new CimiCredentialTemplateCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, expand);
        if ((null != expand) && (true == expand)) {
            if ((null != id) && (id > 0)) {
                List<CimiCredentialTemplate> cimis = new ArrayList<CimiCredentialTemplate>();
                for (int i = 0; i < id; i++) {
                    cimis.add(CimiResourceBuilderHelper.buildCimiCredentialTemplate(id, i, expandItems));
                }
                collec.setArray(cimis.toArray(new CimiCredentialTemplate[cimis.size()]));
            }
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
        return CimiResourceBuilderHelper.buildCimiMachineImageCollection(id, true, false);
    }

    public static CimiMachineImageCollection buildCimiMachineImageCollection(final Integer id, final Boolean expand,
        final Boolean expandItems) {
        return CimiResourceBuilderHelper.buildCimiMachineImageCollection(id, expand, expandItems, false);
    }

    public static CimiMachineImageCollection buildCimiMachineImageCollection(final Integer id, final Boolean expand,
        final Boolean expandItems, final boolean root) {
        CimiMachineImageCollection collec;
        if (false == root) {
            collec = new CimiMachineImageCollection();
        } else {
            collec = new CimiMachineImageCollectionRoot();
        }
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, expand);
        if ((null != expand) && (true == expand)) {
            if ((null != id) && (id > 0)) {
                List<CimiMachineImage> cimis = new ArrayList<CimiMachineImage>();
                for (int i = 0; i < id; i++) {
                    cimis.add(CimiResourceBuilderHelper.buildCimiMachineImage(id, i, expandItems));
                }
                collec.setArray(cimis.toArray(new CimiMachineImage[cimis.size()]));
            }
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
            cimi.setTargetResource(new TargetResource("targetResourceValue" + postfix));
            cimi.setTimeOfStatusChange(CimiResourceBuilderHelper.buildDate(id));

            if ((null != id) && (id > 0)) {
                List<TargetResource> affectedResources = new ArrayList<TargetResource>();
                for (int i = 0; i < id; i++) {
                    affectedResources.add(new TargetResource("affectedResourcesValue" + postfix
                        + CimiResourceBuilderHelper.buildPostfix(id, i)));
                }
                cimi.setAffectedResources(affectedResources.toArray(new TargetResource[affectedResources.size()]));
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
        return CimiResourceBuilderHelper.buildCimiJobCollection(id, true, false);
    }

    public static CimiJobCollection buildCimiJobCollection(final Integer id, final Boolean expand, final Boolean expandItems) {
        CimiJobCollection collec = new CimiJobCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, expand);
        if ((null != expand) && (true == expand)) {
            if ((null != id) && (id > 0)) {
                List<CimiJob> cimis = new ArrayList<CimiJob>();
                for (int i = 0; i < id; i++) {
                    cimis.add(CimiResourceBuilderHelper.buildCimiJob(id, i, expandItems));
                }
                collec.setArray(cimis.toArray(new CimiJob[cimis.size()]));
            }
        }
        return collec;
    }

    public static CimiMachineDisk buildCimiMachineDisk(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiMachineDisk cimi = new CimiMachineDisk();
        CimiResourceBuilderHelper.fillCimiObjectCommon(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setCapacity(id);
            cimi.setInitialLocation("initialLocation" + postfix);
        }
        return cimi;
    }

    public static CimiMachineDiskCollection buildCimiMachineDiskCollection(final Integer id, final Boolean expand) {
        return CimiResourceBuilderHelper.buildCimiMachineDiskCollection(id, expand, false);
    }

    public static CimiMachineDiskCollection buildCimiMachineDiskCollection(final Integer id, final Boolean expand,
        final Boolean expandItems) {
        CimiMachineDiskCollection collec = new CimiMachineDiskCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, expand);
        if ((null != expand) && (true == expand)) {
            if ((null != id) && (id > 0)) {
                for (int i = 0; i < id; i++) {
                    collec.add(CimiResourceBuilderHelper.buildCimiMachineDisk(id, i, expandItems));
                }
            }
        }
        return collec;
    }

    public static CimiDiskConfiguration buildCimiDiskConfiguration(final Integer id, final Integer index) {
        String postfix = CimiResourceBuilderHelper.buildPostfix(id, index);
        CimiDiskConfiguration cimi = new CimiDiskConfiguration();
        cimi.setInitialLocation("initialLocationValue" + postfix);
        cimi.setCapacity(id);
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
            cimi.setCpu(id);
            if ((null != id) && (id > 0)) {
                cimi.setDisks(CimiResourceBuilderHelper.buildCimiMachineDiskCollection(id, expand, true));
            }
            cimi.setMemory(id);
            cimi.setState("stateValue" + postfix);
            // TODO Volume, NetworkInterface, ...
        }
        return cimi;
    }

    public static CimiMachineCollection buildCimiMachineCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachineCollection(id, true, false);
    }

    public static CimiMachineCollection buildCimiMachineCollection(final Integer id, final Boolean expand,
        final Boolean expandItems) {
        CimiMachineCollection collec = new CimiMachineCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, expand);
        if ((null != expand) && (true == expand)) {
            if ((null != id) && (id > 0)) {
                List<CimiMachine> cimis = new ArrayList<CimiMachine>();
                for (int i = 0; i < id; i++) {
                    cimis.add(CimiResourceBuilderHelper.buildCimiMachine(id, i, expandItems));
                }
                collec.setArray(cimis.toArray(new CimiMachine[cimis.size()]));
            }
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
            cimi.setCpu(id);
            cimi.setMemory(id);
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
        return CimiResourceBuilderHelper.buildCimiMachineConfigurationCollection(id, true, false);
    }

    public static CimiMachineConfigurationCollection buildCimiMachineConfigurationCollection(final Integer id,
        final Boolean expand, final Boolean expandItems) {
        return CimiResourceBuilderHelper.buildCimiMachineConfigurationCollection(id, expand, expandItems, false);
    }

    public static CimiMachineConfigurationCollection buildCimiMachineConfigurationCollection(final Integer id,
        final Boolean expand, final Boolean expandItems, final boolean root) {
        CimiMachineConfigurationCollection collec;
        if (false == root) {
            collec = new CimiMachineConfigurationCollection();
        } else {
            collec = new CimiMachineConfigurationCollectionRoot();
        }
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, expand);
        if ((null != expand) && (true == expand)) {
            if ((null != id) && (id > 0)) {
                List<CimiMachineConfiguration> cimis = new ArrayList<CimiMachineConfiguration>();
                for (int i = 0; i < id; i++) {
                    cimis.add(CimiResourceBuilderHelper.buildCimiMachineConfiguration(id, i, expandItems));
                }
                collec.setArray(cimis.toArray(new CimiMachineConfiguration[cimis.size()]));
            }
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
            cimi.setCredential(CimiResourceBuilderHelper.buildCimiCredential(id, index, expand));
            cimi.setMachineConfig(CimiResourceBuilderHelper.buildCimiMachineConfiguration(id, index, expand));
            cimi.setMachineImage(CimiResourceBuilderHelper.buildCimiMachineImage(id, index, expand));
            // TODO Volume, NetworkInterface, ...
            // CimiMachineTemplateVolume mvol1 = new
            // CimiMachineTemplateVolume();
            // mvol1.setInitialLocation("initialLocationVolume_1");
            // mvol1.setHref("hrefVolume_1");
            // mvol1.setId("idVolume_1");
            // mvol1.setName("nameVolume_1");
            // CimiMachineTemplateVolume mvol2 = new
            // CimiMachineTemplateVolume();
            // mvol2.setInitialLocation("initialLocationVolume_1");
            // mvol2.setHref("hrefVolume_2");
            // mvol2.setId("idVolume_2");
            // mvol2.setName("nameVolume_2");
            // cimi.setVolumes(new CimiMachineTemplateVolume[] {mvol1, mvol2});
            //
            // CimiMachineTemplateVolumeTemplate mvoltemp1 = new
            // CimiMachineTemplateVolumeTemplate();
            // mvoltemp1.setInitialLocation("initialLocationVolumeTemplate_1");
            // mvoltemp1.setHref("hrefVolumeTemplate_1");
            // mvoltemp1.setId("idVolumeTemplate_1");
            // mvoltemp1.setName("nameVolumeTemplate_1");
            // CimiMachineTemplateVolumeTemplate mvoltemp2 = new
            // CimiMachineTemplateVolumeTemplate();
            // mvoltemp2.setInitialLocation("initialLocationVolumeTemplate_2");
            // mvoltemp2.setHref("hrefVolumeTemplate_2");
            // mvoltemp2.setId("idVolumeTemplate_2");
            // mvoltemp2.setName("nameVolumeTemplate_2");
            // cimi.setVolumeTemplates(new CimiMachineTemplateVolumeTemplate[]
            // {mvoltemp1, mvoltemp2});
        }
        return cimi;
    }

    public static CimiMachineTemplateCollection buildCimiMachineTemplateCollection(final Integer id) {
        return CimiResourceBuilderHelper.buildCimiMachineTemplateCollection(id, true, false);
    }

    public static CimiMachineTemplateCollection buildCimiMachineTemplateCollection(final Integer id, final Boolean expand,
        final Boolean expandItems) {
        CimiMachineTemplateCollection collec = new CimiMachineTemplateCollection();
        CimiResourceBuilderHelper.fillCimiCollection(collec, id, null, expand);
        if ((null != expand) && (true == expand)) {
            if ((null != id) && (id > 0)) {
                List<CimiMachineTemplate> cimis = new ArrayList<CimiMachineTemplate>();
                for (int i = 0; i < id; i++) {
                    cimis.add(CimiResourceBuilderHelper.buildCimiMachineTemplate(id, i, expandItems));
                }
                collec.setArray(cimis.toArray(new CimiMachineTemplate[cimis.size()]));
            }
        }
        return collec;
    }

}