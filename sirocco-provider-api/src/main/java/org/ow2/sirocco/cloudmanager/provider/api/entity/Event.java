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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.EventVO;

@NamedQueries(value = {
    @NamedQuery(name = "FIND_EVENTS", query = "SELECT e " + Event.IEventManagerQueries.FIND_EVENTS_QUERY),
    @NamedQuery(name = "COUNT_EVENTS", query = "SELECT COUNT(e) " + Event.IEventManagerQueries.FIND_EVENTS_QUERY),
    @NamedQuery(name = "FIND_EVENTS_BY_PROJECT_ID", query = "SELECT e "
        + Event.IEventManagerQueries.FIND_EVENTS_VO_BY_PROJECT_QUERY),
    @NamedQuery(name = "COUNT_EVENTS_BY_PROJECT_ID", query = "SELECT COUNT(e) "
        + Event.IEventManagerQueries.FIND_EVENTS_VO_BY_PROJECT_QUERY)})
@Entity
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String FIND_EVENTS = "FIND_EVENTS";

    public static final String COUNT_EVENTS = "COUNT_EVENTS";

    public static final String FIND_EVENTS_BY_PROJECT_ID = "FIND_EVENTS_BY_PROJECT_ID";

    public static final String COUNT_EVENTS_BY_PROJECT_ID = "COUNT_EVENTS_BY_PROJECT_ID";

    public static enum Level {
        INFO, WARNING, ERROR
    }

    public interface IEventManagerQueries {
        String FIND_EVENTS_QUERY = "FROM Event e WHERE e.time BETWEEN :startTime AND :endTime ORDER BY time DESC";

        String FIND_EVENTS_VO_BY_PROJECT_QUERY = "FROM Event e WHERE e.projectId=:projectId AND e.time BETWEEN :startTime AND :endTime ORDER BY time DESC";
    }

    public interface ITypes {
        String VM_CREATION = "VM.CREATION";

        String VM_DELETION = "VM.DELETION";

        String VM_START = "VM.START";

        String VM_STOP = "VM.STOP";

        String VM_PAUSE = "VM.PAUSE";

        String VM_UNPAUSE = "VM.UNPAUSE";

        String VM_REBOOT = "VM.REBOOT";

        String VM_ERROR = "VM.ERROR";
    }

    private int id;

    private String type;

    private String description;

    @Column(length = 80)
    private String detail;

    private String objectId;

    private String projectId;

    private Level level;

    private Date time;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return this.id;
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

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    @Enumerated(EnumType.STRING)
    public Level getLevel() {
        return this.level;
    }

    public void setLevel(final Level level) {
        this.level = level;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getTime() {
        return this.time;
    }

    public void setTime(final Date time) {
        this.time = time;
    }

    public void setId(final int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Event [type=" + this.type + ", description=" + this.description + ", detail=" + this.detail + ", objectId="
            + this.objectId + ", projectId=" + this.projectId + ", level=" + this.level + ", time=" + this.time + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = prime * result + ((this.detail == null) ? 0 : this.detail.hashCode());
        result = prime * result + this.id;
        result = prime * result + ((this.level == null) ? 0 : this.level.hashCode());
        result = prime * result + ((this.objectId == null) ? 0 : this.objectId.hashCode());
        result = prime * result + ((this.projectId == null) ? 0 : this.projectId.hashCode());
        result = prime * result + ((this.time == null) ? 0 : this.time.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
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
        Event other = (Event) obj;
        if (this.description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!this.description.equals(other.description)) {
            return false;
        }
        if (this.detail == null) {
            if (other.detail != null) {
                return false;
            }
        } else if (!this.detail.equals(other.detail)) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        if (this.level != other.level) {
            return false;
        }
        if (this.objectId == null) {
            if (other.objectId != null) {
                return false;
            }
        } else if (!this.objectId.equals(other.objectId)) {
            return false;
        }
        if (this.projectId == null) {
            if (other.projectId != null) {
                return false;
            }
        } else if (!this.projectId.equals(other.projectId)) {
            return false;
        }
        if (this.time == null) {
            if (other.time != null) {
                return false;
            }
        } else if (!this.time.equals(other.time)) {
            return false;
        }
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!this.type.equals(other.type)) {
            return false;
        }
        return true;
    }

    public EventVO toValueObject() {
        EventVO event = new EventVO();
        event.setType(this.getType());
        event.setDescription(this.getDescription());
        event.setDetail(this.getDetail());
        event.setObjectId(this.getObjectId());
        event.setLevel(this.getLevel());
        event.setTime(this.getTime());
        event.setProjectId(this.getProjectId());
        return event;
    }

}
