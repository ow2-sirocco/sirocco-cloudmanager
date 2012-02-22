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

package org.ow2.sirocco.cloudmanager.core.api;

import java.util.List;
import java.util.Map;

import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplateCollection;

/**
 * Volume management operations
 */
public interface IVolumeManager {
    static final String EJB_JNDI_NAME = "VolumeManagerBean";

    /**
     * Creates a new Volume, this operation is asynchronous
     * 
     * @param volumeCreate contains either a reference to a Volume Template or a
     *        Volume Template itself
     * @return the job representing the asynchronous creation operation
     * @throws CloudProviderException raised if the creation fails
     */
    Job createVolume(VolumeCreate volumeCreate) throws InvalidRequestException, CloudProviderException;

    /**
     * Creates a new Volume Configuration
     * 
     * @param volumeConfig contains initialization values of the Volume
     *        Configuration
     * @return a persisted Volume Configuration instance
     * @throws CloudProviderException raised if the creation fails
     */
    VolumeConfiguration createVolumeConfiguration(VolumeConfiguration volumeConfig) throws InvalidRequestException,
        CloudProviderException;

    /**
     * Creates a new Volume Template
     * 
     * @param volumeTemplate contains initialization values of the Volume
     *        Template
     * @return a persisted Volume Template instance
     * @throws CloudProviderException raised if the creation fails
     */
    VolumeTemplate createVolumeTemplate(VolumeTemplate volumeTemplate) throws InvalidRequestException, CloudProviderException;

    /**
     * Returns the Volume instance with the supplied id
     * 
     * @param volumeId the id of the Volume to retrieve
     * @return if successful, the Volume instance
     * @throws InvalidVolumeIdException raised if the provided id is invalid
     */
    Volume getVolumeById(final String volumeId) throws ResourceNotFoundException;

    /**
     * Returns the VolumeTemplate instance with the supplied id
     * 
     * @param volumeTemplateId the id of the Volume to retrieve
     * @return if successful, the VolumeTemplate instance
     * @throws CloudProviderException raised if the provided id is invalid
     */
    VolumeTemplate getVolumeTemplateById(final String volumeTemplateId) throws CloudProviderException;

    /**
     * Returns the VolumeConfiguration instance with the supplied id
     * 
     * @param volumeConfigId the id of the Volume to retrieve
     * @return if successful, the VolumeConfiguration instance
     * @throws CloudProviderException raised if the provided id is invalid
     */
    VolumeConfiguration getVolumeConfigurationById(final String volumeConfigId) throws CloudProviderException;

    /**
     * Retrieves some attributes of Volume
     * 
     * @param volumeId the id of the Volume
     * @param attributes the list of attributes to retrieve
     * @return a Volume instance whose requested attributes are filled
     * @throws InvalidVolumeIdException raised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    Volume getVolumeAttributes(final String volumeId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    /**
     * Retrieves some attributes of all Volumes belonging to the caller that
     * match a supplied filter expression
     * 
     * @param attributes the list of attributes to retrieve
     * @param filterExpression a filter expression compliant with the DMTF CIMI
     *        syntax, if null no filtering is performed
     * @return a list of Volumes matching the filter expression, for each
     *         Volume, only the requested attributes are guaranteed to be filled
     * @throws CloudProviderException raised if the input parameters are invalid
     */
    List<Volume> getVolumesAttributes(List<String> attributes, String filterExpression) throws InvalidRequestException,
        CloudProviderException;

    /**
     * Retrieves some attributes of all Volumes belonging to the caller within a
     * specific range, only Volumes that match a supplied filter expression are
     * returned
     * 
     * @param first index of the first Volume to return within the Volume
     *        Collection of the caller
     * @param last index of the last Volume to return within the Volume
     *        Collection of the caller
     * @param attributes the list of attributes to retrieve
     * @return a list of Volumes matching the filter expression within the
     *         requested range (if any), for each Volume, only the requested
     *         attributes are guaranteed to be filled
     * @throws CloudProviderException raised if the input parameters are invalid
     */
    List<Volume> getVolumesAttributes(int first, int last, List<String> attributes) throws InvalidRequestException,
        CloudProviderException;

    List<VolumeConfiguration> getVolumeConfigurationsAttributes(List<String> attributes, String filterExpression)
        throws InvalidRequestException, CloudProviderException;

    List<VolumeConfiguration> getVolumeConfigurationsAttributes(int first, int last, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    List<VolumeTemplate> getVolumeTemplatesAttributes(List<String> attributes, String filterExpression)
        throws InvalidRequestException, CloudProviderException;

    List<VolumeTemplate> getVolumeTemplatesConfigurationsAttributes(int first, int last, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    /**
     * Updates some attributes of a Volume, this operation is asynchronous
     * 
     * @param volumeId the id of the Volume
     * @param updatedAttributes the list of attributes to update
     * @return the job representing the asynchronous update operation
     * @throws InvalidVolumeIdException raised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    Job updateVolumeAttributes(String volumeId, Map<String, Object> updatedAttributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    void updateVolumeConfigurationAttributes(String volumeConfigId, Map<String, Object> updatedAttributes)
        throws InvalidRequestException, ResourceNotFoundException, CloudProviderException;

    void updateVolumeTemplateAttributes(String volumeTemplateId, Map<String, Object> updatedAttributes)
        throws InvalidRequestException, ResourceNotFoundException, CloudProviderException;

    /**
     * Deletes a Volume, this operation is asynchronous
     * 
     * @param volumeId the id of the Volume
     * @return the job representing the asynchronous delete operation
     * @throws InvalidVolumeIdExceptionraised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    Job deleteVolume(String volumeId) throws ResourceNotFoundException, CloudProviderException;

    void deleteVolumeTemplate(String volumeTemplateId) throws CloudProviderException;

    void deleteVolumeConfiguration(String volumeConfigId) throws CloudProviderException;

    /**
     * Returns the Volume Collection belonging to the caller
     * 
     * @return the Volume Collection belonging to the caller
     */
    VolumeCollection getVolumeCollection() throws CloudProviderException;

    /**
     * Returns the Volume Configuration Collection belonging to the caller
     * 
     * @return the Volume Configuration Collection belonging to the caller
     */
    VolumeConfigurationCollection getVolumeConfigurationCollection() throws CloudProviderException;

    /**
     * Returns the Volume Template Collection belonging to the caller
     * 
     * @return the Volume Template Collection belonging to the caller
     */
    VolumeTemplateCollection getVolumeTemplateCollection() throws CloudProviderException;

    /**
     * Updates the attributes of the Volume Collection belonging to the caller
     * 
     * @param updatedAttributes each entry of this map contains the name of the
     *        attribute to be update and the new value. Note that the only
     *        allowed attributes are "name", "description" and properties
     */
    void updateVolumeCollection(Map<String, Object> updatedAttributes) throws CloudProviderException;

    /**
     * Updates the attributes of the Volume Configuration Collection belonging
     * to the caller
     * 
     * @param updatedAttributes each entry of this map contains the name of the
     *        attribute to be update and the new value. Note that the only
     *        allowed attributes are "name", "description" and properties
     */
    void updateVolumeConfigurationCollection(Map<String, Object> updatedAttributes) throws CloudProviderException;

    /**
     * Updates the attributes of the Volume Template Collection belonging to the
     * caller
     * 
     * @param updatedAttributes each entry of this map contains the name of the
     *        attribute to be update and the new value. Note that the only
     *        allowed attributes are "name", "description" and properties
     */
    void updateVolumeTemplateCollection(Map<String, Object> updatedAttributes) throws CloudProviderException;

    boolean handleJobCompletion(Job job);

}