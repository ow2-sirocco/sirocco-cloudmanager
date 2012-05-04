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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactoryFinder;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDiskCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Stateless(name = IMachineManager.EJB_JNDI_NAME, mappedName = IMachineManager.EJB_JNDI_NAME)
@Remote(IRemoteMachineManager.class)
@Local(IMachineManager.class)
public class MachineManager implements IMachineManager, IRemoteMachineManager {

	static final String EJB_JNDI_NAME = "MachineManager";

	private static Logger logger = Logger.getLogger(MachineManager.class
			.getName());

	@PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
	private EntityManager em;

	@EJB
	private IUserManager userManager;

	@OSGiResource
	private ICloudProviderConnectorFactoryFinder cloudProviderConnectorFactoryFinder;

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

	private Job createJob(Machine m, Job providerJob)
			throws CloudProviderException {
		MachineManager.logger.info("SHOULD REMOVE THIS ");
		Job j = new Job();
		j.setTargetEntity(m);
		j.setAction(providerJob.getAction());
		j.setStatus(providerJob.getStatus());

		j.setCreated(new Date());
		j.setProviderAssignedId(providerJob.getProviderAssignedId());

		this.em.persist(j);

		return j;
	}

	private List<CloudProvider> selectCloudProviders(final MachineTemplate mt) {
		// TODO selection cloud provider

		List<CloudProvider> l = new ArrayList<CloudProvider>();

		Query q = this.em
				.createQuery("FROM CloudProvider c WHERE c.cloudProviderType=:type");
		q.setParameter("type", "mock");

		q.setMaxResults(1);
		List<CloudProvider> cp = q.getResultList();
		if (cp.size() == 0) {
			return l;
		}
		// List<CloudProviderLocation> cpll =
		// cp.get(0).getCloudProviderLocations();
		// if (cpll.size() == 0) {
		// return l;
		// }

		l.add(cp.get(0));
		return l;
	}

	/**
	 * Operations on CloudProviderEntryPoint
	 */

	@Override
	public CloudEntryPoint getCloudEntryPoint() throws CloudProviderException {
		this.setUser();
		Integer userid = this.user.getId();
		CloudEntryPoint cep = (CloudEntryPoint) this.em
				.createQuery("FROM CloudEntryPoint c WHERE c.user.id=:userid")
				.setParameter("userid", userid).getSingleResult();
		return cep;
	}

	/**
	 * Operations on MachineCollection
	 */
	private CloudProviderAccount selectCloudProviderAccount(
			final CloudProvider provider, final User u,
			final MachineTemplate template) {
		Set<CloudProviderAccount> accounts = provider
				.getCloudProviderAccounts();
		CloudProviderAccount a = null;

		/**
		 * TODO Choose a provider account who can access the image
		 */
		if (accounts.isEmpty() == false) {
			a = accounts.iterator().next();
		}
		return a;
	}

	private boolean checkQuota(final User u, final MachineConfiguration mc) {
		/**
		 * TODO Check current quota
		 */
		return true;
	}

	private ICloudProviderConnector getCloudProviderConnector(
			final CloudProviderAccount account,
			final CloudProviderLocation location) throws CloudProviderException {

		if (account == null) {
			throw new CloudProviderException("Cloud provider account ");
		}
		ICloudProviderConnectorFactory connectorFactory = this.cloudProviderConnectorFactoryFinder
				.getCloudProviderConnectorFactory(account.getCloudProvider()
						.getCloudProviderType());
		if (connectorFactory == null) {
			throw new CloudProviderException(
					" Internal error in connector factory ");
		}
		return connectorFactory.getCloudProviderConnector(account, location);
	}

	/**
	 * User could have passed by value or by reference. Validation is expected
	 * to be done by REST layer
	 */
	private void checkVolumes(final MachineTemplate mt, final User u)
			throws InvalidRequestException {

		MachineVolumeCollection volColl = mt.getVolumes();
		if (volColl == null) {
			throw new InvalidRequestException("Volumes array null");
		}
		if ((volColl.getItems() == null) || volColl.getItems().size() == 0) {
			return;
		}
		List<MachineVolume> volumes = volColl.getItems();

		for (MachineVolume mv : volumes) {

			if (mv.getInitialLocation() == null) {
				throw new InvalidRequestException(
						"initialLocation not set for volume ");
			}

			Volume v = mv.getVolume();
			/**
			 * Volume should not be passed by value. Check that the volume id
			 * exists.
			 */
			if ((v == null) || (v.getId() == null)) {
				throw new InvalidRequestException("No volume id ");
			}
			// TODO volume manager
			Volume vv = this.em.find(Volume.class, v.getId());
			if (vv == null) {
				throw new InvalidRequestException("Volume " + v.getId()
						+ " of name " + v.getName() + " does not exist ");
			}
		}

		MachineVolumeTemplateCollection vtColl = mt.getVolumeTemplates();

		if (vtColl == null) {
			throw new InvalidRequestException("VolumeTemplates array null");
		}
		if ((vtColl.getItems() == null) || (vtColl.getItems().size() == 0)) {
			return;
		}
		Collection<MachineVolumeTemplate> vts = vtColl.getItems();
		for (MachineVolumeTemplate mvt : vts) {
			if (mvt.getInitialLocation() == null) {
				throw new InvalidRequestException(
						"initialLocation not set for volume template");
			}

			VolumeTemplate vt = mvt.getVolumeTemplate();
			if ((vt == null) || (vt.getId() == null)) {
				throw new InvalidRequestException("No volume template id ");
			}
			// TODO volume manager
			VolumeTemplate vvt = this.em.find(VolumeTemplate.class, vt.getId());
			if (vvt == null) {
				throw new InvalidRequestException("VolumeTemplate "
						+ vt.getId() + " of name " + vt.getName()
						+ " does not exist ");
			}
		}
	}

	// TODO
	private void validateMachineImage(final MachineTemplate mt, final User u)
			throws InvalidRequestException {
		MachineImage mi = mt.getMachineImage();
		if ((mi == null) || (mi.getId() == null)) {
			throw new InvalidRequestException(" MachineImage should be set");
		}
		// check that machine image is known
		// use MachineImageManager
		/**
		 * POLICY ABOUT IMAGE AND CLOUD PROVIDER!
		 */
		MachineImage mimage = this.em.find(MachineImage.class, mi.getId());
		if (mimage == null) {
			throw new InvalidRequestException(
					"Unknown machine image in request ");
		}
	}

	private void validateCreationParameters(final MachineTemplate mt,
			final User u) throws CloudProviderException {
		// TODO check all references
		this.checkVolumes(mt, u);
		this.validateMachineConfiguration(mt.getMachineConfiguration());
		this.validateMachineImage(mt, u);

		this.validateCredentials(mt.getCredentials());
		this.validateNetworkInterface(mt.getNetworkInterfaces());

	}

