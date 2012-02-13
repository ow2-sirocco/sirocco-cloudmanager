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

package org.ow2.sirocco.cloudmanager.provider.api.service;

import java.util.List;

import org.ow2.sirocco.cloudmanager.provider.api.entity.Job;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.NetworkInterface;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;

public interface IComputeService {

    Job<Machine> createMachine(MachineCreate machineCreate) throws CloudProviderException;

    Job<Machine.State> startMachine(final String machineId) throws CloudProviderException;

    Job<Machine.State> stopMachine(final String machineId) throws CloudProviderException;

    Job<Machine.State> suspendMachine(final String machineId) throws CloudProviderException;

    Job<Machine.State> resumeMachine(final String machineId) throws CloudProviderException;

    Job<Void> rebootMachine(final String machineId) throws CloudProviderException;

    Job<Machine.State> pauseMachine(final String machineId) throws CloudProviderException;

    Job<Machine.State> unpauseMachine(final String machineId) throws CloudProviderException;

    Job<Void> destroyMachine(final String machineId) throws CloudProviderException;

    Machine.State getMachineState(final String machineId) throws CloudProviderException;

    MachineConfiguration getMachineConfiguration(final String machineId) throws CloudProviderException;

    List<NetworkInterface> getMachineNetworkInterfaces(final String machineId) throws CloudProviderException;

    String getMachineGraphicalConsoleUrl(final String machineId) throws CloudProviderException;

    List<String> listMachines() throws CloudProviderException;

    Job<MachineImage> captureImage(String machineId, ImageCreate imageCreate) throws CloudProviderException;

    Job<VolumeAttachment> attachVolume(String machineId, VolumeAttachment attachement) throws CloudProviderException;

    Job<String> detachVolume(String machineId, String volumeId) throws CloudProviderException;
}
