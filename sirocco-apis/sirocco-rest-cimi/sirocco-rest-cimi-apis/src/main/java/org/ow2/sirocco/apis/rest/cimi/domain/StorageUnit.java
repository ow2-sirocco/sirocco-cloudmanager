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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.domain;

/**
 * Storage unit : Decimal quantity unit.
 */
@Deprecated
public enum StorageUnit {
    BYTE("byte", "B"), KILOBYTE("kilobyte", "kB"), MEGABYTE("megabyte", "MB"), GIGABYTE("gigabyte", "GB"), TERABYTE("terabyte",
        "TB"), PETABYTE("petabyte", "PB"), EXABYTE("exabyte", "EB"), ZETTABYTE("zettabyte", "ZB"), YOTTABYTE("yottabyte", " YB");

    /** The label of unit */
    private String label;

    /** The symbol of unit */
    private String symbol;

    /**
     * Parameter constructor.
     * 
     * @param label The label of unit
     * @param symbol The symbol of unit
     */
    StorageUnit(final String label, final String symbol) {
        this.label = label;
        this.symbol = symbol;
    }

    /**
     * Return the label of unit.
     * 
     * @return The label of unit
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Return the symbol of unit
     * 
     * @return The symbol of unit
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * Find the unit type with a given unit string.
     * <p>
     * The string to find can be a label or a symbol and the case are ignored.
     * </p>
     * 
     * @param toFind The unit string to find
     * @return The unit type or null if not found
     */
    public static StorageUnit findValueOf(final String toFind) {
        StorageUnit unit = null;
        if (null != toFind) {
            for (StorageUnit value : StorageUnit.values()) {
                if (toFind.equalsIgnoreCase(value.getLabel())) {
                    unit = value;
                    break;
                }
                if (toFind.equalsIgnoreCase(value.getSymbol())) {
                    unit = value;
                    break;
                }
            }
        }
        return unit;
    }
}
