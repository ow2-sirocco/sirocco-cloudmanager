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

package org.ow2.sirocco.cloudmanager.provider.api.exception;

public class ProjectInUseException extends CloudProviderException {
    private static final long serialVersionUID = -828494226924709790L;

    /**
     * Constructs a ProjectInUseException with null as its error detail message.
     */
    public ProjectInUseException() {
    }

    /**
     * Constructs a ProjectInUseException with a detail message
     * 
     * @param message the detail message
     */
    public ProjectInUseException(final String message) {
        super(message);
    }
}
