package org.ow2.sirocco.cloudmanager.core.impl.command;

public class VolumeDeleteCommand extends ResourceCommand {
    private static final long serialVersionUID = 1L;

    public static final String VOLUME_DELETE = "volumeDelete";

    public VolumeDeleteCommand() {
        super(VolumeDeleteCommand.VOLUME_DELETE);
    }

}
