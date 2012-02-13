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
import java.util.Properties;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.ow2.sirocco.cloudmanager.provider.api.entity.SystemInstance;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidSystemInstanceStatusException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ResourceQuotaExceededException;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteSystemInstanceManager;
import org.ow2.sirocco.cloudmanager.service.api.ISystemInstanceManager;

@Stateless(name = ISystemInstanceManager.EJB_JNDI_NAME, mappedName = ISystemInstanceManager.EJB_JNDI_NAME)
@Remote(IRemoteSystemInstanceManager.class)
@Local(ISystemInstanceManager.class)
public class SystemInstanceManagerBean implements ISystemInstanceManager, IRemoteSystemInstanceManager {

    private static Logger logger = Logger.getLogger(SystemInstanceManagerBean.class.getName());

    @Override
    public List<SystemInstance> listSystemInstances() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SystemInstance createSystemInstance(final String userName, final String projectId, final String systemTemplateName,
        final String cloudProviderAccountId, final Properties properties) throws ResourceQuotaExceededException,
        PermissionDeniedException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void startSystemInstance(final String userName, final String systemInstanceId) throws InvalidArgumentException,
        InvalidSystemInstanceStatusException, PermissionDeniedException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopSystemInstance(final String userName, final String systemInstanceId) throws InvalidArgumentException,
        InvalidSystemInstanceStatusException, PermissionDeniedException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteSystemInstance(final String userName, final String systemInstanceId) throws InvalidUsernameException,
        InvalidArgumentException, PermissionDeniedException, InvalidSystemInstanceStatusException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void purgeDeletedSystemInstances() {
        // TODO Auto-generated method stub

    }

}