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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.utils.CimiDateAdapter;

/**
 * Class Event.
 */
@XmlRootElement(name = "Event")
@XmlType(propOrder = {"id", "name", "description", "created", "updated", "propertyArray", "timestamp", "type", "content",
    "outcome", "severity", "contact", "operations"})
@JsonPropertyOrder({"resourceURI", "id", "name", "description", "created", "updated", "properties", "timestamp", "type",
    "content", "outcome", "severity", "contact", "operations"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiEvent extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    protected String contact;

    protected CimiEventType content;

    protected String outcome;

    protected String severity;

    protected Date timestamp;

    protected String type;

    /**
     * Default constructor.
     */
    public CimiEvent() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiEvent(final String href) {
        super(href);
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(final String contact) {
        this.contact = contact;
    }

    public CimiEventType getContent() {
        return this.content;
    }

    public void setContent(final CimiEventType content) {
        this.content = content;
    }

    public String getOutcome() {
        return this.outcome;
    }

    public void setOutcome(final String outcome) {
        this.outcome = outcome;
    }

    public String getSeverity() {
        return this.severity;
    }

    public void setSeverity(final String severity) {
        this.severity = severity;
    }

    @XmlJavaTypeAdapter(CimiDateAdapter.class)
    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
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
        return ExchangeType.Event;
    }

}
