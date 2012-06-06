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

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ow2.sirocco.apis.rest.cimi.utils.CimiDateAdapter;

/**
 * Abstract class of a CIMI resource object.
 */
public class CimiObjectCommonImpl extends CimiCommon implements CimiObjectCommon {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * The wrapped resource.
     */
    private CimiResourceWrapped resource;

    /**
     * Field "created".
     * <p>
     * The timestamp when this entity was created. The format is DateTimeUTC
     * (ISO 8601), example : 2012-02-06T08:39:57Z.
     * </p>
     */
    private Date created;

    /**
     * Field "updated".
     * <p>
     * The time at which the last explicit attribute update was made on the
     * resoure. Note, while operations such as 'stop' do implicitly modify the
     * 'state' attribute it does not change the 'updated_time'. The format is
     * DateTimeUTC (ISO 8601), example : 2012-02-06T08:39:57Z.
     * </p>
     */
    private Date updated;

    /**
     * Default constructor.
     */
    public CimiObjectCommonImpl() {
        super();
        this.resource = new CimiResourceWrapped();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiObjectCommonImpl(final String href) {
        this();
        this.setHref(href);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#getId()
     */
    @Override
    public String getId() {
        return this.resource.getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setId(java.lang.String)
     */
    @Override
    public void setId(final String id) {
        this.resource.setId(id);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon#getCreated()
     */
    @Override
    @XmlJavaTypeAdapter(CimiDateAdapter.class)
    public Date getCreated() {
        return this.created;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon#setCreated(java
     *      .util.Date)
     */
    @Override
    public void setCreated(final Date created) {
        this.created = created;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon#getUpdated()
     */
    @Override
    @XmlJavaTypeAdapter(CimiDateAdapter.class)
    public Date getUpdated() {
        return this.updated;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon#setUpdated(java
     *      .util.Date)
     */
    @Override
    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#getResourceURI()
     */
    @Override
    public String getResourceURI() {
        return this.resource.getResourceURI();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setResourceURI(java
     *      .lang.String)
     */
    @Override
    public void setResourceURI(final String resourceURI) {
        this.resource.setResourceURI(resourceURI);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#getHref()
     */
    @Override
    @XmlAttribute
    public String getHref() {
        return this.resource.getHref();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setHref(java.lang.
     *      String)
     */
    @Override
    public void setHref(final String href) {
        this.resource.setHref(href);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#hasReference()
     */
    @Override
    public boolean hasReference() {
        return this.resource.hasReference();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = false;
        has = has || (null != this.getDescription());
        has = has || (null != this.getName());
        has = has || (null != this.getProperties());
        return has;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#getOperations()
     */
    @Override
    @XmlElement(name = "operation")
    @JsonProperty(value = "operations")
    public CimiOperation[] getOperations() {
        return this.resource.getOperations();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setOperations(org.
     *      ow2.sirocco.apis.rest.cimi.domain.CimiOperation[])
     */
    @Override
    public void setOperations(final CimiOperation[] operations) {
        this.resource.setOperations(operations);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#getListOperations()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public List<CimiOperation> getListOperations() {
        return this.resource.getListOperations();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setListOperations(java.util.List)
     */
    @Override
    public void setListOperations(final List<CimiOperation> operations) {
        this.resource.setListOperations(operations);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#add(org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation)
     */
    @Override
    public void add(final CimiOperation operation) {
        this.resource.add(operation);
    }

    /**
     * The wrapped ressource.
     */
    public class CimiResourceWrapped extends CimiResourceAbstract {

        /** Serial number */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         * 
         * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#hasValues()
         */
        @Override
        public boolean hasValues() {
            return false;
        }

    }
}
