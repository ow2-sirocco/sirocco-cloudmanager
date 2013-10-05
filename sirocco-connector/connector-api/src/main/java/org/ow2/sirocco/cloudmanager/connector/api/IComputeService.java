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

package org.ow2.sirocco.cloudmanager.connector.api;

import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;

/**
 * Lifecycle operations on Machines
 */
public interface IComputeService {

    /**
     * Creates a machine
     * 
     * @param machineCreate
     * @param target
     * @return
     * @throws ConnectorException
     */
    Machine createMachine(MachineCreate machineCreate, ProviderTarget target) throws ConnectorException;

    /**
     * Starts a machine
     * 
     * @param machineId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void startMachine(final String machineId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Stops a machine
     * 
     * @param machineId
     * @param force
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void stopMachine(final String machineId, boolean force, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Suspend a machine
     * 
     * @param machineId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void suspendMachine(final String machineId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Restart a machine
     * 
     * @param machineId
     * @param force
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void restartMachine(final String machineId, boolean force, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Pause a machine
     * 
     * @param machineId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void pauseMachine(final String machineId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Delete a machine
     * 
     * @param machineId
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void deleteMachine(final String machineId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Capture a machine
     * 
     * @param machineId
     * @param machineImage
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    MachineImage captureMachine(String machineId, MachineImage machineImage, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

    /**
     * Gets the state of a machine
     * 
     * @param machineId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    Machine.State getMachineState(final String machineId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException;

    /**
     * Gets a machine
     * 
     * @param machineId
     * @param target
     * @return
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    Machine getMachine(final String machineId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException;

    /**
     * Attaches a volume to a machine
     * 
     * @param machineId
     * @param machineVolume
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void addVolumeToMachine(final String machineId, final MachineVolume machineVolume, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;

    /**
     * Removes a volume from a machine
     * 
     * @param machineId
     * @param machineVolume
     * @param target
     * @throws ResourceNotFoundException
     * @throws ConnectorException
     */
    void removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException;
}
