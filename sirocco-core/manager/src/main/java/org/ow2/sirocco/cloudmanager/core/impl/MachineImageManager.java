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


import java.util.List;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.User;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager;

import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.UserException;


@Stateless(name = IMachineImageManager.EJB_JNDI_NAME, mappedName = IMachineImageManager.EJB_JNDI_NAME)
@Remote(IRemoteMachineImageManager.class)
@Local(IMachineImageManager.class)
public class MachineImageManager implements IMachineImageManager {

	private static Logger logger = Logger.getLogger(MachineImageManager.class.getName());

	@PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
	private EntityManager em;

	@EJB
    private IUserManager userManager;
	
	@Resource
	private SessionContext		ctx;
	private	User				user;

	@Resource
	public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
	}

	private void setUser() throws UserException {
		String username = ctx.getCallerPrincipal().getName();
		user = userManager.getUserByUsername(username);
	}

	public Job createMachineImage(MachineImage mi) throws CloudProviderException  {
		Job j = new Job();
	
		mi.setUser(user);
		mi.setCreated(new Date());
		mi.setState(MachineImage.State.AVAILABLE);
		this.em.persist(mi);
		this.em.flush();
		
		j.setTargetEntity(mi.getId().toString());
		j.setStatus(Job.Status.SUCCESS);
		j.setAction("create");
		j.setParentJob(null);
		j.setNestedJobs(null);
		j.setReturnCode(0);
		j.setUser(user);
		this.em.persist(j);
		this.em.flush();
		return j;
	}

	public List<MachineImage> getMachineImages() throws CloudProviderException {
		setUser();
		List<MachineImage> images = null;
		try {
			images = this.em.createQuery("FROM MachineImage i WHERE i.state<>'DELETED' AND i.user=:user").setParameter("user", user).getResultList();
		} catch (Exception e) {
			throw new CloudProviderException("Internal query error");
		}
		return images;
	}

	public MachineImage getMachineImage(String imageId)
			throws CloudProviderException {

		MachineImage image = null;
		try {
			image = this.em.find(MachineImage.class, Integer.valueOf(new String(imageId)));

		} catch (Exception e) {
			throw new CloudProviderException("MachineImage of identity " +imageId + " cannot be found ");
		}
		return image;
	}

	public void deleteImage(String imageId)throws CloudProviderException {

	}

}
