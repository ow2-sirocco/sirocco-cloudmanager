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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Stateless
@Remote(IRemoteMachineImageManager.class)
@Local(IMachineImageManager.class)
@SuppressWarnings("unused")
public class MachineImageManager implements IMachineImageManager {

    private static Logger logger = Logger.getLogger(MachineImageManager.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private IUserManager userManager;

    @Resource
    private SessionContext ctx;

    @Resource
    public void setSessionContext(final SessionContext ctx) {
        this.ctx = ctx;
    }

    private User getUser() throws CloudProviderException {
        String username = this.ctx.getCallerPrincipal().getName();
        return this.userManager.getUserByUsername(username);
    }

    public Job createMachineImage(final MachineImage mi) throws CloudProviderException {
        Job j = new Job();

        // TODO : check whether imageLocation points to a Machine
        mi.setType(MachineImage.Type.IMAGE);

        mi.setUser(this.getUser());
        mi.setCreated(new Date());
        mi.setState(MachineImage.State.AVAILABLE);
        this.em.persist(mi);
        this.em.flush();

        j.setTargetEntity(mi);
        j.setStatus(Job.Status.SUCCESS);
        j.setAction("add");
        j.setParentJob(null);
        j.setNestedJobs(null);
        j.setReturnCode(0);
        j.setUser(this.getUser());
        this.em.persist(j);
        this.em.flush();
        return j;
    }

    @Override
    public List<MachineImage> getMachineImages() throws CloudProviderException {
        return UtilsForManagers.getEntityList("MachineImage", this.em, this.getUser().getUsername());
    }

    public MachineImage getMachineImageById(final String imageId) throws CloudProviderException {

        MachineImage image = null;
        try {
            image = this.em.find(MachineImage.class, Integer.valueOf(new String(imageId)));

        } catch (Exception e) {
            throw new CloudProviderException("MachineImage of identity " + imageId + " cannot be found ");
        }
        if (image.getState() == State.DELETED) {
            throw new ResourceNotFoundException();
        }
        return image;
    }

    @Override
    public MachineImage getMachineImageAttributes(final String imageId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getMachineImageById(imageId);
    }

    public void deleteMachineImage(final String imageId) throws CloudProviderException, ResourceNotFoundException {
        MachineImage image = null;
        try {
            image = this.em.find(MachineImage.class, Integer.valueOf(imageId));

        } catch (Exception e) {
            throw new CloudProviderException("MachineImage of identity " + imageId + " cannot be found ");
        }

        /** if related image is not null then do not permit deletion */
        if (image.getRelatedImage() != null) {
            throw new CloudProviderException("Related images exist for this image" + imageId);
        }
        /** if a machine template referernces the image do not permit deletion */
        List<MachineTemplate> templates = null;
        try {
            templates = this.em.createQuery("FROM MachineTemplate t WHERE t.machineImage.id=:mid")
                .setParameter("mid", Integer.valueOf(imageId)).getResultList();
        } catch (Exception e) {
            throw new CloudProviderException("Internal query error" + e.getMessage());
        }
        if (templates != null && !templates.isEmpty()) {
            throw new CloudProviderException("Machine templates refer to this image " + imageId);
        }

        image.setState(MachineImage.State.DELETED);
        this.em.flush();

    }

    @Override
    public QueryResult<MachineImage> getMachineImages(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        return UtilsForManagers.getEntityList("MachineImage", this.em, this.getUser().getUsername(), first, last, filters,
            attributes, true);
    }

    @Override
    public void updateMachineImage(final MachineImage machineImage) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        // TODO Auto-generated method stub

    }

    public void updateMachineImageAttributes(final String imageId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {

        MachineImage image = null;
        try {
            image = this.em.find(MachineImage.class, Integer.valueOf(imageId));
        } catch (Exception e) {
            throw new ResourceNotFoundException("MachineImage of identity " + imageId + " cannot be found ");
        }
        try {
            // TODO Filter RO attributes
            UtilsForManagers.fillObject(image, attributes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudProviderException("Error updating machine image  " + e.getMessage());
        }
    }

    @Override
    public Job captureMachine(final MachineImage machineImage, final String machineId) throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new ServiceUnavailableException();
    }

}
