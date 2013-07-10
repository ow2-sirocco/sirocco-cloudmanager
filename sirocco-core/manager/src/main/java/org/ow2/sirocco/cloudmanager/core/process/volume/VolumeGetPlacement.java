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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager.Placement;
import org.ow2.sirocco.cloudmanager.core.process.common.OrchestratorUtils;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntityCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ICloudProviderResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local
@EJB(name = "java:global/delegate/VolumeGetPlacement", mappedName = "VolumeGetPlacement", beanInterface = JavaDelegate.class)
public class VolumeGetPlacement implements JavaDelegate {
    Logger logg = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @PersistenceContext
    private EntityManager em;

    @EJB
    private ICloudProviderManager cloudProviderManager;

    public void execute(final DelegateExecution execution) throws Exception {
        try {

            this.logg.info(OrchestratorUtils.infoLog("entering GetPlacement", execution));

            CloudEntityCreate input_objectCreate = (CloudEntityCreate) execution
                .getVariable(OrchestratorUtils.INPUT_OBJECTCREATE);
            String input_objectId = (String) execution.getVariable(OrchestratorUtils.INPUT_OBJECTID);
            String tenantId = (String) execution.getVariable(OrchestratorUtils.INPUT_USERID);

            ICloudProviderResource resource = this.em.find(Volume.class, Integer.valueOf(input_objectId));
            // calling getPlacement

            Placement placementNS = this.cloudProviderManager.placeResource(tenantId, input_objectCreate.getProperties());

            // storing placement in object
            resource.setCloudProviderAccount(placementNS.getAccount());
            resource.setLocation(placementNS.getLocation());

        } catch (Exception e) {
            throw new BpmnError(OrchestratorUtils.errorLog(e.getMessage(), execution));
        }
    }

}
