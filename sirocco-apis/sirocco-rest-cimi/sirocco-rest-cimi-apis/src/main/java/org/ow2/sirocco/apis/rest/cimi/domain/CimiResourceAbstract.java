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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Abstract class of an identified resource exchanged with the server.
 */
@XmlTransient
public abstract class CimiResourceAbstract implements CimiResource {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** Identifier */
    private String id;

    /** Type URI for the resource */
    private String resourceURI;

    /** The refernce */
    private String href;

    /** All the operations of the resource */
    private CimiOperationArray operations;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#getId()
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setId(java.lang.String)
     */
    @Override
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#getHref()
     */
    @Override
    @XmlAttribute
    public String getHref() {
        return this.href;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setHref(java.lang.String)
     */
    @Override
    public void setHref(final String href) {
        this.href = href;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#getResourceURI()
     */
    @Override
    @XmlAttribute
    public String getResourceURI() {
        return this.resourceURI;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setResourceURI(java.lang.String)
     */
    @Override
    public void setResourceURI(final String resourceURI) {
        this.resourceURI = resourceURI;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#hasReference()
     */
    @Override
    public boolean hasReference() {
        return (null != this.href);
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
        CimiOperation[] items = null;
        if (null != this.operations) {
            items = this.operations.getArray();
        }
        return items;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setOperations(org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation[])
     */
    @Override
    public void setOperations(final CimiOperation[] operations) {
        if (null == operations) {
            this.operations = null;
        } else {
            this.operations = new CimiOperationArray();
            this.operations.setArray(operations);
        }
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
        return this.operations;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#setListOperations(java.util.List)
     */
    @Override
    public void setListOperations(final List<CimiOperation> operations) {
        this.operations = new CimiOperationArray();
        this.operations.addAll(operations);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#add(org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation)
     */
    @Override
    public void add(final CimiOperation operation) {
        if (null == this.getListOperations()) {
            this.operations = new CimiOperationArray();
        }
        this.operations.add(operation);
    }

    /**
     * Concrete class of the collection.
     */
    public class CimiOperationArray extends CimiArrayAbstract<CimiOperation> {

        /** Serial number */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         * 
         * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiArray#newEmptyArraySized()
         */
        @Override
        public CimiOperation[] newEmptyArraySized() {
            return new CimiOperation[this.size()];
        }

    }
}
