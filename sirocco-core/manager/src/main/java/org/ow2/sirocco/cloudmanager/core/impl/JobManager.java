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

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.QueryParams;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.remote.IRemoteJobManager;
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

    public Job createJob(final CloudResource targetEntity, final String action, final Integer parentJob)
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
    public Job getJobById(final int id) throws CloudProviderException {
        Job result = this.em.find(Job.class, id);
        if (result == null) {
            throw new ResourceNotFoundException("Invalid Job id " + id);
        }
        result.getNestedJobs().size();
        result.getAffectedResources().size();
        return result;
    }

    @Override
    public Job getJobByUuid(final String uuid) throws ResourceNotFoundException, CloudProviderException {
        try {
            return this.em.createNamedQuery("Job.findByUuid", Job.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public void deleteJob(final String uuid) throws CloudProviderException {
        Job result = this.getJobByUuid(uuid);
        this.em.remove(result);
    }

    @Override
    public Job getJobAttributes(final String id, final List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException {
        Job job = this.getJobByUuid(id);
        return UtilsForManagers.fillResourceAttributes(job, attributes);
    }

    @Override
    public QueryResult<Job> getJobs(final QueryParams... queryParams) throws CloudProviderException {
        if (queryParams.length == 0) {
            @SuppressWarnings("unchecked")
            List<Job> jobs = this.em.createQuery("SELECT j FROM Job j WHERE j.tenant.id=:tenantId")
                .setParameter("tenantId", this.getTenant().getId()).getResultList();
            return new QueryResult<Job>(jobs.size(), jobs);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Job", Job.class)
            .tenantId(this.getTenant().getId()).params(queryParams[0]);
        return QueryHelper.getEntityList(this.em, params);
    }

    @Override
    public QueryResult<Job> getJobs(final int first, final int last, final List<String> filters, final List<String> attributes)
        throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Job", Job.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes));
    }

}
