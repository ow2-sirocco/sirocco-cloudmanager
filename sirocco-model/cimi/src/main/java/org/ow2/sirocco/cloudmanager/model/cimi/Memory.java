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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Memory implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum MemoryUnit {
        HERTZ, MEGA, GIGA, TERA, PETA, EXA, ZETTA
    }

    private MemoryUnit memoryUnit;

    private Float quantity;

    @Enumerated(EnumType.STRING)
    public MemoryUnit getMemoryUnit() {
        return this.memoryUnit;
    }

    public void setMemoryUnit(final MemoryUnit memoryUnit) {
        this.memoryUnit = memoryUnit;
    }

    @Column(name = "memory_quantity")
    public Float getQuantity() {
        return this.quantity;
    }

    public void setQuantity(final Float quantity) {
        this.quantity = quantity;
    }
}