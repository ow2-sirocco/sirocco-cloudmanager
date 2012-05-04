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
 *  
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import javax.persistence.Entity;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Entity
public class MachineTemplateCollection  extends CloudEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public MachineTemplateCollection() {}

	@Transient
	List<String> operations = new ArrayList<String>() {{
		add("add");
	}};

	@Transient
	List<MachineTemplate> machineTemplates;

	@Transient
	public List<MachineTemplate> getMachineTemplates(){
		return this.machineTemplates;
	}
	@Transient
	public void setMachineTemplates(List<MachineTemplate> machineTemplates){
		this.machineTemplates = machineTemplates;
	}

	@Transient
	public List<String> getOperations() {
		return operations;
	}
}

