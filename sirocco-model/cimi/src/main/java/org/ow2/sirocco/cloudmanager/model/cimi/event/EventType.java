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
 *  $Id: $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi.event;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;

// TODO No inheritance for embeddable 
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class EventType implements Serializable {
    private static final long serialVersionUID = 1L;

    // TODO what is this?
    private String          resName;
    private CloudResource   resource;
    private String          detail;
    
    public String getResName() {
        return resName;
    }
    public void setResName(String resName) {
        this.resName = resName;
    }
    
    @OneToOne
    public CloudResource getResource() {
        return resource;
    }
    
    public void setResource(CloudResource resource) {
        this.resource = resource;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    
}