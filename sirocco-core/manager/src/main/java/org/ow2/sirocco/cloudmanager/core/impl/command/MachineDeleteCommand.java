package org.ow2.sirocco.cloudmanager.core.impl.command;

public class MachineDeleteCommand extends ResourceCommand {
    private static final long serialVersionUID = 1L;

    public static final String MACHINE_DELETE = "machineDelete";

    public MachineDeleteCommand() {
        super(MachineDeleteCommand.MACHINE_DELETE);
    }

}
