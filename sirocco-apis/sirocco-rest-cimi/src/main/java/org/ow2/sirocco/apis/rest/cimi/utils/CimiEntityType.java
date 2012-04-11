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
package org.ow2.sirocco.apis.rest.cimi.utils;


/**
 * Enumeration of all the entities used.
 */
public enum CimiEntityType {
    /** */
    CloudEntryPoint(PathType.CloudEntryPoint),
    /** */
    Credentials(PathType.Credentials), CredentialsCollection(PathType.Credentials),
    /** */
    CredentialsTemplate(PathType.CredentialsTemplate), CredentialsTemplateCollection(PathType.CredentialsTemplate),
    /** */
    Job(PathType.Job), JobCollection(PathType.Job),
    /** */
    Machine(PathType.Machine), MachineCollection(PathType.Machine),
    /** */
    MachineConfiguration(PathType.MachineConfiguration), MachineConfigurationCollection(PathType.MachineConfiguration),
    /** */
    MachineImage(PathType.MachineImage), MachineImageCollection(PathType.MachineImage),
    /** */
    MachineTemplate(PathType.MachineTemplate), MachineTemplateCollection(PathType.MachineTemplate);

    /** The path type of the entity. */
    PathType pathType;

    /** Constructor. */
    private CimiEntityType(final PathType pathType) {
        this.pathType = pathType;
    }

    /**
     * Get the path type of the entity.
     * 
     * @return The path type
     */
    public PathType getPathType() {
        return this.pathType;
    }

}
