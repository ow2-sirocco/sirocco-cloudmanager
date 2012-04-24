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
 *  $Id: System.java 788 2012-04-17 11:49:55Z ycas7461 $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class System extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum State {
        CREATING, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, MIXED, DELETING, ERROR
    }
    
    private CredentialsCollection credentialColl;

    private MachineCollection machineColl;

    private SystemCollection systemColl;

    private State state;

    private VolumeCollection volumeColl;
    
    public System() {
    }

    public VolumeCollection getVolumeColl() {
        return volumeColl;
    }

    public void setVolumeColl(VolumeCollection volumeColl) {
        this.volumeColl = volumeColl;
    }

    public CredentialsCollection getCredentialColl() {
        return credentialColl;
    }

    public void setCredentialColl(CredentialsCollection credentialColl) {
        this.credentialColl = credentialColl;
    }

    public MachineCollection getMachineColl() {
        return machineColl;
    }

    public void setMachineColl(MachineCollection machineColl) {
        this.machineColl = machineColl;
    }

    @ManyToOne
    public SystemCollection getSystemColl() {
        return systemColl;
    }

    public void setSystemColl(SystemCollection systemColl) {
        this.systemColl = systemColl;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
