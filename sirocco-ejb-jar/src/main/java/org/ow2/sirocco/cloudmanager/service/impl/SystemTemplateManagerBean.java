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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.service.impl;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.ow2.sirocco.cloudmanager.provider.api.entity.SystemTemplate;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidSystemInstanceStatusException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidSystemTemplateStatusException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteSystemTemplateManager;
import org.ow2.sirocco.cloudmanager.service.api.ISystemTemplateManager;

@Stateless(name = ISystemTemplateManager.EJB_JNDI_NAME, mappedName = ISystemTemplateManager.EJB_JNDI_NAME)
@Remote(IRemoteSystemTemplateManager.class)
@Local(ISystemTemplateManager.class)
public class SystemTemplateManagerBean implements IRemoteSystemTemplateManager, ISystemTemplateManager {

    @Override
    public List<SystemTemplate> listSystemTemplates() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteSystemTemplate(final String userName, final String systemTemplateId) throws InvalidUsernameException,
        InvalidArgumentException, PermissionDeniedException, InvalidSystemInstanceStatusException,
        InvalidSystemTemplateStatusException {
        // TODO Auto-generated method stub

    }

    @Override
    public void purgeDeletedSystemTemplates() throws InvalidSystemInstanceStatusException {
        // TODO Auto-generated method stub

    }

}