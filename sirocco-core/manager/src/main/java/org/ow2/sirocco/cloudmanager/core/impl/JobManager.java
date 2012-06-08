package org.ow2.sirocco.cloudmanager.core.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.System;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;

@Stateless
@Remote(IRemoteJobManager.class)
@Local(IJobManager.class)
@SuppressWarnings("unused")
public class JobManager implements IJobManager {

    private static Logger logger = Logger.getLogger(JobManager.class);

    public static long DEFAULT_JOB_SCHEDULER_PERIOD_IN_SECONDS = 20;

    public long jobLockTimeoutInSeconds = 600;

    public long lockWaitTimeInSeconds = 1;

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

    @Resource
    private EJBContext ctx;
    
    @Resource 
    private SessionContext sessionContext;

    public Job createJob(final CloudResource targetEntity, final String action,
            final String parentJob) throws CloudProviderException {

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
    public JobCollection getJobCollection() throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JobCollection updateJobCollection(final JobCollection jobColl)
            throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteJob(final String id) throws CloudProviderException {
        Job result = this.getJobById(id);

        if (result != null) {
            this.em.remove(result);
        }

    }

    @Override
    public Job getJobAttributes(final String id, final List<String> attributes)
            throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Job> getJobs(final int first, final int last,
            final List<String> attributes) throws InvalidRequestException,
            CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Job> getJobs(final List<String> attributes,
            final String filterExpression) throws InvalidRequestException,
            CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateJobAttributes(final String id,
            final Map<String, Object> updatedAttributes)
            throws ResourceNotFoundException, InvalidRequestException,
            CloudProviderException {
        // TODO Auto-generated method stub
    }
    
    /**
     * to call internal methods by passing by the proxy.<br>
     * It is used to take care of transaction annotations on methods<br>
     * because if EJB method is called with <i>this</i>, it is not taken<br>
     * into account
     * @return
     */
    private IJobManager getThis(){
        return sessionContext.getBusinessObject(IJobManager.class);
    }
    
    /**
     * update entity job with a pojo from provider layer
     * works in its own transaction
     * @param providerJob
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Job updateProviderJob(Job providerJob){
        Job job = null;
        // update Job entity
        try {
            job = (Job) this.em
                    .createQuery(
                            "SELECT j FROM Job j WHERE j.providerAssignedId=:providerAssignedId")
                    .setParameter("providerAssignedId",
                            providerJob.getProviderAssignedId())
                    .getSingleResult();
            job.setStatus(providerJob.getStatus());
            job.setStatusMessage(providerJob.getStatusMessage());
            job.setReturnCode(providerJob.getReturnCode());
            job.setTimeOfStatusChange(new Date());
        } catch (NoResultException e) {
            // should not happen
            JobManager.logger.error("Cannot find job with providerAssignedId "
                    + providerJob.getProviderAssignedId());
            throw e;
        }
        return job;
    }

    @Override
    public void handleWorkflowEvent(Job providerJob) throws Exception {
        // TODO: persist the job and call handleWorkflowEvent(String jobId)

        Job job = getThis().updateProviderJob(providerJob);


        // attemting to obtain a lock on the topmost job
        Job topmost = this.getTopmostJob(job.getId().toString());
        String lockId = "";

        try {
            lockId = getThis().lock(topmost.getId().toString());
        } catch (Exception e) {
            JobManager.logger.error("Unable to lock Job " + topmost.getId());
            throw new CloudProviderException("Unable to lock Job " + topmost.getId());
        }

        // dispatch event to related managers and parent jobs
        while (job != null) {
            // find manager
            CloudResource target = job.getTargetEntity();
            try {

                if (target instanceof Machine) {
                    JobManager.logger.info("calling  machineManager jobCompletionHandler with Job "+job.getId().toString());
                    this.machineManager.jobCompletionHandler(job.getId().toString());
                }
                if (target instanceof MachineImage) {
                    // this.machineImageManager.jobCompletionHandler(job);
                }
                if ((target instanceof Volume)
                        || (target instanceof VolumeImage)) {
                    this.volumeManager.jobCompletionHandler(job.getId().toString());
                }
                if (target instanceof System) {
                    JobManager.logger.info("calling  systemManager jobCompletionHandler with Job "+job.getId().toString());
                    this.systemManager.jobCompletionHandler(job.getId().toString());
                }
                if ((target instanceof Network)
                        || (target instanceof NetworkPort)
                        || (target instanceof ForwardingGroup)) {
                    this.networkManager.jobCompletionHandler(job);
                }
            } catch (Exception e) {
                JobManager.logger.error("Exception in handling event "
                        + e.getMessage());
            }
            // find parent
            job = job.getParentJob();
        }
        
        //we unlock the topmost job after work
        try {
            getThis().unlock(topmost.getId().toString(),lockId);
            
        } catch (Exception e) {
            JobManager.logger.error("Unable to unlock Job " + topmost.getId());
        }

    }

    /**
     * used to lock a Job works in its own transaction
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String lock(String jobId) throws Exception {
        JobManager.logger.info("locking job"+jobId);
        Job j = this.getJobById(jobId);

        // a lock expires after 1 hour
        Date lockedDate = j.getLockedTime();
        if (lockedDate == null) {
            lockedDate = new Date();
        }
        long lockedTime = lockedDate.getTime() + jobLockTimeoutInSeconds * 1000;
        long currentTime = new Date().getTime();

        if ((j.getLocked()) && (currentTime < lockedTime)) {
            throw new CloudProviderException("unable to lock job " + jobId);
        }
        // not locked yet
        this.em.lock(j, LockModeType.WRITE);
        j.setLocked(true);
        j.setLockedTime(new Date());
        String llockedID = UUID.randomUUID().toString();
        j.setLockedID(llockedID);
        
        Thread.sleep(lockWaitTimeInSeconds * 1000);// development environment
        // for now
        
        JobManager.logger.info("locked job"+jobId+" with lockedID "+llockedID);
        return llockedID;
    }

    /**
     * used to unlock a Job works in its own transaction
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void unlock(String jobId, String lockedID) throws Exception {

        JobManager.logger.info("unlocking job"+jobId+" with lockedID "+lockedID);
        Job j = this.getJobById(jobId);
        String jobLockedID = j.getLockedID();
        if (jobLockedID == null) {
            jobLockedID = "";
        }

        // if the job is locked and the lockedID is correct
        // we unlock it, else we throw an exception
        if ((j.getLocked()) && (jobLockedID.equals(lockedID))) {
            this.em.lock(j, LockModeType.WRITE);
            j.setLocked(false);
            j.setLockedID(null);
            j.setLockedTime(null);
        } else {
            if (!j.getLocked()) {
                throw new CloudProviderException("unable to unlock job "
                        + jobId + " because it is not locked");
            }
            if (!jobLockedID.equals(lockedID)) {
                throw new CloudProviderException("unable to unlock job "
                        + jobId + " because its lockedID is bad");
            }
            throw new CloudProviderException("unable to unlock job " + jobId);

        }
        
        Thread.sleep(lockWaitTimeInSeconds * 1000);// development environment
        // for now
        JobManager.logger.info("unlocked job"+jobId);

    }

    /**
     * find the root job in the job tree
     */
    @Override
    public Job getTopmostJob(String jobId) throws CloudProviderException {

        Job j = this.getJobById(jobId);

        Job topmost = j;
        while (topmost.getParentJob() != null) {
            topmost = topmost.getParentJob();
        }

        return topmost;
    }

    /*
     * @Override public void sendJobNotification(String jobId, long
     * emissionDelayInMillis) throws CloudProviderException {
     * 
     * //get job Job job=this.getJobById(jobId);
     * 
     * asynchManager.sendJobNotification(jobId, emissionDelayInMillis); //queue
     * = Queue.create(0, "schedulerQ", org.ob
     * .joram.client.jms.Queue.SCHEDULER_QUEUE, null);
     * 
     * // org.ow2.sirocco.cloudmanager.connector.impl.
     * .util.jobmanager.impl.JobManager
     * .rawEmitDelayedQueueMessage((Serializable)
     * job,emissionDelayInMillis,JobNotificationHandlerBean
     * .JMS_QUEUE_CONNECTION_FACTORY_NAME
     * ,JobNotificationHandlerBean.JMS_QUEUE_NAME);
     * 
     * 
     * JobManager.logger.info("EMITTED sendJobNotification EVENT for Job " +
     * job.getId());
     * 
     * //} catch (Exception e) { // throw new
     * CloudProviderException("sendJobNotification JMS error");
     * 
     * //}
     * 
     * }
     */

    /*
     * public void sendJobNotification(String jobId, long emissionDelayInMillis)
     * throws CloudProviderException {
     * 
     * try { // get job Job job = this.getJobById(jobId);
     * 
     * Thread.sleep(emissionDelayInMillis);
     * 
     * QueueConnectionFactory qcf = (QueueConnectionFactory) ctx
     * .lookup(JobNotificationHandlerBean.JMS_QUEUE_CONNECTION_FACTORY_NAME);
     * QueueConnection queueCon = qcf.createQueueConnection(); QueueSession
     * queueSession = queueCon.createQueueSession(false,
     * Session.AUTO_ACKNOWLEDGE); Queue queue = (Queue) ctx
     * .lookup(JobNotificationHandlerBean.JMS_QUEUE_NAME); QueueSender sender =
     * queueSession.createSender(queue); Message msg =
     * queueSession.createObjectMessage(job); //
     * msg.setLongProperty("scheduleDate", System.currentTimeMillis() + //
     * delayMilli);
     * 
     * sender.send(msg); JobManager.logger .info("EMITTED EVENT for payload " +
     * job.toString());
     * 
     * } catch (JMSException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } catch (InterruptedException e) { // TODO
     * Auto-generated catch block e.printStackTrace(); }
     * 
     * }
     */

}
