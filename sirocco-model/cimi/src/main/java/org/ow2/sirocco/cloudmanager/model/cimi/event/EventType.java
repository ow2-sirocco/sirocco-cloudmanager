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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class EventType implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Integer id;

    private String resName;

    private String resType;

    private CloudResource resource;

    private String detail;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getResName() {
        return this.resName;
    }

    public void setResName(final String resName) {
        this.resName = resName;
    }

    @OneToOne
    public CloudResource getResource() {
        return this.resource;
    }

    public void setResource(final CloudResource resource) {
        this.resource = resource;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(final String detail) {
        this.detail = detail;
    }

    public String getResType() {
        return this.resType;
    }

    public void setResType(final String resType) {
        this.resType = resType;
    }

}
