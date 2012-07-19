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

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Abstract class of a common resource URI exchanged with the server not
 * identified like XxxCreate or Action.
 */
public abstract class CimiCommonResourceUriAbstract extends CimiCommon implements CimiExchange {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** Type URI for the resource */
    private String resourceURI;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#getResourceURI()
     */
    @Override
    @XmlAttribute
    public String getResourceURI() {
        return this.resourceURI;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setResourceURI(java.lang.String)
     */
    @Override
    public void setResourceURI(final String resourceURI) {
        this.resourceURI = resourceURI;
    }
}
