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
 *  $Id: JobManager.java 1296 2012-06-11 15:34:26Z ycas7461 $
 *
 */

package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.utils.QueryHelper;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteJobManager.class)
@Local(IJobManager.class)
@SuppressWarnings("unused")
@IdentityInterceptorBinding
public class JobManager implements IJobManager {

    private static Logger logger = LoggerFactory.getLogger(JobManager.class);

    public static long DEFAULT_JOB_SCHEDULER_PERIOD_IN_SECONDS = 20;

    public long jobLockTimeoutInSeconds = 600;

    public long lockWaitTimeInSeconds = 0;

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private IVolumeManager volumeManager;

    @EJB
    private IMachineManager machineManager;

    @EJB
    private IMachineImageManager machineImageManager;

    @EJB
    private ISystemManager systemManager;

    @EJB
    private INetworkManager networkManager;

    @EJB
    private ITenantManager tenantManager;

    @Inject
    private IdentityContext identityContext;

    @Resource
    private SessionContext sessionContext;

    private Tenant getTenant() throws CloudProviderException {
        return this.tenantManager.getTenant(this.identityContext);
    }

    public Job createJob(final CloudResource targetEntity, final String action, final String parentJob)
        throws CloudProviderException {

        Job j = new Job();
        j.setTargetResource(targetEntity);
        j.setAction(action);
        j.setState(Status.RUNNING);

        if (parentJob != null) {
            Job parent = this.getJobById(parentJob);
            if (parent == null) {
                throw new CloudProviderException();
            } else {
                parent.addNestedJob(j);
            }

        }

        this.em.persist(j);

        return j;
    }

    @Override
    public Job getJobById(final String id) throws CloudProviderException {

        Job result = this.em.find(Job.class, new Integer(id));
        if (result == null) {
            throw new ResourceNotFoundException("Invalid Job id " + id);
        }
        result.getNestedJobs().size();
        result.getAffectedResources().size();
        return result;
    }

    @Override
    public void updateJob(final Job job) throws CloudProviderException {

        Integer jobId = job.getId();
        this.em.merge(job);

    }

    /*
     * public Job updateJob(final String jobId, final Map<String, Object>
     * attributes) throws ResourceNotFoundException, CloudProviderException {
     * Job j = this.em.find(Job.class, new Integer(jobId)); if (j == null) {
     * throw new ResourceNotFoundException("Machine " + jobId +
     * " cannot be found"); } for (Map.Entry<String, Object> entry :
     * attributes.entrySet()) { j.s System.out.println("Key = " + entry.getKey()
     * + ", Value = " + entry.getValue()); } // return j; } private Job
     * updateAttribute(Job j,String Attribute,Object value) { if
     * (Attribute.equals("")) return j; }
     */

    @Override
    public void deleteJob(final String id) throws CloudProviderException {
        Job result = this.getJobById(id);

        if (result != null) {
            this.em.remove(result);
        }

    }

    @Override
    public Job getJobAttributes(final String id, final List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException {
        Job job = this.getJobById(id);
        return UtilsForManagers.fillResourceAttributes(job, attributes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Job> getJobs() throws CloudProviderException {
        return this.em.createQuery("SELECT j FROM Job j WHERE j.tenant.id=:tenantId")
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @Override
    public QueryResult<Job> getJobs(final int first, final int last, final List<String> filters, final List<String> attributes)
        throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Job", Job.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes));
    }

    @Override
    public void updateJobAttributes(final String id, final Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    /**
     * to call internal methods by passing by the proxy.<br>
     * It is used to take care of transaction annotations on methods<br>
     * because if EJB method is called with <i>this</i>, it is not taken<br>
     * into account
     * 
     * @return
     */
    private IJobManager getThis() {
        return this.sessionContext.getBusinessObject(IJobManager.class);
    }

    /**
     * update entity job with a pojo from provider layer
     * 
     * @param providerJob
     * @return
     */
    private Job updateProviderJob(final Job providerJob) {
        Job job = null;
        // update Job entity
        try {
            job = (Job) this.em.createQuery("SELECT j FROM Job j WHERE j.providerAssignedId=:providerAssignedId")
                .setParameter("providerAssignedId", providerJob.getProviderAssignedId()).getSingleResult();
            job.setState(providerJob.getState());
            job.setStatusMessage(providerJob.getStatusMessage());
            job.setReturnCode(providerJob.getReturnCode());
            job.setTimeOfStatusChange(new Date());
        } catch (NoResultException e) {
            // should not happen
            JobManager.logger.error("Cannot find job with providerAssignedId " + providerJob.getProviderAssignedId());
            throw e;
        }
        return job;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String getJobIdFromProvider(final Job providerJob) throws NoResultException {
        Job job = null;
        if (providerJob == null) {
            JobManager.logger.warn("providerJob is null");
        }
        if (providerJob.getProviderAssignedId() == null) {
            JobManager.logger.warn("providerJob ProviderAssignedId is null");
        }
        JobManager.logger.debug(" getting persisted job from provider job of providerAssignedId "
            + providerJob.getProviderAssignedId());
        try {
            job = (Job) this.em.createQuery("SELECT j FROM Job j WHERE j.providerAssignedId=:providerAssignedId")
                .setParameter("providerAssignedId", providerJob.getProviderAssignedId()).getSingleResult();
        } catch (NoResultException e) {
            // should not happen
            JobManager.logger.error("Cannot find job with providerAssignedId " + providerJob.getProviderAssignedId());
            throw e;
        }
        if (job == null) {
            JobManager.logger.warn("job is null");
        }

        JobManager.logger.debug(" got persisted job " + job.getId());
        return job.getId().toString();
    }

    /**
     * find the root job in the job tree
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String getTopmostJobId(final String jobId) throws CloudProviderException {

        Job j = this.getJobById(jobId);

        Job topmost = j;
        while (topmost.getParentJob() != null) {
            topmost = topmost.getParentJob();
        }

        return topmost.getId().toString();
    }

}
