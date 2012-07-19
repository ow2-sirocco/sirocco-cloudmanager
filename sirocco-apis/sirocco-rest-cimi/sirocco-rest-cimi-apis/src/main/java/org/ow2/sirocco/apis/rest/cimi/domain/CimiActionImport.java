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

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;

@XmlRootElement(name = "Action")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiActionImport extends CimiAction {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * Field source.
     */
    @NotNull(groups = GroupWrite.class)
    private String source;

    /**
     * Return the source of import action.
     * 
     * @return The export source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Set the value of the field source
     * 
     * @param source the value
     */
    public void setSource(final String source) {
        this.source = source;
    }
}
