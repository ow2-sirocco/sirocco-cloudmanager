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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to manage CimiExpand expression.
 */
public class CimiExpand implements Serializable {

    private static final long serialVersionUID = 1L;

    public static String EXPAND_ALL = "*";

    private List<String> expands;

    private List<String> attributes;

    private boolean containsExpandAll;

    /**
     * Default constructor.
     */
    public CimiExpand() {
        super();
    }

    /**
     * Set constructor.
     * 
     * @param expand The expand expression
     */
    public CimiExpand(final String expand) {
        this.setExpands(new ArrayList<String>(Arrays.asList(new String[] {expand})));
    }

    /**
     * Set constructor.
     * 
     * @param expands The original CimiExpand list
     */
    public CimiExpand(final List<String> expands) {
        this.setExpands(expands);
    }

    /**
     * Get true if the CimiExpand expression is empty.
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
     * Returns true if the CimiExpand contain '*'.
     * 
     * @return True if contain '*'
     */
    public boolean hasExpandAll() {
        return this.containsExpandAll;
    }

    /**
     * Returns true if the CimiExpand contain '*'.
     * 
     * @return True if contain '*'
     */
    protected boolean containsExpandAll() {
        boolean all = false;
        if (null != this.attributes) {
            for (String item : this.attributes) {
                if (true == CimiExpand.EXPAND_ALL.equals(item)) {
                    all = true;
                    break;
                }
            }
        }
        return all;
    }

    /**
     * Set the original CimiExpand list and build the instance variables.
     * 
     * @param expands The CimiExpand list
     */
    public void setExpands(final List<String> expands) {
        this.expands = expands;
        this.analyze();
    }

    /**
     * Get the original CimiExpand list.
     * 
     * @return CimiExpand list
     */
    public List<String> getExpands() {
        return this.expands;
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
     * Analyse the original expands.
     */
    protected void analyze() {
        this.attributes = new ArrayList<String>();

        Set<String> attributesNames = new HashSet<String>();
        String attr;

        // Split by comma
        List<String> fullAttrs = CimiSelect.splitByComma(this.getExpands());
        for (String full : fullAttrs) {
            attr = CimiSelect.extractBefore(full, '[');
            if ((null != attr) && (attr.length() > 0)) {
                // Only one attribute with same name
                if (false == attributesNames.contains(attr)) {
                    attributesNames.add(attr);
                    this.attributes.add(attr);
                }
            }
        }

        this.containsExpandAll = this.containsExpandAll();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CimiExpand [expands=" + this.expands + ", attributes=" + this.attributes + "]";
    }

}
