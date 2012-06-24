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

import javax.persistence.Embeddable;

@Embeddable
public class EventLogSummary implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer low;
    private Integer medium;
    private Integer high;
    private Integer critical;
    public Integer getLow() {
        return low;
    }
    public void setLow(Integer low) {
        this.low = low;
    }
    public Integer getMedium() {
        return medium;
    }
    public void setMedium(Integer medium) {
        this.medium = medium;
    }
    public Integer getHigh() {
        return high;
    }
    public void setHigh(Integer high) {
        this.high = high;
    }
    public Integer getCritical() {
        return critical;
    }
    public void setCritical(Integer critical) {
        this.critical = critical;
    }
    
    
    
}
