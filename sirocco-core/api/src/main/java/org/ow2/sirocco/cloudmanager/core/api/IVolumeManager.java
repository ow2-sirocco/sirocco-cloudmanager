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

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeVolumeImage;

/**
 * Volume management operations
 */
public interface IVolumeManager extends IJobListener {
    static final String EJB_JNDI_NAME = "java:global/sirocco/sirocco-core/VolumeManager!org.ow2.sirocco.cloudmanager.core.api.IRemoteVolumeManager";

    /**
     * Creates a new Volume, this operation is asynchronous
     * 
     * @param volumeCreate contains either a reference to a Volume Template or a
     *        Volume Template itself
     * @return the job representing the asynchronous creation operation
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws CloudProviderException raised if the creation fails
     */
    Job createVolume(VolumeCreate volumeCreate) throws InvalidRequestException, CloudProviderException;

    /**
     * Creates a new Volume Configuration
     * 
     * @param volumeConfig contains initialization values of the Volume
     *        Configuration
     * @return a persisted Volume Configuration instance
     * @throws InvalidRequestException raised if the the request is invalid
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
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws CloudProviderException raised if the creation fails
     */
    VolumeTemplate createVolumeTemplate(VolumeTemplate volumeTemplate) throws InvalidRequestException, CloudProviderException;

    /**
     * Returns the Volume instance with the supplied id
     * 
     * @param volumeId the id of the Volume to retrieve
     * @return if successful, the Volume instance
     * @throws ResourceNotFoundException raised if the provided id is invalid
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
     * Retrieves some attributes of a Volume Template
     * 
     * @param volumeTemplateId the id of the Volume Template
     * @param attributes the list of attributes to retrieve
     * @return a Volume Template instance whose requested attributes are filled
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    VolumeTemplate getVolumeTemplateAttributes(final String volumeTemplateId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    /**
     * Returns the VolumeConfiguration instance with the supplied id
     * 
     * @param volumeConfigId the id of the Volume to retrieve
     * @return if successful, the VolumeConfiguration instance
     * @throws CloudProviderException raised if the provided id is invalid
     */
    VolumeConfiguration getVolumeConfigurationById(final String volumeConfigId) throws CloudProviderException;

