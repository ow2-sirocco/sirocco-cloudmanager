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

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class MachineConfiguration extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer cpu;

    private Integer memory;

    private List<DiskTemplate> diskTemplates;

    public Integer getCpu() {
        return this.cpu;
    }

    public void setCpu(final Integer cpu) {
        this.cpu = cpu;
    }

    public Integer getMemory() {
        return this.memory;
    }

    public void setMemory(final Integer memory) {
        this.memory = memory;
    }

    @CollectionOfElements
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<DiskTemplate> getDiskTemplates() {
        return this.diskTemplates;
    }

    public void setDiskTemplates(final List<DiskTemplate> diskTemplates) {
        this.diskTemplates = diskTemplates;
    }
}
