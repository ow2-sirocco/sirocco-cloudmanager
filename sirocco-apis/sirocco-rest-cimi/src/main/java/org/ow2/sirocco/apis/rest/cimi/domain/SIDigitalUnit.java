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
 *
 */
public enum SIDigitalUnit {
    Byte("byte", "B"), kiloByte("kilobyte", "kB"), megaByte("megabyte", "MB"), gigaByte("gigabyte", "GB"), teraByte(
            "terabyte", "TB"), petaByte("petabyte", "PB"), exaByte("exabyte", "EB"), zettaByte("zettabyte", "ZB"), yottaByte(
            "yottabyte", " YB");

    /** The label of unit */
    private String label;

    /** The symbol of unit */
    private String symbol;

    /**
     * Parameter constructor.
     * @param label The label of unit
     * @param symbol The symbol of unit
     */
    SIDigitalUnit(String label, String symbol) {
        this.label = label;
        this.symbol = symbol;
    }

    /**
     * Return the label of unit.
     * @return The label of unit
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Return the symbol of unit
     * @return The symbol of unit
     */
    public String getSymbol() {
        return this.symbol;
    }
}
