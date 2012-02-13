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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CollectionOfElements;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VirtualMachineVO;

@NamedQueries(value = {@NamedQuery(name = "GET_MACHINE_BY_JOB_ID", query = "SELECT v FROM Machine v WHERE v.activeJob=:activeJob")})
@Entity
public class Machine extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String GET_MACHINE_BY_JOB_ID = "GET_MACHINE_BY_JOB_ID";

    public static enum State {
        CREATING, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, DELETING, DELETED, ERROR
    }

    private String providerAssignedId;

    private CloudProviderAccount cloudProviderAccount;

    private State state;

    private Long memoryInMB;

    // TODO support more than one disk
    private Long diskCapacityInMB;

    private Integer numberOfCpus;

    private Collection<Volume> volumes;

    private List<NetworkInterface> networkInterfaces;

    private MachineImage image;

    private String location;

    private String activeJob;

    private Date expirationDate;

    private Integer reservationId;

    public Machine() {
        this.volumes = new ArrayList<Volume>();
        this.networkInterfaces = new ArrayList<NetworkInterface>();
    }

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    public MachineImage getImage() {
        return this.image;
    }

    public long getMemoryInMB() {
        return this.memoryInMB;
    }

    /**
     * @return the id of the Vm that has been attributed by the CloudProvider.
     */
    @Column(unique = true)
    public String getProviderAssignedId() {
        return this.providerAssignedId;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public void setDiskCapacityInMB(final Long diskCapacityInMB) {
        this.diskCapacityInMB = diskCapacityInMB;
    }

    public void setMemoryInMB(final Long memoryInMB) {
        this.memoryInMB = memoryInMB;
    }

    public void setProviderAssignedId(final String providerAssignedId) {
        this.providerAssignedId = providerAssignedId;
    }

    public void setImage(final MachineImage vmImage) {
        this.image = vmImage;
    }

    public int getNumberOfCpus() {
        return this.numberOfCpus;
    }

    public void setNumberOfCpus(final Integer numberOfCpus) {
        this.numberOfCpus = numberOfCpus;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public Long getDiskCapacityInMB() {
        return this.diskCapacityInMB;
    }

    public void setExpirationDate(final Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getExpirationDate() {
        return this.expirationDate;
    }

    public void setReservationId(final Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getReservationId() {
        return this.reservationId;
    }

    @ManyToMany
    @JoinTable(name = "MACHINE_VOLUME")
    public Collection<Volume> getVolumes() {
        return this.volumes;
    }

    public void setVolumes(final Collection<Volume> volumes) {
        this.volumes = volumes;
    }

    @ManyToOne
    // @JoinColumn(name = "CloudProviderAccount_id", nullable = false)
    @JoinColumn(name = "CloudProviderAccount_id")
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    public String getActiveJob() {
        return this.activeJob;
    }

    public void setActiveJob(final String activeJob) {
        this.activeJob = activeJob;
    }

    @CollectionOfElements
    public List<NetworkInterface> getNetworkInterfaces() {
        return this.networkInterfaces;
    }

    public void setNetworkInterfaces(final List<NetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    @Override
    public String toString() {
        String s = this.getClass().getSimpleName() + "=[id=" + this.getId() + ", state=" + this.getState() + ", name="
            + this.getName() + ", providerId=" + this.getProviderAssignedId() + "]";
        return s;
    }

    /**
     * Creates a new VirtualMachineVO (value) object representing the Machine
     * business object.
     * 
     * @return a new VirtualMachine value object
     */
    public VirtualMachineVO toValueObject() {
        VirtualMachineVO vmVo = new VirtualMachineVO();
        vmVo.setId(this.getId().toString());
        vmVo.setName(this.getName());
        vmVo.setProviderId(this.getProviderAssignedId());
        vmVo.setUsername(this.getUser().getUsername());
        vmVo.setUserPublicKey(this.getUser().getPublicKey());
        vmVo.setUserEMail(this.getUser().getEmail());
        vmVo.setStorageMB(this.getDiskCapacityInMB());
        vmVo.setMemoryMB(this.getMemoryInMB());
        vmVo.setNumCpus(this.getNumberOfCpus());
        if (!this.networkInterfaces.isEmpty()) {
            Iterator<NetworkInterface> it = this.networkInterfaces.iterator();
            NetworkInterface nic = it.next();
            vmVo.setPrivateIp(nic.getAddress());
            vmVo.setPrivateDnsName(nic.getHostname());
            if (it.hasNext()) {
                nic = it.next();
                vmVo.setPublicIp(nic.getAddress());
                vmVo.setPublicDnsName(nic.getHostname());
            }
        }
        vmVo.setStatus(this.getState());
        vmVo.setImageName(this.getImage().getName());
        vmVo.setImageVmiId(this.getImage().getId());
        vmVo.setImageOsType(this.getImage().getOsType());
        vmVo.setCreationDate(this.getCreated());
        vmVo.setExpirationDate(this.getExpirationDate());
        vmVo.setDeleteDate(this.getDeleted());
        vmVo.setLocation(this.getLocation());
        vmVo.setHostId(null);
        vmVo.setProjectId(this.getProject().getProjectId());
        vmVo.setProjectName(this.getProject().getName());
        vmVo.setAdditionalProperties(new HashMap<String, String>());
        if (this.getProperties() != null) {
            vmVo.getAdditionalProperties().putAll(this.getProperties());
        }
        vmVo.setReservationId(this.getReservationId());

        if (this.getCloudProviderAccount() == null) {
            vmVo.setCloudProviderAccountId(null);
        } else {
            vmVo.setCloudProviderAccountId(this.getCloudProviderAccount().getId().toString());
        }

        return vmVo;
    }
}
