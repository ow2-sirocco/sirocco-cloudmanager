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
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.IMultiCloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.ProviderMapping;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Visibility;

/**
 * Machine image encapsulating a pre-built OS and applications
 */
@Entity
@NamedQueries({@NamedQuery(name = "MachineImage.findByUuid", query = "SELECT m from MachineImage m WHERE m.uuid=:uuid")})
public class MachineImage extends CloudResource implements Serializable, IMultiCloudResource {
    private static final long serialVersionUID = 1L;

    private CloudProviderLocation location;

    public static enum State {
        CREATING, AVAILABLE, DELETING, ERROR, DELETED, UNKNOWN
    }

    public static enum Type {
        IMAGE, SNAPSHOT, PARTIAL_SNAPSHOT
    }

    private State state;

    private Type type;

    private String imageLocation;

    private MachineImage relatedImage;

    private CloudProviderAccount cloudProviderAccount;

    private Visibility visibility = Visibility.PRIVATE;

    private List<ProviderMapping> providerMappings;

    private Integer capacity;

    private String architecture;

    private String osType;

    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

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

    public String getArchitecture() {
        return this.architecture;
    }

    public void setArchitecture(final String architecture) {
        this.architecture = architecture;
    }

    public String getOsType() {
        return this.osType;
    }

    public void setOsType(final String osType) {
        this.osType = osType;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public void setCapacity(final Integer capacity) {
        this.capacity = capacity;
    }

    @ManyToOne
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    @ManyToOne
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    public List<ProviderMapping> getProviderMappings() {
        return this.providerMappings;
    }

    public void setProviderMappings(final List<ProviderMapping> providerMappings) {
        this.providerMappings = providerMappings;
    }

}
