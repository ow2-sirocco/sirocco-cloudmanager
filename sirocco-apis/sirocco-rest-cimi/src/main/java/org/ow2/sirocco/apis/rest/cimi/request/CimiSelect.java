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
import java.util.List;

/**
 * Utility class to manage CimiSelect expression.
 */
public class CimiSelect {

    private List<String> selects;

    private Integer[] rangeNumericArray;

    private Integer indexNumericArray;

    /**
     * Default constructor.
     */
    public CimiSelect() {
        super();
    }

    /**
     * Set constructor.
     * @param selects The original CimiSelect list
     */
    public CimiSelect(List<String> selects) {
        setSelects(selects);
    }

    /**
     * Get true if the CimiSelect expression is empty.
     * @return True if empty
     */
    public boolean isEmpty() {
        return (null == this.selects);
    }

    /**
     * Set the original CimiSelect list and build the instance variables.
     * @param selects The CimiSelect list
     */
    public void setSelects(List<String> selects) {
        this.selects = selects;
        build();
    }

    /**
     * Get the original CimiSelect list.
     * @return CimiSelect list
     */
    public List<String> getSelects() {
        return this.selects;
    }

    /**
     * Get true if the CimiSelect expression is an array with index (a single
     * number).
     * @return True it's an array with a single number
     */
    public boolean isIndexNumericArraySelect() {
        return (null != this.indexNumericArray);
    }

    /**
     * Get the index of a array expression.
     * @return The index or null if it's not a array expression with index
     */
    public Integer getIndexNumericArraySelect() {
        return this.indexNumericArray;
    }

    /**
     * Get true if the CimiSelect expression is an array with a range
     * (low-high).
     * @return True it's an array with a range
     */
    public boolean isRangeNumericArraySelect() {
        return (null != this.rangeNumericArray);
    }

    /**
     * Get the range of a array expression. In the return array, the first is
     * the low value and the second is the high value.
     * @return The array of range or null if it's not a array expression with
     *         range
     */
    public Integer[] getRangeNumericArraySelect() {
        return this.rangeNumericArray;
    }

    /**
     * Initialize the instance variables built.
     */
    protected void init() {
        this.indexNumericArray = null;
        this.rangeNumericArray = null;
    }

    /**
     * Build the instance variables.
     */
    protected void build() {
        init();
        buildArray();
    }

    /**
     * Build and fill the instance variables for a array expression. T if
     * necessary. See {@link #indexNumericArray} and {@link #rangeNumericArray}
     */
    protected void buildArray() {
        if ((null != this.selects) && (this.selects.size() == 1)) {
            String arrayValue = extractBetween(this.selects.get(0), '[', ']');
            Integer[] values = extractNumericArray(arrayValue);
            if (values.length > 0) {
                if (values.length == 1) {
                    this.indexNumericArray = values[0];
                } else if (values.length == 2) {
                    this.rangeNumericArray = values;
                }
            }
        }
    }

    /**
     * Extract the numeric values ​​of the contents of a array expression.
     * @param arrayContents The contents of a array expression (between the
     *        caracters '[' and ']')
     * @return A array of numeric values ​​or null if bad expression
     */
    protected Integer[] extractNumericArray(String arrayContents) {
        Integer value;
        List<Integer> listInt = new ArrayList<Integer>();
        String[] split = arrayContents.split("-");
        for (int i = 0; i < split.length; i++) {
            try {
                value = Integer.valueOf(split[i].trim());
                listInt.add(value);
            } catch (Exception e) {
                listInt.clear();
                break;
            }
        }
        return (Integer[]) listInt.toArray();
    }

    /**
     * Extract the string between two characters. The first character must be
     * located before the second.
     * @param source The string to analyse
     * @param first The first character
     * @param second The second character
     * @return The string between the two characters, or null if the two
     *         characters do not exist or are in reverse location
     */
    protected String extractBetween(String source, char first, char second) {
        String extract = null;
        int indexFirst = source.indexOf(first);
        int indexSecond = source.indexOf(second);
        if ((indexFirst > -1) && (indexSecond > -1)) {
            if (indexFirst < indexSecond) {
                extract = source.substring(indexFirst + 1, indexSecond);
            }
        }
        return extract;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (null != getSelects()) {
            for (String select : getSelects()) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(select.trim());
            }
        }
        return sb.toString();
    }

}
