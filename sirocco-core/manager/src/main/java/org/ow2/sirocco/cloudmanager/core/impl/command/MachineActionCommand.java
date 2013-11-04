/**
 *
 * SIROCCO
 * Copyright (C) 2013 Orange
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 */
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
