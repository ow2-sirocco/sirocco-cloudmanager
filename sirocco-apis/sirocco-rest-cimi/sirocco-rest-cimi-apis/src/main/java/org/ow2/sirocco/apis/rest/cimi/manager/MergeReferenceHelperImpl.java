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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ow2.sirocco.apis.rest.cimi.converter.HrefHelper;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Implementation of a helper to get complete entity passed by reference or by
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
            MachineImage dataService = this.managerMachineImage.getMachineImageById(HrefHelper.extractIdString(cimi.getHref()));
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
            MachineConfiguration dataService = this.managerMachine.getMachineConfigurationById(HrefHelper.extractIdString(cimi
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
        this.merge(context, cimi.getCredentialsTemplate());
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
            CredentialsTemplate dataService = this.managerCredentials.getCredentialsTemplateById(HrefHelper
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
            Credentials dataService = this.managerCredentials.getCredentialsById(HrefHelper.extractIdString(cimi.getHref()));
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
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org
     *      .ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate)
     */
    @Override
    public void merge(final CimiContext context, final CimiMachineTemplate cimi) throws Exception {
        if (true == cimi.hasReference()) {
            MachineTemplate dataService = this.managerMachine
                .getMachineTemplateById(HrefHelper.extractIdString(cimi.getHref()));
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
            // TODO Volumes, Network, ...
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
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfiguration)
     */
    @Override
    public void merge(final CimiContext context, final CimiVolumeConfiguration cimi) throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImage)
     */
    @Override
    public void merge(final CimiContext context, final CimiVolumeImage cimi) throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelper#merge(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate)
     */
    @Override
    public void merge(final CimiContext context, final CimiVolumeTemplate cimi) throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * Merge MachineTemplate entity data.
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
            // TODO Volume, VolumeTemplate, Network, ...
        }
    }

    /**
     * Merge Credentials entity data.
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
     * Merge MachineImage entity data.
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
     * Merge MachineConfiguration entity data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiMachineConfiguration cimiRef, final CimiMachineConfiguration cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getCpu()) {
                cimi.setCpu(cimiRef.getCpu());
            } else {
                this.merge(cimiRef.getCpu(), cimi.getCpu());
            }
            if (null == cimi.getMemory()) {
                cimi.setMemory(cimiRef.getMemory());
            } else {
                this.merge(cimiRef.getMemory(), cimi.getMemory());
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
    }

    /**
     * Merge CPU data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiCpu cimiRef, final CimiCpu cimi) {
        if (null != cimiRef) {
            if (null == cimi.getFrequency()) {
                cimi.setFrequency(cimiRef.getFrequency());
            }
            if (null == cimi.getNumberVirtualCpus()) {
                cimi.setNumberVirtualCpus(cimiRef.getNumberVirtualCpus());
            }
            if (null == cimi.getUnits()) {
                cimi.setUnits(cimiRef.getUnits());
            }
        }
    }

    /**
     * Merge memory data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiMemory cimiRef, final CimiMemory cimi) {
        if (null != cimiRef) {
            if (null == cimi.getQuantity()) {
                cimi.setQuantity(cimiRef.getQuantity());
            }
            if (null == cimi.getUnits()) {
                cimi.setUnits(cimiRef.getUnits());
            }
        }
    }

    /**
     * Merge disk configuration data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiDiskConfiguration cimiRef, final CimiDiskConfiguration cimi) {
        if (null != cimiRef) {
            if (null == cimi.getCapacity()) {
                cimi.setCapacity(cimiRef.getCapacity());
            } else {
                this.merge(cimiRef.getCapacity(), cimi.getCapacity());
            }
            if (null == cimi.getFormat()) {
                cimi.setFormat(cimiRef.getFormat());
            }
            if (null == cimi.getInitialLocation()) {
                cimi.setInitialLocation(cimiRef.getInitialLocation());
            }
        }
    }

    /**
     * Merge disk data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiMachineDisk cimiRef, final CimiMachineDisk cimi) {
        if (null != cimiRef) {
            this.mergeCommon(cimiRef, cimi);
            if (null == cimi.getCapacity()) {
                cimi.setCapacity(cimiRef.getCapacity());
            } else {
                this.merge(cimiRef.getCapacity(), cimi.getCapacity());
            }
            if (null == cimi.getInitialLocation()) {
                cimi.setInitialLocation(cimiRef.getInitialLocation());
            }
        }
    }

    /**
     * Merge capacity data.
     * 
     * @param cimiRef Source to merge
     * @param cimi Merged destination
     */
    protected void merge(final CimiCapacity cimiRef, final CimiCapacity cimi) {
        if (null != cimiRef) {
            if (null == cimi.getQuantity()) {
                cimi.setQuantity(cimiRef.getQuantity());
            }
            if (null == cimi.getUnits()) {
                cimi.setUnits(cimiRef.getUnits());
            }
        }
    }

}
