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

package org.ow2.sirocco.cloudmanager.provider.api.entity.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.ow2.sirocco.cloudmanager.provider.api.entity.Project;

/**
 * Business object representing a project
 */
public class ProjectVO implements Serializable {
    private static final long serialVersionUID = 7820619916406005211L;

    private String projectId;

    private String name;

    private String description;

    private ResourceQuotaVO quotaVo;

    private Date createDate;

    private String owner;
    
    private List<UserVO> userVoList;


    public ProjectVO() {
    }


    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }



    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setQuotaVo(final ResourceQuotaVO quotaVo) {
        this.quotaVo = quotaVo;
    }

    public ResourceQuotaVO getQuotaVo() {
        return this.quotaVo;
    }
    
    public List<UserVO> getUserVoList() {
		return userVoList;
	}
    
    public void setUserVoList(List<UserVO> userVoList) {
		this.userVoList = userVoList;
	}

}
