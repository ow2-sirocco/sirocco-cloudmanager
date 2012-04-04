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
 * Class CredentialsTemplateCollection.
 * <p>
 * </p>
 */
@XmlRootElement(name = "CredentialsTemplateCollection")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiCredentialsTemplateCollection extends CimiCommonId {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "CredentialsTemplates".
     * <p>
     * </p>
     */
    private CimiCredentialsTemplate[] credentialsTemplates;

    /**
     * Return the value of field "CredentialsTemplates".
     * 
     * @return The value
     */
    public CimiCredentialsTemplate[] getCredentialsTemplates() {
        return this.credentialsTemplates;
    }

    /**
     * Set the value of field "CredentialsTemplates".
     * 
     * @param CredentialsTemplates The value
     */
    public void setCredentialsTemplates(final CimiCredentialsTemplate[] credentialsTemplates) {
        this.credentialsTemplates = credentialsTemplates;
    }

}
