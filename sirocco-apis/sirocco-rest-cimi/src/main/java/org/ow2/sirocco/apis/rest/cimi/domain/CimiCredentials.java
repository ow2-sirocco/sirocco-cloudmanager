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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.domain;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreateByValue;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.NotEmptyIfNotNull;

/**
 * Class Credentials.
 */
@XmlRootElement(name = "Credentials")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiCredentials extends CimiCommonId {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** The initial superuser's user name. */
    @NotNull(groups = GroupCreateByValue.class)
    private String userName;

    /** Initial superuser's password. */
    @NotNull(groups = GroupCreateByValue.class)
    private String password;

    /** The digit of the public key for the initial superuser. */
    @NotNull(groups = GroupCreateByValue.class)
    @NotEmptyIfNotNull(groups = GroupWrite.class)
    private byte[] key;

    /**
     * Default constructor.
     */
    public CimiCredentials() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiCredentials(final String href) {
        super(href);
    }

    /**
     * Parameterized constructor.
     * 
     * @param userName The login
     * @param password The password
     * @param key The public key
     */
    public CimiCredentials(final String userName, final String password, final byte[] key) {
        this.userName = userName;
        this.password = password;
        this.key = key;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(final String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Return the value of field "key".
     * 
     * @return The value
     */
    @XmlElement
    public byte[] getKey() {
        return this.key;
    }

    /**
     * Set the value of field "key".
     * 
     * @param key The value
     */
    public void setKey(final byte[] key) {
        this.key = key;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCommonId#hasValues
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getKey());
        has = has || (null != this.getPassword());
        has = has || (null != this.getUserName());
        return has;
    }

}
