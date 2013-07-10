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

package org.ow2.sirocco.cloudmanager.core.process.polling;

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
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.connector.api.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.process.common.OrchestratorUtils;
import org.ow2.sirocco.cloudmanager.model.cimi.ICloudProviderResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local
@EJB(name = "java:global/delegate/ConnectorPolling", mappedName = "ConnectorPolling", beanInterface = JavaDelegate.class)
public class ConnectorPolling implements JavaDelegate {
    private static Logger logger = LoggerFactory.getLogger(ConnectorPolling.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Inject
    @OSGiService(dynamic = true)
    private ICloudProviderConnectorFinder connectorFinder;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        try {

            ConnectorPolling.logger.info(OrchestratorUtils.infoLog("entering ConnectorPolling", execution));

            String input_objectId = (String) execution.getVariable(OrchestratorUtils.INPUT_OBJECTID);
            String input_objectType = (String) execution.getVariable(OrchestratorUtils.INPUT_OBJECT_TYPE);
            String input_initialstate = (String) execution.getVariable(OrchestratorUtils.INPUT_INITIAL_STATE);

            ICloudProviderResource object = (ICloudProviderResource) this.em.find(
                Class.forName((String) execution.getVariable(OrchestratorUtils.INPUT_OBJECT_TYPE)),
                Integer.valueOf(input_objectId));

            execution.setVariable(OrchestratorUtils.LOOP, true);

            ProviderTarget target = new ProviderTarget().account(object.getCloudProviderAccount()).location(
                object.getLocation());

            // waiting for underlying cloud platform end status

            if (Class.forName(input_objectType).equals(Volume.class)) {
                // Volume
                IVolumeService volumeService = OrchestratorUtils.getCloudProviderConnector(this.connectorFinder,
                    target.getAccount()).getVolumeService();

                Volume.State objState = null;
                try {
                    objState = volumeService.getVolumeState(object.getProviderAssignedId(), target);
                } catch (ResourceNotFoundException e) {
                    ConnectorPolling.logger.info("Volume with provider id " + object.getProviderAssignedId() + " not found");
                    objState = Volume.State.DELETED;
                }
                ConnectorPolling.logger.info("Volume " + object.getProviderAssignedId() + " state=" + objState);
                // ok: final state
                if (objState == Volume.State.AVAILABLE || objState == Volume.State.DELETED) {
                    execution.setVariable(OrchestratorUtils.LOOP, false);
                    execution.setVariable(OrchestratorUtils.OUTPUT_OBJECT_STATE, objState);
                }
                if (objState == Volume.State.ERROR) {
                    throw new BpmnError("CONNECTOR ERROR when polling resource" + input_objectId);
                }
            } else if (Class.forName(input_objectType).equals(Machine.class)) {
                // Machine
                IComputeService computeService = OrchestratorUtils.getCloudProviderConnector(this.connectorFinder,
                    target.getAccount()).getComputeService();

                Machine.State objState = null;
                try {
                    objState = computeService.getMachineState(object.getProviderAssignedId(), target);
                } catch (ResourceNotFoundException e) {
                    objState = Machine.State.DELETED;
                }
                // ok: final state
                if (objState == Machine.State.PAUSED || objState == Machine.State.DELETED || objState == Machine.State.STARTED
                    || objState == Machine.State.STOPPED || objState == Machine.State.SUSPENDED) {
                    if (input_initialstate.equals("") || !(objState.name().equals(input_initialstate))) {
                        // state ok, leaving the loop
                        execution.setVariable(OrchestratorUtils.LOOP, false);
                        execution.setVariable(OrchestratorUtils.OUTPUT_OBJECT_STATE, objState);
                    }
                }
                if (objState == Machine.State.ERROR) {
                    throw new BpmnError("CONNECTOR ERROR when polling resource" + input_objectId);
                }

            } else if (Class.forName(input_objectType).equals(MachineVolume.class)) {
                // MachineVolume attach/detach
                IComputeService computeService = OrchestratorUtils.getCloudProviderConnector(this.connectorFinder,
                    target.getAccount()).getComputeService();

                MachineVolume objectMV = (MachineVolume) object;

                Machine owner = computeService.getMachine(objectMV.getOwner().getProviderAssignedId(), target);

                MachineVolume.State objState = null;
                for (MachineVolume mv : owner.getVolumes()) {
                    if (mv.getVolume().getProviderAssignedId().equals(objectMV.getVolume().getProviderAssignedId())) {
                        // the right mv!
                        objState = mv.getState();
                    }
                }
                // if objState==null the signification is that it is detached
                // successfully
                if (objState == null) {
                    objState = MachineVolume.State.DELETED;
                }

                if (objState.equals(MachineVolume.State.ATTACHED) || objState.equals(MachineVolume.State.DELETED)) {
                    // ok finished
                    execution.setVariable(OrchestratorUtils.LOOP, false);
                    execution.setVariable(OrchestratorUtils.OUTPUT_OBJECT_STATE, objState);
                }
                if (objState == MachineVolume.State.ERROR) {
                    throw new BpmnError("CONNECTOR ERROR when polling resource" + input_objectId);
                }

            } else {
                throw new BpmnError("CONNECTOR ERROR when polling resource" + input_objectId + ": unexpected class "
                    + input_objectType);
            }

        } catch (Exception e) {
            throw new BpmnError(execution.getProcessBusinessKey() + "-ConnectorPollingException " + e.getMessage());
        }
    }

}
