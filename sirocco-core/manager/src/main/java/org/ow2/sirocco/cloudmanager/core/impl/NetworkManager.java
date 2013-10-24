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
import javax.jms.JMSContext;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceException;

import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager.Placement;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteNetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IResourceWatcher;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.QueryParams;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.ResourceStateChangeEvent;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.impl.command.NetworkCreateCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.NetworkDeleteCommand;
import org.ow2.sirocco.cloudmanager.core.utils.QueryHelper;
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
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Network.Type;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkNetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteNetworkManager.class)
@Local(INetworkManager.class)
public class NetworkManager implements INetworkManager {
    private static Logger logger = LoggerFactory.getLogger(NetworkManager.class.getName());

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Inject
    private IdentityContext identityContext;

    @Resource
    private EJBContext context;

    @EJB
    private ICloudProviderManager cloudProviderManager;

    @EJB
    private ITenantManager tenantManager;

    @EJB
    private IResourceWatcher resourceWatcher;

    @Resource(lookup = "jms/RequestQueue")
    private Queue requestQueue;

    @Resource(lookup = "jms/ResourceStateChangeTopic")
    private Topic resourceStateChangeTopic;

    @Inject
    private JMSContext jmsContext;

    private Tenant getTenant() throws CloudProviderException {
        return this.tenantManager.getTenant(this.identityContext);
    }

    //
    // Network operations
    //

    private void fireNetworkStateChangeEvent(final Network network) {
        this.jmsContext.createProducer().setProperty("tenantId", network.getTenant().getId().toString())
            .send(this.resourceStateChangeTopic, new ResourceStateChangeEvent(network));
    }

    @Override
    public void updateNetworkState(final String networkId, final Network.State state) throws CloudProviderException {
        Network network = this.getNetworkById(networkId);
        network.setState(state);
        this.fireNetworkStateChangeEvent(network);
    }

    public Network getPublicNetwork() {
        List<Network> publicNetworks = this.em.createQuery(
            "SELECT n FROM Network n WHERE n.networkType=org.ow2.sirocco.cloudmanager.model.cimi.Network$Type.PUBLIC")
            .getResultList();
        return publicNetworks.get(0);
    }

