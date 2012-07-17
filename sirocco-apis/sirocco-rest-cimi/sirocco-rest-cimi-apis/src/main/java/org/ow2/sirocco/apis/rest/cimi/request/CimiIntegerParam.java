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
package org.ow2.sirocco.apis.rest.cimi.request;

/**
 * Utility class to manage the parameter value, type Integer, of the query
 * string. Used for parameter CimiFirst and CimiLast.
 */
public class CimiIntegerParam extends CimiParam {

    private static final long serialVersionUID = 1L;

    public static int ALL = -1;

    private Integer value;

    private String initialValue;

    /**
     * Default constructor.
     */
    public CimiIntegerParam() {
        super();
    }

    /**
     * Set constructor.
     * 
     * @param initialValue The initial value
     */
    public CimiIntegerParam(final String initialValue) {
        this.setInitialValue(initialValue);
    }

    /**
     * Get true if none values.
     * 
     * @return True if empty
     */
    public boolean isEmpty() {
        boolean empty = true;
        if (null != this.value) {
            empty = false;
        }
        return empty;
    }

    /**
     * Set the initial values.
     * 
     * @param initialValues The initial value
     */
    public void setInitialValue(final String initialValue) {
        this.initialValue = initialValue;
        this.prepare();
    }

    /**
     * Get the initial value.
     * 
     * @return The initial value.
     */
    public String getInitialValue() {
        return this.initialValue;
    }

    /**
     * Get the int value.
     * <p>
     * If value is null, return -1.
     * </p>
     * 
     * @return The int value
     */
    public int getInt() {
        int intValue = -1;
        if (null != this.value) {
            intValue = this.value.intValue();
        }
        return intValue;
    }

    /**
     * Get the value.
     * 
     * @return The value
     */
    public Integer getValue() {
        return this.value;
    }

    /**
     * Set the value.
     * 
     * @param The value
     */
    protected void setValue(final Integer value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Convert the initial value to integer and convert 1-based to 0-based.
     * </p>
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiParam#prepare()
     */
    @Override
    protected void prepare() {
        if (null != this.initialValue) {
            try {
                this.value = Integer.valueOf(this.initialValue) - 1;
                if (this.value < CimiIntegerParam.ALL) {
                    this.value = CimiIntegerParam.ALL;
                }
            } catch (Exception e) {
                this.value = null;
            }
        }
    }
}
