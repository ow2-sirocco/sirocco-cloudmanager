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

package org.ow2.sirocco.cloudmanager.core.process.machine;

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
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.core.process.common.OrchestratorUtils;
import org.ow2.sirocco.cloudmanager.model.cimi.ICloudProviderResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local
@EJB(name = "java:global/delegate/MachineDeleteConnectorCall", mappedName = "MachineDeleteConnectorCall", beanInterface = JavaDelegate.class)
public class MachineDeleteConnectorCall implements JavaDelegate {

    private static Logger logg = LoggerFactory.getLogger(MachineDeleteConnectorCall.class.getCanonicalName());

    @PersistenceContext
    private EntityManager em;

    @Inject
    @OSGiService(dynamic = true)
    private ICloudProviderConnectorFinder connectorFinder;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        try {
            MachineDeleteConnectorCall.logg.info(OrchestratorUtils.infoLog("entering ConnectorCall", execution));

            String input_objectId = (String) execution.getVariable(OrchestratorUtils.INPUT_OBJECTID);
            ICloudProviderResource object = this.em.find(Machine.class, Integer.valueOf(input_objectId));

            ProviderTarget target = new ProviderTarget().account(object.getCloudProviderAccount()).location(
                object.getLocation());

            IComputeService computeService = OrchestratorUtils.getCloudProviderConnector(this.connectorFinder,
                target.getAccount()).getComputeService();

            // calling underlying cloud platform
            String inputInitialState = computeService.getMachineState(object.getProviderAssignedId(), target).name();
            computeService.deleteMachine(object.getProviderAssignedId(), target);

            // setting var for polling
            execution.setVariable(OrchestratorUtils.INPUT_OBJECT_TYPE, "org.ow2.sirocco.cloudmanager.model.cimi.Machine");
            execution.setVariable(OrchestratorUtils.INPUT_INITIAL_STATE, inputInitialState);

        } catch (Exception e) {
            throw new BpmnError(OrchestratorUtils.errorLog(e.getMessage(), execution));
        }

    }

}
