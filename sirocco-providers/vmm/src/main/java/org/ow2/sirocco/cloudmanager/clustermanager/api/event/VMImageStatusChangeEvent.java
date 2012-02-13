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

package org.ow2.sirocco.cloudmanager.clustermanager.api.event;

import java.io.Serializable;

public class VMImageStatusChangeEvent implements Serializable {
    private static final long serialVersionUID = -952779787915202156L;

    private String imageStatus;

    private Integer imageId;

    private String imageProviderId;

    private String vmProviderId;

    private String errorDetail;

    public String getImageStatus() {
        return this.imageStatus;
    }

    public void setImageStatus(final String imageStatus) {
        this.imageStatus = imageStatus;
    }

    public Integer getImageId() {
        return this.imageId;
    }

    public void setImageId(final Integer imageId) {
        this.imageId = imageId;
    }

    public String getImageProviderId() {
        return this.imageProviderId;
    }

    public void setImageProviderId(final String imageProviderId) {
        this.imageProviderId = imageProviderId;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public void setErrorDetail(final String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getVmProviderId() {
        return this.vmProviderId;
    }

    public void setVmProviderId(final String vmProviderId) {
        this.vmProviderId = vmProviderId;
    }

    @Override
    public String toString() {
        return "VMImageStatusChangeEvent [imageStatus=" + this.imageStatus + ", imageId=" + this.imageId + ", imageProviderId="
            + this.imageProviderId + ", vmProviderId=" + this.vmProviderId + ", errorDetail=" + this.errorDetail + "]";
    }

}
