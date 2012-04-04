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

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class JobCollection.
 * <p>
 * </p>
 */
@XmlRootElement(name = "JobCollection")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiJobCollection extends CimiCommonId {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "jobs".
     * <p>
     * </p>
     */
    private CimiJob[] jobs;

    /**
     * Default constructor.
     */
    public CimiJobCollection() {
        super();
    }

    /**
     * Return the value of field "jobs".
     * 
     * @return The value
     */
    public CimiJob[] getJobs() {
        return this.jobs;
    }

    /**
     * Set the value of field "jobs".
     * 
     * @param jobs The value
     */
    public void setJobs(final CimiJob[] jobs) {
        this.jobs = jobs;
    }

}
