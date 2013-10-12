package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CloudProviderConnectorFinder implements ICloudProviderConnectorFinder {
    private static Logger logger = LoggerFactory.getLogger(CloudProviderConnectorFinder.class.getName());

    private Map<String, ICloudProviderConnector> connectors = new HashMap<>();

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Override
    public List<ICloudProviderConnector> listCloudProviderConnectors() {
        return new ArrayList<ICloudProviderConnector>(this.connectors.values());
    }

    @Override
    public ICloudProviderConnector getCloudProviderConnector(final String cloudProviderType) {
        if (this.connectors.get(cloudProviderType) == null) {
            CloudProviderProfile profile = null;
            try {
                profile = (CloudProviderProfile) this.em.createQuery("SELECT p FROM CloudProviderProfile p WHERE p.type=:type")
                    .setParameter("type", cloudProviderType).getSingleResult();
            } catch (NoResultException e) {
                CloudProviderConnectorFinder.logger.error("Unknown cloud provider type", e);
                return null;
            }
            ICloudProviderConnector connector;
            try {
                connector = (ICloudProviderConnector) Class.forName(profile.getConnectorClass()).newInstance();
                this.connectors.put(cloudProviderType, connector);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                CloudProviderConnectorFinder.logger.error("Failed to create connector for type " + cloudProviderType, e);
            }
        }

        return this.connectors.get(cloudProviderType);
    }

}
