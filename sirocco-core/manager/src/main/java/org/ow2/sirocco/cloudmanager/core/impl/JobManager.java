package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;

@Stateless(name = IJobManager.EJB_JNDI_NAME, mappedName = IJobManager.EJB_JNDI_NAME)
@Remote(IRemoteJobManager.class)
@Local(IJobManager.class)
@SuppressWarnings("unused")
public class JobManager implements IJobManager {

    private static Logger logger = Logger.getLogger(JobManager.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Resource
    private EJBContext ctx;

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
    public JobCollection updateJobCollection(final JobCollection jobColl) throws CloudProviderException {
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
    public Job getJobAttributes(final String id, final List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Job> getJobs(final int first, final int last, final List<String> attributes) throws InvalidRequestException,
        CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Job> getJobs(final List<String> attributes, final String filterExpression) throws InvalidRequestException,
        CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateJobAttributes(final String id, final Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @SuppressWarnings("unchecked")
    public CloudEntity getCloudEntityById(final String cloudEntityId) {
        Query query = this.em.createQuery("SELECT c FROM CloudEntity c WHERE c.id=:ceid");
        List<CloudEntity> templates = query.setParameter("ceid", cloudEntityId).getResultList();

        return templates.get(0);

    }

}
