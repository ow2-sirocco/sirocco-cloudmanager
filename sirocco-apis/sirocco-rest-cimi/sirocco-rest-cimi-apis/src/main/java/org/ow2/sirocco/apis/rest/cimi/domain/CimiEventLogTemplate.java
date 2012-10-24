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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class EventLogTemplate.
 */
@XmlRootElement(name = "EventLogTemplate")
@XmlType(propOrder = {"id", "name", "description", "created", "updated", "propertyArray", "targetResource", "persistence",
    "operations"})
@JsonPropertyOrder({"resourceURI", "id", "name", "description", "created", "updated", "properties", "targetResource",
    "persistence", "operations"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiEventLogTemplate extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "persistence".
     */
    private String persistence;

    /**
     * Field "targetResource".
     * <p>
     * Read only
     * </p>
     */
    private TargetResource targetResource;

    /**
     * Default constructor.
     */
    public CimiEventLogTemplate() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiEventLogTemplate(final String href) {
        super(href);
    }

    /**
     * Return the value of field "persistence".
     * 
     * @return The value
     */
    public String getPersistence() {
        return this.persistence;
    }

    /**
     * Return the value of field "targetResource".
     * 
     * @return The value
     */
    public TargetResource getTargetResource() {
        return this.targetResource;
    }

    /**
     * Set the value of field "persistence".
     * 
     * @param persistence The value
     */
    public void setPersistence(final String persistence) {
        this.persistence = persistence;
    }

    /**
     * Set the value of field "targetResource".
     * 
     * @param persistence The value
     */
    public void setTargetResource(final TargetResource targetResource) {
        this.targetResource = targetResource;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getPersistence());
        has = has || (null != this.getTargetResource());
        return has;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiExchange#getExchangeType()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public ExchangeType getExchangeType() {
        return ExchangeType.EventLogTemplate;
    }
}
