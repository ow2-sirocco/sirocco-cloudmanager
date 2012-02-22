/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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
import java.util.ArrayList;
import java.util.Map;
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


import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.User;

import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.InvalidMachineIdException;


@Stateless(name = IMachineManager.EJB_JNDI_NAME, mappedName = IMachineManager.EJB_JNDI_NAME)
@Remote(IRemoteMachineManager.class)
@Local(IMachineManager.class)
public class MachineManager implements IMachineManager, IRemoteMachineManager {

	static final String EJB_JNDI_NAME = "MachineManagerBean";

	private static Logger logger = Logger.getLogger(MachineManager.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

	@Resource
	private SessionContext		ctx;
	private	String			user;

	@Resource
	public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
	}

	private void setUser() {
		String user = ctx.getCallerPrincipal().getName();
	}

	/**
	 * Operations on MachineCollection
	 */
	public Job createMachine(MachineCreate machineCreate) 
		throws CloudProviderException {

	
		setUser();


		
		/**
		 * Check quota
		 */
		
		/**
		 * 
		 */
		Job j = null;
		return j;
	}


	public List<Machine> getMachines(int first, int last, List<String> attributes) throws CloudProviderException {

		setUser();
		User u = (User) this.em.createQuery("FROM User WHERE c.username=:username").setParameter("username", user).getSingleResult();
		Query query = em.createNamedQuery("FROM Machine v WHERE v.user.username=:userName AND v.state<>'DELETED' ORDER BY v.id");
		query.setParameter("userName", u.getUsername());
		query.setMaxResults(last-first+1);
		query.setFirstResult(first);
		List<Machine> machines = (List<Machine>)query.setFirstResult(first).setMaxResults(last-first+1).getResultList();
		for (Machine machine : machines) {

			if (attributes.contains(new String("volumes"))) {
				((Machine) machine).getMachineVolumes().size();
			}
			if (attributes.contains(new String("disks"))) {
				machine.getDisks().size();
			}

			if (attributes.contains(new String("networkInterfaces"))) {
				machine.getNetworkInterfaces().size();
			}
		}
		return machines;
	}


	public List<Machine> getMachinesAttributes(List<String> attributes, String queryExpression)  
			throws CloudProviderException {
		List<Machine> machines = new ArrayList<Machine>();

		return machines;
	}

	/**
	 * Operations on Machine
	 */
	public Job startMachine(final String machineId) 
			throws CloudProviderException {
		Job j = null;
		return j;
	}
	public Job stopMachine(final String machineId) 
			throws CloudProviderException {
		Job j = null;
		return j;
	}

	public Job deleteMachine(final String machineId) 
			throws CloudProviderException {
		Job j = null;
		return j;
	}


	public Machine getMachineById(final String machineId) 
		throws InvalidMachineIdException, CloudProviderException {
		Machine m = (Machine)this.em.find(Machine.class, Integer.valueOf(machineId));
		m.getMachineVolumes().size();
		m.getNetworkInterfaces().size();
		m.getDisks().size();
		return m;
	}

	public Machine getMachineAttributes(final String machineId, 
				     List<String> attributes) 
		 throws InvalidMachineIdException, CloudProviderException {
		Machine m = (Machine) this.em.find(Machine.class, Integer.valueOf(machineId));
		if (attributes.contains(new String("volumes"))) {
			m.getMachineVolumes().size();
		}
		if (attributes.contains(new String("disks"))) {
			m.getDisks().size();
		}
		if (attributes.contains(new String("networkInterfaces"))) {
			m.getNetworkInterfaces().size();
		}
		return m;
	}

	public Job updateMachine(final String machineId, 
			  Map<String, Object> updatedAttributes)
		 throws InvalidMachineIdException, CloudProviderException {
		Job j = null;
		return j;
	}


	/**
	 * Operations on MachineCollection
	 */
	public MachineCollection	getMachineCollection()
	         throws CloudProviderException {

		setUser();
		User u = (User) this.em.createQuery("FROM User WHERE c.username=:username").setParameter("username", user).getSingleResult();
		Query query = this.em.createQuery("FROM Machine m WHERE m.user=:user");
		List<Machine> machines = (List<Machine>) query.setParameter("user", u).getResultList();
		MachineCollection collection = (MachineCollection) this.em.createQuery("FROM MachineCollection m WHERE m.user=:user").setParameter("user", u).getSingleResult();
		collection.setMachines(machines);
		return collection;
	}


	public void	updateMachineCollection(Map<String, Object> attributes)
	         throws CloudProviderException {

	}

