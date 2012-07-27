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

import java.util.ArrayList;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

/**
 * Enumeration of all the path name used.
 */
public enum PathType {
    /** */
    CloudEntryPoint(ConstantsPath.CLOUDENTRYPOINT),
    /** */
    Address(ConstantsPath.ADDRESS),
    /** */
    AddressTemplate(ConstantsPath.ADDRESS_TEMPLATE),
    /** */
    Credential(ConstantsPath.CREDENTIAL),
    /** */
    CredentialTemplate(ConstantsPath.CREDENTIAL_TEMPLATE),
    /** */
    Event(ConstantsPath.EVENT),
    /** */
    EventLog(ConstantsPath.EVENT_LOG),
    /** */
    EventLogEvent(ConstantsPath.EVENT, EventLog),
    /** */
    EventLogTemplate(ConstantsPath.EVENT_LOG_TEMPLATE),
    /** */
    ForwardingGroup(ConstantsPath.FORWARDING_GROUP),
    /** */
    ForwardingGroupNetwork(ConstantsPath.NETWORK, ForwardingGroup),
    /** */
    ForwardingGroupTemplate(ConstantsPath.FORWARDING_GROUP_TEMPLATE),
    /** */
    Job(ConstantsPath.JOB),
    /** */
    Machine(ConstantsPath.MACHINE),
    /** */
    MachineConfiguration(ConstantsPath.MACHINE_CONFIGURATION),
    /** */
    MachineImage(ConstantsPath.MACHINE_IMAGE),
    /** */
    MachineTemplate(ConstantsPath.MACHINE_TEMPLATE),
    /** */
    MachineDisk(ConstantsPath.DISK, Machine),
    /** */
    MachineNetworkInterface(ConstantsPath.NETWORK_INTERFACE, Machine),
    /** */
    MachineNetworkInterfaceAddress(ConstantsPath.ADDRESS, MachineNetworkInterface),
    /** */
    MachineVolume(ConstantsPath.VOLUME, Machine),
    /** */
    Network(ConstantsPath.NETWORK),
    /** */
    NetworkNetworkPort(ConstantsPath.NETWORK_PORT, Network),
    /** */
    NetworkConfiguration(ConstantsPath.NETWORK_CONFIGURATION),
    /** */
    NetworkTemplate(ConstantsPath.NETWORK_TEMPLATE),
    /** */
    NetworkPort(ConstantsPath.NETWORK_PORT),
    /** */
    NetworkPortConfiguration(ConstantsPath.NETWORK_PORT_CONFIGURATION),
    /** */
    NetworkPortTemplate(ConstantsPath.NETWORK_PORT_TEMPLATE),
    /** */
    System(ConstantsPath.SYSTEM),
    /** */
    SystemCredential(ConstantsPath.CREDENTIAL, System),
    /** */
    SystemMachine(ConstantsPath.MACHINE, System),
    /** */
    SystemSystem(ConstantsPath.SYSTEM, System),
    /** */
    SystemVolume(ConstantsPath.VOLUME, System),
    /** */
    SystemAddress(ConstantsPath.ADDRESS, System),
    /** */
    SystemNetwork(ConstantsPath.NETWORK, System),
    /** */
    SystemNetworkPort(ConstantsPath.NETWORK_PORT, System),
    /** */
    SystemForwardingGroup(ConstantsPath.FORWARDING_GROUP, System),
    /** */
    SystemTemplate(ConstantsPath.SYSTEM_TEMPLATE),
    /** */
    Volume(ConstantsPath.VOLUME),
    /** */
    VolumeVolumeImage(ConstantsPath.VOLUME_IMAGE, Volume),
    /** */
    VolumeConfiguration(ConstantsPath.VOLUME_CONFIGURATION),
    /** */
    VolumeImage(ConstantsPath.VOLUME_IMAGE),
    /** */
    VolumeTemplate(ConstantsPath.VOLUME_TEMPLATE);

    /** The pathname. */
    String pathname;

    /** The parent path. */
    PathType parent;

    /** Constructor. */
    private PathType(final String pathname) {
        this.pathname = pathname;
        this.parent = null;
    }

    /** Constructor. */
    private PathType(final String pathname, final PathType parent) {
        this.pathname = pathname;
        this.parent = parent;
    }

    /**
     * Get the pathname.
     * 
     * @return The pathname
     */
    public String getPathname() {
        return this.pathname;
    }

    /**
     * Get the complete path with the parents pathname.
     * <p>
     * The ID parents are replaced by a the character "*"
     * </p>
     * 
     * @return The complete path
     */
    public String getPath() {
        StringBuilder sb = new StringBuilder();
        if (true == this.hasParent()) {
            sb.append(this.parent.getPath());
            sb.append("/*/");
        }
        sb.append(this.pathname);
        return sb.toString();
    }

    /**
     * Get the list of pathnames.
     * 
     * @return The list of pathnames
     */
    public List<String> getPaths() {
        List<String> list = null;
        if (true == this.hasParent()) {
            list = this.parent.getPaths();
        } else {
            list = new ArrayList<String>();
        }
        list.add(this.pathname);
        return list;
    }

    /**
     * Indicates if the current PathType has a parent.
     * 
     * @return True if it has a parent
     */
    public boolean hasParent() {
        return (null != this.parent);
    }

    /**
     * Get the number of parents.
     * 
     * @return The number of parents
     */
    public int getParentDepth() {
        int depth = 0;
        if (true == this.hasParent()) {
            depth++;
            depth += this.parent.getParentDepth();
        }
        return depth;
    }

    /**
     * Find the path type with a given path.
     * 
     * @param pathname The pathname
     * @return The path type or null if not found
     */
    public static PathType valueOfPath(final String path) {
        PathType type = null;
        for (PathType value : PathType.values()) {
            if (path.equals(value.getPathname())) {
                type = value;
                break;
            }
        }
        return type;
    }

}
