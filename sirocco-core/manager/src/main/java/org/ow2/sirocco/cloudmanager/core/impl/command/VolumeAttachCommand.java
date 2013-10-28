package org.ow2.sirocco.cloudmanager.core.impl.command;

import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;

public class VolumeAttachCommand extends ResourceCommand {
    private static final long serialVersionUID = 1L;

    public static final String VOLUME_ATTACH = "volumeAttach";

    private final MachineVolume volumeAttachment;

    public VolumeAttachCommand(final MachineVolume volumeAttachment) {
        super(VolumeAttachCommand.VOLUME_ATTACH);
        this.volumeAttachment = volumeAttachment;
    }

    public MachineVolume getVolumeAttachment() {
        return this.volumeAttachment;
    }

}
