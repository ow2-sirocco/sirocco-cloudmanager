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

import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreateByRefOrByValue;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.AssertEntityBy;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.AssertReferencePath;

/**
 * Interface of an identified resource exchanged with the server.
 */
@AssertEntityBy(groups = GroupCreateByRefOrByValue.class)
@AssertReferencePath(groups = GroupWrite.class)
public interface CimiResource extends CimiData {

    /**
     * Get the identifier of resource.
     * 
     * @return The identifier
     */
    String getId();

    /**
     * Set the identifier of resource.
     * 
     * @param id The identifier
     */
    void setId(final String id);

    /**
     * Get the reference of resource.
     * 
     * @return The reference
     */
    String getHref();

    /**
     * Set the reference of resource.
     * 
     * @param href The reference
     */
    void setHref(final String href);

    /**
     * Get the URI of resource.
     * 
     * @return
     */
    String getResourceURI();

    /**
     * Set the URI of resource.
     * 
     * @param resourceURI
     */
    void setResourceURI(final String resourceURI);

    /**
     * Flag indicating whether the contents of the resource has a reference.
     * 
     * @return True if the resource has a reference
     */
    boolean hasReference();

    /**
     * Flag indicating whether the contents of the resource has values.
     * 
     * @return True if the resource has values
     */
    boolean hasValues();

    /**
     * Get the operations of resource.
     * 
     * @return The aray of operations
     */
    CimiOperation[] getOperations();

    /**
     * Set the operations of resource.
     * 
     * @param operations A array of operations
     */
    void setOperations(CimiOperation[] operations);

    /**
     * Get the operations of resource.
     * 
     * @return The list of operations
     */
    List<CimiOperation> getListOperations();

    /**
     * Set the operations of resource.
     * 
     * @param operations A list of operations
     */
    void setListOperations(List<CimiOperation> operations);

    /**
     * Add a operation.
     * 
     * @param operation The operation to add
     */
    void add(CimiOperation operation);
}
