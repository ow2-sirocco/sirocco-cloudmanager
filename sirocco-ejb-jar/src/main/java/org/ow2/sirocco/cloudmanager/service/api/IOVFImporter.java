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

import org.ow2.sirocco.cloudmanager.provider.api.entity.SystemTemplate;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.DuplicateNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.OVFImporterException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;

/**
 * This interface provides public methods for importing an OVF appliance from a
 * file to a local (temporary) file, parsing it, and creating the corresponding
 * SystemTemplate. Note that OVF Archives (i.e., OVA) will be imported (in the
 * local server) by the VirtualMachineImage Manager module.
 */
public interface IOVFImporter {
    static final String EJB_JNDI_NAME = "OVFImporterBean";

    SystemTemplate createSystemTemplateFromOVF(String projectId, final String userName, final String cloudProviderId,
        String ovfURL) throws InvalidUsernameException, InvalidProjectIdException, InvalidArgumentException,
        PermissionDeniedException, DuplicateNameException, OVFImporterException, CloudProviderException;

}
