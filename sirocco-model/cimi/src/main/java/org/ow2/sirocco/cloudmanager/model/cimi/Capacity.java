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

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Capacity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum Unit {
        BYTE, KILOBYTE, MEGABYTE, GIGABYTE, TERABYTE, PETABYTE, EXABYTE, ZETTABYTE, YOTTABYTE
    }

    private Unit units;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    public Unit getUnits() {
        return this.units;
    }

    public void setUnits(final Unit units) {
        this.units = units;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.quantity == null) ? 0 : this.quantity.hashCode());
        result = prime * result + ((this.units == null) ? 0 : this.units.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Capacity other = (Capacity) obj;
        if (this.quantity == null) {
            if (other.quantity != null) {
                return false;
            }
        } else if (!this.quantity.equals(other.quantity)) {
            return false;
        }
        if (this.units != other.units) {
            return false;
        }
        return true;
    }

}