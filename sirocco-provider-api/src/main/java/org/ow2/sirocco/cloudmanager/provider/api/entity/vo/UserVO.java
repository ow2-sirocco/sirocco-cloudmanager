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

/**
 * Business object representing a user account. A User is a client of the cloud
 * service. He/she can access the service either through a Web portal or through
 * an API (with two distinct passwords). A User is the owner of virtual machines
 * and VM images. A virtual machine belongs to one and only one user. A VM image
 * may belong to one user or is public.
 */
public class UserVO implements Serializable {
    private static final long serialVersionUID = 7820619916406005211L;

    private String lastName;

    private String firstName;

    private String username;

    private String email;

    private String publicKey;

    private Date createDate;

    private String password;

    private String apiPassword;

    private boolean isAdmin;

    private ProjectVO defaultProject;

    private String userRightInProjectVo;

    public UserVO() {
    }

    /**
     * Returns the last name of the user
     * 
     * @return the last name of the user
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets the last name of the user
     * 
     * @param lastName the last name of the user
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the first name of the user
     * 
     * @return the first name of the user
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets the first name of the user
     * 
     * @param firstName the first name of the user
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the login (username) of the user
     * 
     * @return the login (username) of the user
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the login (username) of the user
     * 
     * @param username the login (username) of the user
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Returns the email of the user
     * 
     * @return the email of the user
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email of the user
     * 
     * @param email the email of the user
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Returns the SSH public key of the user
     * 
     * @return the SSH public key of the user
     */
    public String getPublicKey() {
        return this.publicKey;
    }

    /**
     * Sets the SSH public key of the user
     * 
     * @param publicKey the SSH public key of the user
     */
    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Returns the creation date of the user account
     * 
     * @return the creation date of the user account
     */
    public Date getCreateDate() {
        return this.createDate;
    }

    /**
     * Sets the creation date of the user account
     * 
     * @param createDate the creation date of the user account
     */
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Returns the password of the user. This password is used to access the Web
     * portal of the Cloud service. Note that if this password is unset, the
     * cloud service will attempt to authenticate the user by accessing a LDAP
     * server.
     * 
     * @return the password of the user required to access the Web portal
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password of the user. This password is used to access the Web
     * portal of the Cloud service. Note that if this password is unset, the
     * cloud service will attempt to authenticate the user by accessing a LDAP
     * server.
     * 
     * @param password the password of the user required to access the Web
     *        portal
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Returns the API password of the user
     * 
     * @return the API password of the user
     */
    public String getApiPassword() {
        return this.apiPassword;
    }

    /**
     * Sets the API password of the user
     * 
     * @param apiPassword the API password of the user
     */
    public void setApiPassword(final String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public boolean getAdmin() {
        return this.isAdmin;
    }

    public void setAdmin(final boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public ProjectVO getDefaultProject() {
        return this.defaultProject;
    }

    public void setDefaultProject(final ProjectVO defaultProject) {
        this.defaultProject = defaultProject;
    }

    public String getUserRightInProjectVo() {
        return this.userRightInProjectVo;
    }

    public void setUserRightInProjectVo(final String userRightInProjectVo) {
        this.userRightInProjectVo = userRightInProjectVo;
    }

}
