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

package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.HashMap;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.glassfish.osgicdi.OSGiService;
import org.ow2.sirocco.cloudmanager.core.api.IWorkflowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class WorkflowManager
 */
@Singleton
@Startup
public class WorkflowManager implements IWorkflowManager {
    Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Inject
    @OSGiService(dynamic = true)
    private ProcessEngine processEngine;

    public void startProcess(final String processKey, final HashMap<String, Object> vars) {
        this.logger.info("startProcess - processKey:" + processKey);
        RuntimeService runtimeService = this.processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, vars);
        this.logger.info("startProcess started process " + processKey + " with id " + processInstance.getId());
    }

}