	public Job createMachine(final MachineCreate machineCreate)
			throws CloudProviderException {

		this.setUser();
		MachineManager.logger.info("createMachine ");
		MachineTemplate mt = machineCreate.getMachineTemplate();

		this.validateCreationParameters(mt, this.user);

		/**
		 * TODO Check quota
		 */
		if (this.checkQuota(this.user, mt.getMachineConfiguration()) == false) {
			throw new CloudProviderException("User exceeded quota ");
		}
		MachineManager.logger.info(" selectCloudProviders ");
		/**
		 * Obtain list of matching provider
		 */
		List<CloudProvider> providers = this.selectCloudProviders(mt);
		if (providers.size() == 0) {
			throw new ServiceUnavailableException(
					"Could not find a suitable cloud provider  ");
		}
		CloudProviderAccount account = null;
		CloudProvider myprovider = null;

		MachineManager.logger.info(" selectCloudProviderAccounts ");
		for (CloudProvider cp : providers) {
			/**
			 * Select provider account to use
			 */
			account = this.selectCloudProviderAccount(cp, this.user, mt);
			if (account != null) {
				myprovider = cp;
				break;
			}
		}

		if (account == null) {
			throw new CloudProviderException(
					"Could not find a cloud provider account ");
		}
		/** there must be at least one location if we are here */
		CloudProviderLocation mylocation = null;// myprovider.getCloudProviderLocations().get(0);
		ICloudProviderConnector connector = this.getCloudProviderConnector(
				account, mylocation);
		if (connector == null) {
			throw new CloudProviderException(
					"Could not obtain connector to provider "
							+ account.getCloudProvider().getCloudProviderType());
		}
		String connectorid = connector.getCloudProviderId();
		MachineManager.logger.info(" got a connector " + connectorid);
		Job creationJob = null;
		IComputeService computeService = null;

		/**
		 * Convention: The entity Ids refer to sirocco given ids. The provider
		 * id is stored in providerAssignedId. The connector layer will use
		 * providerAssignedId in its communication with the provider.
		 */
		try {
			computeService = connector.getComputeService();
			creationJob = computeService.createMachine(machineCreate);
		} catch (Exception e) {
			MachineManager.logger.info("Failed to create machine ");
			throw new CloudProviderException(e.getMessage());
		}

		// TODO Should the creation request be logged?
		if (creationJob.getStatus() == Job.Status.FAILED) {
			throw new ServiceUnavailableException("Machine creation failed ");
		}

		Machine m = new Machine();

		m.setName(machineCreate.getName());
		m.setDescription(machineCreate.getDescription());
		m.setProperties(machineCreate.getProperties());

		m.setState(Machine.State.CREATING);
		m.setUser(this.user);
		m.setCpu(mt.getMachineConfiguration().getCpu());
		m.setMemory(mt.getMachineConfiguration().getMemory());

		m.setCloudProviderAccount(account);
		m.setProviderAssignedId(creationJob.getTargetEntity().getId().toString());
		/** set cloud provider location */
		m.setLocation(mylocation);
		m.setCreated(new Date());

		/**
		 * Persist machine and job according to status of Job returned by
		 * connector
		 */

		Job j = new Job();
		// Should use JobManager to create job
		j.setName(creationJob.getName());
		j.setDescription("Machine creation ");
		j.setProperties(new HashMap<String, String>());

		j.setCreated(new Date());
		j.setProviderAssignedId(creationJob.getProviderAssignedId().toString());
		j.setAction(creationJob.getAction());
		j.setStatus(creationJob.getStatus());
		j.setReturnCode(creationJob.getReturnCode());
		j.setIsCancellable(false);
		j.setParentJob(null);
		j.setNestedJobs(new ArrayList<Job>());
		j.setUser(this.user);

		// TODO Cancelled status

		if (creationJob.getStatus() == Job.Status.SUCCESS) {
			try {
				m.setState(computeService.getMachineState(creationJob
						.getTargetEntity().getId().toString()));
			} catch (ConnectorException ce) {
				throw new ServiceUnavailableException(ce.getMessage());
			}
			this.em.persist(m);
			j.setTargetEntity(m);
			this.em.persist(j);
			this.em.flush();

		} else {
			/** Job is RUNNING. Will be notified when creation completes */

			this.em.persist(m);
			j.setTargetEntity(m);
			this.em.persist(j);
			this.em.flush();

			// Ask for connector to notify when job completes
			try {
				connector.setNotificationOnJobCompletion(creationJob
						.getProviderAssignedId());
			} catch (Exception e) {
				throw new ServiceUnavailableException(e.getMessage());
			}
		}

		return j;
	}

	public List<Machine> getMachines(final int first, final int last,
			final List<String> attributes) throws CloudProviderException {

		this.setUser();
		if ((first < 0) || (last < 0) || (last < first)) {
			throw new InvalidRequestException(" Illegal array index " + first
					+ " " + last);
		}

		Query query = this.em
				.createNamedQuery("FROM Machine v WHERE v.user.username=:userName AND v.state<>'DELETED' ORDER BY v.id");
		query.setParameter("userName", this.user.getUsername());
		query.setMaxResults(last - first + 1);
		query.setFirstResult(first);
		List<Machine> machines = query.setFirstResult(first)
				.setMaxResults(last - first + 1).getResultList();

		for (Machine machine : machines) {
			// do not need to read in the volumes and disks

			if (attributes.contains("networkInterfaces")) {
				machine.getNetworkInterfaces().size();
			}
			// TODO
			machine.initFSM();
		}
		return machines;
	}

	// TODO
	public List<Machine> getMachines(final List<String> attributes,
			final String queryExpression) throws CloudProviderException {
		List<Machine> machines = new ArrayList<Machine>();

		return machines;
	}

	/**
	 * Operations on Machine
	 */
	private Machine checkOps(final String machineId, final String action)
			throws CloudProviderException {
		Machine m = null;

		if (machineId == null) {
			throw new InvalidRequestException("Null machine id ");
		}
		try {
			m = this.em.find(Machine.class, Integer.valueOf(machineId));
		} catch (Exception e) {
			throw new ResourceNotFoundException(e.getMessage());
		}

		m.initFSM();

		Set<String> actions = m.getOperations();

		if (actions.contains(action) == false) {
			throw new InvalidRequestException(" Cannot " + action
					+ "  machine at state " + m.getState());
		}

		return m;
	}

	private void relConnector(final Machine m,
			final ICloudProviderConnector connector)
			throws CloudProviderException {
		String cpType = m.getCloudProviderAccount().getCloudProvider()
				.getCloudProviderType();
		ICloudProviderConnectorFactory cFactory = null;
		try {
			cFactory = this.cloudProviderConnectorFactoryFinder
					.getCloudProviderConnectorFactory(cpType);
			String connectorId = connector.getCloudProviderId();
			cFactory.disposeCloudProviderConnector(connectorId);
		} catch (ConnectorException e) {
			throw new CloudProviderException(e.getMessage());
		}
	}

	private ICloudProviderConnector getConnector(final Machine m)
			throws CloudProviderException {

		ICloudProviderConnector connector = null;

		connector = this.getCloudProviderConnector(m.getCloudProviderAccount(),
				m.getLocation());
		return connector;
	}

	// TODO with JobManager

	private Job initJobToPersist(final Job j, final Machine m,
			final String action) {

		Job persistedJob = new Job();
		persistedJob.setStatus(j.getStatus());
		persistedJob.setAction(action);

		persistedJob.setParentJob(null);
		// TODO machine create may be dispatched as a set of jobs
		persistedJob.setNestedJobs(null);
		persistedJob.setStatusMessage(j.getStatusMessage());
		persistedJob.setTargetEntity(m);
		persistedJob
				.setProviderAssignedId(j.getProviderAssignedId().toString());
		persistedJob.setCreated(new Date());
		persistedJob.setLocation(m.getLocation());
		persistedJob.setUser(m.getUser());
		persistedJob.setReturnCode(j.getReturnCode());
		return persistedJob;
	}

