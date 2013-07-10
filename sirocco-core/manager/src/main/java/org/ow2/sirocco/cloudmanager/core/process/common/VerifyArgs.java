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

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntityCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local
@EJB(name = "java:global/delegate/VerifyArgs", mappedName = "VerifyArgs", beanInterface = JavaDelegate.class)
public class VerifyArgs implements JavaDelegate {
    private static Logger logger = LoggerFactory.getLogger(VerifyArgs.class.getName());

    public void execute(final DelegateExecution execution) throws Exception {
        // verifying init args of the process
        try {
            VerifyArgs.logger.info(OrchestratorUtils.infoLog("entering VerifyArgs", execution));

            if ((execution.getVariable(OrchestratorUtils.INPUT_JOBID) == null)
                || (execution.getVariable(OrchestratorUtils.INPUT_OBJECTID) == null)
                || (execution.getVariable(OrchestratorUtils.INPUT_USERID) == null)
                || (execution.getVariable(OrchestratorUtils.INPUT_PROCESSKEY) == null)) {
                throw new BpmnError(OrchestratorUtils.errorLog("bad input", execution));
            }

            if (((String) execution.getVariable(OrchestratorUtils.INPUT_PROCESSKEY)).startsWith("Create")) {
                CloudEntityCreate input_objectCreate = (CloudEntityCreate) execution
                    .getVariable(OrchestratorUtils.INPUT_OBJECTCREATE);
                // placement is given?
                Boolean placementGiven = false;
                if (input_objectCreate.getProperties() != null) {
                    if (input_objectCreate.getProperties().get("provider") != null) {
                        if (input_objectCreate.getProperties().get("location") != null) {
                            placementGiven = true;
                        }
                    }
                }
                execution.setVariable(OrchestratorUtils.PLACEMENT_GIVEN, placementGiven);

                if (!placementGiven) {
                    // no placement given,not supported for now
                    // throw new
                    // BpmnError("NO PLACEMENT GIVEN - NOT SUPPORTED NOW");
                }
            }

            // job given?
            if (((String) execution.getVariable(OrchestratorUtils.INPUT_JOBID)).isEmpty()) {
                // no job given, we must create one
                execution.setVariable(OrchestratorUtils.JOB_GIVEN, false);
            } else {
                execution.setVariable(OrchestratorUtils.JOB_GIVEN, true);
            }

        } catch (BpmnError e) {
            throw e;
        } catch (Exception e) {
            throw new BpmnError(OrchestratorUtils.errorLog(e.getMessage(), execution));
        }

    }

}