    /**
     * Retrieves some attributes of a Volume Configuration
     * 
     * @param volumeConfigId the id of the Volume Configuration
     * @param attributes the list of attributes to retrieve
     * @return a Volume Configuration instance whose requested attributes are
     *         filled
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    VolumeConfiguration getVolumeConfigurationAttributes(final String volumeConfigId, List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException;

    /**
     * Retrieves some attributes of a Volume
     * 
     * @param volumeId the id of the Volume
     * @param attributes the list of attributes to retrieve
     * @return a Volume instance whose requested attributes are filled
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    Volume getVolumeAttributes(final String volumeId, List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException;

    /**
     * Retrieves some attributes of all Volumes belonging to the caller within a
     * specific range, only Volumes
     * 
     * @param first index of the first Volume to return within the Volume
     *        Collection of the caller
     * @param last index of the last Volume to return within the Volume
     *        Collection of the caller
     * @param attributes the list of attributes to retrieve
     * @return a list of Volumes within the requested range (if any), for each
     *         Volume, only the requested attributes are guaranteed to be filled
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws CloudProviderException raised if the input parameters are invalid
     */
    QueryResult<Volume> getVolumes(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    /**
     * Retrieves some attributes of all VolumeConfigurations belonging to the
     * caller within a specific range
     * 
     * @param first index of the first VolumeConfiguration to return within the
     *        VolumeConfiguration Collection of the caller
     * @param last index of the last VolumeConfiguration to return within the
     *        VolumeConfiguration Collection of the caller
     * @param attributes the list of attributes to retrieve
     * @return a list of VolumeConfiguration within the requested range (if
     *         any), for each Volume, only the requested attributes are
     *         guaranteed to be filled
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws CloudProviderException raised if the input parameters are invalid
     */
    QueryResult<VolumeConfiguration> getVolumeConfigurations(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    /**
     * Retrieves some attributes of all VolumeTemplates belonging to the caller
     * within a specific range
     * 
     * @param first index of the first VolumeTemplate to return within the
     *        VolumeTemplate Collection of the caller
     * @param last index of the last VolumeTemplate to return within the
     *        VolumeTemplate Collection of the caller
     * @param attributes the list of attributes to retrieve
     * @return a list of VolumeTemplate within the requested range (if any), for
     *         each Volume, only the requested attributes are guaranteed to be
     *         filled
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws CloudProviderException raised if the input parameters are invalid
     */
    QueryResult<VolumeTemplate> getVolumeTemplates(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    /**
     * Updates a volume, this operation is asynchronous
     * 
     * @param volume a volume with new values to update
     * @return the job representing the asynchronous update operation
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    Job updateVolume(Volume volume) throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    /**
     * Updates some attributes of a Volume, this operation is asynchronous
     * 
     * @param volumeId the id of the Volume
     * @param updatedAttributes the list of attributes to update
     * @return the job representing the asynchronous update operation
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    Job updateVolumeAttributes(String volumeId, Map<String, Object> updatedAttributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    /**
     * Updates a Volume Configuration
     * 
     * @param volumeConfiguration the new Volume Configuration instance with new
     *        values to update
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    void updateVolumeConfiguration(VolumeConfiguration volumeConfiguration) throws InvalidRequestException,
        ResourceNotFoundException, CloudProviderException;

    /**
     * Updates some attributes of a VolumeConfiguration
     * 
     * @param volumeConfigId the id of the VolumeConfiguration
     * @param updatedAttributes the list of attributes to update
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    void updateVolumeConfigurationAttributes(String volumeConfigId, Map<String, Object> updatedAttributes)
        throws InvalidRequestException, ResourceNotFoundException, CloudProviderException;

    /**
     * Updates a Volume Template
     * 
     * @param volumeTemplate a Volume Template instance with new values to
     *        update
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    void updateVolumeTemplate(VolumeTemplate volumeTemplate) throws InvalidRequestException, ResourceNotFoundException,
        CloudProviderException;

    /**
     * Updates some attributes of a VolumeTemplate
     * 
     * @param volumeTemplateId the id of the VolumeTemplate
     * @param updatedAttributes the list of attributes to update
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws InvalidRequestException raised if the the request is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    void updateVolumeTemplateAttributes(String volumeTemplateId, Map<String, Object> updatedAttributes)
        throws InvalidRequestException, ResourceNotFoundException, CloudProviderException;

    /**
     * Deletes a Volume, this operation is asynchronous
     * 
     * @param volumeId the id of the Volume
     * @return the job representing the asynchronous delete operation
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    Job deleteVolume(String volumeId) throws ResourceNotFoundException, CloudProviderException;

    /**
     * Deletes a VolumeTemplate
     * 
     * @param volumeTemplateId the id of the VolumeTemplate
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    void deleteVolumeTemplate(String volumeTemplateId) throws ResourceNotFoundException, CloudProviderException;

    /**
     * Deletes a VolumeConfiguration
     * 
     * @param volumeConfigId the id of the VolumeConfiguration
     * @throws ResourceNotFoundException raised if the provided id is invalid
     * @throws CloudProviderException raised if any other type of runtime fault
     *         occurs
     */
    void deleteVolumeConfiguration(String volumeConfigId) throws ResourceNotFoundException, CloudProviderException;

    Job createVolumeImage(VolumeImage volumeImage) throws InvalidRequestException, CloudProviderException;

    Job createVolumeSnapshot(Volume volume, VolumeImage volumeImage) throws InvalidRequestException, CloudProviderException;

    VolumeImage getVolumeImageById(final String volumeImageId) throws ResourceNotFoundException;

    VolumeImage getVolumeImageAttributes(final String volumeImageId, List<String> attributes) throws ResourceNotFoundException;

    QueryResult<VolumeImage> getVolumeImages(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    Job updateVolumeImage(VolumeImage volumeImage) throws InvalidRequestException, ResourceNotFoundException,
        CloudProviderException;

    Job updateVolumeImageAttributes(String volumeImageId, Map<String, Object> updatedAttributes)
        throws InvalidRequestException, ResourceNotFoundException, CloudProviderException;

    Job deleteVolumeImage(String volumeImageId) throws ResourceNotFoundException, CloudProviderException;

    Job removeVolumeImageFromVolume(String volumeId, String volumeVolumeImageId) throws ResourceNotFoundException,
        CloudProviderException;

    Job addVolumeImageToVolume(final String volumeId, VolumeVolumeImage volumeVolumeImage) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException;

    void updateVolumeImageInVolume(String volumeId, VolumeVolumeImage volumeVolumeImage) throws ResourceNotFoundException,
        CloudProviderException;

    Job updateVolumeImageAttributesInVolume(String volumeId, String volumeVolumeImageId, Map<String, Object> updatedAttributes)
        throws InvalidRequestException, ResourceNotFoundException, CloudProviderException;

    VolumeVolumeImage getVolumeImageFromVolume(String volumeId, String volumeVolumeImageId) throws ResourceNotFoundException,
        CloudProviderException;

    QueryResult<VolumeVolumeImage> getVolumeVolumeImages(String volumeId, int first, int last, List<String> filters,
        List<String> attributes) throws InvalidRequestException, CloudProviderException;

    List<VolumeVolumeImage> getVolumeVolumeImages(String volumeId) throws ResourceNotFoundException, CloudProviderException;

    List<Volume> getVolumes() throws CloudProviderException;

    List<VolumeConfiguration> getVolumeConfigurations() throws CloudProviderException;

    List<VolumeImage> getVolumeImages() throws CloudProviderException;

    List<VolumeTemplate> getVolumeTemplates() throws CloudProviderException;

}