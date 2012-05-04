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
 *  $Id $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.OneToOne;

@Entity
public class MachineVolumeCollection implements Serializable{
	private static final long serialVersionUID = 1L;

	protected Integer 	id;

	@Transient
	protected List<String> operations = new ArrayList<String>() {{
		add("add");
	}};

	// To change
	@OneToOne(mappedBy = "volumes")
	protected Machine	machine;

	@OneToOne(mappedBy = "volumes")
	protected MachineTemplate	machineTemplate;

	private List<MachineVolume> items;

	public MachineVolumeCollection() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
        this.id = id;
    }

	@OneToMany(mappedBy = "machineVolumeCollection")
	//@OneToMany
	public List<MachineVolume> getItems() {
		return this.items;
	}

	public void setItems(List<MachineVolume> items) {
		this.items = items;
	}

	@OneToOne(mappedBy = "volumes")
	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	@OneToOne(mappedBy = "volumes")
	public MachineTemplate getMachineTemplate() {
		return machineTemplate;
	}

	public void setMachineTemplate(MachineTemplate machineTemplate) {
		this.machineTemplate = machineTemplate;
	}

	@Transient
	public List<String> getOperations() {
		return operations;
	}
}
