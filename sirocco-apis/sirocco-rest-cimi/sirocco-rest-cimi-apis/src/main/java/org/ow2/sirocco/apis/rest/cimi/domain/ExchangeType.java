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

import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

/**
 * Enumeration of all the resource exchanged between the customer and the
 * producer.
 */
public enum ExchangeType {
    /** */
    CloudEntryPoint(PathType.CloudEntryPoint, false),
    /** */
    Credentials(PathType.Credentials), CredentialsCollection(PathType.Credentials, false), CredentialsCreate(
        PathType.Credentials),
    /** */
    CredentialsTemplate(PathType.CredentialsTemplate), CredentialsTemplateCollection(PathType.CredentialsTemplate, false),
    /** */
    Disk(PathType.MachineDisk), DiskCollection(PathType.MachineDisk, false),
    /** */
    Job(PathType.Job), JobCollection(PathType.Job, false),
    /** */
    Machine(PathType.Machine), MachineCollection(PathType.Machine, false),
    /** */
    MachineCreate(PathType.Machine), MachineAction(PathType.Machine),
    /** */
    MachineConfiguration(PathType.MachineConfiguration), MachineConfigurationCollection(PathType.MachineConfiguration, false),
    /** */
    MachineImage(PathType.MachineImage), MachineImageCollection(PathType.MachineImage, false),
    /** */
    MachineTemplate(PathType.MachineTemplate), MachineTemplateCollection(PathType.MachineTemplate, false),
    /** */
    MachineVolume(PathType.MachineVolume), MachineVolumeCollection(PathType.MachineVolume, false),
    /** */
    Volume(PathType.Volume), VolumeCollection(PathType.Volume, false),
    /** */
    VolumeVolumeImage(PathType.VolumeVolumeImage), VolumeVolumeImageCollection(PathType.VolumeVolumeImage, false),
    /** */
    VolumeConfiguration(PathType.VolumeConfiguration), VolumeConfigurationCollection(PathType.VolumeConfiguration, false),
    /** */
    VolumeImage(PathType.VolumeImage), VolumeImageCollection(PathType.VolumeImage, false),
    /** */
    VolumeTemplate(PathType.VolumeTemplate), VolumeTemplateCollection(PathType.VolumeTemplate, false);

    /** The path type of the resource. */
    PathType pathType;

    /** Flag ID in reference. */
    boolean idInReference;

    /** Constructor. */
    private ExchangeType(final PathType pathType) {
        this.pathType = pathType;
        this.idInReference = true;
    }

    /** Constructor. */
    private ExchangeType(final PathType pathType, final boolean idInReference) {
        this.pathType = pathType;
        this.idInReference = idInReference;
    }

    /**
     * Get the URI of the resource.
     * 
     * @return The URI
     */
    public String getResourceURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantsPath.CIMI_XML_NAMESPACE).append('/').append(this.name());
        return sb.toString();
    }

    /**
     * Get the path type of the resource.
     * 
     * @return The path type
     */
    public PathType getPathType() {
        return this.pathType;
    }

    /**
     * Get the pathname of the resource.
     * 
     * @return The path
     */
    public String getPathname() {
        return this.pathType.getPathname();
    }

    /**
     * Get the flag "ID in reference".
     * 
     * @return True if the type must be a ID in reference
     */
    public boolean hasIdInReference() {
        return this.idInReference;
    }

    /**
     * Get the flag {@link PathType} Parent.
     * 
     * @return True if the current PathType has a parent
     */
    public boolean hasParent() {
        return this.pathType.hasParent();
    }

    /**
     * Make a HREF for the current type.
     * 
     * @param baseUri The base URI
     * @param ids All ID necessary : the first is a ID parent, the last is
     *        current ID
     * @return
     */
    public String makeHref(final String baseUri, final String... ids) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseUri);
        List<String> paths = this.getPathType().getPaths();
        for (int i = 0; i < paths.size(); i++) {
            if (i > 0) {
                sb.append('/');
            }
            sb.append(paths.get(i));
            if ((i < (paths.size() - 1)) || (true == this.hasIdInReference())) {
                sb.append('/');
                if (i < ids.length) {
                    sb.append(ids[i]);
                } else {
                    sb.append('*');
                }
            }
        }
        return sb.toString();
    }

    /**
     * Make a HREF for the current type.
     * 
     * @param baseUri The base URI
     * @return
     */
    public String makeHrefPattern(final String baseUri) {
        StringBuilder sb = new StringBuilder();
        sb.append('^').append(baseUri);
        List<String> paths = this.getPathType().getPaths();
        for (int i = 0; i < paths.size(); i++) {
            if (i > 0) {
                sb.append('/');
            }
            sb.append(paths.get(i));
            if ((i < (paths.size() - 1)) || (true == this.hasIdInReference())) {
                sb.append('/').append("([0-9]+){1}");
            }
        }
        sb.append('$');
        return sb.toString();
    }
}
