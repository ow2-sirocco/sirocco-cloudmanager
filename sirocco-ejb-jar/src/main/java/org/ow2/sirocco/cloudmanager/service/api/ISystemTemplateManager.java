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

import org.ow2.sirocco.cloudmanager.provider.api.entity.SystemTemplate;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidSystemInstanceStatusException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidSystemTemplateStatusException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;

/**
 * This interface provides public methods for managing System Instances (i.e.,
 * instances of SystemTemplates).
 */
public interface ISystemTemplateManager {
    static final String EJB_JNDI_NAME = "SystemTemplateManagerBean";

    /**
     * @return non DELETED systemTemplates.
     */
    List<SystemTemplate> listSystemTemplates();

    /**
     * Destroys the SystemTemplate specified by the given systemTemplateId. This
     * method destroys the related/associated objects(s), and the SystemInstance
     * object itself (i.e. move object(s) to DELETED state).
     * 
     * @param userName
     * @param systemTemplateId
     * @throws InvalidUsernameException
     * @throws InvalidArgumentException
     * @throws PermissionDeniedException
     * @throws InvalidSystemInstanceStatusException
     * @throws InvalidSystemTemplateStatusException
     */
    void deleteSystemTemplate(final String userName, final String systemTemplateId) throws InvalidUsernameException,
        InvalidArgumentException, PermissionDeniedException, InvalidSystemInstanceStatusException,
        InvalidSystemTemplateStatusException;

    /**
     * Remove SystemTemplate(s) in the Destroyed state from the DB (remove also
     * SystemTemplates' associated destroyed object(s)/line(s)).
     * 
     * @throws InvalidSystemInstanceStatusException
     */
    void purgeDeletedSystemTemplates() throws InvalidSystemInstanceStatusException;

}
