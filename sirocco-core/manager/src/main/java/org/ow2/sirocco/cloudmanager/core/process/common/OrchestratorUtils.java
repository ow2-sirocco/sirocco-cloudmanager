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
package org.ow2.sirocco.cloudmanager.core.process.common;

import org.activiti.engine.delegate.DelegateExecution;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrchestratorUtils {
    private static Logger logger = LoggerFactory.getLogger(OrchestratorUtils.class.getName());

    // list of variables used in workflows
    static final public String INPUT_JOBID = "INPUT_JOBID";

    static final public String INPUT_OBJECTID = "INPUT_OBJECTID";

    static final public String INPUT_OBJECT_TYPE = "INPUT_OBJECT_TYPE";

    static final public String INPUT_USERID = "INPUT_USERID";

    static final public String INPUT_PROCESSKEY = "INPUT_PROCESSKEY";

    static final public String INPUT_OBJECTCREATE = "INPUT_OBJECTCREATE";

    static final public String PLACEMENT_GIVEN = "PLACEMENT_GIVEN";

    static final public String JOB_GIVEN = "JOB_GIVEN";

    static final public String LOOP = "LOOP";

    static final public String INPUT_INITIAL_STATE = "INPUT_INITIAL_STATE";

    static final public String OUTPUT_OBJECT_STATE = "OUTPUT_OBJECT_STATE";

    static final public String INPUT_ACTION = "INPUT_ACTION";

    static final public String INPUT_ACTION_FORCE = "INPUT_ACTION_FORCE";

    // list of processes
    static final public String CREATE_VOLUME_PROCESS = "CreateVolumeProcess";

    static final public String DELETE_VOLUME_PROCESS = "DeleteVolumeProcess";

    static final public String CREATE_MACHINE_PROCESS = "CreateMachineProcess";

    static final public String DELETE_MACHINE_PROCESS = "DeleteMachineProcess";

    static final public String CONTROL_MACHINE_PROCESS = "ControlMachineProcess";

    static final public String ADD_VOLUME_TO_MACHINE_PROCESS = "AddVolumeToMachineProcess";

    static final public String REMOVE_VOLUME_FROM_MACHINE_PROCESS = "RemoveVolumeFromMachineProcess";

    static final public String POLLING_PROCESS = "PollingProcess";

    public static String errorLog(final String message, final DelegateExecution execution) {
        ;
        return new StringBuffer()
            .append(
                " ORCHESTRATOR_ERROR - " + execution.getCurrentActivityName() + " (id: " + execution.getCurrentActivityId()
                    + ") - processInstanceId:" + execution.getProcessInstanceId() + " - ")
            .append(execution.getVariable(OrchestratorUtils.INPUT_PROCESSKEY)).append(" - ").append(message).toString();
    }

    public static String infoLog(final String message, final DelegateExecution execution) {
        ;
        return new StringBuffer()
            .append(
                " ORCHESTRATOR_INFO - activity:" + execution.getCurrentActivityName() + " (id: "
                    + execution.getCurrentActivityId() + ") - processInstanceId:" + execution.getProcessInstanceId() + " - ")
            .append(execution.getVariable(OrchestratorUtils.INPUT_PROCESSKEY)).append(" - ").append(message).toString();
    }

    public static ICloudProviderConnector getCloudProviderConnector(final ICloudProviderConnectorFinder connectorFinder,
        final CloudProviderAccount cloudProviderAccount) throws CloudProviderException {
        ICloudProviderConnector connector = connectorFinder.getCloudProviderConnector(cloudProviderAccount.getCloudProvider()
            .getCloudProviderType());
        if (connector == null) {
            OrchestratorUtils.logger.error("Cannot find connector for cloud provider type "
                + cloudProviderAccount.getCloudProvider().getCloudProviderType());
            return null;
        }
        return connector;
    }
}
