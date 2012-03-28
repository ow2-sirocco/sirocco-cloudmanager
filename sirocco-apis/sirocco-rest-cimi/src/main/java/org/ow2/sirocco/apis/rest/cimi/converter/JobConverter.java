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
 * $Id: MachineImageConverter.java 1096 2012-03-09 08:08:25Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.converter;

import java.util.ArrayList;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.NestedJob;
import org.ow2.sirocco.apis.rest.cimi.domain.ParentJob;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;

/**
 * Helper class to convert the data of the CIMI model and the service model in
 * both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiJob}</li>
 * <li>Service model: {@link Job}</li>
 * </ul>
 * </p>
 */
public class JobConverter {

    /**
     * Copy the data from the service object in the CIMI object.
     * 
     * @param dataService An instance of {@link MachineImage}
     * @param dataCimi An instance of {@link CimiMachineImage}
     * @param urlBase The URL base
     */
    public static void copyToCimi(final Job dataService, final CimiJob dataCimi, final String urlBase, final boolean expand) {
        if (true == expand) {
            CommonConverter.copyToCimi(dataService, dataCimi, urlBase, ConstantsPath.JOB);

            dataCimi.setAction(dataService.getAction());
            dataCimi.setIsCancellable(dataService.getIsCancellable());
            dataCimi.setProgress(dataService.getProgress());
            dataCimi.setReturnCode(dataService.getReturnCode());
            dataCimi.setStatus(dataService.getStatus().toString());
            dataCimi.setStatusMessage(dataService.getStatusMessage());
            dataCimi.setTargetEntity(dataService.getTargetEntity());
            dataCimi.setTimeOfStatusChange(dataService.getTimeOfStatusChange());

            if (null != dataService.getParentJob()) {
                dataCimi.setParentJob(new ParentJob(HrefHelper.makeHref(urlBase, ConstantsPath.JOB, dataService.getParentJob()
                    .getId())));
            }
            if (null != dataService.getNestedJobs()) {
                List<NestedJob> list = new ArrayList<NestedJob>();
                for (Job job : dataService.getNestedJobs()) {
                    list.add(new NestedJob(HrefHelper.makeHref(urlBase, ConstantsPath.JOB, job.getId())));
                }
                dataCimi.setNestedJobs(list.toArray(new NestedJob[list.size()]));
            }
        }
    }

    /**
     * Copy the data from the CIMI object in the service object.
     * 
     * @param dataCimi An instance of {@link CimiMachineImage}
     * @param dataService An instance of {@link MachineImage}
     */
    public static void copyToService(final CimiMachineImage dataCimi, final MachineImage dataService) {
        CommonConverter.copyToService(dataCimi, dataService);
        // Nothing to do
    }

}
