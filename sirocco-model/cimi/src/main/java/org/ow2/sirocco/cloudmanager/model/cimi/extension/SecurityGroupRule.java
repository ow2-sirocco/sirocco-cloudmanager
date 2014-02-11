/**
 *
 * SIROCCO
 * Copyright (C) 2014 France Telecom
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
 */
package org.ow2.sirocco.cloudmanager.model.cimi.extension;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;

@Entity
@NamedQueries({
    @NamedQuery(name = "SecurityGroupRule.findByUuid", query = "SELECT s from SecurityGroupRule s WHERE s.uuid=:uuid"),
    @NamedQuery(name = "SecurityGroupRule.findUsingGroup", query = "SELECT s from SecurityGroupRule s WHERE s.sourceGroup=:source")})
public class SecurityGroupRule implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String uuid;

    private String providerAssignedId;

    private SecurityGroup parentGroup;

    private String ipProtocol;

    private Integer fromPort;

    private Integer toPort;

    private String sourceIpRange;

    private SecurityGroup sourceGroup;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    @PrePersist
    public void generateUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getProviderAssignedId() {
        return this.providerAssignedId;
    }

    public void setProviderAssignedId(final String providerAssignedId) {
        this.providerAssignedId = providerAssignedId;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @ManyToOne
    public SecurityGroup getParentGroup() {
        return this.parentGroup;
    }

    public void setParentGroup(final SecurityGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public String getIpProtocol() {
        return this.ipProtocol;
    }

    public void setIpProtocol(final String ipProtocol) {
        this.ipProtocol = ipProtocol;
    }

    public Integer getFromPort() {
        return this.fromPort;
    }

    public void setFromPort(final Integer fromPort) {
        this.fromPort = fromPort;
    }

    public Integer getToPort() {
        return this.toPort;
    }

    public void setToPort(final Integer toPort) {
        this.toPort = toPort;
    }

    public String getSourceIpRange() {
        return this.sourceIpRange;
    }

    public void setSourceIpRange(final String sourceIpRange) {
        this.sourceIpRange = sourceIpRange;
    }

    @ManyToOne
    public SecurityGroup getSourceGroup() {
        return this.sourceGroup;
    }

    public void setSourceGroup(final SecurityGroup sourceGroup) {
        this.sourceGroup = sourceGroup;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.fromPort == null) ? 0 : this.fromPort.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.ipProtocol == null) ? 0 : this.ipProtocol.hashCode());
        result = prime * result + ((this.parentGroup == null) ? 0 : this.parentGroup.hashCode());
        result = prime * result + ((this.providerAssignedId == null) ? 0 : this.providerAssignedId.hashCode());
        result = prime * result + ((this.sourceGroup == null) ? 0 : this.sourceGroup.hashCode());
        result = prime * result + ((this.sourceIpRange == null) ? 0 : this.sourceIpRange.hashCode());
        result = prime * result + ((this.toPort == null) ? 0 : this.toPort.hashCode());
        result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
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
        SecurityGroupRule other = (SecurityGroupRule) obj;
        if (this.fromPort == null) {
            if (other.fromPort != null) {
                return false;
            }
        } else if (!this.fromPort.equals(other.fromPort)) {
            return false;
        }
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.ipProtocol == null) {
            if (other.ipProtocol != null) {
                return false;
            }
        } else if (!this.ipProtocol.equals(other.ipProtocol)) {
            return false;
        }
        if (this.parentGroup == null) {
            if (other.parentGroup != null) {
                return false;
            }
        } else if (!this.parentGroup.equals(other.parentGroup)) {
            return false;
        }
        if (this.providerAssignedId == null) {
            if (other.providerAssignedId != null) {
                return false;
            }
        } else if (!this.providerAssignedId.equals(other.providerAssignedId)) {
            return false;
        }
        if (this.sourceGroup == null) {
            if (other.sourceGroup != null) {
                return false;
            }
        } else if (!this.sourceGroup.equals(other.sourceGroup)) {
            return false;
        }
        if (this.sourceIpRange == null) {
            if (other.sourceIpRange != null) {
                return false;
            }
        } else if (!this.sourceIpRange.equals(other.sourceIpRange)) {
            return false;
        }
        if (this.toPort == null) {
            if (other.toPort != null) {
                return false;
            }
        } else if (!this.toPort.equals(other.toPort)) {
            return false;
        }
        if (this.uuid == null) {
            if (other.uuid != null) {
                return false;
            }
        } else if (!this.uuid.equals(other.uuid)) {
            return false;
        }
        return true;
    }

}