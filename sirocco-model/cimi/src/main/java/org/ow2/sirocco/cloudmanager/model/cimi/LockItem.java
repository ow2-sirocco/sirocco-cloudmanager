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

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"lockedObjectId", "lockedObjectType"})})
public class LockItem implements Serializable {
    private static final long serialVersionUID = 1L;

    
    private Integer id;
    
    /**
     * id of locked object
     */
    private String lockedObjectId;
    
    /**
     * type of locked object. the {lockedObjectType,lockedObjectId} couple is unique
     */
    private String lockedObjectType;

    /**
     * lockedTime is used to know when a lock was set, and then to be able to unlock after some time (configurable) to prevent objects to be locked forever 
     */
    private Date lockedTime;
    
    protected long versionNum;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    @Version
    @Column(name="OPTLOCK")    
    protected long getVersionNum() {
        return versionNum;
    }

    protected void setVersionNum(long versionNum) {
        this.versionNum = versionNum;
    }

    public LockItem() {
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(Date lockedTime) {
        this.lockedTime = lockedTime;
    }

    public String getLockedObjectId() {
        return lockedObjectId;
    }

    public void setLockedObjectId(String lockedObjectId) {
        this.lockedObjectId = lockedObjectId;
    }

    public String getLockedObjectType() {
        return lockedObjectType;
    }

    public void setLockedObjectType(String lockedObjectType) {
        this.lockedObjectType = lockedObjectType;
    }

}
