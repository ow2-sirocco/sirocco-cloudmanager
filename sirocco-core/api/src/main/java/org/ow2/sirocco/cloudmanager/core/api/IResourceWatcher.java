package org.ow2.sirocco.cloudmanager.core.api;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;

public interface IResourceWatcher {
    void watchMachine(Machine machine, Job job, Machine.State... expectedStates) throws CloudProviderException;

    void watchNetwork(Network network, Job job, Network.State... expectedStates) throws CloudProviderException;

    void watchVolume(Volume volume, Job job) throws CloudProviderException;

    void watchVolumeAttachment(Machine machine, MachineVolume volumeAttachement, Job job) throws CloudProviderException;

    void watchSystem(org.ow2.sirocco.cloudmanager.model.cimi.system.System system, Job job) throws CloudProviderException;
}
