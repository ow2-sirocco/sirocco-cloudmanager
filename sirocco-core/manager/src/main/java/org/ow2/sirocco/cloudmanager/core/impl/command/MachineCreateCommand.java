package org.ow2.sirocco.cloudmanager.core.impl.command;

import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

public class MachineCreateCommand extends ResourceCommand {
    private static final long serialVersionUID = 1L;

    public static final String MACHINE_CREATE = "machineCreate";

    private final MachineCreate machineCreate;

    private CloudProviderAccount account;

    private CloudProviderLocation location;

    public MachineCreateCommand(final MachineCreate machineCreate) {
        super(MachineCreateCommand.MACHINE_CREATE);
        this.machineCreate = machineCreate;
    }

    public MachineCreate getMachineCreate() {
        return this.machineCreate;
    }

    public CloudProviderAccount getAccount() {
        return this.account;
    }

    public MachineCreateCommand setAccount(final CloudProviderAccount account) {
        this.account = account;
        return this;
    }

    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public MachineCreateCommand setLocation(final CloudProviderLocation location) {
        this.location = location;
        return this;
    }

}