	/**
	 * Operations on MachineConfiguration
	 */
	public MachineConfiguration getMachineConfiguration(final String mcId) {
		MachineConfiguration mc = (MachineConfiguration)this.em.find(MachineConfiguration.class, Integer.valueOf(mcId));
		mc.getDiskTemplates().size();
		return mc;
	}

	public void updateMachineConfiguration(String mcId, Map<String, Object> attributes) throws CloudProviderException {
		MachineConfiguration mc = (MachineConfiguration)this.em.find(MachineConfiguration.class, Integer.valueOf(mcId));
		if (attributes.containsKey("cpu")) {
			Cpu cpu = (Cpu)attributes.get(new String("cpu"));
			mc.setCpu(cpu);
		}
		if (attributes.containsKey("memory")) {
			Memory mem = (Memory) attributes.get(new String("memory"));
			mc.setMemory(mem);
		}

		if (attributes.containsKey("diskTemplates")) {
			List<DiskTemplate> dts = (List<DiskTemplate>)attributes.get(new String("diskTemplates"));
			mc.setDiskTemplates(dts);
		}

		em.flush();
	}


	public void deleteMachineConfiguration(final String mcId)
				        throws CloudProviderException {

		try {
		
			Integer mcid = Integer.valueOf(mcId);

			/**
			 * CHECK IF THIS IS REQUIRED
			 */
			List<MachineTemplate> list = (List<MachineTemplate>) this.em.createQuery("FROM MachineTemplate m WHERE m.machineConfiguration.id=:mcid");
			for (MachineTemplate mt : list) {
				mt.setMachineConfiguration(null);
			}
			MachineConfiguration config = (MachineConfiguration) this.em.find(MachineConfiguration.class, mcid);
			this.em.remove(config);
		} catch (Exception e) {
			throw new CloudProviderException(e.getMessage());
		}
	}


	/**
	 * Operations on MachineConfigurationCollection
	 */
	public MachineConfigurationCollection getMachineConfigurationCollection() {
		setUser();
		User u = (User) this.em.createQuery("FROM User WHERE c.username=:username").setParameter("username", user).getSingleResult();
		Query query = this.em.createQuery("FROM MachineConfiguration c WHERE c.user=:user");
		List<MachineConfiguration> configs = (List<MachineConfiguration>)query.setParameter("user", u).getResultList();
		MachineConfigurationCollection collection = (MachineConfigurationCollection)this.em.createQuery("FROM MachineConfigurationCollection m WHERE m.user=:user").setParameter("user", u).getSingleResult();
		collection.setMachineConfigurations(configs);
		return collection;
	}


	public MachineConfiguration createMachineConfiguration(MachineConfiguration machineConfig) throws CloudProviderException {

		setUser();
		User u = (User) this.em.createQuery("FROM User WHERE c.username=:username").setParameter("username", user).getSingleResult();
		MachineConfiguration mc = (MachineConfiguration ) this.em.createQuery("FROM m MachineConfiguration WHERE m.user=:user AND m.name=:name").setParameter("user", u).setParameter("name", machineConfig.getName()).getSingleResult();
		if (mc != null) {
			throw new CloudProviderException("MachineConfiguration by name already exists " +machineConfig.getName());
		}
		machineConfig.setUser(u);
		machineConfig.setCreated(new Date());
		this.em.persist(machineConfig);
		return machineConfig;
	}

	/**
	 * Operations on MachineTemplate
	 */
	public MachineTemplate getMachineTemplate(String mtId) throws CloudProviderException {
		MachineTemplate mt = (MachineTemplate) this.em.find(MachineTemplate.class, Integer.valueOf(mtId));		
		mt.getVolumes().size();
		mt.getVolumeTemplates().size();
		mt.getNetworkInterfaces().size();
		
		return mt;
	}

