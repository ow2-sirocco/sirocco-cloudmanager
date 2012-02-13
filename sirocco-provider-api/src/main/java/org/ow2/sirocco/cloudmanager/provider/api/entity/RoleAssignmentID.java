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

package org.ow2.sirocco.cloudmanager.provider.api.entity;

import java.io.Serializable;

public class RoleAssignmentID implements Serializable {
    private static final long serialVersionUID = 1L;

    private long projId;

    private long userId;

    public RoleAssignmentID() {

    }

    public RoleAssignmentID(final long projId, final long userId) {
        super();
        this.setProjId(projId);
        this.setUserId(userId);
    }

    public long getProjId() {
        return this.projId;
    }

    public final void setProjId(final long projId) {
        this.projId = projId;
    }

    public long getUserId() {
        return this.userId;
    }

    public final void setUserId(final long userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        final int value32 = 32;
        int result = 1;
        result = prime * result + (int) (this.projId ^ (this.projId >>> value32));
        result = prime * result + (int) (this.userId ^ (this.userId >>> value32));
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        RoleAssignmentID other = (RoleAssignmentID) obj;
        if (this.projId != other.projId) {
            return false;
        }
        if (this.userId != other.userId) {
            return false;
        }
        return true;
    }

}
