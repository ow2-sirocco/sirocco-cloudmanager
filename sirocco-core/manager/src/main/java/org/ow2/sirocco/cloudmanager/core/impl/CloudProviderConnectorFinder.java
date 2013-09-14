package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.ow2.sirocco.cloudmanager.connector.amazon.AmazonCloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.connector.mock.MockCloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.openstack.OpenStackCloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.vcd.VcdCloudProviderConnector;

@Singleton
public class CloudProviderConnectorFinder implements ICloudProviderConnectorFinder {
    private Map<String, ICloudProviderConnector> connectors = new HashMap<>();

    @PostConstruct
    void init() {
        this.connectors.put("amazon", new AmazonCloudProviderConnector());
        this.connectors.put("mock", new MockCloudProviderConnector());
        this.connectors.put("openstack", new OpenStackCloudProviderConnector());
        this.connectors.put("vcd", new VcdCloudProviderConnector());
    }

    @Override
    public List<ICloudProviderConnector> listCloudProviderConnectors() {
        return new ArrayList<ICloudProviderConnector>(this.connectors.values());
    }

    @Override
    public ICloudProviderConnector getCloudProviderConnector(final String cloudProviderType) {
        return this.connectors.get(cloudProviderType);
    }

}
