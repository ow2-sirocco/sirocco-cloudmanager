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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "USER_PROJECT")
@IdClass(RoleAssignmentID.class)
public class RoleAssignment implements Serializable {
    private static final long serialVersionUID = 1L;

    private long projId;

    private long userId;

    private User user;

    private Project project;

    public static enum Rights {
        ADMIN, USER
    }

    private RoleAssignment.Rights rights;

    public RoleAssignment() {
    }

    public RoleAssignment(final User user, final Project project, final Rights rights) {
        super();
        this.projId = project.getId();
        this.userId = user.getId();
        this.user = user;
        this.project = project;
        this.rights = rights;
    }

    @Id
    @Column(name = "PROJECT_ID", insertable = false, updatable = false)
    public long getProjId() {
        return this.projId;
    }

    @Id
    @Column(name = "USER_ID", insertable = false, updatable = false)
    public long getUserId() {
        return this.userId;
    }

    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    public Project getProject() {
        return this.project;
    }

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    public User getUser() {
        return this.user;
    }

    @Enumerated(EnumType.STRING)
    public Rights getRights() {
        return this.rights;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    public void setRights(final Rights rights) {
        this.rights = rights;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public void setProjId(final long projId) {
        this.projId = projId;
    }

    public void setUserId(final long userId) {
        this.userId = userId;
    }

}
