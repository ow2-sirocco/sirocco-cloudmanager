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
 * $Id: Operation.java 101 2012-03-05 08:26:32Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class Operation. <p> The operation executed on the resource. </p>
 */
@XmlRootElement(name = "operation")
@JsonSerialize(include = Inclusion.NON_NULL)
public class Operation implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    // ---------------------------------------- Fields

    /**
     * Field "rel". <p> The name of the operation. </p>
     */
    private String rel;

    /**
     * Field "href". <p> The URL to execute the operation. </p>
     */
    private String href;

    // ---------------------------------------- Constructors

    /**
     * Default constructor.
     */
    public Operation() {
        super();
    }

    // ---------------------------------------- ???com-accesseurs???

    /**
     * Return the value of field "rel".
     * @return The value
     */
    public String getRel() {
        return this.rel;
    }

    /**
     * Set the value of field "rel".
     * @param rel The value
     */
    public void setRel(String rel) {
        this.rel = rel;
    }

    /**
     * Return the value of field "href".
     * @return The value
     */
    public String getHref() {
        return this.href;
    }

    /**
     * Set the value of field "href".
     * @param href The value
     */
    public void setHref(String href) {
        this.href = href;
    }

}
