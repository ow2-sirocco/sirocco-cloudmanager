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
package org.ow2.sirocco.apis.rest.cimi.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ow2.sirocco.apis.rest.cimi.converter.PathHelper;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Implementation of a helper to get complete resource passed by reference or by
 * value during its creation
 */
@Component("MergeReferenceHelper")
public class MergeReferenceHelperImpl implements MergeReferenceHelper {

    @Autowired
    @Qualifier("IMachineImageManager")
    private IMachineImageManager managerMachineImage;

    @Autowired
    @Qualifier("IMachineManager")
    private IMachineManager managerMachine;

    @Autowired
    @Qualifier("IVolumeManager")
    private IVolumeManager managerVolume;

    @Autowired
    @Qualifier("ICredentialsManager")
    private ICredentialsManager managerCredentials;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage)
     */
    @Override
    public void merge(final CimiContext context, final CimiMachineImage cimi) throws Exception {
        if (true == cimi.hasReference()) {
            MachineImage dataService = this.managerMachineImage.getMachineImageById(PathHelper.extractIdString(cimi.getHref()));
            CimiMachineImage cimiRef = (CimiMachineImage) context.convertToCimi(dataService, CimiMachineImage.class);
            this.merge(cimiRef, cimi);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration)
     */
    @Override
    public void merge(final CimiContext context, final CimiMachineConfiguration cimi) throws Exception {
        if (true == cimi.hasReference()) {
            MachineConfiguration dataService = this.managerMachine.getMachineConfigurationById(PathHelper.extractIdString(cimi
                .getHref()));
            CimiMachineConfiguration cimiRef = (CimiMachineConfiguration) context.convertToCimi(dataService,
                CimiMachineConfiguration.class);
            this.merge(cimiRef, cimi);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate)
     */
    @Override
    public void merge(final CimiContext context, final CimiCredentialsCreate cimi) throws Exception {
        this.merge(context, cimi.getCredentialTemplate());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate)
     */
    @Override
    public void merge(final CimiContext context, final CimiCredentialsTemplate cimi) throws Exception {
        if (true == cimi.hasReference()) {
            CredentialsTemplate dataService = this.managerCredentials.getCredentialsTemplateById(PathHelper
                .extractIdString(cimi.getHref()));
            CimiCredentialsTemplate cimiRef = (CimiCredentialsTemplate) context.convertToCimi(dataService,
                CimiCredentialsTemplate.class);
            this.merge(cimiRef, cimi);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials)
     */
    @Override
    public void merge(final CimiContext context, final CimiCredentials cimi) throws Exception {
        if (true == cimi.hasReference()) {
            Credentials dataService = this.managerCredentials.getCredentialsById(PathHelper.extractIdString(cimi.getHref()));
            CimiCredentials cimiRef = (CimiCredentials) context.convertToCimi(dataService, CimiCredentials.class);
            this.merge(cimiRef, cimi);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate)
     */
    @Override
    public void merge(final CimiContext context, final CimiMachineCreate cimi) throws Exception {
        this.merge(context, cimi.getMachineTemplate());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk)
     */
    @Override
    public void merge(final CimiContext context, final CimiMachineDisk cimi) throws Exception {
        if (true == cimi.hasReference()) {
            MachineDisk dataService = this.managerMachine.getDiskFromMachine(context.getRequest().getIdParent(),
                PathHelper.extractIdString(cimi.getHref()));
            CimiMachineDisk cimiRef = (CimiMachineDisk) context.convertToCimi(dataService, CimiMachineDisk.class);
            this.merge(cimiRef, cimi);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineVolume)
     */
    @Override
    public void merge(final CimiContext context, final CimiMachineVolume cimi) throws Exception {
        if (true == cimi.hasReference()) {
            MachineVolume dataService = this.managerMachine.getVolumeFromMachine(context.getRequest().getIdParent(),
                PathHelper.extractIdString(cimi.getHref()));
            CimiMachineVolume cimiRef = (CimiMachineVolume) context.convertToCimi(dataService, CimiMachineVolume.class);
            this.merge(cimiRef, cimi);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org
     *      .ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate)
     */
    @Override
    public void merge(final CimiContext context, final CimiMachineTemplate cimi) throws Exception {
        if (true == cimi.hasReference()) {
            MachineTemplate dataService = this.managerMachine
                .getMachineTemplateById(PathHelper.extractIdString(cimi.getHref()));
            CimiMachineTemplate cimiRef = (CimiMachineTemplate) context.convertToCimi(dataService, CimiMachineTemplate.class);
            this.merge(cimiRef, cimi);
        } else {
            if (null != cimi.getCredentials()) {
                this.merge(context, cimi.getCredentials());
            }
            if (null != cimi.getMachineConfig()) {
                this.merge(context, cimi.getMachineConfig());
            }
            if (null != cimi.getMachineImage()) {
                this.merge(context, cimi.getMachineImage());
            }
            if (null != cimi.getVolumes()) {
                for (CimiMachineTemplateVolume item : cimi.getListVolumes()) {
                    this.merge(context, item);
                }
            }
            if (null != cimi.getVolumeTemplates()) {
                for (CimiMachineTemplateVolumeTemplate item : cimi.getListVolumeTemplates()) {
                    this.merge(context, item);
                }
            }
            // TODO Network, ...

        }
    }

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    protected void merge(final CimiContext context, final CimiMachineTemplateVolume cimi) throws Exception {
        if (true == cimi.hasReference()) {
            Volume dataService = this.managerVolume.getVolumeById(PathHelper.extractIdString(cimi.getHref()));
            CimiVolume cimiRef = (CimiVolume) context.convertToCimi(dataService, CimiVolume.class);
            this.merge(cimiRef, cimi);
        }
    }

    /**
     * Merge the reference of a resource only if necessary.
     * 
     * @param context The working context
     * @param cimi The resource with values or reference
     * @throws Exception If error in call service
     */
    protected void merge(final CimiContext context, final CimiMachineTemplateVolumeTemplate cimi) throws Exception {
        if (true == cimi.hasReference()) {
            VolumeTemplate dataService = this.managerVolume.getVolumeTemplateById(PathHelper.extractIdString(cimi.getHref()));
            CimiVolumeTemplate cimiRef = (CimiVolumeTemplate) context.convertToCimi(dataService, CimiVolumeTemplate.class);
            this.merge(cimiRef, cimi);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeCreate)
     */
    @Override
    public void merge(final CimiContext context, final CimiVolumeCreate cimi) throws Exception {
        this.merge(context, cimi.getVolumeTemplate());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfiguration)
     */
    @Override
    public void merge(final CimiContext context, final CimiVolumeConfiguration cimi) throws Exception {
        if (true == cimi.hasReference()) {
            VolumeConfiguration dataService = this.managerVolume.getVolumeConfigurationById(PathHelper.extractIdString(cimi
                .getHref()));
            CimiVolumeConfiguration cimiRef = (CimiVolumeConfiguration) context.convertToCimi(dataService,
                CimiVolumeConfiguration.class);
            this.merge(cimiRef, cimi);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImage)
     */
    @Override
    public void merge(final CimiContext context, final CimiVolumeImage cimi) throws Exception {
        if (true == cimi.hasReference()) {
            VolumeImage dataService = this.managerVolume.getVolumeImageById(PathHelper.extractIdString(cimi.getHref()));
            CimiVolumeImage cimiRef = (CimiVolumeImage) context.convertToCimi(dataService, CimiVolumeImage.class);
            this.merge(cimiRef, cimi);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate)
     */
    @Override
    public void merge(final CimiContext context, final CimiVolumeTemplate cimi) throws Exception {
        if (true == cimi.hasReference()) {
            VolumeTemplate dataService = this.managerVolume.getVolumeTemplateById(PathHelper.extractIdString(cimi.getHref()));
            CimiVolumeTemplate cimiRef = (CimiVolumeTemplate) context.convertToCimi(dataService, CimiVolumeTemplate.class);
            this.merge(cimiRef, cimi);
        } else {
            if (null != cimi.getVolumeConfig()) {
                this.merge(context, cimi.getVolumeConfig());
            }
            if (null != cimi.getVolumeImage()) {
                this.merge(context, cimi.getVolumeImage());
            }
        }
    }

    /**
     * Merge MachineTemplate resource data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiMachineTemplate cimiRef, final CimiMachineTemplate cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getCredentials()) {
                cimi.setCredentials(cimiRef.getCredentials());
            } else {
                this.merge(cimiRef.getCredentials(), cimi.getCredentials());
            }
            if (null == cimi.getMachineConfig()) {
                cimi.setMachineConfig(cimiRef.getMachineConfig());
            } else {
                this.merge(cimiRef.getMachineConfig(), cimi.getMachineConfig());
            }
            if (null == cimi.getMachineImage()) {
                cimi.setMachineImage(cimiRef.getMachineImage());
            } else {
                this.merge(cimiRef.getMachineImage(), cimi.getMachineImage());
            }
            // TODO Network, ...
            if (null == cimi.getVolumes()) {
                cimi.setVolumes(cimiRef.getVolumes());
            } else {
                this.merge(cimiRef.getListVolumes(), cimi.getListVolumes());
            }
            if (null == cimi.getVolumeTemplates()) {
                cimi.setVolumeTemplates(cimiRef.getVolumeTemplates());
            } else {
                this.merge(cimiRef.getListVolumeTemplates(), cimi.getListVolumeTemplates());
            }
        }
    }

    /**
     * Merge Volume resource data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiVolume cimiRef, final CimiVolume cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getBootable()) {
                cimi.setBootable(cimiRef.getBootable());
            }
            if (null == cimi.getCapacity()) {
                cimi.setCapacity(cimiRef.getCapacity());
            }
            if (null == cimi.getImages()) {
                cimi.setImages(cimiRef.getImages());
            } else {
                this.merge(cimiRef.getImages(), cimi.getImages());
            }
            if (null == cimi.getState()) {
                cimi.setState(cimiRef.getState());
            }
            if (null == cimi.getType()) {
                cimi.setType(cimiRef.getType());
            }
        }
    }

    /**
     * Merge VolumeConfiguration resource data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiVolumeConfiguration cimiRef, final CimiVolumeConfiguration cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getCapacity()) {
                cimi.setCapacity(cimiRef.getCapacity());
            }
            if (null == cimi.getFormat()) {
                cimi.setFormat(cimiRef.getFormat());
            }
            if (null == cimi.getType()) {
                cimi.setType(cimiRef.getType());
            }
        }
    }

    /**
     * Merge VolumeImage resource data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiVolumeImage cimiRef, final CimiVolumeImage cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getBootable()) {
                cimi.setBootable(cimiRef.getBootable());
            }
            if (null == cimi.getImageLocation()) {
                cimi.setImageLocation(cimiRef.getImageLocation());
            }
            if (null == cimi.getState()) {
                cimi.setState(cimiRef.getState());
            }
        }
    }

    /**
     * Merge VolumeTemplate resource data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiVolumeTemplate cimiRef, final CimiVolumeTemplate cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getVolumeConfig()) {
                cimi.setVolumeConfig(cimiRef.getVolumeConfig());
            } else {
                this.merge(cimiRef.getVolumeConfig(), cimi.getVolumeConfig());
            }
            if (null == cimi.getVolumeImage()) {
                cimi.setVolumeImage(cimiRef.getVolumeImage());
            } else {
                this.merge(cimiRef.getVolumeImage(), cimi.getVolumeImage());
            }
        }
    }

    /**
     * Merge CimiCollection<E extends CimiResource> resource data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected <E extends CimiResource> void merge(final CimiCollection<E> cimiRef, final CimiCollection<E> cimi) {
        if (null != cimiRef) {
            if (null != cimiRef.getCollection()) {
                this.merge(cimiRef.getCollection(), cimi.getCollection());
            }
        }
    }

    /**
     * Merge List<E extends CimiResource> resource data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected <E extends CimiResource> void merge(final List<E> cimiRef, final List<E> cimi) {
        if (null != cimiRef) {
            for (E item : cimiRef) {
                if (false == this.containsId(item.getId(), cimi)) {
                    cimi.add(item);
                }
            }
        }
    }

    private boolean containsId(final String id, final Collection<? extends CimiResource> collection) {
        boolean contains = false;
        if (null != collection) {
            for (CimiResource item : collection) {
                if (true == id.equals(item.getId())) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    /**
     * Merge Credentials resource data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiCredentials cimiRef, final CimiCredentials cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getKey()) {
                cimi.setKey(cimiRef.getKey());
            }
            if (null == cimi.getPassword()) {
                cimi.setPassword(cimiRef.getPassword());
            }
            if (null == cimi.getUserName()) {
                cimi.setUserName(cimiRef.getUserName());
            }
        }
    }

    /**
     * Merge MachineImage resource data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiMachineImage cimiRef, final CimiMachineImage cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getImageLocation()) {
                cimi.setImageLocation(cimiRef.getImageLocation());
            }
        }
    }

    /**
     * Merge MachineConfiguration resource data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiMachineConfiguration cimiRef, final CimiMachineConfiguration cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getCpu()) {
                cimi.setCpu(cimiRef.getCpu());
            }
            if (null == cimi.getMemory()) {
                cimi.setMemory(cimiRef.getMemory());
            }
            if ((null != cimiRef.getDisks()) && (cimiRef.getDisks().length > 0)) {
                if (null == cimi.getDisks()) {
                    cimi.setDisks(cimiRef.getDisks());
                } else {
                    List<CimiDiskConfiguration> list = new ArrayList<CimiDiskConfiguration>();
                    for (CimiDiskConfiguration disk : cimiRef.getDisks()) {
                        list.add(disk);
                    }
                    for (CimiDiskConfiguration disk : cimi.getDisks()) {
                        list.add(disk);
                    }
                    cimi.setDisks(list.toArray(new CimiDiskConfiguration[list.size()]));
                }
            }
        }
    }

    /**
     * Merge common data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void mergeCommon(final CimiObjectCommon cimiRef, final CimiObjectCommon cimi) {
        if (null == cimi.getId()) {
            cimi.setId(cimiRef.getId());
        }
        if (null == cimi.getDescription()) {
            cimi.setDescription(cimiRef.getDescription());
        }
        if (null == cimi.getName()) {
            cimi.setName(cimiRef.getName());
        }
        if (null == cimi.getProperties()) {
            if ((null != cimiRef.getProperties()) && (cimiRef.getProperties().size() > 0)) {
                cimi.setProperties(cimiRef.getProperties());
            }
        } else {
            Map<String, String> cimiProps = cimi.getProperties();
            if (null != cimiRef.getProperties()) {
                for (Entry<String, String> entryRef : cimiRef.getProperties().entrySet()) {
                    if (false == cimiProps.containsKey(entryRef.getKey())) {
                        cimiProps.put(entryRef.getKey(), entryRef.getValue());
                    }
                }
            }
        }
        if (null == cimi.getResourceURI()) {
            cimi.setResourceURI(cimiRef.getResourceURI());
        }
    }

    /**
     * Merge machine disk data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiMachineDisk cimiRef, final CimiMachineDisk cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getCapacity()) {
                cimi.setCapacity(cimiRef.getCapacity());
            }
            if (null == cimi.getInitialLocation()) {
                cimi.setInitialLocation(cimiRef.getInitialLocation());
            }
        }
    }

    /**
     * Merge machine volume data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiMachineVolume cimiRef, final CimiMachineVolume cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getInitialLocation()) {
                cimi.setInitialLocation(cimiRef.getInitialLocation());
            }
            if (null == cimi.getVolume()) {
                cimi.setVolume(cimiRef.getVolume());
            } else {
                this.merge(cimiRef.getVolume(), cimi.getVolume());
            }
        }
    }

}
