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
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.AssertActionPath;

/**
 * Class to exchange all actions without validation.
 */
@XmlRootElement(name = "Action")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiAction extends CimiCommonResourceUriAbstract {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * Field action.
     */
    @AssertActionPath(groups = GroupWrite.class)
    private String action;

    /**
     * Field force.
     */
    private boolean force;

    /**
     * Field format.
     */
    private String format;

    /**
     * Field destination.
     */
    private String destination;

    /**
     * Field image.
     */
    private String image;

    /**
     * Return the action of action.
     * 
     * @return The action of action
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Set the value of the field action
     * 
     * @param action the value
     */
    public void setAction(final String action) {
        this.action = action;
    }

    /**
     * Return true if the action is forced.
     * 
     * @return True if forced action
     */
    public boolean isForce() {
        return this.force;
    }

    /**
     * Set the value of the field force
     * 
     * @param force the value
     */
    public void setForce(final boolean force) {
        this.force = force;
    }

    /**
     * Return the format of export action.
     * 
     * @return The export format
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * Set the value of the field format
     * 
     * @param format the value
     */
    public void setFormat(final String format) {
        this.format = format;
    }

    /**
     * Return the destination of export action.
     * 
     * @return The export destination
     */
    public String getDestination() {
        return this.destination;
    }

    /**
     * Set the value of the field destination
     * 
     * @param destination the value
     */
    public void setDestination(final String destination) {
        this.destination = destination;
    }

    /**
     * Return the image of restore action.
     * 
     * @return The export image
     */
    public String getImage() {
        return this.image;
    }

    /**
     * Set the value of the field image
     * 
     * @param image the value
     */
    public void setImage(final String image) {
        this.image = image;
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
        return ExchangeType.Action;
    }

}