	public Job startMachine(final String machineId)
			throws CloudProviderException {

		Job persistedJob = this.doService(machineId, "start");
		return persistedJob;
	}

	public Job stopMachine(final String machineId)
			throws CloudProviderException {

		return this.doService(machineId, "stop");
	}

	private Job doService(final String machineId, final String action)
			throws CloudProviderException {

		Job j;
		Machine m = this.checkOps(machineId, action);
		ICloudProviderConnector connector = this.getConnector(m);
		IComputeService computeService;
		try {
			computeService = connector.getComputeService();
		} catch (ConnectorException e) {
			String eee = e.getMessage();
			throw new ServiceUnavailableException(" " + eee + " action "
					+ action + " machine " + machineId + " "
					+ m.getProviderAssignedId());
		}
		try {
			if (action.equals("start")) {
				j = computeService.startMachine(m.getProviderAssignedId());
				m.setState(Machine.State.STARTING);
			} else if (action.equals("stop")) {
				j = computeService.stopMachine(m.getProviderAssignedId());
				m.setState(Machine.State.STOPPING);
			} else {
				// TODO capabilities
				throw new ServiceUnavailableException(
						"Unsupported operation action " + action
								+ " on machine id " + m.getProviderAssignedId()
								+ " " + m.getId());
			}
		} catch (ConnectorException e) {
			throw new ServiceUnavailableException(e.getMessage() + " action "
					+ action + " machine id " + m.getProviderAssignedId() + " "
					+ m.getId());
		}
		MachineManager.logger.info("operation " + action + " for machine "
				+ m.getId() + " job status " + j.getStatus());
		if ((j.getStatus() == Job.Status.FAILED)
				|| (j.getStatus() == Job.Status.CANCELLED)
				|| (j.getStatus() == Job.Status.SUCCESS)) {
			/**
			 * what to do ? should we immediately obtain the machine status and
			 * update it without creating a job entity?
			 */
			Machine.State s = m.getState();
			try {
				s = computeService.getMachineState(m.getProviderAssignedId());
			} catch (ConnectorException e) {
				/** what to do ? */
			}
			m.setState(s);
		}
		String aaa = "machine." + action;
		Job persistedJob = this.initJobToPersist(j, m, aaa);
		this.em.persist(persistedJob);
		this.em.flush();
		/** Ask connector for notification */

		if (j.getStatus() == Job.Status.RUNNING) {
			try {
				connector.setNotificationOnJobCompletion(persistedJob
						.getProviderAssignedId());
			} catch (Exception e) {
				throw new ServiceUnavailableException(e.getMessage() + "  "
						+ aaa);
			}
		}
		/** Tell connector that we are done with it */
		MachineManager.logger.info("operation " + action + " requested "
				+ j.getStatus());
		this.relConnector(m, connector);
		return persistedJob;
	}

