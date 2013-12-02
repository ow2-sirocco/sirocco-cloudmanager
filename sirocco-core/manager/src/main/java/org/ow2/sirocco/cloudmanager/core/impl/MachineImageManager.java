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
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.ResourceStateChangeEvent;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.remote.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.impl.command.MachineImageDeleteCommand;
import org.ow2.sirocco.cloudmanager.core.utils.QueryHelper;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.ProviderMapping;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteMachineImageManager.class)
@Local(IMachineImageManager.class)
@SuppressWarnings("unused")
public class MachineImageManager implements IMachineImageManager {

    private static Logger logger = LoggerFactory.getLogger(MachineImageManager.class.getName());

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private ITenantManager tenantManager;

    @EJB
    private IJobManager jobManager;

    @EJB
    private ICloudProviderManager cloudProviderManager;

    @Inject
    private IdentityContext identityContext;

    @Resource(lookup = "jms/ResourceStateChangeTopic")
    private Topic resourceStateChangeTopic;

    @Resource(lookup = "jms/RequestQueue")
    private Queue requestQueue;

    @Inject
    private JMSContext jmsContext;

    private Tenant getTenant() throws CloudProviderException {
        return this.tenantManager.getTenant(this.identityContext);
    }

    public Job createMachineImage(final MachineImage mi) throws CloudProviderException {
        // TODO : check whether imageLocation points to a Machine
        mi.setType(MachineImage.Type.IMAGE);

        mi.setTenant(this.getTenant());
        mi.setCreated(new Date());
        mi.setState(MachineImage.State.AVAILABLE);

        if (mi.getProperties() != null) {
            String providerAccountId = mi.getProperties().get("providerAccountId");
            String providerAssignedId = mi.getProperties().get("providerAssignedId");
            if (providerAccountId != null && providerAssignedId != null) {
                CloudProviderAccount account = this.cloudProviderManager.getCloudProviderAccountByUuid(providerAccountId);
                if (account == null) {
                    throw new CloudProviderException("Invalid provider account id: " + providerAccountId);
                }
                ProviderMapping providerMapping = new ProviderMapping();
                providerMapping.setProviderAssignedId(providerAssignedId);
                providerMapping.setProviderAccount(account);
                providerMapping.setProviderLocation(account.getCloudProvider().getCloudProviderLocations().iterator().next());
                mi.setProviderMappings(Collections.singletonList(providerMapping));
            }
        }

        this.em.persist(mi);
        this.em.flush();

        Job j = new Job();
        j.setCreated(new Date());
        j.setDescription("MachineImage creation");
        j.setTargetResource(mi);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(mi);
        j.setAffectedResources(affectedResources);
        j.setState(Job.Status.SUCCESS);
        j.setAction("add");
        j.setParentJob(null);
        j.setNestedJobs(null);
        j.setReturnCode(0);
        j.setTenant(this.getTenant());
        this.em.persist(j);
        this.em.flush();
        return j;
    }

    @Override
    public List<MachineImage> getMachineImages() throws CloudProviderException {
        return QueryHelper.getEntityList("MachineImage", this.em, this.getTenant().getId(), MachineImage.State.DELETED, true);
    }

    public MachineImage getMachineImageById(final int imageId) throws CloudProviderException {
        MachineImage image = null;
        image = this.em.find(MachineImage.class, imageId);
        if (image == null || image.getState() == State.DELETED) {
            throw new ResourceNotFoundException();
        }
        return image;
    }

