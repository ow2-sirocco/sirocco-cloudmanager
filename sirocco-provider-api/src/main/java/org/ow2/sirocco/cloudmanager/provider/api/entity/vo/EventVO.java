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

package org.ow2.sirocco.cloudmanager.provider.api.entity.vo;

import java.io.Serializable;
import java.util.Date;

import org.ow2.sirocco.cloudmanager.provider.api.entity.Event.Level;

@SuppressWarnings("serial")
public class EventVO implements Serializable {

    private String type;

    private String description;

    private String detail;

    private String objectId;

    private String projectId;

    private Level level;

    private Date time;

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(final String detail) {
        this.detail = detail;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(final Level level) {
        this.level = level;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(final Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "EventVO [type=" + this.type + ", description=" + this.description + ", detail=" + this.detail + ", objectId="
            + this.objectId + ", projectId=" + this.projectId + ", level=" + this.level + ", time=" + this.time + "]";
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return this.projectId;
    }

}
