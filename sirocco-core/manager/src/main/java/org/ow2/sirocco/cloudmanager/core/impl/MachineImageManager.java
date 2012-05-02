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
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Stateless(name = IMachineImageManager.EJB_JNDI_NAME, mappedName = IMachineImageManager.EJB_JNDI_NAME)
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

    private User user;

    @Resource
    public void setSessionContext(final SessionContext ctx) {
        this.ctx = ctx;
    }

    private void setUser() throws CloudProviderException {
        String username = this.ctx.getCallerPrincipal().getName();
        this.user = this.userManager.getUserByUsername(username);
    }

    public Job createMachineImage(final MachineImage mi) throws CloudProviderException {
        this.setUser();
        Job j = new Job();

        mi.setUser(this.user);
        mi.setCreated(new Date());
        mi.setState(MachineImage.State.AVAILABLE);
        this.em.persist(mi);
        this.em.flush();

        j.setTargetEntity(mi);
        j.setStatus(Job.Status.SUCCESS);
        j.setAction("create");
        j.setParentJob(null);
        j.setNestedJobs(null);
        j.setReturnCode(0);
        j.setUser(this.user);
        this.em.persist(j);
        this.em.flush();
        return j;
    }

    public List<MachineImage> getMachineImages() throws CloudProviderException {
        this.setUser();
        List<MachineImage> images = null;
        try {
            images = this.em.createQuery("FROM MachineImage i WHERE i.state<>'DELETED' AND i.user=:user")
                .setParameter("user", this.user).getResultList();
        } catch (Exception e) {
            throw new CloudProviderException("Internal query error");
        }
        return images;
    }

    public MachineImage getMachineImageById(final String imageId) throws CloudProviderException {

        MachineImage image = null;
        try {
            image = this.em.find(MachineImage.class, Integer.valueOf(new String(imageId)));

        } catch (Exception e) {
            throw new CloudProviderException("MachineImage of identity " + imageId + " cannot be found ");
        }
        return image;
    }

    @Override
    public MachineImage getMachineImageAttributes(final String imageId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getMachineImageById(imageId);
    }

    public void deleteMachineImage(final String imageId) throws CloudProviderException, ResourceNotFoundException {
        this.setUser();
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
                .setParameter("id", Integer.valueOf(imageId)).getResultList();
        } catch (Exception e) {
            throw new CloudProviderException("Internal query error" + e.getMessage());
        }
        if (templates != null) {
            throw new CloudProviderException("Machine templates refer to this image " + imageId);
        }
        Set<CloudEntity> entities = this.user.getCloudEntities();
        entities.remove(image);
        this.user.setCloudEntities(entities);

        this.em.remove(image);
        this.em.flush();

    }

    public List<MachineImage> getMachineImages(final List<String> attributes, final String filterExpression)
        throws InvalidRequestException, CloudProviderException {
        throw new InvalidRequestException(" Select images by query expression not supported ");
    }

    public List<MachineImage> getMachineImages(final int first, final int last, final List<String> attributes)
        throws InvalidRequestException, CloudProviderException {
        this.setUser();
        if ((first < 0) || (last < 0) || (last < first)) {
            throw new InvalidRequestException(" Illegal array index " + first + " " + last);
        }

        Query query = this.em.createNamedQuery("FROM MachineImage i WHERE i.user.username=:userName ORDER BY i.id");
        query.setParameter("userName", this.user.getUsername());
        query.setMaxResults(last - first + 1);
        query.setFirstResult(first);
        List<MachineImage> images = query.setFirstResult(first).setMaxResults(last - first + 1).getResultList();

        return images;
    }

    public MachineImageCollection getMachineImageCollection() throws CloudProviderException {

        this.setUser();
        Integer userid = this.user.getId();
        Query query = this.em.createQuery("SELECT i FROM MachineImage i WHERE i.user.id=:userid");
        List<MachineImage> images = query.setParameter("userid", userid).getResultList();
        MachineImageCollection collection = null;
        try {
            collection = (MachineImageCollection) this.em.createQuery("FROM MachineImageCollection m WHERE m.user.id=:userid")
                .setParameter("userid", userid).getSingleResult();
        } catch (Exception e) {
            throw new CloudProviderException(" Internal error " + e.getMessage());
        }
        collection.setImages(images);
        return collection;
    }

    public void updateMachineImageCollection(final Map<String, Object> attributes) throws CloudProviderException {
        this.setUser();
        Integer userid = this.user.getId();
        MachineImageCollection collection = null;
        try {
            collection = (MachineImageCollection) this.em.createQuery("FROM MachineImageCollection m WHERE m.user.id=:userid")
                .setParameter("userid", userid).getSingleResult();
        } catch (Exception e) {
            throw new CloudProviderException(" Internal error " + e.getMessage());
        }

        try {
            UtilsForManagers.fillObject(collection, attributes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudProviderException("Error updating machine image collection " + e.getMessage());
        }
    }

    @Override
    public void updateMachineImage(final MachineImage machineImage) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        // TODO Auto-generated method stub

    }

    public void updateMachineImageAttributes(final String imageId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {

        this.setUser();
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

}
