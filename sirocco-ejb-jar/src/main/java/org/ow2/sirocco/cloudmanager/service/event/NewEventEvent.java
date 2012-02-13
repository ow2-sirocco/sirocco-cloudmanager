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

import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.EventVO;

/**
 * Cloud administration event that indicates that a new event has occurred
 */
public class NewEventEvent extends CloudAdminEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private EventVO event;

    /**
     * Constructs a NewEventEvent object
     */
    public NewEventEvent() {
        super();
    }

    /**
     * Constructs a NewEventEvent object with a specified event
     * 
     * @param event event to push
     */
    public NewEventEvent(final EventVO event) {
        this.event = event;
    }

    /**
     * Returns the new event
     * 
     * @return the event that has been created
     */
    public EventVO getEvent() {
        return this.event;
    }

    /**
     * Sets the event that has been created
     * 
     * @param event that has been created
     */
    public void setEvent(final EventVO event) {
        this.event = event;
    }

}
