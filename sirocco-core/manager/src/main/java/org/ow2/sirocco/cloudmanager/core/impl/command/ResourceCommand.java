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

import java.io.Serializable;

import org.ow2.sirocco.cloudmanager.model.cimi.Job;

public class ResourceCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandType;

    private int resourceId;

    private Job job;

    public ResourceCommand(final String commandType) {
        this.commandType = commandType;
    }

    public Job getJob() {
        return this.job;
    }

    public ResourceCommand setJob(final Job job) {
        this.job = job;
        return this;
    }

    public String getCommandType() {
        return this.commandType;
    }

    public int getResourceId() {
        return this.resourceId;
    }

    public ResourceCommand setResourceId(final int resourceId) {
        this.resourceId = resourceId;
        return this;
    }

}
