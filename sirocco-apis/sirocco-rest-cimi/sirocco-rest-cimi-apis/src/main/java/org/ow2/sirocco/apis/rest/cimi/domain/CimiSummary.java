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

package org.ow2.sirocco.apis.rest.cimi.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement(name = "Summary")
@XmlType(propOrder = {"low", "medium", "high", "critical"})
@JsonPropertyOrder({"low", "medium", "high", "critical"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Field "low". */
    private Integer low;

    /** Field "medium". */
    private Integer medium;

    /** Field "high". */
    private Integer high;

    /** Field "critical". */
    private Integer critical;

    /**
     * Return the value of field "low".
     * 
     * @return The value
     */
    public Integer getLow() {
        return this.low;
    }

    /**
     * Set the value of field "low".
     * 
     * @param low The value
     */
    public void setLow(final Integer low) {
        this.low = low;
    }

    /**
     * Return the value of field "medium".
     * 
     * @return The value
     */
    public Integer getMedium() {
        return this.medium;
    }

    /**
     * Set the value of field "medium".
     * 
     * @param medium The value
     */
    public void setMedium(final Integer medium) {
        this.medium = medium;
    }

    /**
     * Return the value of field "high".
     * 
     * @return The value
     */
    public Integer getHigh() {
        return this.high;
    }

    /**
     * Set the value of field "high".
     * 
     * @param high The value
     */
    public void setHigh(final Integer high) {
        this.high = high;
    }

    /**
     * Return the value of field "critical".
     * 
     * @return The value
     */
    public Integer getCritical() {
        return this.critical;
    }

    /**
     * Set the value of field "critical".
     * 
     * @param critical The value
     */
    public void setCritical(final Integer critical) {
        this.critical = critical;
    }

}
