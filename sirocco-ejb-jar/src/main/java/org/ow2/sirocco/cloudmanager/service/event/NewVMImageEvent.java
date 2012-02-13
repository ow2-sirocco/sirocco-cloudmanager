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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.service.event;

import java.io.Serializable;

import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VMImageVO;

/**
 * Cloud administration event that indicates that a new VM image has been
 * created.
 */
public class NewVMImageEvent extends CloudAdminEvent implements Serializable {
    private static final long serialVersionUID = 3012516681670095979L;

    private VMImageVO image;

    /**
     * Constructs a NewVMImageEvent object
     */
    public NewVMImageEvent() {
        super();
    }

    /**
     * Constructs a NewVMImageEvent object with a specific VM image description
     * 
     * @param image description of the VM image that has been created
     */
    public NewVMImageEvent(final VMImageVO image) {
        this.image = image;
    }

    /**
     * Returns the VM image that has been created
     * 
     * @return description of the VM image that has been created
     */
    public VMImageVO getImage() {
        return this.image;
    }

    /**
     * Sets the the VM image that has been created
     * 
     * @param image the VM image that has been created
     */
    public void setImage(final VMImageVO image) {
        this.image = image;
    }

}
