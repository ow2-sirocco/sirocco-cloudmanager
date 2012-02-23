package org.ow2.sirocco.cloudmanager.connector.util.jobmanager.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.api.IJobManager;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.exception.JobException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.User;

@Stateless(name = IJobManager.EJB_JNDI_NAME, mappedName = IJobManager.EJB_JNDI_NAME)
@Remote(IRemoteJobManager.class)
@Local(IJobManager.class)
@SuppressWarnings("unused")
public class JobManager implements IJobManager {

    private static Logger logger = Logger.getLogger(JobManager.class.getName());
    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Resource
    private SessionContext ctx;

    @Override
    public Job createJob(String targetEntity, String action, String parentJob)
            throws JobException {

        Job j = new Job();
        j.setTargetEntity(targetEntity);
        j.setAction(action);

        if (parentJob != null) {
            Job parent = this.getJobById(parentJob);
            if (parent == null) {
                throw new JobException();
            } else {
                j.setParentJob(parent);
            }

        }

        this.em.persist(j);

        return j;
    }

    @Override
    public Job getJobById(String id) throws JobException {

        Job result = this.em.find(Job.class, new Integer(id));
        return result;
    }

    @Override
    public Job updateJob(Job job) throws JobException {

        Integer jobId = job.getId();
        this.em.merge(job);

        return this.getJobById(jobId.toString());
    }

    @Override
    public JobCollection getJobCollection() throws JobException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JobCollection updateJobCollection(JobCollection jobColl)
            throws JobException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteJob(String id) throws JobException {
        Job result = this.getJobById(id);

        if (result != null) {
            this.em.remove(result);
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Job> getNestedJobs(String id) throws JobException {

        List<Job> l = this.em
                .createQuery("FROM Job t WHERE t.parentJob_id=:id")
                .setParameter("id", id).getResultList();
        return l;

    }

}
