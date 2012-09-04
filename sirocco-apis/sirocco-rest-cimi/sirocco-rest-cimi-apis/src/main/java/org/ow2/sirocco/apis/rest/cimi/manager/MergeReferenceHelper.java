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
package org.ow2.sirocco.apis.rest.cimi.manager;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiAddressCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAddressTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEventLogCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEventLogTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiForwardingGroupCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiForwardingGroupNetwork;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiForwardingGroupTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineNetworkInterface;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineNetworkInterfaceAddress;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetworkConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetworkCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetworkNetworkPort;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetworkPortConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetworkPortCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetworkPortTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetworkTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemAddress;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemForwardingGroup;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemNetwork;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemNetworkPort;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemSystem;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;

/**
 * Interface of a helper to get complete resource passed by reference or by
 * value during its creation.
 * <p>
 * For all methods of this interface : the given resource is searched in
 * services only if its reference is known. Then, and only if necessary, the
 * found reference is merged with the given data of resource.
 * </p>
 */
public interface MergeReferenceHelper {

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiCredential cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiCredentialCreate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiCredentialTemplate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiMachineCreate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiMachineConfiguration cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiMachineDisk cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiMachineNetworkInterface cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiMachineNetworkInterfaceAddress cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiMachineVolume cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiMachineImage cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiMachineTemplate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiVolumeCreate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiVolumeConfiguration cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiVolumeImage cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiVolumeVolumeImage cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiVolumeTemplate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiSystemCreate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiSystemAddress cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiSystemCredential cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiSystemForwardingGroup cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiSystemMachine cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiSystemNetwork cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiSystemNetworkPort cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiSystemSystem cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiSystemVolume cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiSystemTemplate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiNetworkCreate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiNetworkConfiguration cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiNetworkTemplate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiNetworkNetworkPort cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiNetworkPortCreate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiNetworkPortConfiguration cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiNetworkPortTemplate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiAddressCreate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiAddressTemplate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiForwardingGroupCreate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiForwardingGroupTemplate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiForwardingGroupNetwork cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiEventLogCreate cimi) throws Exception;

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    void merge(CimiContext context, CimiEventLogTemplate cimi) throws Exception;

}