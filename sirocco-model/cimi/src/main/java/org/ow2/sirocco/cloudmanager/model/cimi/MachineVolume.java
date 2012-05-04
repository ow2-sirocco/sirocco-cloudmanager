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

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;

@Entity
public class MachineVolume implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer 			id;
	private String			initialLocation;
	private Volume			volume;

	@ManyToOne
	private MachineVolumeCollection 	machineVolumeCollection;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return this.id;
	}


	public void setId(Integer id) {
		this.id = id;
	}

	// unidirectional
	@OneToOne
	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public String getInitialLocation() {
		return initialLocation;
	}

	public void setInitialLocation(String initialLocation) {
		this.initialLocation = initialLocation;
	}


	@ManyToOne
	public MachineVolumeCollection getMachineVolumeCollection() {
		return this.machineVolumeCollection;
	}

	public void setMachineVolumeCollection(final MachineVolumeCollection machineVolumeColl) {
		this.machineVolumeCollection = machineVolumeColl;
	}
}
