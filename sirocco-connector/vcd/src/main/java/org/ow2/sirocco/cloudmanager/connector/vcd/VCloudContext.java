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
 */

package org.ow2.sirocco.cloudmanager.connector.vcd;

import java.util.Map;
import java.util.logging.Level;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.slf4j.Logger;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Expression;
import com.vmware.vcloud.sdk.Filter;
import com.vmware.vcloud.sdk.OrgVdcNetwork;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.QueryParams;
import com.vmware.vcloud.sdk.ReferenceResult;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.constants.Version;
import com.vmware.vcloud.sdk.constants.query.ExpressionType;
import com.vmware.vcloud.sdk.constants.query.QueryReferenceField;
import com.vmware.vcloud.sdk.constants.query.QueryReferenceType;

public class VCloudContext {

    private VcloudClient vcloudClient;

    private String orgName;

    private Organization org;

    // private AdminOrganization adminOrg;

    private String vdcName;

    private Vdc vdc;

    // private AdminVdc adminVdc;

    private String cimiPublicOrgVdcNetworkName;

    private OrgVdcNetwork cimiPublicOrgVdcNetwork;

    private Network cimiPublicNetwork;

    private boolean cimiPublicOrgVdcNetworkIsRouted = false;

    private String edgeGatewayName;

    private EdgeGateway edgeGateway;

    /* TODO
     * remove useless setters
     * */

