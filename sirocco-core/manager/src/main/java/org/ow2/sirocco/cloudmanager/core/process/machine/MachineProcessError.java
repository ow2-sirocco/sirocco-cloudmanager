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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.ow2.sirocco.cloudmanager.core.process.common.OrchestratorUtils;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local
@EJB(name = "java:global/delegate/MachineProcessError", mappedName = "MachineProcessError", beanInterface = JavaDelegate.class)
public class MachineProcessError implements JavaDelegate {
    private static Logger logg = LoggerFactory.getLogger(MachineProcessError.class.getCanonicalName());

    @PersistenceContext
    private EntityManager em;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        try {
            MachineProcessError.logg.info(OrchestratorUtils.infoLog("entering ProcessError", execution));
            String input_objectId = (String) execution.getVariable(OrchestratorUtils.INPUT_OBJECTID);
            Machine machine = this.em.find(Machine.class, Integer.valueOf(input_objectId));
            machine.setState(Machine.State.ERROR);

            String input_jobID = (String) execution.getVariable(OrchestratorUtils.INPUT_JOBID);
            Job input_job = this.em.find(Job.class, Integer.valueOf(input_jobID));
            input_job.setState(Job.Status.FAILED);

            // dealing with process exceptions
            throw new BpmnError("error");

        } catch (BpmnError e) {
            throw e;
        } catch (Exception e) {
            throw new BpmnError(OrchestratorUtils.errorLog(e.getMessage(), execution));
        }
    }
}
