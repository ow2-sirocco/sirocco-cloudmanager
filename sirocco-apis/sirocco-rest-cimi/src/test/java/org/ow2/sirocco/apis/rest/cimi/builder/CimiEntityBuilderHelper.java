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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommonId;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.domain.NestedJob;
import org.ow2.sirocco.apis.rest.cimi.domain.ParentJob;

/**
 * Helper to build Cimi Entities to test.
 */
public class CimiEntityBuilderHelper {

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

    public static void fillCimiCommon(final CimiCommon common, final Integer id, final Integer index) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        common.setDescription("descriptionValue" + postfix);
        common.setName("nameValue" + postfix);
        common.setProperties(CimiEntityBuilderHelper.buildProperties(id));
    }

    public static void fillCimiCommonId(final CimiCommonId common, final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        if ((null != expand) && (true == expand)) {
            CimiEntityBuilderHelper.fillCimiCommon(common, id, index);
            common.setCreated(CimiEntityBuilderHelper.buildDate(id));
            common.setId("idValue" + postfix);
            common.setOperations(CimiEntityBuilderHelper.buildOperations(id));
            common.setUpdated(CimiEntityBuilderHelper.buildDate(id));
        }
        common.setHref("hrefValue" + postfix);
    }

    public static CimiCredentials buildCimiCredentials(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiCredentials(id, null, true);
    }

    public static CimiCredentials buildCimiCredentials(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        CimiCredentials cimi = new CimiCredentials();
        CimiEntityBuilderHelper.fillCimiCommonId(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setKey(CimiEntityBuilderHelper.buildBytes(id));
            cimi.setPassword("passwordValue" + postfix);
            cimi.setUserName("userNameValue" + postfix);
        }
        return cimi;
    }

    public static CimiCredentialsCreate buildCimiCredentialsCreate(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiCredentialsCreate(id, null, true);
    }

    public static CimiCredentialsCreate buildCimiCredentialsCreate(final Integer id, final Integer index, final Boolean expand) {
        CimiCredentialsCreate cimi = new CimiCredentialsCreate();
        CimiEntityBuilderHelper.fillCimiCommon(cimi, id, index);
        cimi.setCredentialsTemplate(CimiEntityBuilderHelper.buildCimiCredentialsTemplate(id, index, expand));
        return cimi;
    }

    public static CimiCredentialsTemplate buildCimiCredentialsTemplate(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiCredentialsTemplate(id, null, true);
    }

    public static CimiCredentialsTemplate buildCimiCredentialsTemplate(final Integer id, final Integer index,
        final Boolean expand) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        CimiCredentialsTemplate cimi = new CimiCredentialsTemplate();
        CimiEntityBuilderHelper.fillCimiCommonId(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setKey(CimiEntityBuilderHelper.buildBytes(id));
            cimi.setPassword("passwordValue" + postfix);
            cimi.setUserName("userNameValue" + postfix);
        }
        return cimi;
    }

    public static CimiMachineImage buildCimiMachineImage(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiMachineImage(id, null, true);
    }

    public static CimiMachineImage buildCimiMachineImage(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        CimiMachineImage cimi = new CimiMachineImage();
        CimiEntityBuilderHelper.fillCimiCommonId(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setImageLocation(new ImageLocation("hrefImageLocation" + postfix));
            cimi.setState("stateValue" + postfix);
            cimi.setType("typeValue" + postfix);
        }
        return cimi;
    }

    public static CimiMachineImageCollection buildCimiMachineImageCollection(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiMachineImageCollection(id, false);
    }

    public static CimiMachineImageCollection buildCimiMachineImageCollection(final Integer id, final Boolean expand) {
        CimiMachineImageCollection collec = new CimiMachineImageCollection();
        CimiEntityBuilderHelper.fillCimiCommonId(collec, id, null, true);

        if ((null != id) && (id > 0)) {
            List<CimiMachineImage> cimis = new ArrayList<CimiMachineImage>();
            for (int i = 0; i < id; i++) {
                cimis.add(CimiEntityBuilderHelper.buildCimiMachineImage(id, i, expand));
            }
            collec.setMachineImages(cimis.toArray(new CimiMachineImage[cimis.size()]));
        }
        return collec;
    }

    public static CimiJob buildCimiJob(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiJob(id, null, true);
    }

    public static CimiJob buildCimiJob(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        CimiJob cimi = new CimiJob();

        CimiEntityBuilderHelper.fillCimiCommonId(cimi, id, index, expand);
        if ((null != expand) && (true == expand)) {
            cimi.setAction("actionValue" + postfix);
            cimi.setIsCancellable(CimiEntityBuilderHelper.buildBoolean(id));
            cimi.setProgress(id);
            cimi.setReturnCode(id);
            cimi.setStatus("statusValue" + postfix);
            cimi.setStatusMessage("statusMessageValue" + postfix);
            cimi.setTargetEntity("targetEntityValue" + postfix);
            cimi.setTimeOfStatusChange(CimiEntityBuilderHelper.buildDate(id));

            if ((null != id) && (id > 0)) {
                cimi.setParentJob(new ParentJob("hrefParentValue" + postfix));
                List<NestedJob> nesteds = new ArrayList<NestedJob>();
                for (int i = 0; i < id; i++) {
                    nesteds.add(new NestedJob("hrefNestedValue" + postfix + CimiEntityBuilderHelper.buildPostfix(id, i)));
                }
                cimi.setNestedJobs(nesteds.toArray(new NestedJob[nesteds.size()]));
            }
        }
        return cimi;
    }

    public static CimiMemory buildCimiMemory(final Integer id, final Integer index) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        CimiMemory cimi = new CimiMemory();
        cimi.setQuantity(id);
        cimi.setUnits("unitValue" + postfix);
        return cimi;
    }

    public static CimiCapacity buildCimiCapacity(final Integer id, final Integer index) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        CimiCapacity cimi = new CimiCapacity();
        cimi.setQuantity(id);
        cimi.setUnits("unitValue" + postfix);
        return cimi;
    }

    public static CimiDisk buildCimiDisk(final Integer id, final Integer index) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        CimiDisk cimi = new CimiDisk();
        cimi.setAttachmentPoint("attachmentPointValue" + postfix);
        cimi.setCapacity(CimiEntityBuilderHelper.buildCimiCapacity(id, index));
        cimi.setFormat("formatValue" + postfix);
        return cimi;
    }

    public static CimiMachineConfiguration buildCimiMachineConfiguration(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiMachineConfiguration(id, null, true);
    }

    public static CimiMachineConfiguration buildCimiMachineConfiguration(final Integer id, final Integer index,
        final Boolean expand) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        CimiMachineConfiguration cimi = new CimiMachineConfiguration();
        CimiEntityBuilderHelper.fillCimiCommonId(cimi, id, index, expand);

        if ((null != expand) && (true == expand)) {
            cimi.setCpu(id);
            cimi.setCpuArch("cpuArchValue" + postfix);
            cimi.setMemory(CimiEntityBuilderHelper.buildCimiMemory(id, index));
            if ((null != id) && (id > 0)) {
                List<CimiDisk> cimis = new ArrayList<CimiDisk>();
                for (int i = 0; i < id; i++) {
                    cimis.add(CimiEntityBuilderHelper.buildCimiDisk(id, i));
                }
                cimi.setDisks(cimis.toArray(new CimiDisk[cimis.size()]));
            }
        }
        cimi.setHref("hrefValue" + postfix);
        return cimi;
    }

    public static CimiMachineConfigurationCollection buildCimiMachineConfigurationCollection(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiMachineConfigurationCollection(id, false);
    }

    public static CimiMachineConfigurationCollection buildCimiMachineConfigurationCollection(final Integer id,
        final Boolean expand) {
        CimiMachineConfigurationCollection collec = new CimiMachineConfigurationCollection();
        CimiEntityBuilderHelper.fillCimiCommonId(collec, id, null, true);

        if ((null != id) && (id > 0)) {
            List<CimiMachineConfiguration> cimis = new ArrayList<CimiMachineConfiguration>();
            for (int i = 0; i < id; i++) {
                cimis.add(CimiEntityBuilderHelper.buildCimiMachineConfiguration(id, i, expand));
            }
            collec.setMachineConfigurations(cimis.toArray(new CimiMachineConfiguration[cimis.size()]));
        }
        return collec;
    }

}