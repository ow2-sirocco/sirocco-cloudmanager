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
package org.ow2.sirocco.apis.rest.cimi.converter;

import java.util.ArrayList;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.NestedJob;
import org.ow2.sirocco.apis.rest.cimi.domain.ParentJob;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiJob}</li>
 * <li>Service model: {@link Job}</li>
 * </ul>
 * </p>
 */
public class JobConverter extends CommonIdConverter implements EntityConverter {
    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        CimiJob cimi = new CimiJob();
        this.copyToCimi(context, dataService, cimi);
        return cimi;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#copyToCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void copyToCimi(final CimiContext context, final Object dataService, final Object dataCimi) {
        this.doCopyToCimi(context, (Job) dataService, (CimiJob) dataCimi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        Job service = new Job();
        this.copyToService(context, dataCimi, service);
        return service;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter#copyToService
     *      (org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void copyToService(final CimiContext context, final Object dataCimi, final Object dataService) {
        this.doCopyToService(context, (CimiJob) dataCimi, (Job) dataService);
    }

    /**
     * Copy data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void doCopyToCimi(final CimiContext context, final Job dataService, final CimiJob dataCimi) {
        this.fill(context, dataService, dataCimi);
        if (true == context.mustBeExpanded(dataCimi)) {
            dataCimi.setAction(dataService.getAction());
            dataCimi.setIsCancellable(dataService.getIsCancellable());
            dataCimi.setProgress(dataService.getProgress());
            dataCimi.setReturnCode(dataService.getReturnCode());
            if (null != dataService.getStatus()) {
                dataCimi.setStatus(dataService.getStatus().toString());
            }
            dataCimi.setStatusMessage(dataService.getStatusMessage());
            if (null != dataService.getTargetEntity()) {
                dataCimi.setTargetEntity(this.makeHrefTargetEntity(context, dataService.getTargetEntity()));
            }
            dataCimi.setTimeOfStatusChange(dataService.getTimeOfStatusChange());

            if (null != dataService.getParentJob()) {
                dataCimi.setParentJob(new ParentJob(context.makeHref(dataCimi, dataService.getParentJob().getId())));
            }
            if ((null != dataService.getNestedJobs()) && (dataService.getNestedJobs().size() > 0)) {
                List<NestedJob> list = new ArrayList<NestedJob>();
                for (Job job : dataService.getNestedJobs()) {
                    list.add(new NestedJob(context.makeHref(dataCimi, job.getId())));
                }
                dataCimi.setNestedJobs(list.toArray(new NestedJob[list.size()]));
            }
        }
    }

    /**
     * Copy data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination Service object
     */
    protected void doCopyToService(final CimiContext context, final CimiJob dataCimi, final Job dataService) {
        this.fill(context, dataCimi, dataService);
    }

    protected String makeHrefTargetEntity(final CimiContext context, final Object targetDataService) {
        String href = null;
        CimiEntityType targetType = this.findType(targetDataService);

        href = context.makeHref(targetType, this.getTargetId(targetDataService));
        return href;
    }

    protected Integer getTargetId(final Object targetDataService) {
        Integer id = null;
        if (true == CloudResource.class.isAssignableFrom(targetDataService.getClass())) {
            id = ((CloudResource) targetDataService).getId();
        } else {
            id = ((CloudEntity) targetDataService).getId();
        }
        return id;
    }

    protected CimiEntityType findType(final Object targetDataService) {
        CimiEntityType type = null;
        // TODO Complete with all service entities
        if (targetDataService instanceof CloudEntryPoint) {
            type = CimiEntityType.CloudEntryPoint;
        } else if (targetDataService instanceof Credentials) {
            type = CimiEntityType.Credentials;
        } else if (targetDataService instanceof CredentialsTemplate) {
            type = CimiEntityType.CredentialsTemplate;
        } else if (targetDataService instanceof CredentialsCollection) {
            type = CimiEntityType.CredentialsCollection;
        } else if (targetDataService instanceof Job) {
            type = CimiEntityType.Job;
        } else if (targetDataService instanceof JobCollection) {
            type = CimiEntityType.JobCollection;
        } else if (targetDataService instanceof Machine) {
            type = CimiEntityType.Machine;
        } else if (targetDataService instanceof MachineTemplate) {
            type = CimiEntityType.MachineTemplate;
        } else if (targetDataService instanceof MachineCollection) {
            type = CimiEntityType.MachineCollection;
        } else if (targetDataService instanceof MachineConfiguration) {
            type = CimiEntityType.MachineConfiguration;
        } else if (targetDataService instanceof MachineConfigurationCollection) {
            type = CimiEntityType.MachineConfigurationCollection;
        } else if (targetDataService instanceof MachineImage) {
            type = CimiEntityType.MachineImage;
        } else if (targetDataService instanceof MachineImageCollection) {
            type = CimiEntityType.MachineImageCollection;
        } else {
            throw new InvalidConversionException("Job conversion : target type not found");
        }
        return type;
    }

}
