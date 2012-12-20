package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.glassfish.osgicdi.OSGiService;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactoryFinder;
import org.ow2.sirocco.cloudmanager.connector.api.INetworkService;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteNetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.AddressCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.AddressTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkNetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteNetworkManager.class)
@Local(INetworkManager.class)
public class NetworkManager implements INetworkManager {
    private static Logger logger = LoggerFactory.getLogger(NetworkManager.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Resource
    private EJBContext context;

    @Inject
    @OSGiService(dynamic = true)
    private ICloudProviderConnectorFactoryFinder connectorFactoryFinder;

    @EJB
    private IUserManager userManager;

    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount)
        throws CloudProviderException {
        NetworkManager.logger.info("Getting connector for cloud provider type "
            + cloudProviderAccount.getCloudProvider().getCloudProviderType());
        ICloudProviderConnectorFactory connectorFactory = this.connectorFactoryFinder
            .getCloudProviderConnectorFactory(cloudProviderAccount.getCloudProvider().getCloudProviderType());
        if (connectorFactory == null) {
            NetworkManager.logger.error("Cannot find connector for cloud provider type "
                + cloudProviderAccount.getCloudProvider().getCloudProviderType());
            return null;
        }
        try {
            return connectorFactory.getCloudProviderConnector(cloudProviderAccount, null);
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }
    }

    private User getUser() throws CloudProviderException {
        String username = this.context.getCallerPrincipal().getName();
        User user = this.userManager.getUserByUsername(username);
        if (user == null) {
            throw new CloudProviderException("unknown user: " + username);
        }
        return user;
    }

    //
    // Network operations
    //