    @Override
    public MachineImage getMachineImageByUuid(final String uuid) throws ResourceNotFoundException, CloudProviderException {
        try {
            return this.em.createNamedQuery("MachineImage.findByUuid", MachineImage.class).setParameter("uuid", uuid)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public MachineImage getMachineImageAttributes(final String imageId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        MachineImage machineImage = this.getMachineImageByUuid(imageId);
        return UtilsForManagers.fillResourceAttributes(machineImage, attributes);
    }

    public Job deleteMachineImage(final String imageUuid) throws CloudProviderException, ResourceNotFoundException {
        MachineImage image = this.getMachineImageByUuid(imageUuid);

        // if related image is not null then do not permit deletion
        if (image.getRelatedImage() != null) {
            throw new ResourceConflictException("Related images exist for this image" + imageUuid);
        }
        // if a machine template references this image do not permit deletion
        List<MachineTemplate> templates = this.em
            .createQuery("SELECT  t FROM MachineTemplate t WHERE t.machineImage.uuid=:mid", MachineTemplate.class)
            .setParameter("mid", imageUuid).getResultList();
        if (!templates.isEmpty()) {
            throw new ResourceConflictException("Image used by MachineTemplate");
        }

        // if a machine references this image do not permit deletion
        List<Machine> machines = this.em.createQuery("SELECT  m FROM Machine m WHERE m.image.uuid=:uuid", Machine.class)
            .setParameter("uuid", imageUuid).getResultList();
        if (!machines.isEmpty()) {
            throw new ResourceConflictException("Image used by Machine");
        }

        image.setState(MachineImage.State.DELETING);
        this.fireMachineImageStateChangeEvent(image);

        // creating Job
        Job job = new Job();
        job.setTenant(this.getTenant());
        job.setTargetResource(image);
        job.setCreated(new Date());
        job.setDescription("MachineImage deletion");
        job.setState(Job.Status.RUNNING);
        job.setAction("delete");
        job.setTimeOfStatusChange(new Date());
        this.em.persist(job);
        this.em.flush();

        ObjectMessage message = this.jmsContext.createObjectMessage(new MachineImageDeleteCommand()
            .setResourceId(image.getId()).setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    @Override
    public QueryResult<MachineImage> getMachineImages(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("MachineImage", MachineImage.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .stateToIgnore(MachineImage.State.DELETED).returnPublicEntities());
    }

    @Override
    public void updateMachineImage(final MachineImage machineImage) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        // TODO Auto-generated method stub

    }

    public void updateMachineImageAttributes(final String imageId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {

        MachineImage image = this.getMachineImageByUuid(imageId);
        if (attributes.containsKey("name")) {
            image.setName((String) attributes.get("name"));
        }
        if (attributes.containsKey("description")) {
            image.setDescription((String) attributes.get("description"));
        }
        if (attributes.containsKey("properties")) {
            image.setProperties((Map<String, String>) attributes.get("properties"));
        }
        if (attributes.containsKey("imageLocation")) {
            image.setImageLocation((String) attributes.get("imageLocation"));
        }
        image.setUpdated(new Date());
        this.em.merge(image);
        this.em.flush();
    }

    @Override
    public void updateMachineImageState(final int imageId, final State state) {
        MachineImage image = this.em.find(MachineImage.class, imageId);
        image.setState(state);
        this.fireMachineImageStateChangeEvent(image);
    }

    @Override
    public void syncMachineImage(final int imageId, final MachineImage updatedImage, final int jobId) {
        MachineImage image = this.em.find(MachineImage.class, imageId);
        Job job = this.em.find(Job.class, jobId);
        if (updatedImage == null) {
            image.setState(MachineImage.State.DELETED);
        } else {
            image.setState(updatedImage.getState());
            if (image.getCreated() == null) {
                image.setCreated(new Date());
            }
            image.setUpdated(new Date());
        }
        if (image.getState() == MachineImage.State.DELETED) {
            image.setDeleted(new Date());
        }
        this.fireMachineImageStateChangeEvent(image);
        job.setState(Job.Status.SUCCESS);
    }

    private void fireMachineImageStateChangeEvent(final MachineImage image) {
        this.jmsContext.createProducer().setProperty("tenantId", image.getTenant().getUuid())
            .send(this.resourceStateChangeTopic, new ResourceStateChangeEvent(image));
    }

}
