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
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class MachineVolumeTemplateCollection implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Integer 	id;

	@Transient
	protected List<String> operations = new ArrayList<String>() {{
		add("add");
	}};

	@OneToOne(mappedBy = "volumeTemplates")
	protected MachineTemplate	machineTemplate;

	protected List<MachineVolumeTemplate> items;

	public MachineVolumeTemplateCollection() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
        this.id = id;
    }

	@OneToMany(mappedBy = "machineVolumeTemplateCollection", targetEntity=MachineVolumeTemplate.class)
	//@OneToMany
	public List<MachineVolumeTemplate> getItems() {
		return this.items;
	}

	public void setItems(List<MachineVolumeTemplate> items) {
		this.items = items;
	}

	@OneToOne(mappedBy = "volumeTemplates")
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
