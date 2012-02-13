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

import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.ow2.sirocco.cloudmanager.provider.api.entity.SystemTemplate;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.DuplicateNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.OVFImporterException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.service.api.IOVFImporter;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteOVFImporter;

@Stateless(name = IOVFImporter.EJB_JNDI_NAME, mappedName = IOVFImporter.EJB_JNDI_NAME)
@Remote(IRemoteOVFImporter.class)
@Local(IOVFImporter.class)
public class OVFImporterBean implements IOVFImporter {

    private static Logger logger = Logger.getLogger(OVFImporterBean.class.getName());

    @Override
    public SystemTemplate createSystemTemplateFromOVF(final String projectId, final String userName,
        final String cloudProviderId, final String ovfURL) throws InvalidUsernameException, InvalidProjectIdException,
        InvalidArgumentException, PermissionDeniedException, DuplicateNameException, OVFImporterException,
        CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

}