	public MachineTemplate updateMachineTemplate(String mtId, Map<String, Object> attributes) throws CloudProviderException {
		MachineTemplate mt = null;
		try {
			mt = (MachineTemplate) this.em.find(MachineTemplate.class, Integer.valueOf(mtId));

			if (attributes.containsKey("name")) {
				mt.setName((String)attributes.get("name"));
			}

			if (attributes.containsKey("description")) {
				mt.setDescription((String)attributes.get("description"));
			}

			if (attributes.containsKey("properties")) {
				mt.setProperties((Map<String, String>)attributes.get("properties"));
			}

			if (attributes.containsKey("machineConfiguration")) {

				String mc = (String)attributes.get("machineConfiguration");

				MachineConfiguration config = (MachineConfiguration) em.find(MachineConfiguration.class, Integer.valueOf(mc));
				mt.setMachineConfiguration(config);
			}
			if (attributes.containsKey("machineImage")) {
				String mi = (String)attributes.get("machineImage");

				MachineImage image = (MachineImage) em.find(MachineImage.class, Integer.valueOf(mi));
				mt.setMachineImage(image);
			}
			if (attributes.containsKey("credentials")) {
				String credentials = (String)attributes.get("credentials");

				Credentials cred = (Credentials) em.find(Credentials.class, Integer.valueOf(credentials));
				mt.setCredentials(cred);
			}
			if (attributes.containsKey("volumes")) {
				List<MachineVolume> volumes = (List<MachineVolume>) attributes.get("volumes");
				/** check that the volume exists */
				for (MachineVolume volume : volumes) {
					try {
						em.getReference(Volume.class, volume.getVolume());
					} catch (Exception e) {
						throw new CloudProviderException(e.getMessage());
					}
				}
				mt.setVolumes(volumes);
			}
			if (attributes.containsKey("volumeTemplates")) {
				List<MachineVolumeTemplate> vts = (List<MachineVolumeTemplate>) attributes.get("volumes");
				/** check that each volume exists */
				for (MachineVolumeTemplate vt : vts) {
					try {
						em.getReference(VolumeTemplate.class, /*vt.getVolumeTemplate()*/ 1);
					} catch (Exception e) {
						throw new CloudProviderException(e.getMessage());
					}
				}
				mt.setVolumeTemplates(vts);
			}		

			if (attributes.containsKey("networkInterfaces")) {
				List<NetworkInterface> list = (List<NetworkInterface>) (attributes.get("networkInterfaces"));
				/** validate(list);*/
				mt.setNetworkInterfaces(list);
			}
			mt.setUpdated(new Date());
			this.em.merge(mt);
			this.em.flush();
		} catch (Exception e) {
			throw new CloudProviderException(e.getMessage());
		}
		return mt;
	}


	public void deleteMachineTemplate(String mtId) throws CloudProviderException {
		setUser();
		User u = (User) this.em.createQuery("FROM User WHERE c.username=:username").setParameter("username", user).getSingleResult();

		MachineTemplate mt = this.em.find(MachineTemplate.class, Integer.valueOf(mtId));
		
		if (mt.getUser().equals(u) == false){
			throw new CloudProviderException("Not owner, cannot delete machine template ");
		}
		this.em.remove(mt);
		this.em.flush();
	}

	/**
	 * All checks done in CIMI REST layer:
	 * REST Layer has validated that referenced MachineConfiguration 
	 * etc do really exist.
	 */
	public MachineTemplate	createMachineTemplate(MachineTemplate mt) 
		throws CloudProviderException {

		setUser();
		User u = (User) this.em.createQuery("FROM User WHERE c.username=:username").setParameter("username", user).getSingleResult();

		MachineTemplate mtemplate = (MachineTemplate) this.em.createQuery("FROM m MachineTemplate WHERE m.user=:user AND m.name=:name").setParameter("user", u).setParameter("name", mt.getName()).getSingleResult();
		if (mtemplate != null) {
			throw new CloudProviderException("MachineTemplate by name already exists " +mt.getName());
		}
		mt.setUser(u);
		mt.setCreated(new Date());
		this.em.persist(mt);
		this.em.flush();
		return mt;
	}

	public MachineTemplateCollection getMachineTemplateCollection()
		throws CloudProviderException {
		setUser();
		User u = (User) this.em.createQuery("FROM User WHERE c.username=:username").setParameter("username", user).getSingleResult();
		Query query = this.em.createQuery("SELECT c FROM MachineTemplate c WHERE c.user=:user");
		List<MachineTemplate> templates = query.setParameter("user", u).getResultList();
		MachineTemplateCollection collection = (MachineTemplateCollection) this.em.createQuery("FROM MachineTemplateCollection m WHERE m.user=:user").setParameter("user", u).getSingleResult();
		collection.setMachineTemplates(templates);
		return collection;
	}


	public void updateMachineTemplateCollection(Map<String, Object> attributes )
		throws CloudProviderException {

		setUser();
		User u = (User) this.em.createQuery("FROM User WHERE c.username=:username").setParameter("username", user).getSingleResult();

		MachineTemplateCollection collection = (MachineTemplateCollection) this.em.createQuery("FROM MachineTemplateCollection m WHERE m.user=:user").setParameter("user", u).getSingleResult();

		if (attributes.containsKey("name")) {
			collection.setName((String)attributes.get("name"));
		}
		if (attributes.containsKey("properties")) {
			collection.setProperties((Map<String, String>)attributes.get("properties"));
		}
		if (attributes.containsKey("description")) {
			collection.setDescription((String) attributes.get("description"));
		}
		this.em.flush();
	}


}
