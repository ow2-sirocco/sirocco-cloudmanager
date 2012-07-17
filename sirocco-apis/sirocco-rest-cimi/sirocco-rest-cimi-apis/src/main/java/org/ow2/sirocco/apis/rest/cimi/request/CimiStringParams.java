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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to manage the parameters values, type String, of the query
 * string.
 */
public abstract class CimiStringParams extends CimiParam {

    private static final long serialVersionUID = 1L;

    public static String ALL = "*";

    private List<String> initialValues;

    private List<String> values;

    private boolean containsWildCardAll;

    /**
     * Default constructor.
     */
    public CimiStringParams() {
        super();
    }

    /**
     * Set constructor.
     * 
     * @param initialValue The initial value
     */
    public CimiStringParams(final String initialValue) {
        this.setInitialValues(new ArrayList<String>(Arrays.asList(new String[] {initialValue})));
    }

    /**
     * Set constructor.
     * 
     * @param initialValues The initial values
     */
    public CimiStringParams(final List<String> initialValues) {
        this.setInitialValues(initialValues);
    }

    /**
     * Set constructor.
     * 
     * @param initialValues The initial values
     */
    public CimiStringParams(final String[] initialValues) {
        this.setInitialValues(Arrays.asList(initialValues));
    }

    /**
     * Get true if none values.
     * 
     * @return True if empty
     */
    public boolean isEmpty() {
        boolean empty = true;
        if (null != this.values) {
            if (this.values.size() > 0) {
                empty = false;
            }
        }
        return empty;
    }

    /**
     * Returns true if one parameter contains '*'.
     * 
     * @return True if contains '*'
     */
    public boolean hasAll() {
        return this.containsWildCardAll;
    }

    /**
     * Set the initial values.
     * 
     * @param initialValues The initial values
     */
    public void setInitialValues(final List<String> initialValues) {
        this.initialValues = initialValues;
        this.prepare();
    }

    /**
     * Set the initial values.
     * 
     * @param initialValues The initial values
     */
    public void setInitialValues(final String[] initialValues) {
        this.setInitialValues(Arrays.asList(initialValues));
    }

    /**
     * Get the initial values.
     * 
     * @return The initial values.
     */
    public List<String> getInitialValues() {
        return this.initialValues;
    }

    /**
     * Get the values.
     * 
     * @return The values
     */
    public List<String> getValues() {
        return this.values;
    }

    /**
     * Set the values.
     * 
     * @param The values
     */
    protected void setValues(final List<String> values) {
        this.values = values;
    }

    /**
     * Prepare the original initialValues to split by comma and remove
     * duplicate.
     */
    protected void prepareToSplit() {
        this.values = new ArrayList<String>();

        Set<String> foundValues = new HashSet<String>();

        // Split by comma
        List<String> byCommaValues = CimiStringParams.splitByComma(this.getInitialValues());
        // Remove duplicate value
        for (String value : byCommaValues) {
            if (false == foundValues.contains(value)) {
                foundValues.add(value);
                this.values.add(value);
            }
        }
    }

    /**
     * Analyse values to find '*'.
     */
    protected void prepareWildCardAll() {
        boolean all = false;
        if (null != this.values) {
            for (String item : this.values) {
                if (true == CimiStringParams.ALL.equals(item)) {
                    all = true;
                    break;
                }
            }
        }
        this.containsWildCardAll = all;
    }

    /**
     * Splits all items with comma and adds them in the returned list.
     * 
     * @param initialValues The initial values
     * @return The list with all items without comma
     */
    protected static List<String> splitByComma(final List<String> initialValues) {
        List<String> byComma = new ArrayList<String>();
        if (null != initialValues) {
            String[] split;
            for (String select : initialValues) {
                split = select.split(",");
                for (String element : split) {
                    if (element.trim().length() > 0) {
                        byComma.add(element.trim());
                    }
                }
            }
        }
        return byComma;
    }

}
