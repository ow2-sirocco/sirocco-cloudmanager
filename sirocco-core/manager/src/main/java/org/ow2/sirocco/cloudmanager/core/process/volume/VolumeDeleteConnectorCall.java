/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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

package org.ow2.sirocco.cloudmanager.core.process.volume;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.glassfish.osgicdi.OSGiService;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.process.common.OrchestratorUtils;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local
@EJB(name = "java:global/delegate/VolumeDeleteConnectorCall", mappedName = "VolumeDeleteConnectorCall", beanInterface = JavaDelegate.class)
public class VolumeDeleteConnectorCall implements JavaDelegate {

    private static Logger logger = LoggerFactory.getLogger(VolumeDeleteConnectorCall.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Inject
    @OSGiService(dynamic = true)
    private ICloudProviderConnectorFinder connectorFinder;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        try {
            VolumeDeleteConnectorCall.logger.info(OrchestratorUtils.infoLog("entering ConnectorCall", execution));

            String input_objectId = (String) execution.getVariable(OrchestratorUtils.INPUT_OBJECTID);
            Volume volume = this.em.find(Volume.class, Integer.valueOf(input_objectId));

            ProviderTarget target = new ProviderTarget().account(volume.getCloudProviderAccount()).location(
                volume.getLocation());

            IVolumeService volumeService = this.getCloudProviderConnector(target.getAccount(), target.getLocation())
                .getVolumeService();

            // calling underlying cloud platform
            volumeService.deleteVolume(volume.getProviderAssignedId(), target);

            // setting var for polling
            execution.setVariable(OrchestratorUtils.INPUT_OBJECT_TYPE, "org.ow2.sirocco.cloudmanager.model.cimi.Volume");
            execution.setVariable(OrchestratorUtils.INPUT_INITIAL_STATE, "");

        } catch (Exception e) {
            throw new BpmnError(OrchestratorUtils.errorLog(e.getMessage(), execution));
        }
    }

    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount,
        final CloudProviderLocation location) throws CloudProviderException {
        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(cloudProviderAccount
            .getCloudProvider().getCloudProviderType());
        if (connector == null) {
            VolumeDeleteConnectorCall.logger.error("Cannot find connector for cloud provider type "
                + cloudProviderAccount.getCloudProvider().getCloudProviderType());
            return null;
        }
        return connector;
    }

}
