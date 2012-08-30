/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.manager.system;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemAddress;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemForwardingGroup;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemNetwork;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemNetworkPort;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemSystem;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemVolume;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerCreateAbstract;
import org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Manage CREATE request of Entity of a System.
 */
@Component("CimiManagerCreateSystemEntity")
public class CimiManagerCreateSystemEntity extends CimiManagerCreateAbstract {

    @Autowired
    @Qualifier("MergeReferenceHelper")
    private MergeReferenceHelper mergeReference;

    @Autowired
    @Qualifier("ISystemManager")
    private ISystemManager manager;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#callService(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      java.lang.Object)
     */
    @Override
    protected Object callService(final CimiContext context, final Object dataService) throws Exception {
        return this.manager.addEntityToSystem(context.getRequest().getIdParent(), (CloudCollectionItem) dataService);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#beforeConvertToDataService(org.ow2.sirocco.apis.rest.cimi.request.CimiContext)
     */
    @Override
    protected void beforeConvertToDataService(final CimiContext context) throws Exception {
        CimiData toMerge = context.getRequest().getCimiData();
        if (toMerge instanceof CimiSystemAddress) {
            this.mergeReference.merge(context, (CimiSystemAddress) toMerge);
        } else if (toMerge instanceof CimiSystemCredential) {
            this.mergeReference.merge(context, (CimiSystemCredential) toMerge);
        } else if (toMerge instanceof CimiSystemForwardingGroup) {
            this.mergeReference.merge(context, (CimiSystemForwardingGroup) toMerge);
        } else if (toMerge instanceof CimiSystemMachine) {
            this.mergeReference.merge(context, (CimiSystemMachine) toMerge);
        } else if (toMerge instanceof CimiSystemNetwork) {
            this.mergeReference.merge(context, (CimiSystemNetwork) toMerge);
        } else if (toMerge instanceof CimiSystemNetworkPort) {
            this.mergeReference.merge(context, (CimiSystemNetworkPort) toMerge);
        } else if (toMerge instanceof CimiSystemSystem) {
            this.mergeReference.merge(context, (CimiSystemSystem) toMerge);
        } else if (toMerge instanceof CimiSystemVolume) {
            this.mergeReference.merge(context, (CimiSystemVolume) toMerge);
        }
    }
}
