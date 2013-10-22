package org.ow2.sirocco.cloudmanager.core.impl.command;

import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;

public class VolumeDetachCommand extends ResourceCommand {
    private static final long serialVersionUID = 1L;

    public static final String VOLUME_DETACH = "volumeDetach";

    private final MachineVolume volumeAttachment;

    public VolumeDetachCommand(final MachineVolume volumeAttachment) {
        super(VolumeDetachCommand.VOLUME_DETACH);
        this.volumeAttachment = volumeAttachment;
    }

    public MachineVolume getVolumeAttachment() {
        return this.volumeAttachment;
    }

}
