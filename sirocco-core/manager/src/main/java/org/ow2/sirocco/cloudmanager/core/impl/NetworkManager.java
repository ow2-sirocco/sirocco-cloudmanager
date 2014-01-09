package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.connector.api.INetworkService;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager.Placement;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.QueryParams;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.ResourceStateChangeEvent;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.remote.IRemoteNetworkManager;
import org.ow2.sirocco.cloudmanager.core.impl.command.NetworkCreateCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.NetworkDeleteCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.SecurityGroupCreateCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.SecurityGroupDeleteCommand;
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
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
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
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroup.State;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupRule;
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
    private ICloudProviderConnectorFinder connectorFinder;

    @EJB
    private ICloudProviderManager cloudProviderManager;

    @EJB
    private ITenantManager tenantManager;

    @EJB
    private IMachineManager machineManager;

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

    private void fireResourceStateChangeEvent(final CloudResource network) {
        this.jmsContext.createProducer().setProperty("tenantId", network.getTenant().getUuid())
            .send(this.resourceStateChangeTopic, new ResourceStateChangeEvent(network));
    }

    @Override
    public void updateNetworkState(final int networkId, final Network.State state) throws CloudProviderException {
        Network network = this.getNetworkById(networkId);
        network.setState(state);
        this.fireResourceStateChangeEvent(network);
    }

    public Network getPublicNetwork() {
        List<Network> publicNetworks = this.em.createQuery(
            "SELECT n FROM Network n WHERE n.networkType=org.ow2.sirocco.cloudmanager.model.cimi.Network$Type.PUBLIC",
            Network.class).getResultList();
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

        Placement placement = this.cloudProviderManager.placeResource(tenant.getId(), networkCreate);

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
        network.setCreated(new Date());
        network.setUpdated(network.getCreated());
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

        ObjectMessage message = this.jmsContext
            .createObjectMessage(new NetworkCreateCommand(networkCreate).setAccount(placement.getAccount())
                .setLocation(placement.getLocation()).setResourceId(network.getId()).setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    @Override
    public void syncNetwork(final int networkId, final Network updatedNetwork, final int jobId) {
        Network network = this.em.find(Network.class, networkId);
        Job job = this.em.find(Job.class, jobId);
        if (updatedNetwork == null) {
            network.setState(Network.State.DELETED);
        } else {
            network.setState(updatedNetwork.getState());
            network.setUpdated(new Date());
        }
        if (network.getState() == Network.State.DELETED) {
            network.setDeleted(new Date());
        }
        this.fireResourceStateChangeEvent(network);
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
    public Network getNetworkById(final int networkId) throws ResourceNotFoundException {
        Network network = this.em.find(Network.class, networkId);
        if (network == null || network.getState() == Network.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid network id " + networkId);
        }
        return network;
    }

    @Override
    public Network getNetworkByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("Network.findByUuid", Network.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public Network getNetworkAttributes(final String networkId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkByUuid(networkId);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Job updateNetworkAttributes(final String networkId, final Map<String, Object> updatedAttributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO
        throw new UnsupportedOperationException();
    }

    private Job performActionOnNetwork(final String networkId, final String action) throws ResourceNotFoundException,
        CloudProviderException {
        Network network = this.getNetworkByUuid(networkId);
        Tenant tenant = this.getTenant();

        if (action.equals("start")) {
            network.setState(Network.State.STARTING);
        } else if (action.equals("stop")) {
            network.setState(Network.State.STOPPING);
        } else if (action.equals("delete")) {
            network.setState(Network.State.DELETING);
        }

        this.fireResourceStateChangeEvent(network);

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
                network.getId()).setJob(job));
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
        return this.em
            .createQuery("SELECT v FROM NetworkConfiguration v WHERE v.tenant.id=:tenantId ORDER BY v.id",
                NetworkConfiguration.class).setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @Override
    public NetworkConfiguration getNetworkConfigurationById(final int networkConfigId) throws ResourceNotFoundException {
        NetworkConfiguration networkConfig = this.em.find(NetworkConfiguration.class, networkConfigId);
        if (networkConfig == null) {
            throw new ResourceNotFoundException(" Invalid networkConfig id " + networkConfigId);
        }
        return networkConfig;
    }

    @Override
    public NetworkConfiguration getNetworkConfigurationByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("NetworkConfiguration.findByUuid", NetworkConfiguration.class)
                .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public NetworkConfiguration getNetworkConfigurationAttributes(final String networkConfigId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkConfigurationByUuid(networkConfigId);
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
    public void deleteNetworkConfiguration(final String networkConfigUuid) throws ResourceNotFoundException,
        CloudProviderException, ResourceConflictException {
        if (!this.em.createQuery("SELECT n FROM NetworkTemplate n WHERE n.networkConfig.uuid=:networkConfigUuid")
            .setParameter("networkConfigUuid", networkConfigUuid).getResultList().isEmpty()) {
            throw new ResourceConflictException("Cannot delete NetworkConfiguration with id " + networkConfigUuid
                + " used by a NetworkTemplate");
        }
        NetworkConfiguration networkConfig = this.getNetworkConfigurationByUuid(networkConfigUuid);
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
                "SELECT v FROM NetworkTemplate v WHERE v.tenant.id=:tenantId AND v.isEmbeddedInSystemTemplate=false ORDER BY v.id",
                NetworkTemplate.class).setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @Override
    public NetworkTemplate getNetworkTemplateById(final int networkTemplateId) throws ResourceNotFoundException {
        NetworkTemplate networkTemplate = this.em.find(NetworkTemplate.class, networkTemplateId);
        if (networkTemplate == null) {
            throw new ResourceNotFoundException(" Invalid networkConfig id " + networkTemplateId);
        }
        return networkTemplate;
    }

    @Override
    public NetworkTemplate getNetworkTemplateByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("NetworkTemplate.findByUuid", NetworkTemplate.class).setParameter("uuid", uuid)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public NetworkTemplate getNetworkTemplateAttributes(final String networkTemplateId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkTemplateByUuid(networkTemplateId);
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
    public void deleteNetworkTemplate(final String networkTemplateUuid) throws ResourceNotFoundException,
        CloudProviderException {
        NetworkTemplate networkTemplate = this.getNetworkTemplateByUuid(networkTemplateUuid);
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
        // TODO
        throw new UnsupportedOperationException();
    }

    private Job performActionOnNetworkPort(final String networkPortId, final String action) throws ResourceNotFoundException,
        CloudProviderException {
        NetworkPort networkPort = this.getNetworkPortByUuid(networkPortId);
        // TODO
        throw new UnsupportedOperationException();
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
    public NetworkPort getNetworkPortById(final int networkPortId) throws ResourceNotFoundException {
        NetworkPort networkPort = this.em.find(NetworkPort.class, networkPortId);
        if (networkPort == null || networkPort.getState() == NetworkPort.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid networkPort id " + networkPortId);
        }
        return networkPort;
    }

    @Override
    public NetworkPort getNetworkPortByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("NetworkPort.findByUuid", NetworkPort.class).setParameter("uuid", uuid)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public NetworkPort getNetworkPortAttributes(final String networkPortId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkPortByUuid(networkPortId);
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
        return this.em
            .createQuery("SELECT v FROM NetworkPortConfiguration v WHERE v.tenant.id=:tenantId ORDER BY v.id",
                NetworkPortConfiguration.class).setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @Override
    public NetworkPortConfiguration getNetworkPortConfigurationById(final int networkPortConfigurationId)
        throws ResourceNotFoundException {
        NetworkPortConfiguration networkPort = this.em.find(NetworkPortConfiguration.class, networkPortConfigurationId);
        if (networkPort == null) {
            throw new ResourceNotFoundException(" Invalid NetworkPortConfiguration id " + networkPortConfigurationId);
        }
        return networkPort;
    }

    @Override
    public NetworkPortConfiguration getNetworkPortConfigurationByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("NetworkPortConfiguration.findByUuid", NetworkPortConfiguration.class)
                .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public NetworkPortConfiguration getNetworkPortConfigurationAttributes(final String networkPortConfigurationId,
        final List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkPortConfigurationByUuid(networkPortConfigurationId);
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
    public void deleteNetworkPortConfiguration(final String networkPortConfigUuid) throws ResourceNotFoundException,
        CloudProviderException {
        if (!this.em.createQuery("SELECT n FROM NetworkPortTemplate n WHERE n.networkPortConfig.uuid=:networkPortConfigUuid")
            .setParameter("networkPortConfigUuid", networkPortConfigUuid).getResultList().isEmpty()) {
            throw new ResourceConflictException("Cannot delete NetworkPortConfiguration with id " + networkPortConfigUuid
                + " used by a NetworkPortTemplate");
        }
        NetworkPortConfiguration networkPortConfig = this.getNetworkPortConfigurationByUuid(networkPortConfigUuid);
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
        return this.em
            .createQuery("SELECT v FROM NetworkPortTemplate v WHERE v.tenant.id=:tenantId ORDER BY v.id",
                NetworkPortTemplate.class).setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @Override
    public NetworkPortTemplate getNetworkPortTemplateById(final int networkPortTemplateId) throws ResourceNotFoundException {
        NetworkPortTemplate networkPortTemplate = this.em.find(NetworkPortTemplate.class, networkPortTemplateId);
        if (networkPortTemplate == null) {
            throw new ResourceNotFoundException(" Invalid NetworkPortTemplate id " + networkPortTemplateId);
        }
        return networkPortTemplate;
    }

    @Override
    public NetworkPortTemplate getNetworkPortTemplateByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("NetworkPortTemplate.findByUuid", NetworkPortTemplate.class)
                .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public NetworkPortTemplate getNetworkPortTemplateAttributes(final String networkPortTemplateId,
        final List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
        return this.getNetworkPortTemplateByUuid(networkPortTemplateId);
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
    public void deleteNetworkPortTemplate(final String networkPortTemplateUuidd) throws ResourceNotFoundException,
        CloudProviderException {
        NetworkPortTemplate networkPortTemplate = this.getNetworkPortTemplateByUuid(networkPortTemplateUuidd);
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
        return this.em
            .createQuery("SELECT v FROM ForwardingGroupTemplate v WHERE v.tenant.id=:tenantId ORDER BY v.id",
                ForwardingGroupTemplate.class).setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @Override
    public ForwardingGroupTemplate getForwardingGroupTemplateById(final int forwardingGroupTemplateId)
        throws ResourceNotFoundException {
        ForwardingGroupTemplate forwardingGroupTemplate = this.em
            .find(ForwardingGroupTemplate.class, forwardingGroupTemplateId);
        if (forwardingGroupTemplate == null) {
            throw new ResourceNotFoundException(" Invalid ForwardingGroupTemplate id " + forwardingGroupTemplateId);
        }
        return forwardingGroupTemplate;
    }

    @Override
    public ForwardingGroupTemplate getForwardingGroupTemplateByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("ForwardingGroupTemplate.findByUuid", ForwardingGroupTemplate.class)
                .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public ForwardingGroupTemplate getForwardingGroupTemplateAttributes(final String forwardingGroupTemplateId,
        final List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
        return this.getForwardingGroupTemplateByUuid(forwardingGroupTemplateId);
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
    public void deleteForwardingGroupTemplate(final String forwardingGroupTemplateUuid) throws ResourceNotFoundException,
        CloudProviderException {
        ForwardingGroupTemplate forwardingGroupTemplate = this.getForwardingGroupTemplateByUuid(forwardingGroupTemplateUuid);
        this.em.remove(forwardingGroupTemplate);
    }

    //
    // ForwardingGroup operations
    //

    @Override
    public Job createForwardingGroup(final ForwardingGroupCreate forwardingGroupCreate) throws InvalidRequestException,
        CloudProviderException {
        NetworkManager.logger.info("Creating ForwardingGroup");
        Tenant tenant = this.getTenant();

        Placement placement = this.cloudProviderManager.placeResource(tenant.getId(), forwardingGroupCreate);

        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(placement.getAccount()
            .getCloudProvider().getCloudProviderType());
        if (connector == null) {
            throw new CloudProviderException("Cannot find connector for cloud provider type "
                + placement.getAccount().getCloudProvider().getCloudProviderType());
        }

        try {
            connector.getNetworkService().createForwardingGroup(forwardingGroupCreate,
                new ProviderTarget().account(placement.getAccount()).location(placement.getLocation()));
        } catch (ConnectorException e) {
            throw new CloudProviderException("Failed to create ForwardingGroup", e);
        }

        // prepare the ForwardingGroup entity to be persisted

        ForwardingGroup forwardingGroup = new ForwardingGroup();
        forwardingGroup.setName(forwardingGroupCreate.getName());
        forwardingGroup.setDescription(forwardingGroupCreate.getDescription());
        forwardingGroup.setProperties(forwardingGroupCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(forwardingGroupCreate.getProperties()));
        forwardingGroup.setTenant(tenant);

        forwardingGroup.setCloudProviderAccount(placement.getAccount());
        forwardingGroup.setLocation(placement.getLocation());

        List<ForwardingGroupNetwork> networks = new ArrayList<ForwardingGroupNetwork>();
        if (forwardingGroupCreate.getForwardingGroupTemplate().getNetworks() != null) {
            for (Network net : forwardingGroupCreate.getForwardingGroupTemplate().getNetworks()) {
                ForwardingGroupNetwork forwardingGroupNetwork = new ForwardingGroupNetwork();
                forwardingGroupNetwork.setNetwork(net);
                this.em.persist(forwardingGroupNetwork);
                networks.add(forwardingGroupNetwork);
            }
        }
        forwardingGroup.setNetworks(networks);

        forwardingGroup.setState(ForwardingGroup.State.AVAILABLE);
        forwardingGroup.setCreated(new Date());
        forwardingGroup.setUpdated(forwardingGroup.getCreated());
        this.em.persist(forwardingGroup);

        Job job = new Job();
        job.setTenant(tenant);
        job.setTargetResource(forwardingGroup);
        job.setCreated(new Date());
        job.setState(Job.Status.SUCCESS);
        job.setAction("create");
        this.em.persist(job);
        this.em.flush();

        return job;
    }

    private List<ForwardingGroup> getForwardingGroups() throws CloudProviderException {
        return QueryHelper.getEntityList("ForwardingGroup", this.em, this.getTenant().getId(), ForwardingGroup.State.DELETED,
            false);
    }

    @Override
    public ForwardingGroup getForwardingGroupById(final int forwardingGroupId) throws ResourceNotFoundException {
        ForwardingGroup forwardingGroup = this.em.find(ForwardingGroup.class, forwardingGroupId);
        if (forwardingGroup == null || forwardingGroup.getState() == ForwardingGroup.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid ForwardingGroup id " + forwardingGroupId);
        }
        forwardingGroup.getNetworks().size();
        return forwardingGroup;
    }

    @Override
    public ForwardingGroup getForwardingGroupByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("ForwardingGroup.findByUuid", ForwardingGroup.class).setParameter("uuid", uuid)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public ForwardingGroup getForwardingGroupAttributes(final String forwardingGroupId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getForwardingGroupByUuid(forwardingGroupId);
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
        ForwardingGroup forwardingGroup = this.getForwardingGroupByUuid(forwardingGroupId);
        if (forwardingGroup == null) {
            throw new ResourceNotFoundException("ForwardingGroup " + forwardingGroupId + " doesn't not exist");
        }

        try {
            ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(forwardingGroup
                .getCloudProviderAccount().getCloudProvider().getCloudProviderType());
            connector.getNetworkService()
                .deleteForwardingGroup(
                    forwardingGroup,
                    new ProviderTarget().account(forwardingGroup.getCloudProviderAccount()).location(
                        forwardingGroup.getLocation()));
        } catch (ConnectorException e) {
            throw new CloudProviderException("Failed to create ForwardingGroup", e);
        }

        forwardingGroup.setState(ForwardingGroup.State.DELETED);

        Job job = new Job();
        job.setTenant(this.getTenant());
        job.setTargetResource(forwardingGroup);
        job.setCreated(new Date());
        job.setState(Job.Status.SUCCESS);
        job.setAction("delete");
        this.em.persist(job);

        return job;
    }

    @Override
    public Job addNetworkToForwardingGroup(final String forwardingGroupId, final ForwardingGroupNetwork forwardingGroupNetwork)
        throws ResourceNotFoundException, CloudProviderException {
        ForwardingGroup forwardingGroup = this.getForwardingGroupByUuid(forwardingGroupId);
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
        // TODO
        throw new UnsupportedOperationException();
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
        Tenant tenant = this.getTenant();
        Placement placement = this.cloudProviderManager.placeResource(tenant.getId(), addressCreate);

        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(placement.getAccount()
            .getCloudProvider().getCloudProviderType());

        Address externalAddress;
        try {
            INetworkService networkService = connector.getNetworkService();
            externalAddress = networkService.allocateAddress(addressCreate.getProperties(),
                new ProviderTarget().account(placement.getAccount()).location(placement.getLocation()));
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }

        Address address = new Address();
        address.setState(Address.State.CREATED);
        address.setProviderAssignedId(externalAddress.getProviderAssignedId());
        address.setTenant(tenant);
        address.setCloudProviderAccount(placement.getAccount());
        address.setLocation(placement.getLocation());
        address.setIp(externalAddress.getIp());

        address.setCreated(new Date());
        address.setUpdated(address.getCreated());
        this.em.persist(address);
        this.em.flush();

        Job job = Job.newBuilder().tenant(tenant).action(Job.Action.ADD).status(Status.SUCCESS).target(address).build();
        this.em.persist(job);
        this.em.flush();

        return job;
    }

    private List<Address> getAddresses() throws CloudProviderException {
        return QueryHelper.getEntityList("Address", this.em, this.getTenant().getId(), Address.State.DELETED, false);
    }

    @Override
    public Address getAddressById(final int addressId) throws ResourceNotFoundException {
        Address address = this.em.find(Address.class, addressId);
        if (address == null) {
            throw new ResourceNotFoundException(" Invalid Address id " + addressId);
        }
        return address;
    }

    @Override
    public Address getAddressByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("Address.findByUuid", Address.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public Address getAddressAttributes(final String addressId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getAddressByUuid(addressId);
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
    public Job deleteAddress(final String addressUuid) throws ResourceNotFoundException, CloudProviderException {
        Address address = this.getAddressByUuid(addressUuid);
        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(address.getCloudProviderAccount()
            .getCloudProvider().getCloudProviderType());

        try {
            INetworkService networkService = connector.getNetworkService();
            networkService.deleteAddress(address,
                new ProviderTarget().account(address.getCloudProviderAccount()).location(address.getLocation()));
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }

        address.setState(Address.State.DELETED);
        address.setDeleted(new Date());
        address.setResource(null);

        Job job = Job.newBuilder().tenant(this.getTenant()).action(Job.Action.DELETE).status(Status.SUCCESS).target(address)
            .build();
        this.em.persist(job);
        this.em.flush();

        return job;
    }

    @Override
    public Job addAddressToMachine(final String machineUuid, final String ip) throws ResourceNotFoundException,
        CloudProviderException {
        Tenant tenant = this.getTenant();
        Machine machine = this.machineManager.getMachineByUuid(machineUuid);
        Address address;
        try {
            address = this.em.createNamedQuery("Address.findByIp", Address.class).setParameter("ip", ip)
                .setParameter("tenant", tenant).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
        if (address.getState() == Address.State.DELETED) {
            throw new ResourceNotFoundException();
        }
        if (address.getResource() != null) {
            throw new ResourceConflictException("Address " + ip + " already mapped");
        }

        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(address.getCloudProviderAccount()
            .getCloudProvider().getCloudProviderType());
        try {
            INetworkService networkService = connector.getNetworkService();
            networkService.addAddressToMachine(machine.getProviderAssignedId(), address,
                new ProviderTarget().account(address.getCloudProviderAccount()).location(address.getLocation()));
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }

        address.setResource(machine);
        // TODO add Machine nic.address

        Job job = Job.newBuilder().tenant(this.getTenant()).action(Job.Action.ADD).status(Status.SUCCESS).target(machine)
            .affectedResource(address).build();
        this.em.persist(job);
        this.em.flush();

        return job;
    }

    @Override
    public Job removeAddressFromMachine(final String machineUuid, final String ip) throws ResourceNotFoundException,
        CloudProviderException {
        Tenant tenant = this.getTenant();
        Machine machine = this.machineManager.getMachineByUuid(machineUuid);
        Address address;
        try {
            address = this.em.createNamedQuery("Address.findByIp", Address.class).setParameter("ip", ip)
                .setParameter("tenant", tenant).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
        if (address.getState() == Address.State.DELETED) {
            throw new ResourceNotFoundException();
        }
        if (address.getResource() == null || !((Machine) address.getResource()).getUuid().equals(machineUuid)) {
            throw new ResourceConflictException("Address " + ip + " not associated with machine " + machineUuid);
        }

        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(address.getCloudProviderAccount()
            .getCloudProvider().getCloudProviderType());
        try {
            INetworkService networkService = connector.getNetworkService();
            networkService.removeAddressFromMachine(machine.getProviderAssignedId(), address,
                new ProviderTarget().account(address.getCloudProviderAccount()).location(address.getLocation()));
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }

        address.setResource(null);
        // TODO remove Machine nic.address

        Job job = Job.newBuilder().tenant(this.getTenant()).action(Job.Action.ADD).status(Status.SUCCESS).target(machine)
            .affectedResource(address).build();
        this.em.persist(job);
        this.em.flush();

        return job;
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
        return this.em
            .createQuery("SELECT v FROM AddressTemplate v WHERE v.tenant.id=:tenantId ORDER BY v.id", AddressTemplate.class)
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @Override
    public AddressTemplate getAddressTemplateById(final int addressTemplateId) throws ResourceNotFoundException {
        AddressTemplate addressTemplate = this.em.find(AddressTemplate.class, addressTemplateId);
        if (addressTemplate == null) {
            throw new ResourceNotFoundException(" Invalid AddressTemplate id " + addressTemplateId);
        }
        return addressTemplate;
    }

    @Override
    public AddressTemplate getAddressTemplateByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("AddressTemplate.findByUuid", AddressTemplate.class).setParameter("uuid", uuid)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public AddressTemplate getAddressTemplateAttributes(final String addressTemplateId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        return this.getAddressTemplateByUuid(addressTemplateId);
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
    public void deleteAddressTemplate(final String addressTemplateUuid) throws ResourceNotFoundException,
        CloudProviderException {
        AddressTemplate addressTemplate = this.getAddressTemplateByUuid(addressTemplateUuid);
        this.em.remove(addressTemplate);
    }

    public Network getNetworkByProviderAssignedId(final String providerAssignedId) {
        List<Network> networks = this.em.createNamedQuery("Network.findByProviderAssignedId", Network.class)
            .setParameter("providerAssignedId", providerAssignedId).getResultList();
        if (!networks.isEmpty()) {
            return networks.get(0);
        }
        return null;
    }

    private NetworkPort getNetworkPortByProviderAssignedId(final String providerAssignedId) {
        NetworkPort networkPort = this.em.createNamedQuery("NetworkPort.findByProviderAssignedId", NetworkPort.class)
            .setParameter("providerAssignedId", providerAssignedId).getSingleResult();
        return networkPort;
    }

    private ForwardingGroup getForwardingGroupByProviderAssignedId(final String providerAssignedId) {
        ForwardingGroup forwardingGroup = this.em
            .createNamedQuery("ForwardingGroup.findByProviderAssignedId", ForwardingGroup.class)
            .setParameter("providerAssignedId", providerAssignedId).getSingleResult();
        return forwardingGroup;
    }

    @Override
    public List<ForwardingGroupNetwork> getForwardingGroupNetworks(final String forwardingGroupId)
        throws ResourceNotFoundException, CloudProviderException {
        ForwardingGroup forwardingGroup = this.getForwardingGroupByUuid(forwardingGroupId);
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
        ForwardingGroup forwardingGroup = this.getForwardingGroupByUuid(forwardingGroupId);
        ForwardingGroupNetwork forwardingGroupNetwork = null;
        try {
            forwardingGroupNetwork = this.em
                .createNamedQuery("ForwardingGroupNetwork.findByUuid", ForwardingGroupNetwork.class)
                .setParameter("uuid", fgNetworkId).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
        if (!forwardingGroup.getNetworks().contains(forwardingGroupNetwork)) {
            throw new ResourceNotFoundException();
        }
        return forwardingGroupNetwork;
    }

    //
    // Security groups
    //

    @Override
    public Job createSecurityGroup(final SecurityGroupCreate securityGroupCreate) throws InvalidRequestException,
        CloudProviderException {
        Tenant tenant = this.getTenant();
        Placement placement = this.cloudProviderManager.placeResource(tenant.getId(), securityGroupCreate);

        SecurityGroup secGroup = new SecurityGroup();
        secGroup.setName(securityGroupCreate.getName());
        secGroup.setDescription(securityGroupCreate.getDescription());
        secGroup.setProperties(securityGroupCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(securityGroupCreate.getProperties()));
        secGroup.setTenant(tenant);
        secGroup.setCloudProviderAccount(placement.getAccount());
        secGroup.setLocation(placement.getLocation());

        secGroup.setState(SecurityGroup.State.CREATING);
        secGroup.setCreated(new Date());
        secGroup.setUpdated(secGroup.getUpdated());
        this.em.persist(secGroup);
        this.em.flush();

        Job job = Job.newBuilder().tenant(tenant).action(Job.Action.ADD).target(secGroup).build();
        this.em.persist(job);
        this.em.flush();

        ObjectMessage message = this.jmsContext.createObjectMessage(new SecurityGroupCreateCommand(securityGroupCreate)
            .setAccount(placement.getAccount()).setLocation(placement.getLocation()).setResourceId(secGroup.getId())
            .setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    @Override
    public Job deleteSecurityGroup(final String securityGroupUuid) throws CloudProviderException {
        SecurityGroup secGroup = this.getSecurityGroupByUuid(securityGroupUuid);
        Tenant tenant = this.getTenant();

        if (!secGroup.getMembers().isEmpty()) {
            throw new ResourceConflictException("Security group " + secGroup.getName() + " used by machines");
        }

        if (!this.em.createNamedQuery("SecurityGroupRule.findUsingGroup", SecurityGroupRule.class)
            .setParameter("source", secGroup).getResultList().isEmpty()) {
            throw new ResourceConflictException("Security group " + secGroup.getName() + " used by another security group");
        }

        secGroup.setState(SecurityGroup.State.DELETING);

        this.fireResourceStateChangeEvent(secGroup);

        Job job = Job.newBuilder().tenant(tenant).action(Job.Action.DELETE).target(secGroup).build();
        this.em.persist(job);
        this.em.flush();

        ObjectMessage message = this.jmsContext.createObjectMessage(new SecurityGroupDeleteCommand().setResourceId(
            secGroup.getId()).setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    @Override
    public void updateSecurityGroupState(final int securityGroupId, final State state) throws CloudProviderException {
        SecurityGroup secGroup = this.getSecurityGroupById(securityGroupId);
        secGroup.setState(state);
        if (state == State.DELETED) {
            for (SecurityGroupRule rule : secGroup.getRules()) {
                this.em.remove(rule);
            }
            secGroup.getRules().clear();
            secGroup.setDeleted(new Date());
        } else if (state == State.AVAILABLE) {
            secGroup.setUpdated(new Date());
        }
        this.fireResourceStateChangeEvent(secGroup);
    }

    private SecurityGroupRule addRuleToSecurityGroup(final SecurityGroup secGroup, final SecurityGroupRule rule)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(secGroup.getCloudProviderAccount()
            .getCloudProvider().getCloudProviderType());

        String ruleProviderAssignedId;
        try {
            INetworkService networkService = connector.getNetworkService();
            ruleProviderAssignedId = networkService.addRuleToSecurityGroup(secGroup.getProviderAssignedId(), rule,
                new ProviderTarget().account(secGroup.getCloudProviderAccount()).location(secGroup.getLocation()));
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }

        rule.setProviderAssignedId(ruleProviderAssignedId);
        this.em.persist(rule);

        rule.setParentGroup(secGroup);
        secGroup.getRules().add(rule);

        this.em.flush();

        return rule;
    }

    @Override
    public SecurityGroupRule addRuleToSecurityGroupUsingIpRange(final String securityGroupUuid, final String cidr,
        final String ipProtocol, final int fromPort, final int toPort) throws CloudProviderException {
        SecurityGroup secGroup = this.getSecurityGroupByUuid(securityGroupUuid);
        SecurityGroupRule rule = new SecurityGroupRule();
        rule.setIpProtocol(ipProtocol);
        rule.setFromPort(fromPort);
        rule.setToPort(toPort);
        rule.setSourceIpRange(cidr);

        return this.addRuleToSecurityGroup(secGroup, rule);
    }

    @Override
    public SecurityGroupRule addRuleToSecurityGroupUsingSourceGroup(final String securityGroupUuid,
        final String sourceGroupUuid, final String ipProtocol, final int fromPort, final int toPort)
        throws CloudProviderException {
        SecurityGroup secGroup = this.getSecurityGroupByUuid(securityGroupUuid);
        SecurityGroup sourceSecGroup = this.getSecurityGroupByUuid(sourceGroupUuid);
        SecurityGroupRule rule = new SecurityGroupRule();
        rule.setIpProtocol(ipProtocol);
        rule.setFromPort(fromPort);
        rule.setToPort(toPort);
        rule.setSourceGroup(sourceSecGroup);

        return this.addRuleToSecurityGroup(secGroup, rule);
    }

    @Override
    public void deleteRuleFromSecurityGroup(final String securityGroupUuid, final String ruleUuid)
        throws CloudProviderException {
        SecurityGroup secGroup = this.getSecurityGroupByUuid(securityGroupUuid);
        SecurityGroupRule rule;
        try {
            rule = this.em.createNamedQuery("SecurityGroupRule.findByUuid", SecurityGroupRule.class)
                .setParameter("uuid", ruleUuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(secGroup.getCloudProviderAccount()
            .getCloudProvider().getCloudProviderType());

        try {
            INetworkService networkService = connector.getNetworkService();
            networkService.deleteRuleFromSecurityGroup(secGroup.getProviderAssignedId(), rule,
                new ProviderTarget().account(secGroup.getCloudProviderAccount()).location(secGroup.getLocation()));
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }
        rule.setParentGroup(null);
        secGroup.getRules().remove(rule);
        this.em.remove(rule);
    }

    @Override
    public SecurityGroup getSecurityGroupByUuid(final String groupUuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("SecurityGroup.findByUuid", SecurityGroup.class).setParameter("uuid", groupUuid)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public SecurityGroup getSecurityGroupById(final int groupId) throws ResourceNotFoundException {
        SecurityGroup secGroup = this.em.find(SecurityGroup.class, groupId);
        if (secGroup == null || secGroup.getState() == SecurityGroup.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid security group id " + groupId);
        }
        return secGroup;
    }

    private List<SecurityGroup> getSecurityGroups() throws CloudProviderException {
        return QueryHelper
            .getEntityList("SecurityGroup", this.em, this.getTenant().getId(), SecurityGroup.State.DELETED, false);
    }

    @Override
    public QueryResult<SecurityGroup> getSecurityGroups(final QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException {
        if (queryParams.length == 0) {
            List<SecurityGroup> groups = this.getSecurityGroups();
            return new QueryResult<SecurityGroup>(groups.size(), groups);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("SecurityGroup", SecurityGroup.class)
            .params(queryParams[0]);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).stateToIgnore(SecurityGroup.State.DELETED));
    }

}
