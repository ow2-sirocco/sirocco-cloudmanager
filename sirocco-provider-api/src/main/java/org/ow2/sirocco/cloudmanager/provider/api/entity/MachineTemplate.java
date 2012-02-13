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

package org.ow2.sirocco.cloudmanager.provider.api.entity;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class MachineTemplate extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private MachineConfiguration machineConfig;

    private MachineImage machineImage;

    private MachineAdmin machineAdmin;

    // TODO move this attribute within networkInterface
    private String hostname;

    public MachineConfiguration getMachineConfig() {
        return this.machineConfig;
    }

    public void setMachineConfig(final MachineConfiguration machineConfig) {
        this.machineConfig = machineConfig;
    }

    public MachineImage getMachineImage() {
        return this.machineImage;
    }

    public void setMachineImage(final MachineImage machineImage) {
        this.machineImage = machineImage;
    }

    public MachineAdmin getMachineAdmin() {
        return this.machineAdmin;
    }

    public void setMachineAdmin(final MachineAdmin machineAdmin) {
        this.machineAdmin = machineAdmin;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

}
