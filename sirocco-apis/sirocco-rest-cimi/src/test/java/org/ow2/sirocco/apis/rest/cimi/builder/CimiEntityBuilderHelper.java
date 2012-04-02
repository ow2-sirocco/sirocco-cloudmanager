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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
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

    public static void fillCimiCommon(final CimiCommon common, final Integer id, final Integer index) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        common.setCreated(CimiEntityBuilderHelper.buildDate(id));
        common.setDescription("descriptionValue" + postfix);
        common.setId("idValue" + postfix);
        common.setName("nameValue" + postfix);
        common.setProperties(CimiEntityBuilderHelper.buildProperties(id));
        common.setOperations(CimiEntityBuilderHelper.buildOperations(id));
        common.setUpdated(CimiEntityBuilderHelper.buildDate(id));
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

    public static CimiMachineImage buildCimiMachineImage(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiMachineImage(id, null, true);
    }

    public static CimiMachineImage buildCimiMachineImage(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        CimiMachineImage image = new CimiMachineImage();

        if ((null != expand) && (true == expand)) {
            CimiEntityBuilderHelper.fillCimiCommon(image, id, index);
            image.setImageLocation(new ImageLocation("hrefImageLocation" + postfix));
            image.setState("stateValue" + postfix);
            image.setType("typeValue" + postfix);
        }
        image.setHref("hrefValue" + postfix);
        return image;
    }

    public static CimiMachineImageCollection buildCimiMachineImageCollection(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiMachineImageCollection(id, false);
    }

    public static CimiMachineImageCollection buildCimiMachineImageCollection(final Integer id, final Boolean expand) {
        CimiMachineImageCollection collec = new CimiMachineImageCollection();
        CimiEntityBuilderHelper.fillCimiCommon(collec, id, null);

        if ((null != id) && (id > 0)) {
            List<CimiMachineImage> ops = new ArrayList<CimiMachineImage>();
            for (int i = 0; i < id; i++) {
                ops.add(CimiEntityBuilderHelper.buildCimiMachineImage(id, i, expand));
            }
            collec.setMachineImages(ops.toArray(new CimiMachineImage[ops.size()]));
        }
        return collec;
    }

    public static CimiJob buildCimiJob(final Integer id) {
        return CimiEntityBuilderHelper.buildCimiJob(id, null, true);
    }

    public static CimiJob buildCimiJob(final Integer id, final Integer index, final Boolean expand) {
        String postfix = CimiEntityBuilderHelper.buildPostfix(id, index);
        CimiJob job = new CimiJob();

        if ((null != expand) && (true == expand)) {
            CimiEntityBuilderHelper.fillCimiCommon(job, id, index);
            job.setAction("actionValue" + postfix);
            job.setIsCancellable(CimiEntityBuilderHelper.buildBoolean(id));
            job.setProgress(id);
            job.setReturnCode(id);
            job.setStatus("statusValue" + postfix);
            job.setStatusMessage("statusMessageValue" + postfix);
            job.setTargetEntity("targetEntityValue" + postfix);
            job.setTimeOfStatusChange(CimiEntityBuilderHelper.buildDate(id));

            if ((null != id) && (id > 0)) {
                job.setParentJob(new ParentJob("hrefParentValue" + postfix));
                List<NestedJob> nesteds = new ArrayList<NestedJob>();
                for (int i = 0; i < id; i++) {
                    nesteds.add(new NestedJob("hrefNestedValue" + postfix + CimiEntityBuilderHelper.buildPostfix(id, i)));
                }
                job.setNestedJobs(nesteds.toArray(new NestedJob[nesteds.size()]));
            }

        }
        // image.setHref("hrefValue" + postfix);
        return job;
    }

}