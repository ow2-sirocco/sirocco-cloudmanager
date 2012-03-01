package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.exception.JobException;
import org.ow2.sirocco.cloudmanager.core.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
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
    private EJBContext ctx;

    public Job createJob(String targetEntity, String action, String parentJob)
            throws JobException {

        Job j = new Job();
        j.setTargetEntity(targetEntity);
        j.setAction(action);
        j.setStatus(Status.RUNNING);

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

    /*public Job updateJob(final String jobId,
            final Map<String, Object> attributes)
            throws ResourceNotFoundException, CloudProviderException {

        Job j = this.em.find(Job.class, new Integer(jobId));
        if (j == null) {
            throw new ResourceNotFoundException("Machine " + jobId
                    + " cannot be found");
        }

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            j.s
            
            System.out.println("Key = " + entry.getKey() + ", Value = "
                    + entry.getValue());
        }

       // return j;
    }
    
    private Job updateAttribute(Job j,String Attribute,Object value)
    {
        if (Attribute.equals(""))

        return j;
    }*/

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

}
