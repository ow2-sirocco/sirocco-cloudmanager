package org.ow2.sirocco.cloudmanager.core.impl.command;

public class MachineActionCommand extends ResourceCommand {
    private static final long serialVersionUID = 1L;

    public static final String MACHINE_ACTION = "machineAction";

    private final String action;

    private boolean force;

    public MachineActionCommand(final String action) {
        super(MachineActionCommand.MACHINE_ACTION);
        this.action = action;
    }

    public boolean isForce() {
        return this.force;
    }

    public MachineActionCommand setForce(final boolean force) {
        this.force = force;
        return this;
    }

    public String getAction() {
        return this.action;
    }

}