	// TODO
	// Delete may be done in any state of the machine
	public Job deleteMachine(final String machineId)
			throws CloudProviderException {
		Job j = null;
		MachineManager.logger.info("deleteMachine " + machineId);
		if (machineId == null) {
			throw new InvalidRequestException(" Null machine id");
		}
		Machine m = this.em.find(Machine.class, Integer.valueOf(machineId));
		if (m == null) {
			throw new ResourceNotFoundException(" Invalid machine id "
					+ machineId);
		}

		ICloudProviderConnector connector = this.getConnector(m);
		IComputeService computeService;

		try {
			computeService = connector.getComputeService();
		} catch (ConnectorException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			j = computeService.deleteMachine(m.getProviderAssignedId());
		} catch (ConnectorException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		if ((j.getStatus() == Job.Status.FAILED)
				|| (j.getStatus() == Job.Status.CANCELLED)
				|| (j.getStatus() == Job.Status.SUCCESS)) {
			/**
			 * what to do ? should we immediately obtain the machine status and
			 * update it without creating a job entity?
			 */
			Machine.State s = m.getState();
			try {
				s = computeService.getMachineState(m.getProviderAssignedId());
			} catch (ConnectorException e) {
				/** what to do ? */
			}
			m.setState(s);
		}

		Job persistedJob = this.initJobToPersist(j, m, "machine.delete");
		this.em.persist(persistedJob);
		this.em.flush();
		/** Ask connector for notification when job completes */
		boolean deletedone = false;

		if (j.getStatus() == Job.Status.RUNNING) {
			try {
				connector.setNotificationOnJobCompletion(persistedJob
						.getProviderAssignedId());
			} catch (Exception e) {
				throw new ServiceUnavailableException(e.getMessage());
			}
		} else if (j.getStatus() == Job.Status.SUCCESS) {
			/** machine is deleted */
			MachineManager.logger.info("deleteMachine done " + machineId);
			deletedone = true;
		}
		/** Tell connector that we are done with it */
		this.relConnector(m, connector);
		if (deletedone == true) {
			this.removeMachine(m);
		}

		this.em.persist(persistedJob);
		this.em.flush();

		return persistedJob;
	}

	private Machine getMachineFromId(final String machineId)
			throws ResourceNotFoundException, CloudProviderException {
		if (machineId == null) {
			throw new InvalidRequestException(" null machine id");
		}
		Machine m = this.em.find(Machine.class, Integer.valueOf(machineId));
		if (m == null) {
			throw new ResourceNotFoundException(" Invalid machine id "
					+ machineId);
		}
		return m;
	}

	public Machine getMachineById(final String machineId)
			throws ResourceNotFoundException, CloudProviderException {
		Machine m = getMachineFromId(machineId);
		m.getNetworkInterfaces().size();
		m.getDisks().getItems().size();
		return m;
	}

	public Machine getMachineAttributes(final String machineId,
			final List<String> attributes) throws ResourceNotFoundException,
			CloudProviderException {
		Machine m = getMachineFromId(machineId);
		if (attributes.contains("networkInterfaces")) {
			m.getNetworkInterfaces().size();
		}
		return m;
	}

	/**
	 * for each update operation change the local state of machine only after
	 * having received the new state from server.
	 */
	/** TEMP: filter out updates not accepted */
	private Map<String, Object> filterUpdates(
			final Map<String, Object> requested) {
		Map<String, Object> s = new HashMap<String, Object>();
		if (requested.containsKey("cpu")) {
			s.put("cpu", requested.get("cpu"));
		}
		if (requested.containsKey("memory")) {
			s.put("memory", requested.get("memory"));
		}
		if (requested.containsKey("properties")) {
			s.put("properties", requested.get("properties"));
		}
		if (requested.containsKey("name")) {
			s.put("name", requested.get("name"));
		}
		if (requested.containsKey("description")) {
			s.put("description", requested.get("description"));
		}
		return s;
	}

	@Override
	public Job updateMachine(final Machine machine)
			throws ResourceNotFoundException, CloudProviderException {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO
	public Job updateMachineAttributes(final String machineId,
			final Map<String, Object> attributes)
			throws ResourceNotFoundException, CloudProviderException {

		Job j = null;

		Machine m = getMachineFromId(machineId);

		if (attributes.containsKey("name")) {
			m.setName((String) attributes.get("name"));
		}

		if (attributes.containsKey("description")) {
			m.setDescription((String) attributes.get("description"));
		}

		j = new Job();
		j.setTargetEntity(m);
		j.setStatus(Job.Status.SUCCESS);
		j.setAction("update");
		j.setParentJob(null);
		j.setNestedJobs(null);
		j.setReturnCode(0);
		j.setUser(this.user);
		this.em.persist(j);
		this.em.flush();
		return j;

		// TODO
		// if (attributes.size() > 0) {
		// throw new
		// ServiceUnavailableException(" Come back later for update ");
		// }
		// if (attributes.size() > 1) {
		// throw new InvalidRequestException("May update only one by one ");
		// }
		// Map<String, Object> allowedUpdates = this.filterUpdates(attributes);
		//
		// /** invoke connector */
		// ICloudProviderConnector connector = this.getConnector(m);
		// IComputeService computeService;
		// try {
		// computeService = connector.getComputeService();
		// } catch (ConnectorException e) {
		// throw new ServiceUnavailableException(e.getMessage());
		// }
		//
		// // j = computeService.updateMachine(m, allowedUpdates);
		//
		// // this is getting quite complicated :(
		// return j;
	}

	/**
	 * Operations on MachineCollection
	 */
	public MachineCollection getMachineCollection()
			throws CloudProviderException {
		this.setUser();
		Integer userid = this.user.getId();

		MachineCollection collection = (MachineCollection) this.em
				.createQuery("FROM MachineCollection m WHERE m.user.id=:userid")
				.setParameter("userid", userid).getSingleResult();
		List<Machine> machines = this.em.createQuery("FROM Machine m WHERE m.user.username=:username ORDER BY m.id")
	            .setParameter("username", user.getUsername()).getResultList();
		collection.setMachines(machines);
		return collection;
	}

	/**
	 * Operations on MachineConfiguration
	 */
	public MachineConfiguration getMachineConfigurationById(final String mcId)
			throws CloudProviderException {
		if (mcId == null) {
			throw new InvalidRequestException(" null machine configuration id");
		}
		MachineConfiguration mc = this.em.find(MachineConfiguration.class,
				Integer.valueOf(mcId));
		if (mc == null) {
			throw new ResourceNotFoundException(
					"Unknown machine configuration " + mcId);
		}
		mc.getDiskTemplates().size();
		return mc;
	}

	@Override
	public void updateMachineConfiguration(
			final MachineConfiguration machineConfiguration)
			throws ResourceNotFoundException, InvalidRequestException,
			CloudProviderException {
		// TODO Auto-generated method stub

	}

	public void updateMachineConfigurationAttributes(final String mcId,
			final Map<String, Object> attributes) throws CloudProviderException {
		if ((mcId == null) || (attributes == null)) {
			throw new InvalidRequestException(" null machine configuration id");
		}
		MachineConfiguration mc = this.em.find(MachineConfiguration.class,
				Integer.valueOf(mcId));
		if (mc == null) {
			throw new ResourceNotFoundException(
					"Unknown machine configuration " + mcId);
		}
		if (attributes.containsKey("cpu")) {
			Cpu cpu = (Cpu) attributes.get("cpu");
			mc.setCpu(cpu);
		}
		if (attributes.containsKey("memory")) {
			Memory mem = (Memory) attributes.get("memory");
			mc.setMemory(mem);
		}

		if (attributes.containsKey("disks")) {
			List<DiskTemplate> dts = (List<DiskTemplate>) attributes
					.get("disks");
			mc.setDiskTemplates(dts);
		}

		this.em.flush();
	}

	public void deleteMachineConfiguration(final String mcId)
			throws CloudProviderException {

		MachineConfiguration config = (MachineConfiguration) getObjectFromEM(
				MachineConfiguration.class, mcId);

		List<MachineTemplate> mts = null;
		try {
			/**
			 * Refuse delete if configuration is being used.
			 */
			mts = this.em
					.createQuery(
							"FROM MachineTemplate m WHERE m.machineConfiguration.id=:mcid")
					.setParameter("mcid", Integer.valueOf(mcId))
					.getResultList();
		} catch (Exception e) {
			return;
		}
		if ((mts != null) && (mts.size() > 0)) {
			throw new ResourceConflictException("MachineTemplates "
					+ mts.get(0).getId() + " uses the configuration " + mcId);
		}

		// CHECK that disktemplates are deleted

		config.setUser(null);

		this.em.remove(config);
		this.em.flush();
	}

	/**
	 * Operations on MachineConfigurationCollection
	 */
	public MachineConfigurationCollection getMachineConfigurationCollection()
			throws CloudProviderException {
		this.setUser();
		Integer userid = this.user.getId();
		// There should be only one collection
		MachineConfigurationCollection collection = (MachineConfigurationCollection) this.em
				.createQuery(
						"FROM MachineConfigurationCollection m WHERE m.user.id=:userid")
				.setParameter("userid", userid).getSingleResult();
		List<MachineConfiguration> configs = this.em.createQuery("FROM MachineConfiguration m WHERE m.user.username=:username ORDER BY m.id")
	            .setParameter("username", user.getUsername()).getResultList();
		collection.setMachineConfigurations(configs);
		return collection;
	}

	@Override
	public List<MachineConfiguration> getMachineConfigurations(final int first,
			final int last, final List<String> attributes)
			throws InvalidRequestException, CloudProviderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MachineConfiguration> getMachineConfigurations(
			final List<String> attributes, final String queryExpression)
			throws InvalidRequestException, CloudProviderException {
		// TODO Auto-generated method stub
		return null;
	}

	public MachineConfiguration createMachineConfiguration(
			final MachineConfiguration machineConfig)
			throws CloudProviderException {

		this.setUser();
		Integer userid = this.user.getId();
		this.validateMachineConfiguration(machineConfig);
		boolean exists = true;
		try {
			MachineConfiguration mc = (MachineConfiguration) this.em
					.createQuery(
							"FROM MachineConfiguration m WHERE m.user.id=:userid AND m.name=:name")
					.setParameter("userid", userid)
					.setParameter("name", machineConfig.getName())
					.getSingleResult();
		} catch (NoResultException e) {
			exists = false;
		}
		if (exists == true) {
			throw new CloudProviderException(
					"MachineConfiguration by name already exists "
							+ machineConfig.getName());
		}
		machineConfig.setUser(this.user);
		machineConfig.setCreated(new Date());
		this.em.persist(machineConfig);
		this.em.flush();
		return machineConfig;
	}

	/**
	 * Operations on MachineTemplate
	 */
	public MachineTemplate getMachineTemplateById(final String mtId)
			throws CloudProviderException {
		if (mtId == null) {
			throw new InvalidRequestException(" null machine template id");
		}
		MachineTemplate mt = this.em.find(MachineTemplate.class,
				Integer.valueOf(mtId));
		if (mt == null) {
			throw new ResourceNotFoundException(
					" Could not find machine template" + mtId);
		}
		if ((mt.getVolumes() != null) && (mt.getVolumes().getItems() != null)) {
			mt.getVolumes().getItems().size();
		}
		if ((mt.getVolumeTemplates() != null)
				&& (mt.getVolumeTemplates().getItems() != null)) {
			mt.getVolumeTemplates().getItems().size();
		}
		mt.getNetworkInterfaces().size();

		return mt;
	}

	@Override
	public void updateMachineTemplate(final MachineTemplate machineTemplate)
			throws ResourceNotFoundException, InvalidRequestException,
			CloudProviderException {
		// TODO Auto-generated method stub

	}

	public void updateMachineTemplateAttributes(final String mtId,
			final Map<String, Object> attributes) throws CloudProviderException {
		MachineTemplate mt = null;
		if ((mtId == null) || (attributes == null)) {
			throw new InvalidRequestException(" null machine template id");
		}
		mt = this.em.find(MachineTemplate.class, Integer.valueOf(mtId));
		if (mt == null) {
			throw new ResourceNotFoundException(
					" Could not find machine template" + mtId);
		}
		try {

			if (attributes.containsKey("name")) {
				mt.setName((String) attributes.get("name"));
			}

			if (attributes.containsKey("description")) {
				mt.setDescription((String) attributes.get("description"));
			}

			if (attributes.containsKey("properties")) {
				mt.setProperties((Map<String, String>) attributes
						.get("properties"));
			}
			// Cannot change attributes of original machineConfig
			// only reference is changed
			if (attributes.containsKey("machineConfiguration")) {

				String mc = (String) attributes.get("machineConfiguration");

				MachineConfiguration config = this.em.find(
						MachineConfiguration.class, Integer.valueOf(mc));
				if (config == null) {
					throw new InvalidRequestException(
							" Could not find machine configuration" + mc);
				}
				mt.setMachineConfiguration(config);
			}
			if (attributes.containsKey("machineImage")) {
				String mi = (String) attributes.get("machineImage");

				MachineImage image = this.em.find(MachineImage.class,
						Integer.valueOf(mi));
				if (image == null) {
					throw new InvalidRequestException(
							" Could not find machine image" + mi);
				}
				mt.setMachineImage(image);
			}
			if (attributes.containsKey("credentials")) {
				String credentials = (String) attributes.get("credentials");

				Credentials cred = this.em.find(Credentials.class,
						Integer.valueOf(credentials));
				if (cred == null) {
					throw new InvalidRequestException(
							" Could not find credentials" + credentials);
				}
				mt.setCredentials(cred);
			}

			if (attributes.containsKey("networkInterfaces")) {
				List<NetworkInterface> list = (List<NetworkInterface>) (attributes
						.get("networkInterfaces"));

				/** validate(list); */
				mt.setNetworkInterfaces(list);
			}
			mt.setUpdated(new Date());
			this.em.merge(mt);
			this.em.flush();
		} catch (Exception e) {
			throw new CloudProviderException(e.getMessage());
		}
	}

	private void deleteMachineTemplateFromDb(final MachineTemplate mt) {
		mt.setUser(null);

		MachineVolumeCollection vColl = mt.getVolumes();

		mt.setVolumes(null);
		if (vColl != null) {
			List<MachineVolume> volItems = vColl.getItems();
			if (volItems != null) {
				for (MachineVolume mv : volItems) {
					mv.setMachineVolumeCollection(null);
					mv.setVolume(null);
					this.em.remove(mv);
				}
			}
			this.em.remove(vColl);
		}

		MachineVolumeTemplateCollection vtColl = mt.getVolumeTemplates();
		mt.setVolumeTemplates(null);
		if (vtColl != null) {
			Collection<MachineVolumeTemplate> vtItems = vtColl.getItems();
			if (vtItems != null) {
				for (MachineVolumeTemplate mvt : vtItems) {
					mvt.setMachineVolumeTemplateCollection(null);
					mvt.setVolumeTemplate(null);
					this.em.remove(mvt);
				}
			}
			this.em.remove(vtColl);
		}

		this.em.remove(mt);
		this.em.flush();
	}

	public void deleteMachineTemplate(final String mtId)
			throws CloudProviderException {
		this.setUser();

		MachineTemplate mt = this.em.find(MachineTemplate.class,
				Integer.valueOf(mtId));
		if (mt == null) {
			throw new ResourceNotFoundException("Cannot find machine template "
					+ mtId);
		}
		if (mt.getUser().equals(this.user) == false) {
			throw new CloudProviderException(
					"Not owner, cannot delete machine template ");
		}

		this.deleteMachineTemplateFromDb(mt);

	}

	/**
	 * For initial creation of machine template
	 */
	private void createVolumeTemplateCollectionForMt(MachineTemplate mt)
			throws CloudProviderException {

		MachineVolumeTemplateCollection vtColl = mt.getVolumeTemplates();
		if (vtColl == null) {
			vtColl = new MachineVolumeTemplateCollection();
		}

		mt.setVolumeTemplates(vtColl);
		this.em.persist(vtColl);

		if ((vtColl.getItems() == null) || (vtColl.getItems().size() == 0)) {
			return;
		}

		Collection<MachineVolumeTemplate> volumeTemplates = vtColl.getItems();

		for (MachineVolumeTemplate mvt : volumeTemplates) {

			if (mvt.getInitialLocation() == null) {
				// ignore this entry
				continue;
			}

			VolumeTemplate vt = mvt.getVolumeTemplate();
			if ((vt == null) || (vt.getId() == null)) {
				// ignore this entry
				continue;
			}

			// TODO volume manager
			VolumeTemplate vtt = null;
			try {
				vtt = (VolumeTemplate) getObjectFromEM(VolumeTemplate.class, vt
						.getId().toString());
			} catch (CloudProviderException e) {
				MachineManager.logger
						.info(" Incorrect volume template being attached to machine template "
								+ vt.getId() + " ignoring ");
				continue;
			}

			// TODO unidirectional
			mvt.setVolumeTemplate(vtt);
			mvt.setMachineVolumeTemplateCollection(vtColl);
			this.em.persist(mvt);
		}
	}

	private void createVolumeCollectionForMt(MachineTemplate mt)
			throws CloudProviderException {

		MachineVolumeCollection volColl = mt.getVolumes();
		if (volColl == null) {
			volColl = new MachineVolumeCollection();
		}

		mt.setVolumes(volColl);
		this.em.persist(volColl);

		if ((volColl.getItems() == null) || (volColl.getItems().size() == 0)) {
			return;
		}

		List<MachineVolume> volumes = volColl.getItems();

		for (MachineVolume mv : volumes) {

			if (mv.getInitialLocation() == null) {
				// ignore this entry
				continue;
			}

			Volume v = mv.getVolume();
			if ((v == null) || (v.getId() == null)) {
				// ignore this entry
				continue;
			}

			// TODO volume manager
			Volume vv = null;
			try {
				vv = (Volume) this.em.find(Volume.class, v.getId());
			} catch (Exception e) {
				MachineManager.logger
						.info(" Incorrect volume being attached to machine template "
								+ v.getId() + " ignoring ");
				continue;
			}

			// TODO unidirectional
			mv.setVolume(vv);
			mv.setMachineVolumeCollection(volColl);
			this.em.persist(mv);
		}
	}

	/**
	 * All checks done in CIMI REST layer: REST Layer has validated that
	 * referenced MachineConfiguration etc do really exist.
	 */
	public MachineTemplate createMachineTemplate(final MachineTemplate mt)
			throws CloudProviderException {

		this.setUser();
		Integer userid = this.user.getId();
		boolean exists = true;
		try {
			MachineTemplate mtemplate = (MachineTemplate) this.em
					.createQuery(
							"FROM MachineTemplate m WHERE m.user.id=:userid AND m.name=:name")
					.setParameter("userid", userid)
					.setParameter("name", mt.getName()).getSingleResult();
		} catch (NoResultException e) {
			exists = false;
		}
		if (exists == true) {
			throw new InvalidRequestException(
					"MachineTemplate by name already exists " + mt.getName());
		}
		MachineConfiguration mc = mt.getMachineConfiguration();
		if (mc == null) {
			throw new InvalidRequestException("No machineconfiguration ");
		}
		MachineConfiguration mc1 = this.em.find(MachineConfiguration.class,
				Integer.valueOf(mc.getId()));
		if (mc1 == null) {
			throw new InvalidRequestException(
					"Invalid reference to machine configuraiton " + mc.getId());
		}
		this.validateMachineConfiguration(mt.getMachineConfiguration());

		/**
		 * create volume and volume template collection.
		 */
		createVolumeCollectionForMt(mt);
		createVolumeTemplateCollectionForMt(mt);

		this.validateNetworkInterface(mt.getNetworkInterfaces());
		mt.setUser(this.user);
		mt.setCreated(new Date());
		this.em.persist(mt);
		this.em.flush();
		return mt;
	}

	public MachineTemplateCollection getMachineTemplateCollection()
			throws CloudProviderException {
		this.setUser();
		Integer userid = this.user.getId();

		MachineTemplateCollection collection = (MachineTemplateCollection) this.em
				.createQuery(
						"FROM MachineTemplateCollection m WHERE m.user.id=:userid")
				.setParameter("userid", userid).getSingleResult();
		List<MachineTemplate> templates = this.em.createQuery("FROM MachineTemplate m WHERE m.user.username=:username ORDER BY m.id")
	            .setParameter("username", user.getUsername()).getResultList();
		collection.setMachineTemplates(templates);
		return collection;
	}

	@Override
	public List<MachineTemplate> getMachineTemplates(final int first,
			final int last, final List<String> attributes)
			throws InvalidRequestException, CloudProviderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MachineTemplate> getMachineTemplates(
			final List<String> attributes, final String queryExpression)
			throws InvalidRequestException, CloudProviderException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * To complete:
	 */
	private void validateMachineConfiguration(final MachineConfiguration mc)
			throws CloudProviderException {

		if (mc.getCpu() == null) {
			throw new InvalidRequestException(" Cpu attribute should be set");
		}
		/** cpu values */
		if (mc.getCpu().getNumberCpu() < 0) {
			throw new InvalidRequestException("Incorrect MachineConfiguration ");
		}
		if (mc.getCpu().getQuantity() < 0) {
			throw new InvalidRequestException("Incorrect MachineConfiguration ");
		}

		if (mc.getMemory() == null) {
			throw new InvalidRequestException(" Memory attribute should be set");
		}
		/** memory values */
		if (mc.getMemory().getQuantity() < 0) {
			throw new InvalidRequestException("Incorrect MachineConfiguration ");
		}

		/** disk */
		List<DiskTemplate> disks = mc.getDiskTemplates();
		if (disks == null) {
			return;
		}
		if (disks.size() == 0) {
			return;
		}
		for (DiskTemplate d : disks) {
			if (d.getQuantity() < 0) {
				throw new InvalidRequestException(
						"Incorrect MachineConfiguration ");
			}
			if (d.getFormat() == null) {
				throw new InvalidRequestException(
						"Incorrect MachineConfiguration format should be set ");
			}
			// TODO initialLocation
		}
	}

	private void removeMachine(final Machine deleted) {
		MachineManager.logger.info(" deleting machine " + deleted.getId());
		User u = deleted.getUser();
		
		u.getCloudEntities().remove(deleted);
		deleted.setCloudProviderAccount(null);

		/**
		 * TODO: CHECK what to do with the volumes? Should they be deleted?
		 */
		MachineVolumeCollection volColl = deleted.getVolumes();
		MachineDiskCollection diskColl = deleted.getDisks();

		deleted.setVolumes(null);
		deleted.setDisks(null);

		if (volColl != null) {
			List<MachineVolume> volItems = volColl.getItems();
			if (volItems != null) {
				for (MachineVolume mv : volItems) {
					mv.setMachineVolumeCollection(null);
					mv.setVolume(null);
					this.em.remove(mv);
				}
			}
			this.em.remove(volColl);
		}

		if (diskColl != null) {
			this.em.remove(diskColl);
		}
		this.em.remove(deleted);
		this.em.flush();
	}

	/**
	 * Initialize volumes and disks for newly created machine TODO Check
	 */
	private void initializeStorage(Machine persisted, Machine created) {
		MachineVolumeCollection volumeColl = new MachineVolumeCollection();
		MachineDiskCollection diskColl = new MachineDiskCollection();

		if (persisted.getVolumes() != null) {
			MachineManager.logger.info(" strange! machine " + persisted.getId()
					+ " already has a volume collection ");
		}
		if (persisted.getDisks() != null) {
			MachineManager.logger.info(" strange! machine " + persisted.getId()
					+ " already has a disk collection ");
		}
		
		this.em.persist(volumeColl);
		this.em.persist(diskColl);
		this.em.flush();
		persisted.setVolumes(volumeColl);
		persisted.setDisks(diskColl);

		List<MachineVolume> vItems = created.getVolumes().getItems();
		List<MachineDisk> dItems = created.getDisks().getItems();

		if (vItems != null) {
			for (MachineVolume mv : vItems) {
				mv.setMachineVolumeCollection(volumeColl);
				this.em.persist(mv);
			}
		}

		if (dItems != null) {
			diskColl.setItems(dItems);
		}

		
		this.em.flush();
	}

	/**
	 * Handler job completions
	 */

	public boolean machineCompletionHandler(final Job notification) {

		/**
		 * Find the machine by providerAssignedId (or the job as well)
		 */
		String jid = notification.getProviderAssignedId().toString();
		/** providerAssignedMachineId */
		String pamid = notification.getTargetEntity().getId().toString();
		Job jpersisted = null;
		MachineManager.logger.info(" Notification for job "
				+ notification.getProviderAssignedId().toString() + " " + pamid);
		try {
			jpersisted = (Job) this.em
					.createQuery("FROM Job j WHERE j.providerAssignedId=:jid")
					.setParameter("jid", jid).getSingleResult();
		} catch (NoResultException e) {
			/** ignore for now */
			MachineManager.logger.info("Cannot find job for machine" + pamid);
			return false;
		} catch (NonUniqueResultException e) {
			MachineManager.logger.info("No single job for machine !!" + pamid);
			return false;
		} catch (Exception e) {
			MachineManager.logger
					.info("Internal error in finding job for machine" + pamid);
			return false;
		}
		Machine mpersisted = null;

		try {
			if (jpersisted == null) {
				/**
				 * find the machine from its providerAssignedId in fact there
				 * could be more than one machine with same same
				 * providerAssignedId?
				 */
				mpersisted = (Machine) this.em
						.createQuery(
								"FROM Machine m WHERE m.providerAssignedId=:pamid")
						.setParameter("pamid", pamid).getSingleResult();

			} else {
				/** find the machine from its id */
				Integer mid = Integer.valueOf(jpersisted.getTargetEntity().getId().toString());
				mpersisted = this.em.find(Machine.class, mid);
			}

		} catch (NoResultException e) {
			MachineManager.logger.info("Could not find the machine or job for "
					+ pamid);
			return false;
		} catch (NonUniqueResultException e) {
			MachineManager.logger.info("Multiple machines found for " + pamid);
			return false;
		} catch (Exception e) {
			MachineManager.logger
					.info("Unknown error : Could not find the machine or job for "
							+ pamid);
			return false;
		}

		/** update the machine by invoking the connector */
		CloudProviderAccount cpa = mpersisted.getCloudProviderAccount();
		CloudProviderLocation loc = mpersisted.getLocation();
		ICloudProviderConnector connector;
		try {
			connector = this.getCloudProviderConnector(cpa, loc);
		} catch (CloudProviderException e) {
			/** no point to return false? */
			MachineManager.logger.info("Could not get cloud connector "
					+ e.getMessage());
			return false;
		}
		String connectorid = connector.getCloudProviderId();
		IComputeService computeService = null;
		Machine updated = null;
		try {
			computeService = connector.getComputeService();
			updated = computeService.getMachine(mpersisted
					.getProviderAssignedId());
		} catch (ConnectorException e) {
			MachineManager.logger.info(" Could not get compute service "
					+ e.getMessage());
			return false;
		}

		if (updated == null) {
			// TODO : what to do?
		}

		try {
			this.relConnector(mpersisted, connector);
		} catch (CloudProviderException e) {
			MachineManager.logger.info("Error in releasing connector "
					+ e.getMessage());
		}

		if ((notification.getAction().equals("machine.create"))) {
			mpersisted.setCreated(new Date());
		} else if ((notification.getAction().equals("machine.delete"))) {
			/**
			 * delete the machine locally if the delete had correctly completed
			 */
			MachineManager.logger.info("machine deleted ok "
					+ mpersisted.getId());
			this.removeMachine(mpersisted);
			/** job will get removed at retention timeout */

		} else {
			mpersisted.setUpdated(new Date());
		}

		if (updated != null) { // paranoia
			mpersisted.setCpu(updated.getCpu());
			mpersisted.setNetworkInterfaces(updated.getNetworkInterfaces());
			mpersisted.setState(updated.getState());

			initializeStorage(mpersisted, updated);

		}
		/**
		 * Update the job
		 */

		jpersisted.setStatus(notification.getStatus());
		jpersisted.setReturnCode(notification.getReturnCode());
		jpersisted.setStatusMessage(notification.getStatusMessage());
		jpersisted.setTimeOfStatusChange(notification.getTimeOfStatusChange());

		// TODO JobManager (connector side) should delete job
		this.em.flush();
		return true;
	}

	private void validateCredentials(final Credentials cred)
			throws CloudProviderException {

		// if (cred.getKey().length == 0) {
		// throw new
		// CloudProviderException("Incorrect credentials key length ");
		// }

	}

	private void validateNetworkInterface(final List<NetworkInterface> nics)
			throws CloudProviderException {

	}

	private void validateVolumeTemplates(
			final List<VolumeTemplate> volumeTemplates)
			throws CloudProviderException {

	}

	private Object getObjectFromEM(Class targetClass, String id)
			throws InvalidRequestException, ResourceNotFoundException {
		if (id == null) {
			throw new InvalidRequestException(" null resource id");
		}
		Object o = this.em.find(targetClass, Integer.valueOf(id));
		if (o == null) {
			throw new ResourceNotFoundException(" Invalid id " + id);
		}
		return o;
	}

	public List<MachineVolumeTemplate> getMachineVolumeTemplates(final String mtId) throws ResourceNotFoundException,
			CloudProviderException, InvalidRequestException {

		MachineTemplate mt = (MachineTemplate) getObjectFromEM(MachineTemplate.class, mtId);
		MachineVolumeTemplateCollection volTemplateColl = mt.getVolumeTemplates();
		
		if (volTemplateColl.getItems() != null) {
			volTemplateColl.getItems().size();
		}
		return volTemplateColl.getItems();
	}

	public List<MachineVolume> getMachineVolumes(final String machineId) throws ResourceNotFoundException,
			CloudProviderException, InvalidRequestException {

		Machine m = getMachineById(machineId);
		
		MachineVolumeCollection volColl = m.getVolumes();
		if (volColl.getItems() != null) {
			volColl.getItems().size();
		}
		return volColl.getItems();
	}

	private Job addVolumeToMachine(Machine m, final String volumeId,
			final String initialLocation) throws ResourceNotFoundException,
			CloudProviderException, InvalidRequestException {

		/**
		 * Allow operation only in STARTED or STOPPED states.
		 */
		if ((m.getState() != Machine.State.STARTED)
				|| (m.getState() != Machine.State.STOPPED)) {
			throw new InvalidRequestException(
					"Can add volume only in started or stopped state "
							+ m.getState());
		}

		MachineVolumeCollection volColl = m.getVolumes();
		if (volColl == null) {
			throw new CloudProviderException(
					" No machine volume collection for " + m.getId());
		}
		if (volColl.getItems() != null) {
			volColl.getItems().size();
		}

		/**
		 * TODO: volumeManager
		 */
		Volume volume = this.em.find(Volume.class, Integer.valueOf(volumeId));
		if (volume == null) {
			throw new ResourceNotFoundException(" Invalid volume id "
					+ volumeId);
		}

		MachineVolume mv = new MachineVolume();
		mv.setVolume(volume);
		mv.setInitialLocation(initialLocation);

		/**
		 * Invoke the connector to add volume to machine
		 */
		ICloudProviderConnector connector = getConnector(m);
		IComputeService computeService;

		try {
			computeService = connector.getComputeService();
		} catch (ConnectorException e) {
			String eee = e.getMessage();
			throw new ServiceUnavailableException(" " + eee
					+ " adding volume to machine " + m.getId() + " "
					+ m.getProviderAssignedId());
		}

		/** TODO : what will be action, targetEntity and affectedEntities */
		/**
		 * action = addVolume targetEntity = machine affectedEntity = volume
		 * and/or machinevolume
		 */
		Job j = null;
		try {
			j = computeService
					.addVolumeToMachine(m.getProviderAssignedId(), mv);
		} catch (ConnectorException e) {
			throw new ServiceUnavailableException(e.getMessage()
					+ " in add volume to machine " + m.getId());
		}
		// REMOVE
		if (j == null) {
			throw new CloudProviderException("Could not add volume to machine "
					+ m.getId());
		}
		if (j.getStatus() == Job.Status.FAILED) {
			throw new CloudProviderException("Could not add volume to machine "
					+ m.getId());
		}
		// TODO if status is RUNNING then update database in completion handler
		if (j.getStatus() == Job.Status.SUCCESS) {
			mv.setMachineVolumeCollection(volColl);
			this.em.persist(mv);
			this.em.flush();
		}
		Job persisted = createJob(m, j);
		return persisted;
	}

	public Job addVolumeToMachine(final String machineId,
			final String volumeId, final String initialLocation)
			throws ResourceNotFoundException, CloudProviderException,
			InvalidRequestException {
		if ((machineId == null) || (volumeId == null)
				|| (initialLocation == null)) {
			throw new InvalidRequestException(" null arguments ");
		}
		Machine m = getMachineFromId(machineId);
		return addVolumeToMachine(m, volumeId, initialLocation);
	}

	private void addVolumeToMachineTemplate(MachineTemplate mt,
			String volumeId, String initialLocation)
			throws ResourceNotFoundException, InvalidRequestException,
			CloudProviderException {

		MachineVolumeCollection volColl = mt.getVolumes();
		if (volColl == null) {
			throw new CloudProviderException(
					" No machine volume collection for " + mt.getId());
		}
		if (volColl.getItems() != null) {
			volColl.getItems().size();
		}

		/**
		 * TODO: volumeManager
		 */
		Volume volume = this.em.find(Volume.class, Integer.valueOf(volumeId));
		if (volume == null) {
			throw new ResourceNotFoundException(" Invalid volume id "
					+ volumeId);
		}

		MachineVolume mv = new MachineVolume();

		mv.setVolume(volume);
		mv.setInitialLocation(initialLocation);
		mv.setMachineVolumeCollection(volColl);

		this.em.persist(mv);
		this.em.flush();
	}

	public void addVolumeToMachineTemplate(final String mtId,
			final String volumeId, final String initialLocation)
			throws ResourceNotFoundException, CloudProviderException,
			InvalidRequestException {
		if ((mtId == null) || (volumeId == null) || (initialLocation == null)) {
			throw new InvalidRequestException(" null arguments ");
		}
		MachineTemplate mt = (MachineTemplate) getObjectFromEM(
				MachineTemplate.class, mtId);

		MachineVolumeCollection volColl = mt.getVolumes();
		if (volColl == null) {
			throw new InvalidRequestException(
					" MachineTemplate does not have volumes " + mtId);
		}
		addVolumeToMachineTemplate(mt, volumeId, initialLocation);
	}


	private void addVolumeTemplateToMachineTemplate(MachineTemplate mt,
			String vtId, String initialLocation)
			throws ResourceNotFoundException, InvalidRequestException,
			CloudProviderException {
		MachineVolumeTemplateCollection vtColl = mt.getVolumeTemplates();
		if (vtColl == null) {
			throw new CloudProviderException(
					" No machine volume template collection for " + mt.getId());
		}

		/**
		 * TODO: volumeManager
		 */
		VolumeTemplate vt = this.em.find(VolumeTemplate.class,
				Integer.valueOf(vtId));
		if (vt == null) {
			throw new ResourceNotFoundException(" Invalid volume template id "
					+ vt);
		}

		MachineVolumeTemplate mvt = new MachineVolumeTemplate();

		mvt.setVolumeTemplate(vt);
		mvt.setInitialLocation(initialLocation);
		mvt.setMachineVolumeTemplateCollection(vtColl);

		this.em.persist(mvt);
		this.em.flush();
	}

	public void addVolumeTemplateToMachineTemplate(final String mtId,
			final String vtId, final String initialLocation)
			throws ResourceNotFoundException, CloudProviderException,
			InvalidRequestException {
		if ((mtId == null) || (vtId == null) || (initialLocation == null)) {
			throw new InvalidRequestException(" null argument ");
		}

		MachineTemplate mt = (MachineTemplate) getObjectFromEM(
				MachineTemplate.class, mtId);

		MachineVolumeCollection volColl = mt.getVolumes();
		if (volColl == null) {
			throw new InvalidRequestException(
					" MachineTemplate does not have volumes " + mtId);
		}
		addVolumeTemplateToMachineTemplate(mt, vtId, initialLocation);
	}

	public void removeVolumeFromMachineTemplate(String mtId, String mvId)
			throws ResourceNotFoundException, CloudProviderException,
			InvalidRequestException {
		if ((mtId == null) || (mvId == null)) {
			throw new InvalidRequestException(" null argument ");
		}
		MachineTemplate mt = (MachineTemplate) getObjectFromEM(
				MachineTemplate.class, mtId);
		MachineVolumeCollection vColl = mt.getVolumes();

		MachineVolume mv = (MachineVolume) getObjectFromEM(MachineVolume.class,
				mvId);
		if ((vColl.getItems() == null) || (vColl.getItems().size() == 0)) {
			throw new CloudProviderException("Error: volume collection for "
					+ mtId + " is empty ");
		}
		List<MachineVolume> items = vColl.getItems();
		items.size();
		if (items.contains(mv) == false) {
			throw new InvalidRequestException(
					" removing invalid machine volume " + mvId
							+ " from machine template " + mtId);
		}

		mv.setMachineVolumeCollection(null);
		mv.setVolume(null);
		this.em.remove(mv);
		this.em.flush();
	}

	public void removeVolumeTemplateFromMachineTemplate(String mtId,
			String mvtId) throws ResourceNotFoundException,
			CloudProviderException, InvalidRequestException {
		if ((mtId == null) || (mvtId == null)) {
			throw new InvalidRequestException(" null argument ");
		}
		MachineTemplate mt = (MachineTemplate) getObjectFromEM(
				MachineTemplate.class, mtId);
		MachineVolumeTemplateCollection vtColl = mt.getVolumeTemplates();

		MachineVolumeTemplate mvt = (MachineVolumeTemplate) getObjectFromEM(
				MachineVolumeTemplate.class, mvtId);
		if ((vtColl.getItems() == null) || (vtColl.getItems().size() == 0)) {
			throw new CloudProviderException(
					"Error: volume template collection for " + mtId
							+ " is empty ");
		}
		Collection<MachineVolumeTemplate> items = vtColl.getItems();
		items.size();
		if (items.contains(mvt) == false) {
			throw new InvalidRequestException(
					" removing invalid machine volume template " + mvtId
							+ " from machine template " + mtId);
		}

		mvt.setMachineVolumeTemplateCollection(null);
		mvt.setVolumeTemplate(null);
		this.em.remove(mvt);
		this.em.flush();
	}

	public Job removeVolumeFromMachine(String machineId, String mvId)
			throws ResourceNotFoundException, CloudProviderException,
			InvalidRequestException {

		if ((machineId == null) || (mvId == null)) {
			throw new InvalidRequestException(" null arguments ");
		}
		Machine m = getMachineFromId(machineId);
		MachineVolumeCollection volColl = m.getVolumes();

		if ((volColl == null) || (volColl.getItems() == null)
				|| (volColl.getItems().size() == 0)) {
			throw new CloudProviderException(
					" No machine volume collection for " + m.getId());
		}
		MachineVolume mv = (MachineVolume) getObjectFromEM(MachineVolume.class,
				mvId);
		List<MachineVolume> items = volColl.getItems();
		items.size();
		if (items.contains(mv) == false) {
			throw new InvalidRequestException(
					" removing invalid machine volume " + mvId
							+ " from machine  " + machineId);
		}

		/**
		 * Invoke the connector to add volume to machine
		 */
		ICloudProviderConnector connector = getConnector(m);
		IComputeService computeService;

		try {
			computeService = connector.getComputeService();
		} catch (ConnectorException e) {
			String eee = e.getMessage();
			throw new ServiceUnavailableException(" " + eee
					+ " adding volume to machine " + m.getId() + " "
					+ m.getProviderAssignedId());
		}

		/**
		 * action = addVolume targetEntity = machine affectedEntity = volume
		 * and/or machinevolume
		 */
		Job j = null;
		try {
			j = computeService.removeVolumeFromMachine(
					m.getProviderAssignedId(), mv);
		} catch (ConnectorException e) {
			throw new ServiceUnavailableException(e.getMessage()
					+ " in remove volume from machine " + m.getId());
		}
		if (j == null) {
			MachineManager.logger.info("REMOVE THIS CHECK ");
			throw new ServiceUnavailableException(
					" in remove volume from machine " + m.getId());
		}
		if (j.getStatus() == Job.Status.FAILED) {
			throw new CloudProviderException("Could not add volume to machine "
					+ m.getId());
		}
		if (j.getStatus() == Job.Status.SUCCESS) {
			mv.setMachineVolumeCollection(null);
			mv.setVolume(null);
			this.em.remove(mv);
			this.em.flush();
		}
		Job persisted = createJob(m, j);
		return persisted;
	}

	// TODO
	public Job updateMachineVolume(String machineId, MachineVolume mVol)
			throws ResourceNotFoundException, CloudProviderException,
			InvalidRequestException {
		throw new ServiceUnavailableException(" Operation not permitted ");
	}
}