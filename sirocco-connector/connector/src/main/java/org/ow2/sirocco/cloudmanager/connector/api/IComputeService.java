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

import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;

public interface IComputeService {

    Job createMachine(MachineCreate machineCreate) throws ConnectorException;

    Job startMachine(final String machineId) throws ConnectorException;

    Job stopMachine(final String machineId) throws ConnectorException;

    Job suspendMachine(final String machineId) throws ConnectorException;

    Job restartMachine(final String machineId, boolean force) throws ConnectorException;

    Job pauseMachine(final String machineId) throws ConnectorException;

    Job deleteMachine(final String machineId) throws ConnectorException;

    Machine.State getMachineState(final String machineId) throws ConnectorException;

    Machine getMachine(final String machineId) throws ConnectorException;

    Job addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException;

    Job removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException;
}
