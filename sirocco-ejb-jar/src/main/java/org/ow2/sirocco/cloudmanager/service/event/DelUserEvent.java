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

package org.ow2.sirocco.cloudmanager.service.event;

import java.io.Serializable;

/**
 * Cloud administration event that indicates that a VM image has been destroyed.
 */
public class DelUserEvent extends CloudAdminEvent implements Serializable {
    private static final long serialVersionUID = -3435852688587633703L;

    private String username;

    /**
     * Constructs a new DelUserEvent object
     */
    public DelUserEvent() {
        super();
    }

    /**
     * Constructs a new DelUserEvent object with a user name
     * 
     * @param username VM image id of the image that has been destroyed
     */
    public DelUserEvent(final String username) {
        super();
        this.username = username;
    }

    /**
     * Returns the name of the user who has been deleted
     * 
     * @return name of the user who has been deleted
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the VM image id of the image that has been destroyed
     * 
     * @param username user name
     */
    public void setUsername(final String username) {
        this.username = username;
    }

}
