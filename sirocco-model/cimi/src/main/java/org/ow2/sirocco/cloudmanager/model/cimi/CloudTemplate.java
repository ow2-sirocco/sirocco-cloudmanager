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
 *  $Id: CloudResource.java 1258 2012-05-21 12:35:04Z ycas7461S $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.Visibility;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CloudTemplate extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Boolean isEmbeddedInSystemTemplate = false;

    private Visibility visibility = Visibility.PRIVATE;

    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

    public Boolean getIsEmbeddedInSystemTemplate() {
        return this.isEmbeddedInSystemTemplate;
    }

    public void setIsEmbeddedInSystemTemplate(final Boolean isEmbeddedInSystemTemplate) {
        this.isEmbeddedInSystemTemplate = isEmbeddedInSystemTemplate;
    }

}
