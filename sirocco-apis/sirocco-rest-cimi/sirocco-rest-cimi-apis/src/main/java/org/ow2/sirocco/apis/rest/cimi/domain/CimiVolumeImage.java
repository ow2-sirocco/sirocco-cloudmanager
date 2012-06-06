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
 * Class VolumeImage.
 */
@XmlRootElement(name = "volumeImage")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiVolumeImage extends CimiObjectCommonImpl {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "state".
     */
    private String state;

    /**
     * Field "imageLocation".
     */
    private ImageLocation imageLocation;

    /**
     * Field "imageData". Marshaling to verify
     */
    private byte[] imageData;

    /**
     * Field "bootable".
     */
    private Boolean bootable;

    /**
     * Default constructor.
     */
    public CimiVolumeImage() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiVolumeImage(final String href) {
        super(href);
    }

    /**
     * Return the value of field "state".
     * 
     * @return The value
     */
    public String getState() {
        return this.state;
    }

    /**
     * Set the value of field "state".
     * 
     * @param state The value
     */
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * Return the value of field "imageLocation".
     * 
     * @return The value
     */
    public ImageLocation getImageLocation() {
        return this.imageLocation;
    }

    /**
     * Set the value of field "imageLocation".
     * 
     * @param imageLocation The value
     */
    public void setImageLocation(final ImageLocation imageLocation) {
        this.imageLocation = imageLocation;
    }

    /**
     * Return the value of field "imageData".
     * 
     * @return The value
     */
    public byte[] getImageData() {
        return this.imageData;
    }

    /**
     * Set the value of field "imageData".
     * 
     * @param imageData The value
     */
    public void setImageData(final byte[] imageData) {
        this.imageData = imageData;
    }

    /**
     * Return the value of field "bootable".
     * 
     * @return The value
     */
    public Boolean getBootable() {
        return this.bootable;
    }

    /**
     * Set the value of field "bootable".
     * 
     * @param bootable The value
     */
    public void setBootable(final Boolean bootable) {
        this.bootable = bootable;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonImpl#hasValues()
     */
    @Override
    public boolean hasValues() {
        // TODO Auto-generated method stub
        return false;
    }

}