    @Override
    public Job createNetwork(final NetworkCreate networkCreate) throws InvalidRequestException, CloudProviderException {
        NetworkManager.logger.info("Creating Network");

        // retrieve user
        User user = this.getUser();

        // pick up first cloud provider account associated with user
        if (user.getCloudProviderAccounts().isEmpty()) {
            throw new CloudProviderException("No cloud provider account for user " + user.getUsername());
        }
        CloudProviderAccount defaultAccount = user.getCloudProviderAccounts().iterator().next();
        ICloudProviderConnector connector = this.getCloudProviderConnector(defaultAccount);
        if (connector == null) {
            throw new CloudProviderException("Cannot find cloud provider connector "
                + defaultAccount.getCloudProvider().getCloudProviderType());
        }

        // delegates network creation to cloud provider connector
        Job providerJob = null;

        try {
            INetworkService networkService = connector.getNetworkService();
            providerJob = networkService.createNetwork(networkCreate);
        } catch (ConnectorException e) {
            NetworkManager.logger.error("Failed to create network: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        // prepare the Network entity to be persisted

        Network network = new Network();
        network.setName(networkCreate.getName());
        network.setDescription(networkCreate.getDescription());
        network.setProperties(networkCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(networkCreate.getProperties()));
        network.setUser(user);

        network.setProviderAssignedId(providerJob.getTargetResource().getProviderAssignedId());
        network.setCloudProviderAccount(defaultAccount);

        network.setMtu(networkCreate.getNetworkTemplate().getNetworkConfig().getMtu());
        network.setClassOfService(networkCreate.getNetworkTemplate().getNetworkConfig().getClassOfService());
        network.setNetworkType(networkCreate.getNetworkTemplate().getNetworkConfig().getNetworkType());
        network.setForwardingGroup(networkCreate.getNetworkTemplate().getForwardingGroup());

        network.setState(Network.State.CREATING);
        this.em.persist(network);
        this.em.flush();

        Job job = new Job();
        job.setTargetResource(network);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(network);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setState(providerJob.getState());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            UtilsForManagers.emitJobListenerMessage(providerJob.getProviderAssignedId(), this.context);
        } catch (Exception e) {
            NetworkManager.logger.error(e.getMessage(), e);
        }
        return job;
    }

    @Override
    public Job startNetwork(final String networkId) throws ResourceNotFoundException, CloudProviderException {
        return this.performActionOnNetwork(networkId, "start");
    }

    @Override
    public Job startNetwork(final String networkId, final Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException {
        return this.performActionOnNetwork(networkId, "start");
    }

    @Override
    public Job stopNetwork(final String networkId) throws ResourceNotFoundException, CloudProviderException {
        return this.performActionOnNetwork(networkId, "stop");
    }

    @Override
    public Job stopNetwork(final String networkId, final Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException {
        return this.performActionOnNetwork(networkId, "stop");
    }

    @Override
    public Network getNetworkById(final String networkId) throws ResourceNotFoundException {
        Network network = this.em.find(Network.class, Integer.valueOf(networkId));
        if (network == null || network.getState() == Network.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid network id " + networkId);
        }
        return network;
    }

    @Override
    public Network getNetworkAttributes(final String networkId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkById(networkId);
    }

    @Override
    public List<Network> getNetworks() throws CloudProviderException {
        return UtilsForManagers.getEntityList("Network", this.em, this.getUser().getUsername());
    }

    @SuppressWarnings("unchecked")
    @Override
    public QueryResult<Network> getNetworks(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("Network", Network.class, this.em, user.getUsername(), first, last, filters,
            attributes, true);
    }

    @Override
    public Job updateNetwork(final Network network) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        Network networkInDb = this.getNetworkById(network.getId().toString());
        if (networkInDb == null) {
            throw new ResourceNotFoundException("Network " + network.getId() + " doesn't not exist");
        }
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Job updateNetworkAttributes(final String networkId, final Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        Network networkInDb = this.getNetworkById(networkId);
        if (networkInDb == null) {
            throw new ResourceNotFoundException("Network " + networkId + " doesn't not exist");
        }
        // TODO
        throw new UnsupportedOperationException();
    }

    private Job performActionOnNetwork(final String networkId, final String action) throws ResourceNotFoundException,
        CloudProviderException {
        Network network = this.getNetworkById(networkId);
        if (network == null) {
            throw new ResourceNotFoundException("Network " + networkId + " doesn't not exist");
        }

        // delegates volume deletion to cloud provider connector
        ICloudProviderConnector connector = this.getCloudProviderConnector(network.getCloudProviderAccount());
        Job providerJob = null;

        try {
            INetworkService networkService = connector.getNetworkService();
            if (action.equals("delete")) {
                providerJob = networkService.deleteNetwork(network.getProviderAssignedId());
            } else if (action.equals("start")) {
                providerJob = networkService.startNetwork(network.getProviderAssignedId());
            } else if (action.equals("stop")) {
                providerJob = networkService.stopNetwork(network.getProviderAssignedId());
            }
        } catch (ConnectorException e) {
            NetworkManager.logger.error("Failed to " + action + " network: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        // if by change the job is done and has failed, bail out
        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        if (action.equals("delete")) {
            network.setState(Network.State.DELETING);
        } else if (action.equals("start")) {
            network.setState(Network.State.STARTING);
        } else if (action.equals("stop")) {
            network.setState(Network.State.STOPPING);
        }

        this.em.persist(network);
        this.em.flush();

        Job job = new Job();
        job.setTargetResource(network);
        job.setCreated(new Date());
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setState(providerJob.getState());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            UtilsForManagers.emitJobListenerMessage(providerJob.getProviderAssignedId(), this.context);
        } catch (Exception e) {
            NetworkManager.logger.error("", e);
        }
        return job;
    }

    @Override
    public Job deleteNetwork(final String networkId) throws ResourceNotFoundException, CloudProviderException {
        return this.performActionOnNetwork(networkId, "delete");
    }

    @Override
    public Job addNetworkPortToNetwork(final String networkId, final NetworkNetworkPort networkPort)
        throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job removeNetworkPortFromNetwork(final String networkId, final String networkNetworkPortId)
        throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NetworkNetworkPort getNetworkPortFromNetwork(final String networkId, final String networkNetworkPortId)
        throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<NetworkNetworkPort> getNetworkNetworkPorts(final String networkId) throws ResourceNotFoundException,
        CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryResult<NetworkNetworkPort> getNetworkNetworkPorts(final String networkId, final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateNetworkPortInNetwork(final String networkId, final NetworkNetworkPort networkPort)
        throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    //
    // NetworkConfiguration operations
    //

    @Override
    public NetworkConfiguration createNetworkConfiguration(final NetworkConfiguration networkConfig)
        throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();

        if (networkConfig.getName() != null) {
            if (!this.em.createQuery("FROM NetworkConfiguration v WHERE v.user.username=:username AND v.name=:name")
                .setParameter("username", user.getUsername()).setParameter("name", networkConfig.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("NetworkConfiguration already exists with name " + networkConfig.getName());
            }
        }
        networkConfig.setUser(user);
        networkConfig.setCreated(new Date());
        this.em.persist(networkConfig);
        this.em.flush();
        return networkConfig;
    }

    @Override
    public List<NetworkConfiguration> getNetworkConfigurations() throws CloudProviderException {
        return this.em.createQuery("FROM NetworkConfiguration v WHERE v.user.username=:username ORDER BY v.id")
            .setParameter("username", this.getUser().getUsername()).getResultList();
    }

    @Override
    public NetworkConfiguration getNetworkConfigurationById(final String networkConfigId) throws ResourceNotFoundException {
        NetworkConfiguration networkConfig = this.em.find(NetworkConfiguration.class, Integer.valueOf(networkConfigId));
        if (networkConfig == null) {
            throw new ResourceNotFoundException(" Invalid networkConfig id " + networkConfigId);
        }
        return networkConfig;
    }

    @Override
    public NetworkConfiguration getNetworkConfigurationAttributes(final String networkConfigId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkConfigurationById(networkConfigId);
    }

    @Override
    public QueryResult<NetworkConfiguration> getNetworkConfigurations(final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("NetworkConfiguration", NetworkConfiguration.class, this.em, user.getUsername(),
            first, last, filters, attributes, false);
    }

    @Override
    public void updateNetworkConfiguration(final NetworkConfiguration networkConfig) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNetworkConfigurationAttributes(final String networkConfigId, final Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteNetworkConfiguration(final String networkConfigId) throws ResourceNotFoundException,
        CloudProviderException, ResourceConflictException {
        if (!this.em.createQuery("FROM NetworkTemplate n WHERE n.networkConfig.id=:networkConfigId")
            .setParameter("networkConfigId", Integer.valueOf(networkConfigId)).getResultList().isEmpty()) {
            throw new ResourceConflictException("Cannot delete NetworkConfiguration with id " + networkConfigId
                + " used by a NetworkTemplate");
        }
        NetworkConfiguration networkConfig = this.em.find(NetworkConfiguration.class, Integer.valueOf(networkConfigId));
        if (networkConfig == null) {
            throw new CloudProviderException("NetworkConfiguration does't exist with id " + networkConfigId);
        }
        this.em.remove(networkConfig);
    }

    //
    // NetworkTemplate operations
    //

    @Override
    public NetworkTemplate createNetworkTemplate(final NetworkTemplate networkTemplate) throws InvalidRequestException,
        CloudProviderException {
        User user = this.getUser();

        if (networkTemplate.getName() != null) {
            if (!this.em.createQuery("FROM NetworkTemplate v WHERE v.user.username=:username AND v.name=:name")
                .setParameter("username", user.getUsername()).setParameter("name", networkTemplate.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("NetworkTemplate already exists with name " + networkTemplate.getName());
            }
        }
        networkTemplate.setUser(user);
        networkTemplate.setCreated(new Date());
        this.em.persist(networkTemplate);
        this.em.flush();
        return networkTemplate;
    }

    @Override
    public List<NetworkTemplate> getNetworkTemplates() throws CloudProviderException {
        return this.em.createQuery("FROM NetworkTemplate v WHERE v.user.username=:username ORDER BY v.id")
            .setParameter("username", this.getUser().getUsername()).getResultList();
    }

    @Override
    public NetworkTemplate getNetworkTemplateById(final String networkTemplateId) throws ResourceNotFoundException {
        NetworkTemplate networkTemplate = this.em.find(NetworkTemplate.class, Integer.valueOf(networkTemplateId));
        if (networkTemplate == null) {
            throw new ResourceNotFoundException(" Invalid networkConfig id " + networkTemplateId);
        }
        return networkTemplate;
    }

    @Override
    public NetworkTemplate getNetworkTemplateAttributes(final String networkTemplateId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkTemplateById(networkTemplateId);
    }

    @Override
    public QueryResult<NetworkTemplate> getNetworkTemplates(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("NetworkTemplate", NetworkTemplate.class, this.em, user.getUsername(), first,
            last, filters, attributes, false);
    }

    @Override
    public void updateNetworkTemplate(final NetworkTemplate networkTemplate) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNetworkTemplateAttributes(final String networkTemplateId, final Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteNetworkTemplate(final String networkTemplateId) throws ResourceNotFoundException, CloudProviderException {
        NetworkTemplate networkTemplate = this.em.find(NetworkTemplate.class, Integer.valueOf(networkTemplateId));
        if (networkTemplate == null) {
            throw new CloudProviderException("NetworkTemplate does't exist with id " + networkTemplateId);
        }
        this.em.remove(networkTemplate);
    }

    private void validateNetworkPortTemplate(final NetworkPortTemplate networkPortTemplate) throws InvalidRequestException {
        if (networkPortTemplate.getNetwork() == null) {
            throw new InvalidRequestException("Missing network");
        }
    }

    //
    // NetworkPort operations
    //

    @Override
    public Job createNetworkPort(final NetworkPortCreate networkPortCreate) throws InvalidRequestException,
        CloudProviderException {
        NetworkManager.logger.info("Creating NetworkPort");

        this.validateNetworkPortTemplate(networkPortCreate.getNetworkPortTemplate());

        // retrieve user
        User user = this.getUser();

        // pick up first cloud provider account associated with user
        if (user.getCloudProviderAccounts().isEmpty()) {
            throw new CloudProviderException("No cloud provider account for user " + user.getUsername());
        }
        CloudProviderAccount defaultAccount = user.getCloudProviderAccounts().iterator().next();
        ICloudProviderConnector connector = this.getCloudProviderConnector(defaultAccount);
        if (connector == null) {
            throw new CloudProviderException("Cannot find cloud provider connector "
                + defaultAccount.getCloudProvider().getCloudProviderType());
        }

        // delegates network port creation to cloud provider connector
        Job providerJob = null;

        try {
            INetworkService networkService = connector.getNetworkService();
            providerJob = networkService.createNetworkPort(networkPortCreate);
        } catch (ConnectorException e) {
            NetworkManager.logger.error("Failed to create network port: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        // prepare the Network entity to be persisted

        NetworkPort networkPort = new NetworkPort();
        networkPort.setName(networkPortCreate.getName());
        networkPort.setDescription(networkPortCreate.getDescription());
        networkPort.setProperties(networkPortCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(networkPortCreate.getProperties()));
        networkPort.setUser(user);

        networkPort.setProviderAssignedId(providerJob.getTargetResource().getProviderAssignedId());
        networkPort.setCloudProviderAccount(defaultAccount);

        networkPort.setNetwork(networkPortCreate.getNetworkPortTemplate().getNetwork());
        networkPort.setClassOfService(networkPortCreate.getNetworkPortTemplate().getNetworkPortConfig().getClassOfService());
        networkPort.setPortType(networkPortCreate.getNetworkPortTemplate().getNetworkPortConfig().getPortType());

        networkPort.setState(NetworkPort.State.CREATING);
        this.em.persist(networkPort);
        this.em.flush();

        Job job = new Job();
        job.setTargetResource(networkPort);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(networkPort);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setState(providerJob.getState());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            UtilsForManagers.emitJobListenerMessage(providerJob.getProviderAssignedId(), this.context);
        } catch (Exception e) {
            NetworkManager.logger.error(e.getMessage(), e);
        }
        return job;
    }

    private Job performActionOnNetworkPort(final String networkPortId, final String action) throws ResourceNotFoundException,
        CloudProviderException {
        NetworkPort networkPort = this.getNetworkPortById(networkPortId);
        if (networkPort == null) {
            throw new ResourceNotFoundException("NetworkPort " + networkPortId + " doesn't not exist");
        }

        // delegates volume deletion to cloud provider connector
        ICloudProviderConnector connector = this.getCloudProviderConnector(networkPort.getCloudProviderAccount());
        Job providerJob = null;

        try {
            INetworkService networkService = connector.getNetworkService();
            if (action.equals("delete")) {
                providerJob = networkService.deleteNetworkPort(networkPort.getProviderAssignedId());
            } else if (action.equals("start")) {
                providerJob = networkService.startNetworkPort(networkPort.getProviderAssignedId());
            } else if (action.equals("stop")) {
                providerJob = networkService.stopNetworkPort(networkPort.getProviderAssignedId());
            }
        } catch (ConnectorException e) {
            NetworkManager.logger.error("Failed to " + action + " network: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        // if by change the job is done and has failed, bail out
        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        if (action.equals("delete")) {
            networkPort.setState(NetworkPort.State.DELETING);
        } else if (action.equals("start")) {
            networkPort.setState(NetworkPort.State.STARTING);
        } else if (action.equals("stop")) {
            networkPort.setState(NetworkPort.State.STOPPING);
        }

        this.em.persist(networkPort);
        this.em.flush();

        Job job = new Job();
        job.setTargetResource(networkPort);
        job.setCreated(new Date());
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setState(providerJob.getState());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            UtilsForManagers.emitJobListenerMessage(providerJob.getProviderAssignedId(), this.context);
        } catch (Exception e) {
            NetworkManager.logger.error("", e);
        }
        return job;
    }

    @Override
    public Job startNetworkPort(final String networkPortId) throws ResourceNotFoundException, CloudProviderException {
        return this.performActionOnNetworkPort(networkPortId, "start");
    }

    @Override
    public Job startNetworkPort(final String networkPortId, final Map<String, String> properties)
        throws ResourceNotFoundException, CloudProviderException {
        return this.performActionOnNetworkPort(networkPortId, "start");
    }

    @Override
    public Job stopNetworkPort(final String networkPortId) throws ResourceNotFoundException, CloudProviderException {
        return this.performActionOnNetworkPort(networkPortId, "stop");
    }

    @Override
    public Job stopNetworkPort(final String networkPortId, final Map<String, String> properties)
        throws ResourceNotFoundException, CloudProviderException {
        return this.performActionOnNetworkPort(networkPortId, "stop");
    }

    @Override
    public List<NetworkPort> getNetworkPorts() throws CloudProviderException {
        return UtilsForManagers.getEntityList("NetworkPort", this.em, this.getUser().getUsername());
    }

    @Override
    public NetworkPort getNetworkPortById(final String networkPortId) throws ResourceNotFoundException {
        NetworkPort networkPort = this.em.find(NetworkPort.class, Integer.valueOf(networkPortId));
        if (networkPort == null || networkPort.getState() == NetworkPort.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid networkPort id " + networkPortId);
        }
        return networkPort;
    }

    @Override
    public NetworkPort getNetworkPortAttributes(final String networkPortId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkPortById(networkPortId);
    }

    @Override
    public QueryResult<NetworkPort> getNetworkPorts(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("NetworkPort", NetworkPort.class, this.em, user.getUsername(), first, last,
            filters, attributes, true);
    }

    @Override
    public Job updateNetworkPort(final NetworkPort networkPort) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job updateNetworkPortAttributes(final String networkPortId, final Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job deleteNetworkPort(final String networkPortId) throws ResourceNotFoundException, CloudProviderException {
        return this.performActionOnNetworkPort(networkPortId, "delete");
    }

    //
    // NetworkPortConfiguration operations
    //

    @Override
    public NetworkPortConfiguration createNetworkPortConfiguration(final NetworkPortConfiguration networkPortConfiguration)
        throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();

        if (networkPortConfiguration.getName() != null) {
            if (!this.em.createQuery("FROM NetworkPortConfiguration v WHERE v.user.username=:username AND v.name=:name")
                .setParameter("username", user.getUsername()).setParameter("name", networkPortConfiguration.getName())
                .getResultList().isEmpty()) {
                throw new CloudProviderException("NetworkPortConfiguration already exists with name "
                    + networkPortConfiguration.getName());
            }
        }
        networkPortConfiguration.setUser(user);
        networkPortConfiguration.setCreated(new Date());
        this.em.persist(networkPortConfiguration);
        this.em.flush();
        return networkPortConfiguration;
    }

    @Override
    public List<NetworkPortConfiguration> getNetworkPortConfigurations() throws CloudProviderException {
        return this.em.createQuery("FROM NetworkPortConfiguration v WHERE v.user.username=:username ORDER BY v.id")
            .setParameter("username", this.getUser().getUsername()).getResultList();
    }

    @Override
    public NetworkPortConfiguration getNetworkPortConfigurationById(final String networkPortConfigurationId)
        throws ResourceNotFoundException {
        NetworkPortConfiguration networkPort = this.em.find(NetworkPortConfiguration.class,
            Integer.valueOf(networkPortConfigurationId));
        if (networkPort == null) {
            throw new ResourceNotFoundException(" Invalid NetworkPortConfiguration id " + networkPortConfigurationId);
        }
        return networkPort;
    }

    @Override
    public NetworkPortConfiguration getNetworkPortConfigurationAttributes(final String networkPortConfigurationId,
        final List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkPortConfigurationById(networkPortConfigurationId);
    }

    @Override
    public QueryResult<NetworkPortConfiguration> getNetworkPortConfigurations(final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("NetworkPortConfiguration", NetworkPortConfiguration.class, this.em,
            user.getUsername(), first, last, filters, attributes, false);
    }

    @Override
    public void updateNetworkPortConfiguration(final NetworkPortConfiguration networkPort) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNetworkPortConfigurationAttributes(final String networkPortConfigurationId,
        final Map<String, Object> updatedAttributes) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteNetworkPortConfiguration(final String networkPortConfigurationId) throws ResourceNotFoundException,
        CloudProviderException {
        if (!this.em.createQuery("FROM NetworkPortTemplate n WHERE n.networkPortConfig.id=:networkPortConfigurationId")
            .setParameter("networkPortConfigurationId", Integer.valueOf(networkPortConfigurationId)).getResultList().isEmpty()) {
            throw new ResourceConflictException("Cannot delete NetworkPortConfiguration with id " + networkPortConfigurationId
                + " used by a NetworkPortTemplate");
        }
        NetworkPortConfiguration networkPortConfig = this.em.find(NetworkPortConfiguration.class,
            Integer.valueOf(networkPortConfigurationId));
        if (networkPortConfig == null) {
            throw new CloudProviderException("NetworkPortConfiguration does't exist with id " + networkPortConfigurationId);
        }
        this.em.remove(networkPortConfig);
    }

    //
    // NetworkPortTemplate operations
    //

    @Override
    public NetworkPortTemplate createNetworkPortTemplate(final NetworkPortTemplate networkPortTemplate)
        throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();

        if (networkPortTemplate.getName() != null) {
            if (!this.em.createQuery("FROM NetworkPortTemplate v WHERE v.user.username=:username AND v.name=:name")
                .setParameter("username", user.getUsername()).setParameter("name", networkPortTemplate.getName())
                .getResultList().isEmpty()) {
                throw new CloudProviderException("NetworkPortTemplate already exists with name "
                    + networkPortTemplate.getName());
            }
        }
        networkPortTemplate.setUser(user);
        networkPortTemplate.setCreated(new Date());
        this.em.persist(networkPortTemplate);
        this.em.flush();
        return networkPortTemplate;
    }

    @Override
    public List<NetworkPortTemplate> getNetworkPortTemplates() throws CloudProviderException {
        return this.em.createQuery("FROM NetworkPortTemplate v WHERE v.user.username=:username ORDER BY v.id")
            .setParameter("username", this.getUser().getUsername()).getResultList();
    }

    @Override
    public NetworkPortTemplate getNetworkPortTemplateById(final String networkPortTemplateId) throws ResourceNotFoundException {
        NetworkPortTemplate networkPortTemplate = this.em.find(NetworkPortTemplate.class,
            Integer.valueOf(networkPortTemplateId));
        if (networkPortTemplate == null) {
            throw new ResourceNotFoundException(" Invalid NetworkPortTemplate id " + networkPortTemplateId);
        }
        return networkPortTemplate;
    }

    @Override
    public NetworkPortTemplate getNetworkPortTemplateAttributes(final String networkPortTemplateId,
        final List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkPortTemplateById(networkPortTemplateId);
    }

    @Override
    public QueryResult<NetworkPortTemplate> getNetworkPortTemplates(final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("NetworkPortTemplate", NetworkPortTemplate.class, this.em, user.getUsername(),
            first, last, filters, attributes, false);
    }

    @Override
    public void updateNetworkPortTemplate(final NetworkPortTemplate networkPort) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNetworkPortTemplateAttributes(final String networkPortTemplateId,
        final Map<String, Object> updatedAttributes) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteNetworkPortTemplate(final String networkPortTemplateId) throws ResourceNotFoundException,
        CloudProviderException {
        NetworkPortTemplate networkPortTemplate = this.em.find(NetworkPortTemplate.class,
            Integer.valueOf(networkPortTemplateId));
        if (networkPortTemplate == null) {
            throw new CloudProviderException("NetworkPortTemplate does't exist with id " + networkPortTemplateId);
        }
        this.em.remove(networkPortTemplate);
    }

    //
    // ForwardingGroupTemplate operations
    //

    @Override
    public ForwardingGroupTemplate createForwardingGroupTemplate(final ForwardingGroupTemplate forwardingGroupTemplate)
        throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();

        if (forwardingGroupTemplate.getName() != null) {
            if (!this.em.createQuery("FROM ForwardingGroupTemplate v WHERE v.user.username=:username AND v.name=:name")
                .setParameter("username", user.getUsername()).setParameter("name", forwardingGroupTemplate.getName())
                .getResultList().isEmpty()) {
                throw new CloudProviderException("ForwardingGroupTemplate already exists with name "
                    + forwardingGroupTemplate.getName());
            }
        }
        forwardingGroupTemplate.setUser(user);
        this.em.persist(forwardingGroupTemplate);
        this.em.flush();
        return forwardingGroupTemplate;
    }

    @Override
    public List<ForwardingGroupTemplate> getForwardingGroupTemplates() throws CloudProviderException {
        return this.em.createQuery("FROM ForwardingGroupTemplate v WHERE v.user.username=:username ORDER BY v.id")
            .setParameter("username", this.getUser().getUsername()).getResultList();
    }

    @Override
    public ForwardingGroupTemplate getForwardingGroupTemplateById(final String forwardingGroupTemplateId)
        throws ResourceNotFoundException {
        ForwardingGroupTemplate forwardingGroupTemplate = this.em.find(ForwardingGroupTemplate.class,
            Integer.valueOf(forwardingGroupTemplateId));
        if (forwardingGroupTemplate == null) {
            throw new ResourceNotFoundException(" Invalid ForwardingGroupTemplate id " + forwardingGroupTemplateId);
        }
        return forwardingGroupTemplate;
    }

    @Override
    public ForwardingGroupTemplate getForwardingGroupTemplateAttributes(final String forwardingGroupTemplateId,
        final List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
        return this.getForwardingGroupTemplateById(forwardingGroupTemplateId);
    }

    @Override
    public QueryResult<ForwardingGroupTemplate> getForwardingGroupTemplates(final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("ForwardingGroupTemplate", ForwardingGroupTemplate.class, this.em,
            user.getUsername(), first, last, filters, attributes, false);
    }

    @Override
    public void updateForwardingGroupTemplate(final ForwardingGroupTemplate forwardingGroupTemplate)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateForwardingGroupTemplateAttributes(final String forwardingGroupTemplateId,
        final Map<String, Object> updatedAttributes) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteForwardingGroupTemplate(final String forwardingGroupTemplateId) throws ResourceNotFoundException,
        CloudProviderException {
        ForwardingGroupTemplate forwardingGroupTemplate = this.em.find(ForwardingGroupTemplate.class,
            Integer.valueOf(forwardingGroupTemplateId));
        if (forwardingGroupTemplate == null) {
            throw new CloudProviderException("ForwardingGroupTemplate does't exist with id " + forwardingGroupTemplateId);
        }
        this.em.remove(forwardingGroupTemplate);
    }

    //
    // ForwardingGroup operations
    //

    @Override
    public Job createForwardingGroup(final ForwardingGroupCreate forwardingGroupCreate) throws InvalidRequestException,
        CloudProviderException {
        NetworkManager.logger.info("Creating ForwardingGroup");

        // retrieve user
        User user = this.getUser();

        // pick up first cloud provider account associated with user
        if (user.getCloudProviderAccounts().isEmpty()) {
            throw new CloudProviderException("No cloud provider account for user " + user.getUsername());
        }
        CloudProviderAccount defaultAccount = user.getCloudProviderAccounts().iterator().next();
        ICloudProviderConnector connector = this.getCloudProviderConnector(defaultAccount);
        if (connector == null) {
            throw new CloudProviderException("Cannot find cloud provider connector "
                + defaultAccount.getCloudProvider().getCloudProviderType());
        }

        // delegates network port creation to cloud provider connector
        Job providerJob = null;

        try {
            INetworkService networkService = connector.getNetworkService();
            providerJob = networkService.createForwardingGroup(forwardingGroupCreate);
        } catch (ConnectorException e) {
            NetworkManager.logger.error("Failed to create ForwardingGroup: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        // prepare the ForwardingGroup entity to be persisted

        ForwardingGroup forwardingGroup = new ForwardingGroup();
        forwardingGroup.setName(forwardingGroupCreate.getName());
        forwardingGroup.setDescription(forwardingGroupCreate.getDescription());
        forwardingGroup.setProperties(forwardingGroupCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(forwardingGroupCreate.getProperties()));
        forwardingGroup.setUser(user);

        forwardingGroup.setProviderAssignedId(providerJob.getTargetResource().getProviderAssignedId());
        forwardingGroup.setCloudProviderAccount(defaultAccount);

        Set<ForwardingGroupNetwork> networks = new HashSet<ForwardingGroupNetwork>();
        if (forwardingGroupCreate.getForwardingGroupTemplate().getNetworks() != null) {
            for (Network net : forwardingGroupCreate.getForwardingGroupTemplate().getNetworks()) {
                ForwardingGroupNetwork forwardingGroupNetwork = new ForwardingGroupNetwork();
                forwardingGroupNetwork.setNetwork(net);
                this.em.persist(forwardingGroupNetwork);
                networks.add(forwardingGroupNetwork);
            }
        }
        forwardingGroup.setNetworks(networks);

        forwardingGroup.setState(ForwardingGroup.State.CREATING);
        this.em.persist(forwardingGroup);
        this.em.flush();

        Job job = new Job();
        job.setTargetResource(forwardingGroup);
        job.setCreated(new Date());
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setState(providerJob.getState());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            UtilsForManagers.emitJobListenerMessage(providerJob.getProviderAssignedId(), this.context);
        } catch (Exception e) {
            NetworkManager.logger.error(e.getMessage(), e);
        }
        return job;
    }

    @Override
    public List<ForwardingGroup> getForwardingGroups() throws CloudProviderException {
        return UtilsForManagers.getEntityList("ForwardingGroup", this.em, this.getUser().getUsername());
    }

    @Override
    public ForwardingGroup getForwardingGroupById(final String forwardingGroupId) throws ResourceNotFoundException {
        ForwardingGroup forwardingGroup = this.em.find(ForwardingGroup.class, Integer.valueOf(forwardingGroupId));
        if (forwardingGroup == null || forwardingGroup.getState() == ForwardingGroup.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid ForwardingGroup id " + forwardingGroupId);
        }
        forwardingGroup.getNetworks().size();
        return forwardingGroup;
    }

    @Override
    public ForwardingGroup getForwardingGroupAttributes(final String forwardingGroupId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getForwardingGroupById(forwardingGroupId);
    }

    @Override
    public QueryResult<ForwardingGroup> getForwardingGroups(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("ForwardingGroup", ForwardingGroup.class, this.em, user.getUsername(), first,
            last, filters, attributes, true);
    }

    @Override
    public Job updateForwardingGroup(final ForwardingGroup forwardingGroup) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job updateForwardingGroupAttributes(final String forwardingGroupId, final Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job deleteForwardingGroup(final String forwardingGroupId) throws ResourceNotFoundException, CloudProviderException {
        ForwardingGroup forwardingGroup = this.getForwardingGroupById(forwardingGroupId);
        if (forwardingGroup == null) {
            throw new ResourceNotFoundException("ForwardingGroup " + forwardingGroupId + " doesn't not exist");
        }

        // delegates ForwardingGroup deletion to cloud provider connector
        ICloudProviderConnector connector = this.getCloudProviderConnector(forwardingGroup.getCloudProviderAccount());
        Job providerJob = null;

        try {
            INetworkService networkService = connector.getNetworkService();
            providerJob = networkService.deleteForwardingGroup(forwardingGroup.getProviderAssignedId());
        } catch (ConnectorException e) {
            NetworkManager.logger.error("Failed to delete forwarding group: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        // if by change the job is done and has failed, bail out
        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        forwardingGroup.setState(ForwardingGroup.State.DELETING);

        this.em.persist(forwardingGroup);
        this.em.flush();

        Job job = new Job();
        job.setTargetResource(forwardingGroup);
        job.setCreated(new Date());
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setState(providerJob.getState());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            UtilsForManagers.emitJobListenerMessage(providerJob.getProviderAssignedId(), this.context);
        } catch (Exception e) {
            NetworkManager.logger.error("", e);
        }
        return job;
    }

    @Override
    public Job addNetworkToForwardingGroup(final String forwardingGroupId, final ForwardingGroupNetwork forwardingGroupNetwork)
        throws ResourceNotFoundException, CloudProviderException {
        ForwardingGroup forwardingGroup = this.getForwardingGroupById(forwardingGroupId);
        if (forwardingGroup == null) {
            throw new ResourceNotFoundException("ForwardingGroup " + forwardingGroupId + " doesn't not exist");
        }

        Network network = forwardingGroupNetwork.getNetwork();
        if (network == null) {
            throw new ResourceNotFoundException("Network cannot be null");
        }

        ForwardingGroupNetwork fgNetwork = new ForwardingGroupNetwork();
        fgNetwork.setState(ForwardingGroupNetwork.State.ATTACHING);
        fgNetwork.setNetwork(network);
        this.em.persist(fgNetwork);
        forwardingGroup.getNetworks().add(fgNetwork);
        this.em.persist(forwardingGroup);

        // delegates ForwardingGroup add to cloud provider connector
        ICloudProviderConnector connector = this.getCloudProviderConnector(forwardingGroup.getCloudProviderAccount());
        Job providerJob = null;

        try {
            INetworkService networkService = connector.getNetworkService();
            providerJob = networkService.addNetworkToForwardingGroup(forwardingGroup.getProviderAssignedId(),
                forwardingGroupNetwork);
        } catch (ConnectorException e) {
            NetworkManager.logger.error("Failed to add network to forwarding group: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        Job job = new Job();
        job.setTargetResource(forwardingGroup);
        job.setAffectedResources(Collections.<CloudResource> singletonList(network));
        job.setCreated(new Date());
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setState(providerJob.getState());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            UtilsForManagers.emitJobListenerMessage(providerJob.getProviderAssignedId(), this.context);
        } catch (Exception e) {
            NetworkManager.logger.error("", e);
        }
        return job;
    }

    @Override
    public Job removeNetworkFromForwardingGroup(final String forwardingGroupId, final String fgNetworkId)
        throws ResourceNotFoundException, CloudProviderException {
        ForwardingGroup forwardingGroup = this.getForwardingGroupById(forwardingGroupId);
        if (forwardingGroup == null) {
            throw new ResourceNotFoundException("ForwardingGroup " + forwardingGroupId + " doesn't not exist");
        }

        ForwardingGroupNetwork forwardingGroupNetwork = this.em
            .find(ForwardingGroupNetwork.class, Integer.valueOf(fgNetworkId));
        if (forwardingGroupNetwork == null) {
            throw new ResourceNotFoundException();
        }
        if (!forwardingGroup.getNetworks().contains(forwardingGroupNetwork)) {
            throw new ResourceNotFoundException();
        }

        forwardingGroupNetwork.setState(ForwardingGroupNetwork.State.DETACHING);

        // delegates ForwardingGroup deletion to cloud provider connector
        ICloudProviderConnector connector = this.getCloudProviderConnector(forwardingGroup.getCloudProviderAccount());
        Job providerJob = null;

        try {
            INetworkService networkService = connector.getNetworkService();
            providerJob = networkService.removeNetworkFromForwardingGroup(forwardingGroup.getProviderAssignedId(),
                forwardingGroupNetwork.getNetwork().getProviderAssignedId());
        } catch (ConnectorException e) {
            NetworkManager.logger.error("Failed to remove network from forwarding group: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        Job job = new Job();
        job.setTargetResource(forwardingGroup);
        job.setAffectedResources(Collections.<CloudResource> singletonList(forwardingGroupNetwork.getNetwork()));
        job.setCreated(new Date());
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setState(providerJob.getState());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            UtilsForManagers.emitJobListenerMessage(providerJob.getProviderAssignedId(), this.context);
        } catch (Exception e) {
            NetworkManager.logger.error("", e);
        }
        return job;
    }

    @Override
    public void updateNetworkInForwardingGroup(final String forwardingGroupId,
        final ForwardingGroupNetwork forwardingGroupNetwork) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    //
    // Address operations
    //

    @Override
    public Job createAddress(final AddressCreate addressCreate) throws InvalidRequestException, CloudProviderException {
        // TODO
        return null;
    }

    @Override
    public List<Address> getAddresses() throws CloudProviderException {
        return this.em.createQuery("FROM Address v WHERE v.user.username=:username ORDER BY v.id")
            .setParameter("username", this.getUser().getUsername()).getResultList();
    }

    @Override
    public Address getAddressById(final String addressId) throws ResourceNotFoundException {
        Address address = this.em.find(Address.class, Integer.valueOf(addressId));
        if (address == null) {
            throw new ResourceNotFoundException(" Invalid Address id " + addressId);
        }
        return address;
    }

    @Override
    public Address getAddressAttributes(final String addressId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getAddressById(addressId);
    }

    @Override
    public QueryResult<Address> getAddresses(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("Address", Address.class, this.em, user.getUsername(), first, last, filters,
            attributes, false);
    }

    @Override
    public Job updateAddress(final Address address) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job updateAddressAttributes(final String addressId, final Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job deleteAddress(final String addressId) throws ResourceNotFoundException, CloudProviderException {
        // TODO
        return null;
    }

    //
    // AddressTemplate operations
    //

    @Override
    public AddressTemplate createAddressTemplate(final AddressTemplate addressTemplate) throws InvalidRequestException,
        CloudProviderException {
        User user = this.getUser();

        if (addressTemplate.getName() != null) {
            if (!this.em.createQuery("FROM AddressTemplate v WHERE v.user.username=:username AND v.name=:name")
                .setParameter("username", user.getUsername()).setParameter("name", addressTemplate.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("AddressTemplate already exists with name " + addressTemplate.getName());
            }
        }
        addressTemplate.setUser(user);
        addressTemplate.setCreated(new Date());
        this.em.persist(addressTemplate);
        this.em.flush();
        return addressTemplate;
    }

    @Override
    public List<AddressTemplate> getAddressTemplates() throws CloudProviderException {
        return this.em.createQuery("FROM AddressTemplate v WHERE v.user.username=:username ORDER BY v.id")
            .setParameter("username", this.getUser().getUsername()).getResultList();
    }

    @Override
    public AddressTemplate getAddressTemplateById(final String addressTemplateId) throws ResourceNotFoundException {
        AddressTemplate addressTemplate = this.em.find(AddressTemplate.class, Integer.valueOf(addressTemplateId));
        if (addressTemplate == null) {
            throw new ResourceNotFoundException(" Invalid AddressTemplate id " + addressTemplateId);
        }
        return addressTemplate;
    }

    @Override
    public AddressTemplate getAddressTemplateAttributes(final String addressTemplateId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getAddressTemplateById(addressTemplateId);
    }

    @Override
    public QueryResult<AddressTemplate> getAddressTemplates(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("AddressTemplate", AddressTemplate.class, this.em, user.getUsername(), first,
            last, filters, attributes, false);
    }

    @Override
    public void updateAddressTemplate(final AddressTemplate addressTemplate) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateAddressTemplateAttributes(final String addressTemplateId, final Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAddressTemplate(final String addressTemplateId) throws ResourceNotFoundException, CloudProviderException {
        AddressTemplate addressTemplate = this.em.find(AddressTemplate.class, Integer.valueOf(addressTemplateId));
        if (addressTemplate == null) {
            throw new CloudProviderException("AddressTemplate does't exist with id " + addressTemplateId);
        }
        this.em.remove(addressTemplate);
    }

    //
    // Job completion handler
    //

    @Override
    public boolean jobCompletionHandler(final Job job) throws CloudProviderException {
        if (job.getTargetResource() instanceof Network) {
            return this.networkCompletionHandler(job);
        } else if (job.getTargetResource() instanceof NetworkPort) {
            return this.networkPortCompletionHandler(job);
        } else if (job.getTargetResource() instanceof ForwardingGroup) {
            return this.forwardingGroupCompletionHandler(job);
        }
        return false;
    }

    private Network getNetworkByProviderAssignedId(final String providerAssignedId) {
        Network network = (Network) this.em.createNamedQuery(Network.GET_NETWORK_BY_PROVIDER_ASSIGNED_ID)
            .setParameter("providerAssignedId", providerAssignedId).getSingleResult();
        return network;
    }

    private NetworkPort getNetworkPortByProviderAssignedId(final String providerAssignedId) {
        NetworkPort networkPort = (NetworkPort) this.em.createNamedQuery(NetworkPort.GET_NETWORKPORT_BY_PROVIDER_ASSIGNED_ID)
            .setParameter("providerAssignedId", providerAssignedId).getSingleResult();
        return networkPort;
    }

    private ForwardingGroup getForwardingGroupByProviderAssignedId(final String providerAssignedId) {
        ForwardingGroup forwardingGroup = (ForwardingGroup) this.em
            .createNamedQuery(ForwardingGroup.GET_FORWARDINGGROUP_BY_PROVIDER_ASSIGNED_ID)
            .setParameter("providerAssignedId", providerAssignedId).getSingleResult();
        return forwardingGroup;
    }

    private boolean networkCompletionHandler(final Job providerJob) throws CloudProviderException {
        // retrieve the Network whose providerAssignedId is
        // job.getTargetEntity()
        Network network = null;

        try {
            network = this.getNetworkByProviderAssignedId(providerJob.getTargetResource().getProviderAssignedId());
        } catch (PersistenceException e) {
            NetworkManager.logger.error("Cannot find Network with provider-assigned id " + providerJob.getTargetResource());
            return false;
        }

        // update Network entity
        ICloudProviderConnector connector = this.getCloudProviderConnector(network.getCloudProviderAccount());

        if (providerJob.getAction().equals("add")) {
            if (providerJob.getState() == Job.Status.SUCCESS) {
                try {
                    network.setState(connector.getNetworkService().getNetwork(network.getProviderAssignedId()).getState());
                    network.setCreated(new Date());
                    this.em.persist(network);
                } catch (Exception ex) {
                    NetworkManager.logger.error("Failed to create network " + network.getName(), ex);
                }
            } else if (providerJob.getState() == Job.Status.FAILED) {
                network.setState(Network.State.ERROR);
                NetworkManager.logger.error("Failed to create network  " + network.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(network);
            }
        }
        if (providerJob.getAction().equals("start")) {
            if (providerJob.getState() == Job.Status.SUCCESS) {
                try {
                    network.setState(connector.getNetworkService().getNetwork(network.getProviderAssignedId()).getState());
                    network.setUpdated(new Date());
                    this.em.persist(network);
                } catch (Exception ex) {
                    NetworkManager.logger.error("Failed to start network " + network.getName(), ex);
                }
            } else if (providerJob.getState() == Job.Status.FAILED) {
                network.setState(Network.State.ERROR);
                NetworkManager.logger.error("Failed to start network  " + network.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(network);
            }
        }
        if (providerJob.getAction().equals("stop")) {
            if (providerJob.getState() == Job.Status.SUCCESS) {
                try {
                    network.setState(connector.getNetworkService().getNetwork(network.getProviderAssignedId()).getState());
                    network.setUpdated(new Date());
                    this.em.persist(network);
                } catch (Exception ex) {
                    NetworkManager.logger.error("Failed to stop network " + network.getName(), ex);
                }
            } else if (providerJob.getState() == Job.Status.FAILED) {
                network.setState(Network.State.ERROR);
                NetworkManager.logger.error("Failed to create network  " + network.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(network);
            }
        } else if (providerJob.getAction().equals("delete")) {
            if (providerJob.getState() == Job.Status.SUCCESS) {
                network.setState(Network.State.DELETED);
                this.em.persist(network);
                this.em.flush();
            } else if (providerJob.getState() == Job.Status.FAILED) {
                network.setState(Network.State.ERROR);
                NetworkManager.logger.error("Failed to delete network  " + network.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(network);
            }
        }

        return true;
    }

    private boolean networkPortCompletionHandler(final Job providerJob) throws CloudProviderException {
        // retrieve the Network whose providerAssignedId is
        // job.getTargetEntity()
        NetworkPort networkPort = null;

        try {
            networkPort = this.getNetworkPortByProviderAssignedId(providerJob.getTargetResource().getProviderAssignedId());
        } catch (PersistenceException e) {
            NetworkManager.logger.error("Cannot find NetworkPort with provider-assigned id " + providerJob.getTargetResource());
            return false;
        }

        // update NetworkPort entity
        ICloudProviderConnector connector = this.getCloudProviderConnector(networkPort.getCloudProviderAccount());

        if (providerJob.getAction().equals("add")) {
            if (providerJob.getState() == Job.Status.SUCCESS) {
                try {
                    networkPort.setState(connector.getNetworkService().getNetworkPort(networkPort.getProviderAssignedId())
                        .getState());
                    networkPort.setCreated(new Date());
                    this.em.persist(networkPort);
                } catch (Exception ex) {
                    NetworkManager.logger.error("Failed to create network port" + networkPort.getName(), ex);
                }
            } else if (providerJob.getState() == Job.Status.FAILED) {
                networkPort.setState(NetworkPort.State.ERROR);
                NetworkManager.logger.error("Failed to create network port " + networkPort.getName() + " : "
                    + providerJob.getStatusMessage());
                this.em.persist(networkPort);
            }
        }
        if (providerJob.getAction().equals("start")) {
            if (providerJob.getState() == Job.Status.SUCCESS) {
                try {
                    networkPort.setState(connector.getNetworkService().getNetworkPort(networkPort.getProviderAssignedId())
                        .getState());
                    networkPort.setUpdated(new Date());
                    this.em.persist(networkPort);
                } catch (Exception ex) {
                    NetworkManager.logger.error("Failed to start network port " + networkPort.getName(), ex);
                }
            } else if (providerJob.getState() == Job.Status.FAILED) {
                networkPort.setState(NetworkPort.State.ERROR);
                NetworkManager.logger.error("Failed to start network port " + networkPort.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(networkPort);
            }
        }
        if (providerJob.getAction().equals("stop")) {
            if (providerJob.getState() == Job.Status.SUCCESS) {
                try {
                    networkPort.setState(connector.getNetworkService().getNetworkPort(networkPort.getProviderAssignedId())
                        .getState());
                    networkPort.setUpdated(new Date());
                    this.em.persist(networkPort);
                } catch (Exception ex) {
                    NetworkManager.logger.error("Failed to stop network port " + networkPort.getName(), ex);
                }
            } else if (providerJob.getState() == Job.Status.FAILED) {
                networkPort.setState(NetworkPort.State.ERROR);
                NetworkManager.logger.error("Failed to create network port " + networkPort.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(networkPort);
            }
        } else if (providerJob.getAction().equals("delete")) {
            if (providerJob.getState() == Job.Status.SUCCESS) {
                networkPort.setState(NetworkPort.State.DELETED);
                this.em.persist(networkPort);
                this.em.flush();
            } else if (providerJob.getState() == Job.Status.FAILED) {
                networkPort.setState(NetworkPort.State.ERROR);
                NetworkManager.logger.error("Failed to delete network port " + networkPort.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(networkPort);
            }
        }

        return true;
    }

    private boolean forwardingGroupCompletionHandler(final Job providerJob) throws CloudProviderException {
        ForwardingGroup forwardingGroup = null;

        try {
            forwardingGroup = this.getForwardingGroupByProviderAssignedId(providerJob.getTargetResource()
                .getProviderAssignedId());
        } catch (PersistenceException e) {
            NetworkManager.logger.error("Cannot find ForwardingGroup with provider-assigned id "
                + providerJob.getTargetResource());
            return false;
        }

        ICloudProviderConnector connector = this.getCloudProviderConnector(forwardingGroup.getCloudProviderAccount());

        Network affectedNetwork = null;
        if (providerJob.getAffectedResources().size() == 1
            && providerJob.getAffectedResources().iterator().next() instanceof Network) {
            affectedNetwork = this.getNetworkByProviderAssignedId(providerJob.getAffectedResources().iterator().next()
                .getProviderAssignedId());
        }

        if (providerJob.getAction().equals("add")) {
            if (affectedNetwork == null) {
                if (providerJob.getState() == Job.Status.SUCCESS) {
                    try {
                        forwardingGroup.setState(connector.getNetworkService()
                            .getForwardingGroup(forwardingGroup.getProviderAssignedId()).getState());
                        forwardingGroup.setCreated(new Date());
                        this.em.persist(forwardingGroup);
                    } catch (Exception ex) {
                        NetworkManager.logger.error("Failed to create forwarding group " + forwardingGroup.getName(), ex);
                    }
                } else if (providerJob.getState() == Job.Status.FAILED) {
                    forwardingGroup.setState(ForwardingGroup.State.ERROR);
                    NetworkManager.logger.error("Failed to create forwarding group  " + forwardingGroup.getName() + ": "
                        + providerJob.getStatusMessage());
                    this.em.persist(forwardingGroup);
                }
            } else {
                // add network to forwarding group
                if (providerJob.getState() == Job.Status.SUCCESS) {
                    try {
                        ForwardingGroupNetwork forwardingGroupNetwork = null;
                        for (ForwardingGroupNetwork net : forwardingGroup.getNetworks()) {
                            if (net.getNetwork().getId() == affectedNetwork.getId()) {
                                forwardingGroupNetwork = net;
                                break;
                            }
                        }
                        if (forwardingGroupNetwork != null) {
                            forwardingGroupNetwork.setState(ForwardingGroupNetwork.State.AVAILABLE);
                            this.em.persist(forwardingGroupNetwork);
                            forwardingGroup.setUpdated(new Date());
                            this.em.persist(forwardingGroup);
                        } else {
                            NetworkManager.logger.error("Cannot find added network in ForwardingGroupNetwork)");
                        }
                    } catch (Exception ex) {
                        NetworkManager.logger.error("Failed to add network to forwarding group " + forwardingGroup.getName(),
                            ex);
                    }
                } else if (providerJob.getState() == Job.Status.FAILED) {
                    forwardingGroup.setState(ForwardingGroup.State.ERROR);
                    NetworkManager.logger.error("Failed to add network to forwarding group  " + forwardingGroup.getName()
                        + ": " + providerJob.getStatusMessage());
                    this.em.persist(forwardingGroup);
                }

            }
        } else if (providerJob.getAction().equals("delete")) {
            if (affectedNetwork == null) {
                if (providerJob.getState() == Job.Status.SUCCESS) {
                    forwardingGroup.setState(ForwardingGroup.State.DELETED);
                    forwardingGroup.setNetworks(Collections.<ForwardingGroupNetwork> emptySet());
                    this.em.persist(forwardingGroup);
                    this.em.flush();
                } else if (providerJob.getState() == Job.Status.FAILED) {
                    forwardingGroup.setState(ForwardingGroup.State.ERROR);
                    NetworkManager.logger.error("Failed to delete forwarding group  " + forwardingGroup.getName() + ": "
                        + providerJob.getStatusMessage());
                    this.em.persist(forwardingGroup);
                }
            } else {
                // remove network from forwarding group
                if (providerJob.getState() == Job.Status.SUCCESS) {
                    boolean found = false;
                    for (Iterator<ForwardingGroupNetwork> it = forwardingGroup.getNetworks().iterator(); it.hasNext();) {
                        ForwardingGroupNetwork net = it.next();
                        if (net.getNetwork().getId() == affectedNetwork.getId()) {
                            it.remove();
                            this.em.remove(net);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        NetworkManager.logger.error("Attempting to remove network " + affectedNetwork.getId()
                            + " not a member of forwarding group " + forwardingGroup.getId());
                    }
                    forwardingGroup.getNetworks().remove(affectedNetwork);
                    this.em.persist(forwardingGroup);
                    this.em.flush();
                } else if (providerJob.getState() == Job.Status.FAILED) {
                    forwardingGroup.setState(ForwardingGroup.State.ERROR);
                    NetworkManager.logger.error("Failed to remove network from forwarding group  " + forwardingGroup.getName()
                        + ": " + providerJob.getStatusMessage());
                    this.em.persist(forwardingGroup);
                }
            }

        }

        return true;
    }

    @Override
    public List<ForwardingGroupNetwork> getForwardingGroupNetworks(final String forwardingGroupId)
        throws ResourceNotFoundException, CloudProviderException {
        ForwardingGroup forwardingGroup = this.getForwardingGroupById(forwardingGroupId);
        return new ArrayList<ForwardingGroupNetwork>(forwardingGroup.getNetworks());
    }

    @Override
    public QueryResult<ForwardingGroupNetwork> getForwardingGroupNetworks(final String forwardingGroupId, final int first,
        final int last, final List<String> filters, final List<String> attributes) throws InvalidRequestException,
        CloudProviderException {
        return UtilsForManagers
            .getCollectionItemList("ForwardingGroupNetwork", ForwardingGroupNetwork.class, this.em, this.getUser()
                .getUsername(), first, last, filters, attributes, false, "ForwardingGroup", "networks", forwardingGroupId);
    }

    @Override
    public ForwardingGroupNetwork getNetworkFromForwardingGroup(final String forwardingGroupId, final String fgNetworkId)
        throws ResourceNotFoundException, CloudProviderException {
        ForwardingGroup forwardingGroup = this.getForwardingGroupById(forwardingGroupId);
        ForwardingGroupNetwork forwardingGroupNetwork = this.em
            .find(ForwardingGroupNetwork.class, Integer.valueOf(fgNetworkId));
        if (forwardingGroupNetwork == null) {
            throw new ResourceNotFoundException();
        }
        if (!forwardingGroup.getNetworks().contains(forwardingGroupNetwork)) {
            throw new ResourceNotFoundException();
        }
        return forwardingGroupNetwork;
    }
}
