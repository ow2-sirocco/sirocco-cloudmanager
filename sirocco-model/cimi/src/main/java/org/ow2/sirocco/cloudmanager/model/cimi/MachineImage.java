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

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

@Entity
public class MachineImage extends CloudResource implements Serializable {
    private static final long serialVersionUID = 1L;

    private CloudProviderLocation location;

    public static enum State {
        CREATING, AVAILABLE, DELETING, ERROR
    }

    public static enum Type {
        IMAGE, SNAPSHOT, PARTIAL_SNAPSHOT
    }

    private State state;

    private Type type;

    private String imageLocation;

    private MachineImage relatedImage;

    private CloudProviderAccount cloudProviderAccount;

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    @Enumerated(EnumType.STRING)
    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    @OneToOne
    public MachineImage getRelatedImage() {
        return this.relatedImage;
    }

    public void setRelatedImage(final MachineImage relatedImage) {
        this.relatedImage = relatedImage;
    }

    public String getImageLocation() {
        return this.imageLocation;
    }

    public void setImageLocation(final String imageLocation) {
        this.imageLocation = imageLocation;
    }

    @ManyToOne
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    public CloudProviderLocation getLocation() {
        return this.location;
    }

    @ManyToOne
    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

}
