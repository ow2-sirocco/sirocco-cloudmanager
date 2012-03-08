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
 * $Id: ImageLocation.java 101 2012-03-05 08:26:32Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class Common. <p> Attributes common to all entities </p>
 */
@XmlRootElement(name = "imageLocation")
@JsonSerialize(include = Inclusion.NON_NULL)
public class ImageLocation implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    // ---------------------------------------- Fields

    /**
     * Field "id". <p> The unique self-reference to this entity; assigned upon
     * entity creation. This attribute value shall be unique in the Provider’s
     * cloud. </p>
     */
    private String href;

    // ---------------------------------------- Constructors

    /**
     * Default constructor.
     */
    public ImageLocation() {
        super();
    }

    /**
     * Default constructor.
     */
    public ImageLocation(String href) {
        setHref(href);
    }

    // ---------------------------------------- ???com-accesseurs???

    /**
     * Return the value of field "id".
     * @return The value
     */
    @XmlAttribute
    public String getHref() {
        return this.href;
    }

    /**
     * Set the value of field "id".
     * @param id The value
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ImageLocation [href=" + href + "]";
    }

}
