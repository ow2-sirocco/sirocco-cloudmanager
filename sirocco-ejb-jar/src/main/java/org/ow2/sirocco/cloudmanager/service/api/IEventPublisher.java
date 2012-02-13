package org.ow2.sirocco.cloudmanager.service.api;

import java.io.Serializable;

import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Volume;

public interface IEventPublisher {
    static final String EJB_JNDI_NAME = "EventPublisherBean";

    void emitTopicMessage(final Serializable obj, final String property, final String value);

    void notifyMachineChange(final Machine machine);

    void notifyMachineCreation(final Machine machine);

    void notifyMachineDeletion(final Machine machine);

    void notifyVolumeChange(final Volume volume);

    void notifyVolumeCreation(final Volume volume);

    void notifyVolumeDeletion(final Volume volume);

    void notifyImageChange(final MachineImage image);

    void notifyImageCreation(final MachineImage image);

    void notifyImageDeletion(final MachineImage image);
}
