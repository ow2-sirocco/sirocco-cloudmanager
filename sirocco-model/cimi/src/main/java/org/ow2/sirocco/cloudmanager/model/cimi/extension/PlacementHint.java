/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
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
 *  $Id$
 *
 */
package org.ow2.sirocco.cloudmanager.model.cimi.extension;

import java.io.Serializable;
import java.util.List;

public class PlacementHint implements Serializable {
    public static String AFFINITY_CONSTRAINT = "affinity_constraint";

    public static String ANTI_AFFINITY_CONSTRAINT = "anti_affinity_constraint";

    private List<String> machineIds;

    private String placementConstraint;

    public List<String> getMachineIds() {
        return this.machineIds;
    }

    public void setMachineIds(final List<String> machineIds) {
        this.machineIds = machineIds;
    }

    public String getPlacementConstraint() {
        return this.placementConstraint;
    }

    public void setPlacementConstraint(final String placementConstraint) {
        this.placementConstraint = placementConstraint;
    }

    @Override
    public String toString() {
        return "PlacementHint [machineIds=" + this.machineIds + ", placementConstraint=" + this.placementConstraint + "]";
    }

}
