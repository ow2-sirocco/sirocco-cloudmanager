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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class VolumeTemplate.
 */
@XmlRootElement(name = "volumeTemplate")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiVolumeTemplate extends CimiCommonId implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    private String href;

    /**
     * Field "volumeConfig".
     */
    private CimiVolumeConfiguration volumeConfig;

    /**
     * Return the value of field "volumeConfig".
     * 
     * @return The value
     */
    public CimiVolumeConfiguration getVolumeConfig() {
        return this.volumeConfig;
    }

    /**
     * Set the value of field "volumeConfig".
     * 
     * @param volumeConfig The value
     */
    public void setVolumeConfig(final CimiVolumeConfiguration volumeConfig) {
        this.volumeConfig = volumeConfig;
    }

    /**
     * @return the href
     */
    @Override
    @XmlAttribute
    public String getHref() {
        return this.href;
    }

    /**
     * @param href the href to set
     */
    @Override
    public void setHref(final String href) {
        this.href = href;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCommonId#hasValues()
     */
    @Override
    public boolean hasValues() {
        // TODO Auto-generated method stub
        return false;
    }

}
