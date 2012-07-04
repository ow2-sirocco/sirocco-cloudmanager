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
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.ILockManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;

@Stateless
@Remote(IRemoteJobManager.class)
@Local(IJobManager.class)
@SuppressWarnings("unused")
public class JobManager implements IJobManager {

    private static Logger logger = Logger.getLogger(JobManager.class);

    public static long DEFAULT_JOB_SCHEDULER_PERIOD_IN_SECONDS = 20;

    public long jobLockTimeoutInSeconds = 600;

    public long lockWaitTimeInSeconds = 0;

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
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
    private ILockManager lockManager;

    @EJB
    private IUserManager userManager;

    @Resource
    private EJBContext ctx;

    @Resource
    private SessionContext sessionContext;

    private User getUser() throws CloudProviderException {
        String username = this.ctx.getCallerPrincipal().getName();
        return this.userManager.getUserByUsername(username);
    }

    public Job createJob(final CloudResource targetEntity, final String action, final String parentJob)
        throws CloudProviderException {

        Job j = new Job();
        j.setTargetEntity(targetEntity);
        j.setAction(action);
        j.setStatus(Status.RUNNING);

        if (parentJob != null) {
            Job parent = this.getJobById(parentJob);
            if (parent == null) {
                throw new CloudProviderException();
            } else {
                j.setParentJob(parent);
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
        result.getAffectedEntities().size();
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
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Job> getJobs() throws CloudProviderException {
        return this.em.createQuery("SELECT j FROM Job j WHERE j.user.id=:userid")
            .setParameter("userid", this.getUser().getId()).getResultList();
    }

    @Override
    public QueryResult<Job> getJobs(final int first, final int last, final List<String> filters, final List<String> attributes)
        throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("Job", this.em, user.getUsername(), first, last, filters, attributes, false);
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
            job.setStatus(providerJob.getStatus());
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
        JobManager.logger.info(" getting persisted job from provider job of providerAssignedId "
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

        JobManager.logger.info(" got persisted job " + job.getId());
        return job.getId().toString();
    }

    @Override
    public void handleWorkflowEvent(final Job providerJob) throws Exception {

        // attemting to obtain a lock on the topmost job
        String topmostid = this.getThis().getTopmostJobId(this.getThis().getJobIdFromProvider(providerJob));
        String lockId = "";

        try {
            this.lockManager.lock(topmostid, Job.class.getCanonicalName());
        } catch (CloudProviderException e) {
            JobManager.logger.warn("Unable to lock Job " + topmostid + " - " + e.getMessage());
            throw e;
        }

        try {
            Job job = this.updateProviderJob(providerJob);

            Job topmost = this.getJobById(topmostid);

            // dispatch event to related managers and parent jobs
            while (job != null) {
                // find manager
                CloudResource target = job.getTargetEntity();

                if (target instanceof Machine) {
                    JobManager.logger.info("calling  machineManager jobCompletionHandler with Job " + job.getId().toString());
                    this.machineManager.jobCompletionHandler(job.getId().toString());
                }
                if (target instanceof MachineImage) {
                    // this.machineImageManager.jobCompletionHandler(job);
                }
                if ((target instanceof Volume) || (target instanceof VolumeImage)) {
                    this.volumeManager.jobCompletionHandler(job.getId().toString());
                }
                if (target instanceof System) {
                    JobManager.logger.info("calling  systemManager jobCompletionHandler with Job " + job.getId().toString());
                    this.systemManager.jobCompletionHandler(job.getId().toString());
                }
                if ((target instanceof Network) || (target instanceof NetworkPort) || (target instanceof ForwardingGroup)) {
                    this.networkManager.jobCompletionHandler(job);
                }

                // find parent
                job = job.getParentJob();
            }

            // no exception: unlocking in current transaction
            try {
                this.lockManager.unlock(topmostid, Job.class.getCanonicalName());
            } catch (CloudProviderException e) {
                // if an exception occurs for unlocking, don't rollback!
            }

        } catch (Exception e) {
            // exception, will be rollbacked: unlocking in separate transaction
            // to ensure unlocking is done
            this.lockManager.unlockUntransacted(topmostid, Job.class.getCanonicalName());
            throw e;
        }

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
