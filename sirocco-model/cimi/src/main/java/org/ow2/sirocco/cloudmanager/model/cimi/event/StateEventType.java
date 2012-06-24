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
 *  $Id: $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi.event;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class StateEventType extends EventType implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Should be one of the valid states of the corresponding CloudResource
    private String  state;
    private String  previousState;
    
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getPreviousState() {
        return previousState;
    }
    public void setPreviousState(String previousState) {
        this.previousState = previousState;
    }
    
    
}