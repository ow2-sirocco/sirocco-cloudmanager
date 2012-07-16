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

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreateByValue;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;
import org.ow2.sirocco.apis.rest.cimi.validator.ValidChild;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.NotEmptyIfNotNull;

/**
 * Class MachineTemplate.
 */
@XmlRootElement(name = "MachineTemplate")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiMachineTemplate extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "machineConfig".
     */
    @ValidChild
    @NotNull(groups = GroupCreateByValue.class)
    private CimiMachineConfiguration machineConfig;

    /**
     * Field "machineImage".
     */
    @ValidChild
    @NotNull(groups = GroupCreateByValue.class)
    private CimiMachineImage machineImage;

    /**
     * Field "credential".
     */
    @ValidChild
    private CimiCredential credential;

    /**
     * Field "volumes".
     */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiMachineTemplateVolumeArray volumes;

    /**
     * Field "volumeTemplates".
     */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiMachineTemplateVolumeTemplateArray volumeTemplates;

    /**
     * Field "networkInterfaces".
     */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiMachineTemplateNetworkInterfaceArray networkInterfaces;

    /**
     * Field "initialState".
     */
    private String initialState;

    /**
     * Field "userData".
     */
    private String userData;

    /**
     * Default constructor.
     */
    public CimiMachineTemplate() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiMachineTemplate(final String href) {
        super(href);
    }

    /**
     * Return the value of field "machineConfig".
     * 
     * @return The value
     */
    public CimiMachineConfiguration getMachineConfig() {
        return this.machineConfig;
    }

    /**
     * Set the value of field "machineConfig".
     * 
     * @param machineConfig The value
     */
    public void setMachineConfig(final CimiMachineConfiguration machineConfig) {
        this.machineConfig = machineConfig;
    }

    /**
     * Return the value of field "machineImage".
     * 
     * @return The value
     */
    public CimiMachineImage getMachineImage() {
        return this.machineImage;
    }

    /**
     * Set the value of field "machineImage".
     * 
     * @param machineImage The value
     */
    public void setMachineImage(final CimiMachineImage machineImage) {
        this.machineImage = machineImage;
    }

    /**
     * Return the value of field "credential".
     * 
     * @return The value
     */
    public CimiCredential getCredential() {
        return this.credential;
    }

    /**
     * Set the value of field "credential".
     * 
     * @param CimiCredential The value
     */
    public void setCredential(final CimiCredential credential) {
        this.credential = credential;
    }

    /**
     * Return the value of field "volumes".
     * 
     * @return The value
     */
    @XmlElement(name = "volume")
    @JsonProperty(value = "volumes")
    public CimiMachineTemplateVolume[] getVolumes() {
        CimiMachineTemplateVolume[] items = null;
        if (null != this.volumes) {
            items = this.volumes.getArray();
        }
        return items;
    }

    /**
     * Set the value of field "volumes".
     * 
     * @param volumes The value
     */
    public void setVolumes(final CimiMachineTemplateVolume[] volumes) {
        if (null == volumes) {
            this.volumes = null;
        } else {
            this.volumes = new CimiMachineTemplateVolumeArray();
            this.volumes.setArray(volumes);
        }
    }

    /**
     * Return the value of field "volumes".
     * 
     * @return The value
     */
    @XmlTransient
    @JsonIgnore
    public List<CimiMachineTemplateVolume> getListVolumes() {
        return this.volumes;
    }

    /**
     * Set the value of field "volumes".
     * 
     * @param volumes The value
     */
    public void setListVolumes(final List<CimiMachineTemplateVolume> volumes) {
        if (null == volumes) {
            this.volumes = null;
        } else {
            this.volumes = new CimiMachineTemplateVolumeArray();
            this.volumes.addAll(volumes);
        }
    }

    /**
     * Return the value of field "volumeTemplates".
     * 
     * @return The value
     */
    @XmlElement(name = "volumeTemplate")
    @JsonProperty(value = "volumeTemplates")
    public CimiMachineTemplateVolumeTemplate[] getVolumeTemplates() {
        CimiMachineTemplateVolumeTemplate[] items = null;
        if (null != this.volumeTemplates) {
            items = this.volumeTemplates.getArray();
        }
        return items;
    }

    /**
     * Set the value of field "volumeTemplates".
     * 
     * @param volumeTemplates The value
     */
    public void setVolumeTemplates(final CimiMachineTemplateVolumeTemplate[] volumeTemplates) {
        if (null == volumeTemplates) {
            this.volumeTemplates = null;
        } else {
            this.volumeTemplates = new CimiMachineTemplateVolumeTemplateArray();
            this.volumeTemplates.setArray(volumeTemplates);
        }
    }

    /**
     * Return the value of field "volumeTemplates".
     * 
     * @return The value
     */
    @XmlTransient
    @JsonIgnore
    public List<CimiMachineTemplateVolumeTemplate> getListVolumeTemplates() {
        return this.volumeTemplates;
    }

    /**
     * Set the value of field "volumeTemplates".
     * 
     * @param volumeTemplates The value
     */
    public void setListVolumeTemplates(final List<CimiMachineTemplateVolumeTemplate> volumeTemplates) {
        if (null == volumeTemplates) {
            this.volumeTemplates = null;
        } else {
            this.volumeTemplates = new CimiMachineTemplateVolumeTemplateArray();
            this.volumeTemplates.addAll(volumeTemplates);
        }
    }

    /**
     * Return the value of field "networkInterfaces".
     * 
     * @return The value
     */
    @XmlElement(name = "networkInterface")
    @JsonProperty(value = "networkInterfaces")
    public CimiMachineTemplateNetworkInterface[] getNetworkInterfaces() {
        CimiMachineTemplateNetworkInterface[] items = null;
        if (null != this.networkInterfaces) {
            items = this.networkInterfaces.getArray();
        }
        return items;
    }

    /**
     * Set the value of field "networkInterfaces".
     * 
     * @param networkInterfaces The value
     */
    public void setNetworkInterfaces(final CimiMachineTemplateNetworkInterface[] networkInterfaces) {
        if (null == networkInterfaces) {
            this.networkInterfaces = null;
        } else {
            this.networkInterfaces = new CimiMachineTemplateNetworkInterfaceArray();
            this.networkInterfaces.setArray(networkInterfaces);
        }
    }

    /**
     * Return the value of field "networkInterfaces".
     * 
     * @return The value
     */
    @XmlTransient
    @JsonIgnore
    public List<CimiMachineTemplateNetworkInterface> getListNetworkInterfaces() {
        return this.networkInterfaces;
    }

    /**
     * Set the value of field "networkInterfaces".
     * 
     * @param networkInterfaces The value
     */
    public void setListNetworkInterfaces(final List<CimiMachineTemplateNetworkInterface> networkInterfaces) {
        if (null == networkInterfaces) {
            this.networkInterfaces = null;
        } else {
            this.networkInterfaces = new CimiMachineTemplateNetworkInterfaceArray();
            this.networkInterfaces.addAll(networkInterfaces);
        }
    }

    /**
     * Return the value of field "userData".
     * 
     * @return The value
     */
    public String getUserData() {
        return this.userData;
    }

    /**
     * Set the value of field "userData".
     * 
     * @param userData The value
     */
    public void setUserData(final String userData) {
        this.userData = userData;
    }

    /**
     * Return the value of field "initialState".
     * 
     * @return The value
     */
    public String getInitialState() {
        return this.initialState;
    }

    /**
     * Set the value of field "initialState".
     * 
     * @param initialState The value
     */
    public void setInitialState(final String initialState) {
        this.initialState = initialState;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getCredential());
        has = has || (null != this.getInitialState());
        has = has || (null != this.getMachineConfig());
        has = has || (null != this.getMachineImage());
        has = has || (null != this.getListNetworkInterfaces());
        has = has || (null != this.getUserData());
        has = has || (null != this.getListVolumes());
        has = has || (null != this.getListVolumeTemplates());
        return has;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiExchange#getExchangeType()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public ExchangeType getExchangeType() {
        return ExchangeType.MachineTemplate;
    }

    /**
     * Concrete class of the collection.
     */
    public class CimiMachineTemplateVolumeArray extends CimiArrayAbstract<CimiMachineTemplateVolume> {

        /** Serial number */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         * 
         * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiArray#newEmptyArraySized()
         */
        @Override
        public CimiMachineTemplateVolume[] newEmptyArraySized() {
            return new CimiMachineTemplateVolume[this.size()];
        }
    }

    /**
     * Concrete class of the collection.
     */
    public class CimiMachineTemplateVolumeTemplateArray extends CimiArrayAbstract<CimiMachineTemplateVolumeTemplate> {

        /** Serial number */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         * 
         * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiArray#newEmptyArraySized()
         */
        @Override
        public CimiMachineTemplateVolumeTemplate[] newEmptyArraySized() {
            return new CimiMachineTemplateVolumeTemplate[this.size()];
        }
    }

    /**
     * Concrete class of the collection.
     */
    public class CimiMachineTemplateNetworkInterfaceArray extends CimiArrayAbstract<CimiMachineTemplateNetworkInterface> {

        /** Serial number */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         * 
         * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiArray#newEmptyArraySized()
         */
        @Override
        public CimiMachineTemplateNetworkInterface[] newEmptyArraySized() {
            return new CimiMachineTemplateNetworkInterface[this.size()];
        }
    }

}
