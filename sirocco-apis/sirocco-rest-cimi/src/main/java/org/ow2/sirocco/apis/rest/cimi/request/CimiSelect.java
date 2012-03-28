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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to manage CimiSelect expression.
 */
public class CimiSelect {

    private List<String> selects;

    private List<String> attributes;

    private Map<String, List<Integer>> numericArrays;

    private Map<String, String> expressionArrays;

    /**
     * Default constructor.
     */
    public CimiSelect() {
        super();
    }

    /**
     * Set constructor.
     * 
     * @param selects The original CimiSelect list
     */
    public CimiSelect(final List<String> selects) {
        this.setSelects(selects);
    }

    /**
     * Get true if the CimiSelect expression is empty.
     * 
     * @return True if empty
     */
    public boolean isEmpty() {
        boolean empty = true;
        if (null != this.attributes) {
            if (this.attributes.size() > 0) {
                empty = false;
            }
        }
        return empty;
    }

    /**
     * Set the original CimiSelect list and build the instance variables.
     * 
     * @param selects The CimiSelect list
     */
    public void setSelects(final List<String> selects) {
        this.selects = selects;
        this.analyze();
    }

    /**
     * Get the original CimiSelect list.
     * 
     * @return CimiSelect list
     */
    public List<String> getSelects() {
        return this.selects;
    }

    /**
     * Get the attributes.
     * 
     * @return The attributes
     */
    public List<String> getAttributes() {
        return this.attributes;
    }

    /**
     * Get the last attribute.
     * 
     * @return The last attribute or null if empty
     */
    public String getLastAttribute() {
        String attr = null;
        if (false == this.isEmpty()) {
            attr = this.attributes.get(this.attributes.size() - 1);
        }
        return attr;
    }

    /**
     * Get the numerics array of the last attribute.
     * 
     * @return The numerics array or null if the last attribute has not numerics
     *         array
     */
    public List<Integer> getLastNumericArray() {
        List<Integer> nums = null;
        if (false == this.isEmpty()) {
            nums = this.numericArrays.get(this.getLastAttribute());
        }
        return nums;
    }

    /**
     * The indicator of the presence of a array (expression or numeric) in the
     * selects.
     * 
     * @return Returns true if there is an array
     */
    public boolean isArrayPresent() {
        boolean hasArray = false;
        if (false == this.isEmpty()) {
            hasArray = this.isExpressionArrayPresent() || this.isNumericArrayPresent();
        }
        return hasArray;
    }

    /**
     * The indicator of the presence of a numeric array in the selects.
     * 
     * @return Returns true if there is a numeric array
     */
    public boolean isNumericArrayPresent() {
        boolean hasArray = false;
        if (false == this.isEmpty()) {
            hasArray = null != this.getLastNumericArray();
        }
        return hasArray;
    }

    /**
     * The indicator of the presence of a expression array in the selects.
     * 
     * @return Returns true if there is a expression array
     */
    public boolean isExpressionArrayPresent() {
        boolean hasArray = false;
        if (false == this.isEmpty()) {
            hasArray = null != this.getLastExpressionArray();
        }
        return hasArray;
    }

    /**
     * Get the expression array of the last attribute.
     * 
     * @return The expression array or null if the last attribute has not
     *         expression array
     */
    public String getLastExpressionArray() {
        String exp = null;
        if (false == this.isEmpty()) {
            exp = this.expressionArrays.get(this.getLastAttribute());
        }
        return exp;
    }

    /**
     * Analyse the original selects.
     */
    protected void analyze() {
        this.attributes = new ArrayList<String>();
        this.expressionArrays = new HashMap<String, String>();
        this.numericArrays = new HashMap<String, List<Integer>>();

        List<Integer> numerics;
        String attr;
        String arrayExp;

        List<String> fullAttrs = CimiSelect.splitByComma(this.getSelects());
        for (String full : fullAttrs) {
            attr = CimiSelect.extractBefore(full, '[');
            if ((null != attr) && (attr.length() > 0)) {
                this.attributes.add(attr);
                arrayExp = CimiSelect.extractBetween(full, '[', ']');
                if (null != arrayExp) {
                    numerics = CimiSelect.extractNumericArray(arrayExp);
                    if (numerics.size() > 0) {
                        this.numericArrays.put(attr, numerics);
                    } else {
                        this.expressionArrays.put(attr, arrayExp);
                    }
                }
            }
        }
    }

    /**
     * Splits all items with comma and adds them in the returned list.
     * 
     * @param selects The orginal list
     * @return The list with all items without comma
     */
    public static List<String> splitByComma(final List<String> selects) {
        List<String> byComma = new ArrayList<String>();
        if (null != selects) {
            String[] split;
            for (String select : selects) {
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

    /**
     * Extract the string before a character.
     * 
     * @param source The string to analyse
     * @param before The character before which the extraction should be
     *        performed
     * @return The string before the character or the source string if the
     *         character not found
     */
    public static String extractBefore(final String source, final char before) {
        String extract = null;
        if (null != source) {
            int index = source.indexOf(before);
            if (index > -1) {
                extract = source.substring(0, index).trim();
            } else {
                extract = source.trim();
            }
        }
        return extract;
    }

    /**
     * Extract the string between two characters. The first character must be
     * located before the second.
     * 
     * @param source The string to analyse
     * @param first The first character
     * @param second The second character
     * @return The string between the two characters, or null if the two
     *         characters do not exist or are in reverse location
     */
    public static String extractBetween(final String source, final char first, final char second) {
        String extract = null;
        if (null != source) {
            int indexFirst = source.indexOf(first);
            int indexSecond = source.indexOf(second);
            if ((indexFirst > -1) && (indexSecond > -1)) {
                if (indexFirst < indexSecond) {
                    extract = source.substring(indexFirst + 1, indexSecond).trim();
                }
            }
        }
        return extract;
    }

    /**
     * Extract the numeric values ​​of the contents of a array expression.
     * <p>
     * The size of the return list is always 2. Example :
     * <ul>
     * <li>"1-5" = [1, 5]</li>
     * <li>"9" = [9, 9]</li>
     * <li>"7-12-45" = [7, 12]</li>
     * </ul>
     * </p>
     * 
     * @param arrayContents The contents of a array expression (between the
     *        caracters '[' and ']')
     * @return A list of numeric values
     */
    public static List<Integer> extractNumericArray(final String arrayContents) {
        Integer value;
        List<Integer> listInt = new ArrayList<Integer>();
        if (null != arrayContents) {
            String[] split = arrayContents.split("-");
            for (String element : split) {
                try {
                    value = Integer.valueOf(element.trim());
                    listInt.add(value);
                    // Max size
                    if (listInt.size() == 2) {
                        break;
                    }
                } catch (Exception e) {
                    listInt.clear();
                    break;
                }
            }
            // Adjust the size to 2
            if (listInt.size() == 1) {
                listInt.add(listInt.get(0));
            }
        }
        return listInt;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String to = null;
        if (null != this.getSelects()) {
            to = this.getSelects().toString();
        }
        return to;
    }

}
