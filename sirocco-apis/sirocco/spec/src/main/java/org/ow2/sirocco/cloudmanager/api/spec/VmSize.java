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

package org.ow2.sirocco.cloudmanager.api.spec;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "", propOrder = {"name", "numCpu", "memorySizeMB"})
@XmlRootElement(name = "VmSize")
public class VmSize {
    private String name;

    private int numCpu;

    private long memorySizeMB;

    public VmSize() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getNumCpu() {
        return this.numCpu;
    }

    public void setNumCpu(final int numCpu) {
        this.numCpu = numCpu;
    }

    public long getMemorySizeMB() {
        return this.memorySizeMB;
    }

    public void setMemorySizeMB(final long memorySizeMB) {
        this.memorySizeMB = memorySizeMB;
    }

    @Override
    public String toString() {
        return "VmSize [name=" + this.name + ", numCpu=" + this.numCpu + ", memorySizeMB=" + this.memorySizeMB + "]";
    }

}
