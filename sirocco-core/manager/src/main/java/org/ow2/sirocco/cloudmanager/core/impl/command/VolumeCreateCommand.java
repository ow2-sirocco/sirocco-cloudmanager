package org.ow2.sirocco.cloudmanager.core.impl.command;

import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

public class VolumeCreateCommand extends ResourceCommand {
    private static final long serialVersionUID = 1L;

    public static final String VOLUME_CREATE = "volumeCreate";

    private final VolumeCreate VolumeCreate;

    private CloudProviderAccount account;

    private CloudProviderLocation location;

    public VolumeCreateCommand(final VolumeCreate volumeCreate) {
        super(VolumeCreateCommand.VOLUME_CREATE);
        this.VolumeCreate = volumeCreate;
    }

    public VolumeCreate getVolumeCreate() {
        return this.VolumeCreate;
    }

    public CloudProviderAccount getAccount() {
        return this.account;
    }

    public VolumeCreateCommand setAccount(final CloudProviderAccount account) {
        this.account = account;
        return this;
    }

    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public VolumeCreateCommand setLocation(final CloudProviderLocation location) {
        this.location = location;
        return this;
    }

}