    public VCloudContext(final CloudProviderAccount cloudProviderAccount, final Logger logger) throws ConnectorException {

        /*Map<String, String> properties = cloudProviderAccount.getCloudProvider().getProperties();*/
        Map<String, String> properties = cloudProviderAccount.getProperties();
        if (properties == null || properties.get("orgName") == null || properties.get("vdcName") == null
            || properties.get("cimiPublicOrgVdcNetworkName") == null) {
            throw new ConnectorException("No access to properties: orgName or vdcName or cimiPublicOrgVdcNetworkName");
        }
        this.orgName = properties.get("orgName");
        this.vdcName = properties.get("vdcName");
        this.cimiPublicOrgVdcNetworkName = properties.get("cimiPublicOrgVdcNetworkName");
        logger.info("connect: " + cloudProviderAccount.getLogin() + " to Organization=" + this.orgName + ", VirtualDataCenter="
            + this.vdcName + ", cimiPublicOrgVdcNetwork=" + this.cimiPublicOrgVdcNetworkName);

        try {
            VcloudClient.setLogLevel(Level.OFF);
            // VcloudClient.setLogLevel(Level.INFO);
            this.vcloudClient = new VcloudClient(cloudProviderAccount.getCloudProvider().getEndpoint(), Version.V5_1);
            this.vcloudClient.registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
            String user = cloudProviderAccount.getLogin() + "@" + this.orgName;
            /*String user = "Administrator@System";*/// !!!
            this.vcloudClient.login(user, cloudProviderAccount.getPassword());

            // Org
            ReferenceType orgRef = this.vcloudClient.getOrgRefByName(this.orgName);
            if (orgRef == null) {
                throw new ConnectorException("No Organization: " + this.orgName);
            }
            this.org = Organization.getOrganizationByReference(this.vcloudClient, orgRef);
            // require orgAdmin role
            /*ReferenceType adminOrgRef = this.vcloudClient.getVcloudAdmin().getAdminOrgRefByName(this.orgName);
            this.adminOrg = AdminOrganization.getAdminOrgByReference(this.vcloudClient, adminOrgRef);*/

            // Vdc
            ReferenceType vdcRef = this.org.getVdcRefByName(this.vdcName);
            if (vdcRef == null) {
                throw new ConnectorException("No Vdc: " + this.vdcName);
            }
            this.vdc = Vdc.getVdcByReference(this.vcloudClient, vdcRef);
            // require sysAdmin role
            /*ReferenceType adminVdcRef = this.adminOrg.getAdminVdcRefByName(this.vdcName);
            this.adminVdc = AdminVdc.getAdminVdcByReference(this.vcloudClient, adminVdcRef);*/

            // PublicOrgVdcNetwork
            logger.info("Available OrgVdcNetworks: " + this.vdc.getAvailableNetworkRefsByName());
            ReferenceType orgVdcNetworkNameRef = this.vdc.getAvailableNetworkRefByName(this.cimiPublicOrgVdcNetworkName);
            if (orgVdcNetworkNameRef == null) {
                throw new ConnectorException("No OrgVdcNetwork: " + this.cimiPublicOrgVdcNetworkName);
            }
            this.cimiPublicOrgVdcNetwork = OrgVdcNetwork.getOrgVdcNetworkByReference(this.vcloudClient, orgVdcNetworkNameRef);
            /*VcdCloudProviderConnectorFactory.logger.info("publicOrgVdcNetwork=" + this.cimiPublicOrgVdcNetwork.getResource().getName());*/

            this.cimiPublicNetwork = new Network();
            // this.cimiPublicNetwork.setName(this.cimiPublicOrgVdcNetworkName);
            // this.cimiPublicNetwork.setName(this.getCimiPublicOrgVdcNetwork().getResource().getName());
            // this.cimiPublicNetwork.setProviderAssignedId(this.getCimiPublicOrgVdcNetwork().getResource().getHref());
            this.cimiPublicNetwork.setName(this.cimiPublicOrgVdcNetwork.getResource().getName());
            this.cimiPublicNetwork.setProviderAssignedId(this.cimiPublicOrgVdcNetwork.getResource().getHref());
            this.cimiPublicNetwork.setState(Network.State.STARTED);
            this.cimiPublicNetwork.setNetworkType(Network.Type.PUBLIC);

            // Bridged/NatRouted
            if (this.cimiPublicOrgVdcNetwork.getResource().getConfiguration().getFenceMode()
                .equals(FenceModeValuesType.BRIDGED.value())) {
                this.cimiPublicOrgVdcNetworkIsRouted = false;
            } else if (this.cimiPublicOrgVdcNetwork.getResource().getConfiguration().getFenceMode()
                .equals(FenceModeValuesType.NATROUTED.value())) {
                this.cimiPublicOrgVdcNetworkIsRouted = true;
                if (properties.get("edgeGatewayName") == null) {
                    throw new ConnectorException("No access to properties: edgeGatewayName");
                }
                /* NB: Network services associated to the cimiPublicOrgVdcNetwork are not visible (is this a bug of the SDK ?).
                 * Therefore, we get them from the edge Gateway. */
                this.edgeGatewayName = properties.get("edgeGatewayName");
                QueryParams<QueryReferenceField> params = new QueryParams<QueryReferenceField>();
                Filter filter = new Filter(
                    new Expression(QueryReferenceField.NAME, this.edgeGatewayName, ExpressionType.EQUALS));
                params.setFilter(filter);
                ReferenceResult result = this.vcloudClient.getQueryService().queryReferences(QueryReferenceType.EDGEGATEWAY,
                    params);
                if (result.getReferences().size() == 0) {
                    throw new ConnectorException("No edgeGateway : " + this.edgeGatewayName);
                }
                this.edgeGateway = EdgeGateway.getEdgeGatewayByReference(this.vcloudClient, result.getReferences().get(0));
                /*VcdCloudProviderConnectorFactory.logger
                    .info("edgeGateway name=" + this.edgeGateway.getResource().getName());*/
            } else {
                throw new ConnectorException(this.cimiPublicOrgVdcNetworkName + "OrgVdcNetwork type="
                    + this.cimiPublicOrgVdcNetwork.getResource().getConfiguration().getFenceMode()
                    + " : should be Direct or Routed");
            }
            logger.info("CIMI public OrgVdcNetwork=" + this.cimiPublicOrgVdcNetwork.getResource().getName() + ", isRouted="
                + this.cimiPublicOrgVdcNetworkIsRouted);

        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    public VcloudClient getVcloudClient() {
        return this.vcloudClient;
    }

    /*public void setVcloudClient(final VcloudClient vcloudClient) {
        this.vcloudClient = vcloudClient;
    }*/

    public String getOrgName() {
        return this.orgName;
    }

    /*public void setOrgName(final String orgName) {
        this.orgName = orgName;
    }*/

    public Organization getOrg() {
        return this.org;
    }

    /*public void setOrg(final Organization org) {
        this.org = org;
    }*/

    public String getVdcName() {
        return this.vdcName;
    }

    /*public void setVdcName(final String vdcName) {
        this.vdcName = vdcName;
    }*/

    public Vdc getVdc() {
        return this.vdc;
    }

    /*public void setVdc(final Vdc vdc) {
        this.vdc = vdc;
    }*/

    public String getCimiPublicOrgVdcNetworkName() {
        return this.cimiPublicOrgVdcNetworkName;
    }

    /*public void setCimiPublicOrgVdcNetworkName(final String cimiPublicOrgVdcNetworkName) {
        this.cimiPublicOrgVdcNetworkName = cimiPublicOrgVdcNetworkName;
    }*/

    /*public OrgVdcNetwork getCimiPublicOrgVdcNetwork() {
        return this.cimiPublicOrgVdcNetwork;
    }*/

    /*public void setCimiPublicOrgVdcNetwork(final OrgVdcNetwork cimiPublicOrgVdcNetwork) {
        this.cimiPublicOrgVdcNetwork = cimiPublicOrgVdcNetwork;
    }*/

    public boolean isCimiPublicOrgVdcNetworkIsRouted() {
        return this.cimiPublicOrgVdcNetworkIsRouted;
    }

    /*public void setCimiPublicOrgVdcNetworkIsRouted(final boolean cimiPublicOrgVdcNetworkIsRouted) {
        this.cimiPublicOrgVdcNetworkIsRouted = cimiPublicOrgVdcNetworkIsRouted;
    }*/

    public Network getCimiPublicNetwork() {
        return this.cimiPublicNetwork;
    }

    public String getEdgeGatewayName() {
        return this.edgeGatewayName;
    }

    /*public void setEdgeGatewayName(final String edgeGatewayName) {
        this.edgeGatewayName = edgeGatewayName;
    }*/

    public EdgeGateway getEdgeGateway() {
        return this.edgeGateway;
    }

    /*public void setEdgeGateway(final EdgeGateway edgeGateway) {
        this.edgeGateway = edgeGateway;
    }*/
}
