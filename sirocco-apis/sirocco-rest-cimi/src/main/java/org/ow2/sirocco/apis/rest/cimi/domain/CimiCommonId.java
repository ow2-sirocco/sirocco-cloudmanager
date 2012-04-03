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

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Class Common with ID.
 * <p>
 * Attributes common to all entities
 * </p>
 */
public class CimiCommonId extends CimiCommon {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "id".
     * <p>
     * The unique self-reference to this entity; assigned upon entity creation.
     * This attribute value shall be unique in the Provider’s cloud.
     * </p>
     */
    private String id;

    /**
     * Field "created".
     * <p>
     * The timestamp when this entity was created. The format is DateTimeUTC
     * (ISO 8601), example : 2012-02-06T08:39:57Z.
     * </p>
     */
    private Date created;

    /**
     * Field "updated".
     * <p>
     * The time at which the last explicit attribute update was made on the
     * resoure. Note, while operations such as 'stop' do implicitly modify the
     * 'state' attribute it does not change the 'updated_time'. The format is
     * DateTimeUTC (ISO 8601), example : 2012-02-06T08:39:57Z.
     * </p>
     */
    private Date updated;

    /**
     * Field "operations".
     * <p>
     * A list of possible operations.
     * </p>
     */
    @JsonProperty
    private CimiOperation[] operations;

    /**
     * Return the value of field "id".
     * 
     * @return The value
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the value of field "id".
     * 
     * @param id The value
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Return the value of field "created".
     * 
     * @return The value
     */
    public Date getCreated() {
        return this.created;
    }

    /**
     * Set the value of field "created".
     * 
     * @param created The value
     */
    public void setCreated(final Date created) {
        this.created = created;
    }

    /**
     * Return the value of field "updated".
     * 
     * @return The value
     */
    public Date getUpdated() {
        return this.updated;
    }

    /**
     * Set the value of field "updated".
     * 
     * @param updated The value
     */
    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    /**
     * Return the value of field "operations".
     * 
     * @return The value
     */
    @JsonIgnore
    @XmlElement(name = "operation")
    public CimiOperation[] getOperations() {
        return this.operations;
    }

    /**
     * Set the value of field "operations".
     * 
     * @param operations The value
     */
    public void setOperations(final CimiOperation[] operations) {
        this.operations = operations;
    }

}
