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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ResourceQuota implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private int cpuQuota;

    private int ramQuotaInMB;

    private int diskQuotaInMB;

    private int vmQuota;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public int getCpuQuota() {
        return this.cpuQuota;
    }

    public void setCpuQuota(final int cpuQuota) {
        this.cpuQuota = cpuQuota;
    }

    public int getRamQuotaInMB() {
        return this.ramQuotaInMB;
    }

    public void setRamQuotaInMB(final int ramQuotaInMB) {
        this.ramQuotaInMB = ramQuotaInMB;
    }

    public int getDiskQuotaInMB() {
        return this.diskQuotaInMB;
    }

    public void setDiskQuotaInMB(final int diskQuotaInMB) {
        this.diskQuotaInMB = diskQuotaInMB;
    }

    public int getVmQuota() {
        return this.vmQuota;
    }

    public void setVmQuota(final int vmQuota) {
        this.vmQuota = vmQuota;
    }

}
