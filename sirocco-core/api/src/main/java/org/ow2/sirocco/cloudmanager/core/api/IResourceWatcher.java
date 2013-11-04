package org.ow2.sirocco.cloudmanager.core.api;

import java.util.concurrent.Future;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;

/**
 * Watches resource state transition
 */
public interface IResourceWatcher {
    Future<Void> watchMachine(Machine machine, Job job, Machine.State... expectedStates) throws CloudProviderException;

    Future<Void> watchNetwork(Network network, Job job, Network.State... expectedStates) throws CloudProviderException;

    Future<Void> watchVolume(Volume volume, Job job) throws CloudProviderException;

    Future<Void> watchVolumeAttachment(Machine machine, MachineVolume volumeAttachement, Job job) throws CloudProviderException;

    Future<Void> watchSystem(org.ow2.sirocco.cloudmanager.model.cimi.system.System system, Job job,
        org.ow2.sirocco.cloudmanager.model.cimi.system.System.State... expectedStates) throws CloudProviderException;
}
