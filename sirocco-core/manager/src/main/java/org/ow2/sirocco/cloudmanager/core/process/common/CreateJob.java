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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local
@EJB(name = "java:global/delegate/CreateJob", mappedName = "CreateJob", beanInterface = JavaDelegate.class)
public class CreateJob implements JavaDelegate {
    private static Logger logg = LoggerFactory.getLogger(CreateJob.class.getName());

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        try {
            CreateJob.logg.info(OrchestratorUtils.infoLog("entering CreateJob", execution));

        } catch (BpmnError e) {
            throw e;
        } catch (Exception e) {
            throw new BpmnError(execution.getProcessBusinessKey() + "-ConnectorPollingException");
        }
    }
}
