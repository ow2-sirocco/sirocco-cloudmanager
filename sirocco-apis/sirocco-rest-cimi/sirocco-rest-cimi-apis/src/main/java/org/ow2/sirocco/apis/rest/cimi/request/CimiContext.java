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
package org.ow2.sirocco.apis.rest.cimi.request;

import java.io.Serializable;

import org.ow2.sirocco.apis.rest.cimi.configuration.ConfigFactory;
import org.ow2.sirocco.apis.rest.cimi.configuration.ConfigurationException;
import org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Resource;

/**
 * The context used by a REST request during his processing.
 */
public interface CimiContext extends Serializable {

    /**
     * Get the current request.
     * 
     * @return The current request
     */
    CimiRequest getRequest();

    /**
     * Get the current response.
     * 
     * @return The current response
     */
    CimiResponse getResponse();

    /**
     * Get the converter for a CIMI class.
     * 
     * @param klass The class to convert
     * @return The converter
     */
    CimiConverter getConverter(Class<?> klass);

    /**
     * Convert a service root object to a CIMI root object.
     * <p>
     * Initialize the conversion stack.
     * </p>
     * 
     * @param service The service instance to convert
     * @param cimiAssociate The CIMI class associate to the service class
     * @return A CIMI instance converted
     */
    Object convertToCimi(Object service, Class<?> cimiAssociate);

    /**
     * Convert a service root object to a CIMI root object.
     * <p>
     * Before the convert, finds the CIMI Resource class associate to the Cloud
     * Resource class of the given instance. The association is defined by the
     * configuration.
     * </p>
     * 
     * @param service The service instance to convert
     * @return A CIMI instance converted or null if service is null
     * @see CimiContext#findAssociate(Class)
     */
    Object convertToCimi(Resource service);

    /**
     * Convert a service child object to a CIMI child object.
     * <p>
     * Before the convert, finds the CIMI Resource class associate to the Cloud
     * Resource class of the given instance. The association is defined by the
     * configuration.
     * </p>
     * 
     * @param service The service instance to convert
     * @return A CIMI instance converted or null if service is null
     * @see CimiContext#findAssociate(Class)
     */
    Object convertNextCimi(Resource service);

    /**
     * Convert a service child object to a CIMI child object.
     * 
     * @param service The service instance to convert
     * @param cimiAssociate The CIMI class associate to the service class
     * @return A CIMI instance converted
     */
    Object convertNextCimi(Object service, Class<?> cimiAssociate);

    /**
     * Convert a CIMI root object to a service root object.
     * <p>
     * Initialize the conversion stack.
     * </p>
     * 
     * @param cimi The CIMI instance to convert
     * @return A service instance converted
     */
    Object convertToService(Object cimi);

    /**
     * Convert a CIMI child object to a service child object.
     * 
     * @param cimi The CIMI instance to convert
     * @return A service instance converted
     */
    Object convertNextService(Object cimi);

    /**
     * Convert a CIMI child object to a service child object.
     * 
     * @param cimi The CIMI instance to convert
     * @param cimiToUse The CIMI class to use
     * @return A service instance converted
     */
    Object convertNextService(Object cimi, Class<?> cimiToUse);

    /**
     * Returns true if the given resource must be expanded.
     * 
     * @param resource A instance of a CIMI resource
     * @return True if must be expanded.
     */
    boolean mustBeExpanded(CimiResource resource);

    /**
     * Returns true if the given resource must be referenced.
     * 
     * @param resource A instance of a CIMI resource
     * @return True if must be referenced.
     */
    boolean mustBeReferenced(CimiResource resource);

    /**
     * Make the base HREF without ID.
     * 
     * @param data
     * @return
     */
    String makeHrefBase(CimiResource data);

    /**
     * Make a HREF for a resource.
     * 
     * @param resource The instance of the resource
     * @param id The ID of the current service object
     * @return The HREF made
     */
    String makeHref(CimiResource resource, String id);

    /**
     * Make a HREF for a resource.
     * 
     * @param resource The class of the resource
     * @param @param ids All ID necessary : the first is a ID parent, the last
     *        is current ID
     * @return The HREF made
     */
    public String makeHref(Class<? extends CimiResource> classToUse, String id);

    /**
     * Returns true if the converters must process the write-only data when
     * convert service data to CIMI data.
     * 
     * @return True if must convert the write-only data.
     */
    boolean isConvertedWriteOnly();

    /**
     * Set the indicator to process the conversion of write-only data when
     * convert service data to CIMI data.
     * 
     * @param convertedWriteOnly True to convert the write-only data
     */
    void setConvertedWriteOnly(boolean convertedWriteOnly);

    /**
     * Find a CIMI class associate to a service class.
     * 
     * @param service The service class
     * @return The CIMI class found
     * @throws ConfigurationException If associate CIMI class not found
     * @see ConfigFactory
     */
    Class<? extends CimiResource> findAssociate(Class<? extends Resource> service);

}