/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;


@Entity
public class MachineVolume extends CloudResource implements Serializable, Identifiable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String initialLocation;

    private Machine owner;

    private Volume volume;

    public static enum State {
        PENDING, ATTACHING, ATTACHED, DETACHING, DELETED, ERROR
    }

    private State state;

    @ManyToOne
    public Machine getOwner() {
        return this.owner;
    }

    public void setOwner(final Machine owner) {
        this.owner = owner;
    }

    // unidirectional
    // oneToOne until shareable volume support is added
    @OneToOne
    public Volume getVolume() {
        return this.volume;
    }

    public void setVolume(final Volume volume) {
        this.volume = volume;
    }

    public String getInitialLocation() {
        return this.initialLocation;
    }

    public void setInitialLocation(final String initialLocation) {
        this.initialLocation = initialLocation;
    }

    @Enumerated(EnumType.STRING)
    public MachineVolume.State getState() {
        return this.state;
    }

    public void setState(final MachineVolume.State state) {
        this.state = state;
    }
}
