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

public class SecurityGroupRuleParams {
    private String ipProtocol;

    private Integer fromPort;

    private Integer toPort;

    private String sourceIpRange;

    private String sourceGroupUuid;

    public String getIpProtocol() {
        return this.ipProtocol;
    }

    public SecurityGroupRuleParams setIpProtocol(final String ipProtocol) {
        this.ipProtocol = ipProtocol;
        return this;
    }

    public Integer getFromPort() {
        return this.fromPort;
    }

    public SecurityGroupRuleParams setFromPort(final Integer fromPort) {
        this.fromPort = fromPort;
        return this;
    }

    public Integer getToPort() {
        return this.toPort;
    }

    public SecurityGroupRuleParams setToPort(final Integer toPort) {
        this.toPort = toPort;
        return this;
    }

    public String getSourceIpRange() {
        return this.sourceIpRange;
    }

    public SecurityGroupRuleParams setSourceIpRange(final String sourceIpRange) {
        this.sourceIpRange = sourceIpRange;
        return this;
    }

    public String getSourceGroupUuid() {
        return this.sourceGroupUuid;
    }

    public SecurityGroupRuleParams setSourceGroupUuid(final String sourceGroupUuid) {
        this.sourceGroupUuid = sourceGroupUuid;
        return this;
    }

}
