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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiEventLogEventCollection;

/**
 * Class EventLog.
 */
@XmlRootElement(name = "EventLog")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiEventLog extends CimiObjectCommonAbstract {

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
     * Field "events".
     * <p>
     * Read only
     * </p>
     */
    private CimiEventLogEventCollection events;

    /**
     * Field "summary".
     * <p>
     * Read only
     * </p>
     */
    private Summary summary;

    /**
     * Default constructor.
     */
    public CimiEventLog() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiEventLog(final String href) {
        super(href);
    }

    /**
     * Return the value of field "events".
     * 
     * @return The value
     */
    public CimiEventLogEventCollection getEvents() {
        return this.events;
    }

    /**
     * Set the value of field "events".
     * 
     * @param events The value
     */
    public void setEvents(final CimiEventLogEventCollection events) {
        this.events = events;
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
     * Set the value of field "persistence".
     * 
     * @param persistence The value
     */
    public void setPersistence(final String persistence) {
        this.persistence = persistence;
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
     * Set the value of field "targetResource".
     * 
     * @param persistence The value
     */
    public void setTargetResource(final TargetResource targetResource) {
        this.targetResource = targetResource;
    }

    /**
     * Return the value of field "summary".
     * 
     * @return The value
     */
    public Summary getSummary() {
        return this.summary;
    }

    /**
     * Set the value of field "summary".
     * 
     * @param persistence The value
     */
    public void setSummary(final Summary summary) {
        this.summary = summary;
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
        return ExchangeType.EventLog;
    }

}
