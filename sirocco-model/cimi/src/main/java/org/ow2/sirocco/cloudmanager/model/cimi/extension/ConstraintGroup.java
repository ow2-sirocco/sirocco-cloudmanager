/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi.extension;

import javax.persistence.Entity;

import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;

/**
 * Represents a placement group where resources are placed according to some
 * placement constraints
 */
@Entity
public class ConstraintGroup extends CloudEntity {
    private static final long serialVersionUID = 1L;

    private String attribute;

    private String operator;

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(final String operator) {
        this.operator = operator;
    }
}
