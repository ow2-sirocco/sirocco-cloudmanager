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

import java.util.Date;

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
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local
@EJB(name = "java:global/delegate/MachineUpdateBeans", mappedName = "MachineUpdateBeans", beanInterface = JavaDelegate.class)
public class MachineUpdateBeans implements JavaDelegate {
    private static Logger logg = LoggerFactory.getLogger(MachineUpdateBeans.class.getCanonicalName());

    @PersistenceContext
    private EntityManager em;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        try {
            MachineUpdateBeans.logg.info(OrchestratorUtils.infoLog("entering UpdateBeans ", execution));

            String input_jobID = (String) execution.getVariable(OrchestratorUtils.INPUT_JOBID);
            Job input_job = this.em.find(Job.class, Integer.valueOf(input_jobID));
            Machine machine = this.em.find(Machine.class,
                Integer.valueOf((String) execution.getVariable(OrchestratorUtils.INPUT_OBJECTID)));

            machine.setCreated(new Date());
            machine.setState((Machine.State) execution.getVariable(OrchestratorUtils.OUTPUT_OBJECT_STATE));

            // calling EJB to persist job
            input_job.setState(Status.SUCCESS);

        } catch (BpmnError e) {
            throw e;
        } catch (Exception e) {
            throw new BpmnError(OrchestratorUtils.errorLog(e.getMessage(), execution));
        }

    }
}
