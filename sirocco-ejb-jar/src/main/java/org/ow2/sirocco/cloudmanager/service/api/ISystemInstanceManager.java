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

package org.ow2.sirocco.cloudmanager.service.api;

import java.util.List;
import java.util.Properties;

import org.ow2.sirocco.cloudmanager.provider.api.entity.SystemInstance;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidSystemInstanceStatusException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ResourceQuotaExceededException;

/**
 * This interface provides public methods for managing System Instances (i.e.,
 * instances of SystemTemplates).
 */
public interface ISystemInstanceManager {
    static final String EJB_JNDI_NAME = "SystemInstanceManagerBean";

    List<SystemInstance> listSystemInstances();

    /**
     * Creates a SystemInstance starting from a SystemTemplate (specified via
     * its systemTemplateId). This method will create a SystemInstance object,
     * and it will, at least, create the related VirtualMachines via the use of
     * the CloudProvider's API (XXX how can we choose the cloudProvider ? Is it
     * defined in the systemTemplate ? Is it given as an input parameter ?)
     * 
     * @param userName
     * @param projectId
     * @param systemTemplateName
     * @param cloudProviderAccountId
     * @param properties
     * @return
     * @throws ResourceQuotaExceededException
     * @throws PermissionDeniedException
     * @throws CloudProviderException
     */
    SystemInstance createSystemInstance(final String userName, final String projectId, final String systemTemplateName,
        final String cloudProviderAccountId, final Properties properties) throws ResourceQuotaExceededException,
        PermissionDeniedException, CloudProviderException;

    /**
     * Starts, in the specfied order, all the VirtualMachines belonging to the
     * SystemInstance specified by the given systemInstanceId.
     * 
     * @param userName
     * @param systemInstanceId
     * @throws InvalidArgumentException
     * @throws InvalidSystemInstanceStatusException
     * @throws PermissionDeniedException
     * @throws CloudProviderException
     */
    void startSystemInstance(final String userName, final String systemInstanceId) throws InvalidArgumentException,
        InvalidSystemInstanceStatusException, PermissionDeniedException, CloudProviderException;

    /**
     * Stops, in the specified order, all the VirtualMachines belonging to the
     * SystemInstance specified by the given systemInstanceId.
     * 
     * @param userName
     * @param systemInstanceId
     * @throws InvalidArgumentException
     * @throws InvalidSystemInstanceStatusException
     * @throws PermissionDeniedException
     * @throws CloudProviderException
     */
    void stopSystemInstance(final String userName, final String systemInstanceId) throws InvalidArgumentException,
        InvalidSystemInstanceStatusException, PermissionDeniedException, CloudProviderException;

    /**
     * Destroys the SystemInstance specified by the given systemInstanceId. This
     * method destroys the related VirtualMachine(s), and the SystemInstance
     * object itself (i.e. move object(s) to DELETED state).
     * 
     * @param userName
     * @param systemInstanceId
     * @throws InvalidUsernameException
     * @throws InvalidArgumentException
     * @throws PermissionDeniedException
     * @throws InvalidSystemInstanceStatusException
     * @throws CloudProviderException
     */
    void deleteSystemInstance(final String userName, final String systemInstanceId) throws InvalidUsernameException,
        InvalidArgumentException, PermissionDeniedException, InvalidSystemInstanceStatusException, CloudProviderException;

    /**
     * Remove SystemInstances in the Destroyed state from the DB (remove also
     * SystemInstances' destroyed NetworkInstances, VirtualMachines, Volumes).
     */
    void purgeDeletedSystemInstances();

}
