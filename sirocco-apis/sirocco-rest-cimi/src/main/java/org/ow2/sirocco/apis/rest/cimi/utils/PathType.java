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
 * Enumeration of all the path name used.
 */
public enum PathType {
    /** */
    CloudEntryPoint(ConstantsPath.CLOUDENTRYPOINT),
    /** */
    Credentials(ConstantsPath.CREDENTIALS),
    /** */
    CredentialsTemplate(ConstantsPath.CREDENTIALS_TEMPLATE),
    /** */
    Job(ConstantsPath.JOB),
    /** */
    Machine(ConstantsPath.MACHINE),
    /** */
    MachineConfiguration(ConstantsPath.MACHINE_CONFIGURATION),
    /** */
    MachineImage(ConstantsPath.MACHINE_IMAGE),
    /** */
    MachineTemplate(ConstantsPath.MACHINE_TEMPLATE);

    /** The pathname. */
    String pathname;

    /** Constructor. */
    private PathType(final String pathname) {
        this.pathname = pathname;
    }

    /**
     * Get the pathname.
     * 
     * @return The pathname
     */
    public String getPathname() {
        return this.pathname;
    }

    /**
     * Get the complete path.
     * 
     * @return The complete path
     */
    public String getCompletePath() {
        return "/" + this.pathname;
    }

    /**
     * Find the path type with a given pathname.
     * 
     * @param pathname The pathname
     * @return The path type or null if not found
     */
    public static PathType valueOfPathname(final String pathname) {
        PathType type = null;
        for (PathType value : PathType.values()) {
            if (pathname.equals(value.getPathname())) {
                type = value;
                break;
            }
        }
        return type;
    }

}
