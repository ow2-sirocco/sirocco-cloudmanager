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

import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VMSizeVO;

@Entity
public class MachineConfiguration extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numCPUs;

    private long memorySizeMB;

    private long diskSizeMB;

    public int getNumCPUs() {
        return this.numCPUs;
    }

    public void setNumCPUs(final int numCPUs) {
        this.numCPUs = numCPUs;
    }

    public long getMemorySizeMB() {
        return this.memorySizeMB;
    }

    public void setMemorySizeMB(final long memorySizeMB) {
        this.memorySizeMB = memorySizeMB;
    }

    public long getDiskSizeMB() {
        return this.diskSizeMB;
    }

    public void setDiskSizeMB(final long diskSizeMB) {
        this.diskSizeMB = diskSizeMB;
    }

    /**
     * Builds a new VMSizeVO from its business object counterpart
     * 
     * @return a new VMSizeVO value object
     */
    public VMSizeVO toValueObject() {
        VMSizeVO size = new VMSizeVO();
        size.setName(this.getName());
        size.setNumCPUs(this.getNumCPUs());
        size.setMemorySizeMB(this.getMemorySizeMB());
        return size;
    }
}