    @Override
    public Job createNetwork(final NetworkCreate networkCreate) throws InvalidRequestException, CloudProviderException {
        NetworkManager.logger.info("Creating Network");

        if (networkCreate.getNetworkTemplate().getNetworkConfig().getNetworkType() == Type.PUBLIC) {
            throw new InvalidRequestException("Cannot create public network");
        }

        // retrieve user
        Tenant tenant = this.getTenant();

        Placement placement = this.cloudProviderManager.placeResource(tenant.getId().toString(), networkCreate.getProperties());

        Network network = new Network();

        // prepare the Network entity to be persisted

        network.setName(networkCreate.getName());
        network.setDescription(networkCreate.getDescription());
        network.setProperties(networkCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(networkCreate.getProperties()));
        network.setTenant(tenant);

        network.setCloudProviderAccount(placement.getAccount());
        network.setLocation(placement.getLocation());

        network.setMtu(networkCreate.getNetworkTemplate().getNetworkConfig().getMtu());
        network.setClassOfService(networkCreate.getNetworkTemplate().getNetworkConfig().getClassOfService());
        network.setNetworkType(networkCreate.getNetworkTemplate().getNetworkConfig().getNetworkType());
        network.setForwardingGroup(networkCreate.getNetworkTemplate().getForwardingGroup());
        network.setSubnets(networkCreate.getNetworkTemplate().getNetworkConfig().getSubnets());

        network.setState(Network.State.CREATING);
        this.em.persist(network);
        this.em.flush();

        Job job = new Job();
        job.setTargetResource(network);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(network);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setState(Job.Status.RUNNING);
        job.setAction("add");
        this.em.persist(job);
        this.em.flush();

        ObjectMessage message = this.jmsContext.createObjectMessage(new NetworkCreateCommand(networkCreate)
            .setAccount(placement.getAccount()).setLocation(placement.getLocation()).setResourceId(network.getId().toString())
            .setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    @Override
    public void syncNetwork(final String networkId, final Network updatedNetwork, final String jobId) {
        Network network = this.em.find(Network.class, Integer.valueOf(networkId));
        Job job = this.em.find(Job.class, Integer.valueOf(jobId));
        if (updatedNetwork == null) {
            network.setState(Network.State.DELETED);
        } else {
            network.setState(updatedNetwork.getState());
            if (network.getCreated() == null) {
                network.setCreated(new Date());
            }
            network.setUpdated(new Date());
        }
        this.fireNetworkStateChangeEvent(network);
        job.setState(Job.Status.SUCCESS);
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

    private List<Network> getNetworks() throws CloudProviderException {
        return QueryHelper.getEntityList("Network", this.em, this.getTenant().getId(), Network.State.DELETED, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public QueryResult<Network> getNetworks(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Network", Network.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .stateToIgnore(Network.State.DELETED));
    }

    @Override
    public QueryResult<Network> getNetworks(final QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException {
        if (queryParams.length == 0) {
            List<Network> nets = this.getNetworks();
            return new QueryResult<Network>(nets.size(), nets);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Network", Network.class).params(
            queryParams[0]);
        return QueryHelper.getEntityList(this.em, params.tenantId(this.getTenant().getId())
            .stateToIgnore(Network.State.DELETED));
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

        Tenant tenant = this.getTenant();

        if (action.equals("start")) {
            network.setState(Network.State.STARTING);
        } else if (action.equals("stop")) {
            network.setState(Network.State.STOPPING);
        } else if (action.equals("delete")) {
            network.setState(Network.State.DELETING);
        }

        this.fireNetworkStateChangeEvent(network);

        Job job = new Job();
        job.setTenant(tenant);
        job.setTargetResource(network);
        job.setCreated(new Date());
        job.setState(Status.RUNNING);
        job.setAction(action);
        job.setTimeOfStatusChange(new Date());
        this.em.persist(job);
        this.em.flush();

        if (action.equals("delete")) {
            ObjectMessage message = this.jmsContext.createObjectMessage(new NetworkDeleteCommand().setResourceId(
                network.getId().toString()).setJob(job));
            this.jmsContext.createProducer().send(this.requestQueue, message);
        } else {
            // TODO
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
        Tenant tenant = this.getTenant();

        if (networkConfig.getName() != null) {
            if (!this.em.createQuery("SELECT v FROM NetworkConfiguration v WHERE v.tenant.id=:tenantId AND v.name=:name")
                .setParameter("tenantId", tenant.getId()).setParameter("name", networkConfig.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("NetworkConfiguration already exists with name " + networkConfig.getName());
            }
        }
        networkConfig.setTenant(tenant);
        networkConfig.setCreated(new Date());
        this.em.persist(networkConfig);
        this.em.flush();
        return networkConfig;
    }

    private List<NetworkConfiguration> getNetworkConfigurations() throws CloudProviderException {
        return this.em.createQuery("SELECT v FROM NetworkConfiguration v WHERE v.tenant.id=:tenantId ORDER BY v.id")
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("NetworkConfiguration",
            NetworkConfiguration.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .returnPublicEntities());
    }

    @Override
    public QueryResult<NetworkConfiguration> getNetworkConfigurations(final QueryParams... queryParams)
        throws InvalidRequestException, CloudProviderException {
        if (queryParams.length == 0) {
            List<NetworkConfiguration> netConfigs = this.getNetworkConfigurations();
            return new QueryResult<NetworkConfiguration>(netConfigs.size(), netConfigs);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("NetworkConfiguration",
            NetworkConfiguration.class).params(queryParams[0]);
        return QueryHelper.getEntityList(this.em, params.tenantId(this.getTenant().getId()).returnPublicEntities());
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
        if (!this.em.createQuery("SELECT n FROM NetworkTemplate n WHERE n.networkConfig.id=:networkConfigId")
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
        Tenant tenant = this.getTenant();

        if (networkTemplate.getName() != null) {
            if (!this.em.createQuery("SELECT  v FROM NetworkTemplate v WHERE v.tenant.id=:tenantId AND v.name=:name")
                .setParameter("tenantId", tenant.getId()).setParameter("name", networkTemplate.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("NetworkTemplate already exists with name " + networkTemplate.getName());
            }
        }
        networkTemplate.setTenant(tenant);
        networkTemplate.setCreated(new Date());
        this.em.persist(networkTemplate);
        this.em.flush();
        return networkTemplate;
    }

    private List<NetworkTemplate> getNetworkTemplates() throws CloudProviderException {
        return this.em
            .createQuery(
                "SELECT v FROM NetworkTemplate v WHERE v.tenant.id=:tenantId AND v.isEmbeddedInSystemTemplate=false ORDER BY v.id")
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder
            .builder("NetworkTemplate", NetworkTemplate.class);

        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .filterEmbbededTemplate().returnPublicEntities());
    }

    @Override
    public QueryResult<NetworkTemplate> getNetworkTemplates(final QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException {
        if (queryParams.length == 0) {
            List<NetworkTemplate> netTemplates = this.getNetworkTemplates();
            return new QueryResult<NetworkTemplate>(netTemplates.size(), netTemplates);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder
            .builder("NetworkTemplate", NetworkTemplate.class).params(queryParams[0]);

        return QueryHelper.getEntityList(this.em, params.tenantId(this.getTenant().getId()).filterEmbbededTemplate()
            .returnPublicEntities());
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

        // retrieve tenant
        Tenant tenant = this.getTenant();

        Placement placement = this.cloudProviderManager.placeResource(tenant.getId().toString(),
            networkPortCreate.getProperties());
        ICloudProviderConnector connector = null;// this.getCloudProviderConnector(placement.getAccount(),
                                                 // placement.getLocation());
        if (connector == null) {
            throw new CloudProviderException("Cannot retrieve cloud provider connector "
                + placement.getAccount().getCloudProvider().getCloudProviderType());
        }

        // delegates network port creation to cloud provider connector
        Job providerJob = null;

        // TODO:workflowtry {
        // TODO:workflow INetworkService networkService =
        // connector.getNetworkService();
        // TODO:workflow providerJob =
        // networkService.createNetworkPort(networkPortCreate);
        // TODO:workflow} catch (ConnectorException e) {
        // TODO:workflow
        // NetworkManager.logger.error("Failed to create network port: ", e);
        // TODO:workflow throw new CloudProviderException(e.getMessage());
        // TODO:workflow}

        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        // prepare the Network entity to be persisted

        NetworkPort networkPort = new NetworkPort();
        networkPort.setName(networkPortCreate.getName());
        networkPort.setDescription(networkPortCreate.getDescription());
        networkPort.setProperties(networkPortCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(networkPortCreate.getProperties()));
        networkPort.setTenant(tenant);

        networkPort.setProviderAssignedId(providerJob.getTargetResource().getProviderAssignedId());
        networkPort.setCloudProviderAccount(placement.getAccount());

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
        // TODO:workflowICloudProviderConnector connector =
        // this.getCloudProviderConnector(networkPort.getCloudProviderAccount(),
        // TODO:workflow networkPort.getLocation());
        Job providerJob = null;

        // TODO:workflow try {
        // TODO:workflow INetworkService networkService =
        // connector.getNetworkService();
        // TODO:workflow if (action.equals("delete")) {
        // TODO:workflow providerJob =
        // networkService.deleteNetworkPort(networkPort.getProviderAssignedId());
        // TODO:workflow } else if (action.equals("start")) {
        // TODO:workflow providerJob =
        // networkService.startNetworkPort(networkPort.getProviderAssignedId());
        // TODO:workflow } else if (action.equals("stop")) {
        // TODO:workflow providerJob =
        // networkService.stopNetworkPort(networkPort.getProviderAssignedId());
        // TODO:workflow }
        // TODO:workflow} catch (ConnectorException e) {
        // TODO:workflow NetworkManager.logger.error("Failed to " + action +
        // " network: ", e);
        // TODO:workflow throw new CloudProviderException(e.getMessage());
        // TODO:workflow}

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

    private List<NetworkPort> getNetworkPorts() throws CloudProviderException {
        return QueryHelper.getEntityList("NetworkPort", this.em, this.getTenant().getId(), NetworkPort.State.DELETED, false);
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("NetworkPort", NetworkPort.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .stateToIgnore(NetworkPort.State.DELETED));
    }

    @Override
    public QueryResult<NetworkPort> getNetworkPorts(final QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException {
        if (queryParams.length == 0) {
            List<NetworkPort> netPorts = this.getNetworkPorts();
            return new QueryResult<NetworkPort>(netPorts.size(), netPorts);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("NetworkPort", NetworkPort.class)
            .params(queryParams[0]);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).stateToIgnore(NetworkPort.State.DELETED));
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
        Tenant tenant = this.getTenant();

        if (networkPortConfiguration.getName() != null) {
            if (!this.em.createQuery("SELECT v FROM NetworkPortConfiguration v WHERE v.tenant.id=:tenantId AND v.name=:name")
                .setParameter("tenantId", tenant.getId()).setParameter("name", networkPortConfiguration.getName())
                .getResultList().isEmpty()) {
                throw new CloudProviderException("NetworkPortConfiguration already exists with name "
                    + networkPortConfiguration.getName());
            }
        }
        networkPortConfiguration.setTenant(tenant);
        networkPortConfiguration.setCreated(new Date());
        this.em.persist(networkPortConfiguration);
        this.em.flush();
        return networkPortConfiguration;
    }

    @Override
    public List<NetworkPortConfiguration> getNetworkPortConfigurations() throws CloudProviderException {
        return this.em.createQuery("SELECT v FROM NetworkPortConfiguration v WHERE v.tenant.id=:tenantId ORDER BY v.id")
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("NetworkPortConfiguration",
            NetworkPortConfiguration.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .returnPublicEntities());
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
        if (!this.em
            .createQuery("SELECT n FROM NetworkPortTemplate n WHERE n.networkPortConfig.id=:networkPortConfigurationId")
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
        Tenant tenant = this.getTenant();

        if (networkPortTemplate.getName() != null) {
            if (!this.em.createQuery("SELECT v FROM NetworkPortTemplate v WHERE v.tenant.id=:tenantId AND v.name=:name")
                .setParameter("tenantId", tenant.getId()).setParameter("name", networkPortTemplate.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("NetworkPortTemplate already exists with name "
                    + networkPortTemplate.getName());
            }
        }
        networkPortTemplate.setTenant(tenant);
        networkPortTemplate.setCreated(new Date());
        this.em.persist(networkPortTemplate);
        this.em.flush();
        return networkPortTemplate;
    }

    @Override
    public List<NetworkPortTemplate> getNetworkPortTemplates() throws CloudProviderException {
        return this.em.createQuery("SELECT v FROM NetworkPortTemplate v WHERE v.tenant.id=:tenantId ORDER BY v.id")
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("NetworkPortTemplate",
            NetworkPortTemplate.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .filterEmbbededTemplate().returnPublicEntities());
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
        Tenant tenant = this.getTenant();

        if (forwardingGroupTemplate.getName() != null) {
            if (!this.em.createQuery("SELECT v FROM ForwardingGroupTemplate v WHERE v.tenant.id=:tenantId AND v.name=:name")
                .setParameter("tenantId", tenant.getId()).setParameter("name", forwardingGroupTemplate.getName())
                .getResultList().isEmpty()) {
                throw new CloudProviderException("ForwardingGroupTemplate already exists with name "
                    + forwardingGroupTemplate.getName());
            }
        }
        forwardingGroupTemplate.setTenant(tenant);
        this.em.persist(forwardingGroupTemplate);
        this.em.flush();
        return forwardingGroupTemplate;
    }

    @Override
    public List<ForwardingGroupTemplate> getForwardingGroupTemplates() throws CloudProviderException {
        return this.em.createQuery("SELECT v FROM ForwardingGroupTemplate v WHERE v.tenant.id=:tenantId ORDER BY v.id")
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("ForwardingGroupTemplate",
            ForwardingGroupTemplate.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .filterEmbbededTemplate());
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
        Tenant tenant = this.getTenant();

        Placement placement = this.cloudProviderManager.placeResource(tenant.getId().toString(),
            forwardingGroupCreate.getProperties());
        ICloudProviderConnector connector = null;// this.getCloudProviderConnector(placement.getAccount(),
                                                 // placement.getLocation());
        if (connector == null) {
            throw new CloudProviderException("Cannot retrieve cloud provider connector "
                + placement.getAccount().getCloudProvider().getCloudProviderType());
        }

        // delegates network port creation to cloud provider connector
        Job providerJob = null;

        // TODO:workflowtry {
        // TODO:workflow INetworkService networkService =
        // connector.getNetworkService();
        // TODO:workflow providerJob =
        // networkService.createForwardingGroup(forwardingGroupCreate);
        // TODO:workflow} catch (ConnectorException e) {
        // TODO:workflow
        // NetworkManager.logger.error("Failed to create ForwardingGroup: ", e);
        // TODO:workflow throw new CloudProviderException(e.getMessage());
        // TODO:workflow}

        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        // prepare the ForwardingGroup entity to be persisted

        ForwardingGroup forwardingGroup = new ForwardingGroup();
        forwardingGroup.setName(forwardingGroupCreate.getName());
        forwardingGroup.setDescription(forwardingGroupCreate.getDescription());
        forwardingGroup.setProperties(forwardingGroupCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(forwardingGroupCreate.getProperties()));
        forwardingGroup.setTenant(tenant);

        forwardingGroup.setProviderAssignedId(providerJob.getTargetResource().getProviderAssignedId());
        forwardingGroup.setCloudProviderAccount(placement.getAccount());

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

    private List<ForwardingGroup> getForwardingGroups() throws CloudProviderException {
        return QueryHelper.getEntityList("ForwardingGroup", this.em, this.getTenant().getId(), ForwardingGroup.State.DELETED,
            false);
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder
            .builder("ForwardingGroup", ForwardingGroup.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .stateToIgnore(ForwardingGroup.State.DELETED));
    }

    @Override
    public QueryResult<ForwardingGroup> getForwardingGroups(final QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException {
        if (queryParams.length == 0) {
            List<ForwardingGroup> forwardingGroups = this.getForwardingGroups();
            return new QueryResult<ForwardingGroup>(forwardingGroups.size(), forwardingGroups);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder
            .builder("ForwardingGroup", ForwardingGroup.class).params(queryParams[0]);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).stateToIgnore(ForwardingGroup.State.DELETED));
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
        // TODO:workflowICloudProviderConnector connector =
        // this.getCloudProviderConnector(forwardingGroup.getCloudProviderAccount(),
        // TODO:workflow forwardingGroup.getLocation());
        Job providerJob = null;

        // TODO:workflow try {
        // TODO:workflow INetworkService networkService =
        // connector.getNetworkService();
        // TODO:workflow providerJob =
        // networkService.deleteForwardingGroup(forwardingGroup.getProviderAssignedId());
        // TODO:workflow} catch (ConnectorException e) {
        // TODO:workflow
        // NetworkManager.logger.error("Failed to delete forwarding group: ",
        // e);
        // TODO:workflow throw new CloudProviderException(e.getMessage());
        // TODO:workflow}

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
        // TODO:workflowICloudProviderConnector connector =
        // this.getCloudProviderConnector(forwardingGroup.getCloudProviderAccount(),
        // TODO:workflow forwardingGroup.getLocation());
        Job providerJob = null;

        // TODO:workflow try {
        // TODO:workflow INetworkService networkService =
        // connector.getNetworkService();
        // TODO:workflow providerJob =
        // networkService.addNetworkToForwardingGroup(forwardingGroup.getProviderAssignedId(),
        // TODO:workflow forwardingGroupNetwork);
        // TODO:workflow} catch (ConnectorException e) {
        // TODO:workflow
        // NetworkManager.logger.error("Failed to add network to forwarding group: ",
        // e);
        // TODO:workflow throw new CloudProviderException(e.getMessage());
        // TODO:workflow}

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
        // TODO:workflowICloudProviderConnector connector =
        // this.getCloudProviderConnector(forwardingGroup.getCloudProviderAccount(),
        // TODO:workflow forwardingGroup.getLocation());
        Job providerJob = null;

        // TODO:workflowtry {
        // TODO:workflow INetworkService networkService =
        // connector.getNetworkService();
        // TODO:workflow providerJob =
        // networkService.removeNetworkFromForwardingGroup(forwardingGroup.getProviderAssignedId(),
        // TODO:workflow
        // forwardingGroupNetwork.getNetwork().getProviderAssignedId());
        // TODO:workflow} catch (ConnectorException e) {
        // TODO:workflow
        // NetworkManager.logger.error("Failed to remove network from forwarding group: ",
        // e);
        // TODO:workflow throw new CloudProviderException(e.getMessage());
        // TODO:workflow}

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

    private List<Address> getAddresses() throws CloudProviderException {
        return this.em.createQuery("SELECT v FROM Address v WHERE v.tenant.id=:tenantId ORDER BY v.id")
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
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
    public QueryResult<Address> getAddresses(final QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException {
        if (queryParams.length == 0) {
            List<Address> addresses = this.getAddresses();
            return new QueryResult<Address>(addresses.size(), addresses);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Address", Address.class);
        return QueryHelper.getEntityList(this.em, params.tenantId(this.getTenant().getId()).params(queryParams[0]));
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
        Tenant tenant = this.getTenant();

        if (addressTemplate.getName() != null) {
            if (!this.em.createQuery("SELECT v FROM AddressTemplate v WHERE v.tenant.id=:tenantId AND v.name=:name")
                .setParameter("tenantId", tenant.getId()).setParameter("name", addressTemplate.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("AddressTemplate already exists with name " + addressTemplate.getName());
            }
        }
        addressTemplate.setTenant(tenant);
        addressTemplate.setCreated(new Date());
        this.em.persist(addressTemplate);
        this.em.flush();
        return addressTemplate;
    }

    @Override
    public List<AddressTemplate> getAddressTemplates() throws CloudProviderException {
        return this.em.createQuery("SELECT v FROM AddressTemplate v WHERE v.tenant.id=:tenantId ORDER BY v.id")
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder
            .builder("AddressTemplate", AddressTemplate.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .filterEmbbededTemplate());
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

    public Network getNetworkByProviderAssignedId(final String providerAssignedId) {
        List<Network> networks = this.em.createNamedQuery(Network.GET_NETWORK_BY_PROVIDER_ASSIGNED_ID)
            .setParameter("providerAssignedId", providerAssignedId).getResultList();
        if (!networks.isEmpty()) {
            return networks.get(0);
        }
        return null;
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
        // TODO:workflowICloudProviderConnector connector =
        // this.getCloudProviderConnector(network.getCloudProviderAccount(),
        // TODO:workflow network.getLocation());

        if (providerJob.getAction().equals("add")) {
            if (providerJob.getState() == Job.Status.SUCCESS) {
                try {
                    // TODO:workflownetwork.setState(connector.getNetworkService().getNetwork(network.getProviderAssignedId()).getState());
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
                    // TODO:workflownetwork.setState(connector.getNetworkService().getNetwork(network.getProviderAssignedId()).getState());
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
                    // TODO:workflownetwork.setState(connector.getNetworkService().getNetwork(network.getProviderAssignedId()).getState());
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
        // TODO:workflowICloudProviderConnector connector =
        // this.getCloudProviderConnector(networkPort.getCloudProviderAccount(),
        // TODO:workflow networkPort.getLocation());

        if (providerJob.getAction().equals("add")) {
            if (providerJob.getState() == Job.Status.SUCCESS) {
                try {
                    // TODO:workflownetworkPort.setState(connector.getNetworkService().getNetworkPort(networkPort.getProviderAssignedId())
                    // TODO:workflow .getState());
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
                    // TODO:workflownetworkPort.setState(connector.getNetworkService().getNetworkPort(networkPort.getProviderAssignedId())
                    // TODO:workflow .getState());
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
                    // TODO:workflownetworkPort.setState(connector.getNetworkService().getNetworkPort(networkPort.getProviderAssignedId())
                    // TODO:workflow .getState());
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

        // TODO:workflowICloudProviderConnector connector =
        // this.getCloudProviderConnector(forwardingGroup.getCloudProviderAccount(),
        // TODO:workflow forwardingGroup.getLocation());

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
                        // TODO:workflowforwardingGroup.setState(connector.getNetworkService()
                        // TODO:workflow
                        // .getForwardingGroup(forwardingGroup.getProviderAssignedId()).getState());
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
                    forwardingGroup.setNetworks(new HashSet<ForwardingGroupNetwork>());
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("ForwardingGroupNetwork",
            ForwardingGroupNetwork.class);
        return QueryHelper.getCollectionItemList(this.em, params.tenantId(this.getTenant().getId()).first(first).last(last)
            .filter(filters).attributes(attributes).containerType("ForwardingGroup").containerId(forwardingGroupId)
            .containerAttributeName("networks"));
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
