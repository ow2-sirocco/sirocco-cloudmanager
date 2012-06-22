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
package org.ow2.sirocco.apis.rest.cimi.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDataCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJobCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDiskCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeVolumeImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CloudEntryPointAggregate;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.FrequencyUnit;
import org.ow2.sirocco.apis.rest.cimi.domain.MemoryUnit;
import org.ow2.sirocco.apis.rest.cimi.domain.StorageUnit;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiExpand;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestHeader;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu.Frequency;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Identifiable;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;

/**
 * Converters tests of common data.
 */
public class CommonsConverterTest {

    private CimiRequest request;

    private CimiContext context;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setBaseUri("http://www.test.org/");
        RequestHeader header = new RequestHeader();
        header.setCimiSelect(new CimiSelect());
        header.setCimiExpand(new CimiExpand());
        this.request.setHeader(header);

        this.context = new CimiContextImpl(this.request, new CimiResponse());
    }

    @Test
    public void testFrequencyUnit() throws Exception {
        // Null Cimi -> Service
        Assert.assertNull(this.context.getConverter(FrequencyUnit.class).toService(this.context, null));
        // Empty Cimi -> Service
        try {
            this.context.getConverter(FrequencyUnit.class).toService(this.context, new String());
            // KO
            Assert.fail();
        } catch (InvalidConversionException e) {
        }
        // Full Cimi -> Service
        for (FrequencyUnit cimi : FrequencyUnit.values()) {
            try {
                this.context.getConverter(FrequencyUnit.class).toService(this.context, cimi.getLabel().toString());
                switch (cimi) {
                case HERTZ:
                case MEGAHERTZ:
                case GIGAHERTZ:
                    // OK
                    break;
                default:
                    // KO
                    Assert.fail();
                }
            } catch (InvalidConversionException e) {
                // OK
            }
        }

        // Null Service -> Cimi
        Assert.assertNull(this.context.getConverter(FrequencyUnit.class).toCimi(this.context, null));
        // Full Service -> Cimi
        for (Frequency service : Frequency.values()) {
            this.context.getConverter(FrequencyUnit.class).toCimi(this.context, service);
        }
    }

    @Test
    public void testMemoryUnit() throws Exception {
        // Null Cimi -> Service
        Assert.assertNull(this.context.getConverter(MemoryUnit.class).toService(this.context, null));
        // Empty Cimi -> Service
        try {
            this.context.getConverter(MemoryUnit.class).toService(this.context, new String());
            // KO
            Assert.fail();
        } catch (InvalidConversionException e) {
        }
        // Full Cimi -> Service
        for (MemoryUnit cimi : MemoryUnit.values()) {
            this.context.getConverter(MemoryUnit.class).toService(this.context, cimi.getLabel().toString());
        }

        // Null Service -> Cimi
        Assert.assertNull(this.context.getConverter(MemoryUnit.class).toCimi(this.context, null));
        // Full Service -> Cimi
        for (Memory.MemoryUnit service : Memory.MemoryUnit.values()) {
            this.context.getConverter(MemoryUnit.class).toCimi(this.context, service);
        }
    }

    @Test
    public void testStorageUnit() throws Exception {
        // Null Cimi -> Service
        Assert.assertNull(this.context.getConverter(StorageUnit.class).toService(this.context, null));
        // Empty Cimi -> Service
        try {
            this.context.getConverter(StorageUnit.class).toService(this.context, new String());
            // KO
            Assert.fail();
        } catch (InvalidConversionException e) {
        }
        // Full Cimi -> Service
        for (StorageUnit cimi : StorageUnit.values()) {
            this.context.getConverter(StorageUnit.class).toService(this.context, cimi.getLabel().toString());
        }

        // Null Service -> Cimi
        Assert.assertNull(this.context.getConverter(StorageUnit.class).toCimi(this.context, null));
        // Full Service -> Cimi
        for (org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit service : org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit
            .values()) {
            this.context.getConverter(StorageUnit.class).toCimi(this.context, service);
        }
    }

    @Test
    public void testCimiCpu() throws Exception {
        CimiCpu cimi;
        Cpu service;

        // Empty Cimi -> Service
        service = (Cpu) this.context.convertToService(new CimiCpu());
        Assert.assertNull(service.getCpuSpeedUnit());
        Assert.assertNull(service.getNumberCpu());
        Assert.assertNull(service.getQuantity());

        // Empty Service -> Cimi
        cimi = (CimiCpu) this.context.convertToCimi(new Cpu(), CimiCpu.class);
        Assert.assertNull(cimi.getFrequency());
        Assert.assertNull(cimi.getNumberVirtualCpus());
        Assert.assertNull(cimi.getUnits());

        // Full Cimi -> Service
        cimi = new CimiCpu();
        cimi.setFrequency(2f);
        cimi.setNumberVirtualCpus(3);
        cimi.setUnits(FrequencyUnit.GIGAHERTZ.getLabel());

        service = (Cpu) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.getQuantity().longValue());
        Assert.assertEquals(3, service.getNumberCpu().intValue());
        Assert.assertEquals(Cpu.Frequency.GIGA, service.getCpuSpeedUnit());

        // Full Service -> Cimi
        service = new Cpu();
        service.setCpuSpeedUnit(Frequency.MEGA);
        service.setNumberCpu(3);
        service.setQuantity(4f);

        cimi = (CimiCpu) this.context.convertToCimi(service, CimiCpu.class);
        Assert.assertEquals(4, cimi.getFrequency().longValue());
        Assert.assertEquals(3, cimi.getNumberVirtualCpus().intValue());
        Assert.assertEquals(FrequencyUnit.MEGAHERTZ.getLabel(), cimi.getUnits());
    }

    @Test
    public void testCimiMemory() throws Exception {
        CimiMemory cimi;
        Memory service;

        // Empty Cimi -> Service
        service = (Memory) this.context.convertToService(new CimiMemory());
        Assert.assertNull(service.getQuantity());
        Assert.assertNull(service.getUnit());

        // Empty Service -> Cimi
        cimi = (CimiMemory) this.context.convertToCimi(new Memory(), CimiMemory.class);
        Assert.assertNull(cimi.getQuantity());
        Assert.assertNull(cimi.getUnits());

        // Full Cimi -> Service
        cimi = new CimiMemory();
        cimi.setQuantity(5);
        cimi.setUnits(MemoryUnit.GibiBYTE.getLabel());

        service = (Memory) this.context.convertToService(cimi);
        Assert.assertEquals(5, service.getQuantity().longValue());
        Assert.assertEquals(Memory.MemoryUnit.GIGIBYTE, service.getUnit());

        // Full Service -> Cimi
        service = new Memory();
        service.setQuantity(4f);
        service.setUnit(Memory.MemoryUnit.MEGIBYTE);

        cimi = (CimiMemory) this.context.convertToCimi(service, CimiMemory.class);
        Assert.assertEquals(4, cimi.getQuantity().longValue());
        Assert.assertEquals(MemoryUnit.MebiBYTE.getLabel(), cimi.getUnits());
    }

    @Test
    public void testCimiCapacity() throws Exception {
        CimiCapacity cimi;
        Object service;

        // Empty Cimi (CimiDiskConfiguration) -> Service
        service = this.context.convertToService(new CimiDiskConfiguration());
        Assert.assertEquals(DiskTemplate.class, service.getClass());
        Assert.assertNull(((DiskTemplate) service).getQuantity());
        Assert.assertNull(((DiskTemplate) service).getUnit());

        // Empty Cimi (CimiCapacity) -> Service
        service = this.context.convertToService(new CimiCapacity());
        Assert.assertEquals(Disk.class, service.getClass());
        Assert.assertNull(((Disk) service).getQuantity());
        Assert.assertNull(((Disk) service).getUnits());

        // Empty Service (DiskTemplate) -> Cimi
        cimi = (CimiCapacity) this.context.convertToCimi(new DiskTemplate(), CimiCapacity.class);
        Assert.assertNull(cimi.getQuantity());
        Assert.assertNull(cimi.getUnits());

        // Empty Service (Disk) -> Cimi
        cimi = (CimiCapacity) this.context.convertToCimi(new Disk(), CimiCapacity.class);
        Assert.assertNull(cimi.getQuantity());
        Assert.assertNull(cimi.getUnits());

        // Full Cimi (CimiDiskConfiguration) -> Service
        CimiDiskConfiguration cimiDiskConf = new CimiDiskConfiguration();
        cimiDiskConf.setCapacity(new CimiCapacity(5, StorageUnit.GIGABYTE.getLabel()));
        service = this.context.convertToService(cimiDiskConf);
        Assert.assertEquals(5, ((DiskTemplate) service).getQuantity().longValue());
        Assert.assertEquals(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.GIGABYTE, ((DiskTemplate) service).getUnit());

        // Full Cimi (CimiCapacity) -> Service
        cimi = new CimiCapacity(7, StorageUnit.EXABYTE.getLabel());
        service = this.context.convertToService(cimi);
        Assert.assertEquals(7, ((Disk) service).getQuantity().longValue());
        Assert.assertEquals(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.EXABYTE, ((Disk) service).getUnits());

        // Full Service (DiskTemplate) -> Cimi
        DiskTemplate serviceDiskTemplate = new DiskTemplate();
        serviceDiskTemplate.setQuantity(44f);
        serviceDiskTemplate.setUnit(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.KILOBYTE);

        cimi = (CimiCapacity) this.context.convertToCimi(serviceDiskTemplate, CimiCapacity.class);
        Assert.assertEquals(44, cimi.getQuantity().longValue());
        Assert.assertEquals(StorageUnit.KILOBYTE.getLabel(), cimi.getUnits());

        // Full Service (Disk) -> Cimi
        Disk serviceDisk = new Disk();
        serviceDisk.setQuantity(17f);
        serviceDisk.setUnit(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.TERABYTE);

        cimi = (CimiCapacity) this.context.convertToCimi(serviceDisk, CimiCapacity.class);
        Assert.assertEquals(17, cimi.getQuantity().longValue());
        Assert.assertEquals(StorageUnit.TERABYTE.getLabel(), cimi.getUnits());

    }

    @Test
    public void testCimiDiskConfiguration() throws Exception {
        CimiDiskConfiguration cimi;
        DiskTemplate service;

        // Empty Cimi -> Service
        service = (DiskTemplate) this.context.convertToService(new CimiDiskConfiguration());
        Assert.assertNull(service.getInitialLocation());
        Assert.assertNull(service.getFormat());
        Assert.assertNull(service.getQuantity());
        Assert.assertNull(service.getUnit());

        // Empty Service -> Cimi
        cimi = (CimiDiskConfiguration) this.context.convertToCimi(new DiskTemplate(), CimiDiskConfiguration.class);
        Assert.assertNull(cimi.getInitialLocation());
        Assert.assertNull(cimi.getFormat());
        Assert.assertNotNull(cimi.getCapacity());
        Assert.assertNull(cimi.getCapacity().getQuantity());
        Assert.assertNull(cimi.getCapacity().getUnits());

        // Full Cimi -> Service
        cimi = new CimiDiskConfiguration();
        cimi.setInitialLocation("initialLocation");
        cimi.setFormat("format");
        cimi.setCapacity(new CimiCapacity(5, StorageUnit.PETABYTE.getLabel()));

        service = (DiskTemplate) this.context.convertToService(cimi);
        Assert.assertEquals("initialLocation", service.getInitialLocation());
        Assert.assertEquals("format", service.getFormat());
        Assert.assertEquals(5, service.getQuantity().longValue());
        Assert.assertEquals(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.PETABYTE, service.getUnit());

        // Full Service -> Cimi
        service = new DiskTemplate();
        service.setInitialLocation("initialLocation");
        service.setFormat("format");
        service.setQuantity(7f);
        service.setUnit(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.MEGABYTE);

        cimi = (CimiDiskConfiguration) this.context.convertToCimi(service, CimiDiskConfiguration.class);
        Assert.assertEquals("initialLocation", cimi.getInitialLocation());
        Assert.assertEquals("format", cimi.getFormat());
        Assert.assertEquals(7, cimi.getCapacity().getQuantity().intValue());
        Assert.assertEquals(StorageUnit.MEGABYTE.getLabel(), cimi.getCapacity().getUnits());
    }

    @Test
    public void testCimiCommonFill() throws Exception {
        CimiDataCommon cimi;
        MachineImage service;
        CommonConverter converter = new CommonConverter();

        // Empty
        cimi = new CimiCommon();
        service = new MachineImage();

        converter.fill(cimi, service);
        Assert.assertNull(service.getDescription());
        Assert.assertNull(service.getName());
        Assert.assertNull(service.getProperties());

        converter.fill(service, cimi);
        Assert.assertNull(cimi.getDescription());
        Assert.assertNull(cimi.getName());
        Assert.assertNull(cimi.getProperties());

        // Full
        cimi = new CimiCommon();
        cimi.setDescription("description");
        cimi.setName("name");
        Map<String, String> props = new HashMap<String, String>();
        props.put("keyOne", "valueOne");
        props.put("keyTwo", "valueTwo");
        props.put("keyThree", "valueThree");
        cimi.setProperties(props);
        service = new MachineImage();

        converter.fill(cimi, service);
        Assert.assertEquals("description", service.getDescription());
        Assert.assertEquals("name", service.getName());
        Assert.assertNotNull(service.getProperties());
        Assert.assertEquals(3, service.getProperties().size());
        Assert.assertEquals("valueOne", service.getProperties().get("keyOne"));
        Assert.assertEquals("valueTwo", service.getProperties().get("keyTwo"));
        Assert.assertEquals("valueThree", service.getProperties().get("keyThree"));

        cimi = new CimiCommon();
        converter.fill(service, cimi);
        Assert.assertEquals("description", cimi.getDescription());
        Assert.assertEquals("name", cimi.getName());
        Assert.assertNotNull(cimi.getProperties());
        Assert.assertEquals(3, cimi.getProperties().size());
        Assert.assertEquals("valueOne", cimi.getProperties().get("keyOne"));
        Assert.assertEquals("valueTwo", cimi.getProperties().get("keyTwo"));
        Assert.assertEquals("valueThree", cimi.getProperties().get("keyThree"));
    }

    // FIXME
    @Test
    public void testObjectCommon() throws Exception {
        CimiMachineImage cimi;
        MachineImage service;

        // Empty
        service = (MachineImage) this.context.convertToService(new CimiMachineImage());
        Assert.assertNull(service.getId());
        Assert.assertNull(service.getCreated());
        Assert.assertNull(service.getUpdated());

        cimi = (CimiMachineImage) this.context.convertToCimi(new MachineImage(), CimiMachineImage.class);
        Assert.assertNull(cimi.getId());
        Assert.assertNull(cimi.getHref());
        Assert.assertNull(cimi.getCreated());
        Assert.assertNull(cimi.getUpdated());

        // Full
        cimi = new CimiMachineImage();
        cimi.setId(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/13");
        cimi.setHref(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/13");
        cimi.setCreated(new Date());
        cimi.setUpdated(new Date());
        cimi.setOperations(new CimiOperation[] {new CimiOperation("rel", "href")});
        service = new MachineImage();

        service = (MachineImage) this.context.convertToService(cimi);
        Assert.assertEquals(13, service.getId().intValue());
        Assert.assertNull(service.getCreated());
        Assert.assertNull(service.getUpdated());

        cimi = new CimiMachineImage();
        service = new MachineImage();
        service.setId(29);
        Date created = new Date();
        service.setCreated(created);
        Date updated = new Date();
        service.setUpdated(updated);

        cimi = (CimiMachineImage) this.context.convertToCimi(service, CimiMachineImage.class);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/29", cimi.getId());
        Assert.assertNull(cimi.getHref());
        Assert.assertEquals(created, cimi.getCreated());
        Assert.assertEquals(updated, cimi.getUpdated());

        // Full in collection
        CimiMachineImageCollection cimiCollection;
        List<MachineImage> serviceCollection = new ArrayList<MachineImage>();
        serviceCollection.add(service);

        cimiCollection = (CimiMachineImageCollection) this.context.convertToCimi(serviceCollection,
            CimiMachineImageCollection.class);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImageCollection.getPathname(),
            cimiCollection.getId());
        Assert.assertNull(cimiCollection.getHref());

        cimi = cimiCollection.getCollection().get(0);
        Assert.assertNull(cimi.getId());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/29", cimi.getHref());
        Assert.assertNull(cimi.getCreated());
        Assert.assertNull(cimi.getUpdated());
    }

    @Test
    public void testId() throws Exception {
        CimiResource cimi = null;
        Object service = null;
        Class<? extends CimiResource> cimiClass = null;

        for (ExchangeType type : ExchangeType.values()) {

            // Removes idParent of request
            this.request.setIdParent(null);

            switch (type) {
            case CloudEntryPoint:
                service = new CloudEntryPointAggregate(new CloudEntryPoint());
                ((Identifiable) service).setId(11);
                cimiClass = CimiCloudEntryPoint.class;
                break;
            case Credentials:
                service = new Credentials();
                ((Identifiable) service).setId(11);
                cimiClass = CimiCredentials.class;
                break;
            case CredentialsCollection:
                service = new ArrayList<Credentials>();
                cimiClass = CimiCredentialsCollection.class;
                break;
            case CredentialsCreate:
                service = null;
                break;
            case CredentialsTemplate:
                service = new CredentialsTemplate();
                ((Identifiable) service).setId(11);
                cimiClass = CimiCredentialsTemplate.class;
                break;
            case CredentialsTemplateCollection:
                service = new ArrayList<CredentialsTemplate>();
                cimiClass = CimiCredentialsTemplateCollection.class;
                break;
            case Disk:
                service = new MachineDisk();
                ((MachineDisk) service).setId(111);
                cimiClass = CimiMachineDisk.class;
                // Add idParent of request
                this.request.setIdParent("999");
                break;
            case DiskCollection:
                service = new ArrayList<Disk>();
                cimiClass = CimiMachineDiskCollection.class;
                // Add idParent of request
                this.request.setIdParent("999");
                break;
            case Job:
                service = new Job();
                ((Identifiable) service).setId(11);
                cimiClass = CimiJob.class;
                break;
            case JobCollection:
                service = new ArrayList<Job>();
                cimiClass = CimiJobCollection.class;
                break;
            case Machine:
                service = new Machine();
                ((Identifiable) service).setId(11);
                cimiClass = CimiMachine.class;
                break;
            case MachineAction:
                service = null;
                break;
            case MachineCollection:
                service = new ArrayList<Machine>();
                cimiClass = CimiMachineCollection.class;
                break;
            case MachineConfiguration:
                service = new MachineConfiguration();
                ((Identifiable) service).setId(11);
                cimiClass = CimiMachineConfiguration.class;
                break;
            case MachineConfigurationCollection:
                service = new ArrayList<MachineConfiguration>();
                cimiClass = CimiMachineConfigurationCollection.class;
                break;
            case MachineCreate:
                service = null;
                break;
            case MachineImage:
                service = new MachineImage();
                ((Identifiable) service).setId(11);
                cimiClass = CimiMachineImage.class;
                break;
            case MachineImageCollection:
                service = new ArrayList<MachineImage>();
                cimiClass = CimiMachineImageCollection.class;
                break;
            case MachineTemplate:
                service = new MachineTemplate();
                ((Identifiable) service).setId(11);
                cimiClass = CimiMachineTemplate.class;
                break;
            case MachineTemplateCollection:
                service = new ArrayList<MachineTemplate>();
                cimiClass = CimiMachineTemplateCollection.class;
                break;
            case MachineVolume:
                service = new MachineVolume();
                ((MachineVolume) service).setId(111);
                cimiClass = CimiMachineVolume.class;
                // Add idParent of request
                this.request.setIdParent("999");
                break;
            case MachineVolumeCollection:
                service = new ArrayList<MachineVolume>();
                cimiClass = CimiMachineVolumeCollection.class;
                // Add idParent of request
                this.request.setIdParent("999");
                break;
            case Volume:
                service = new Volume();
                ((Identifiable) service).setId(11);
                cimiClass = CimiVolume.class;
                break;
            case VolumeCollection:
                service = new ArrayList<Volume>();
                cimiClass = CimiVolumeCollection.class;
                break;
            case VolumeConfiguration:
                service = new VolumeConfiguration();
                ((Identifiable) service).setId(11);
                cimiClass = CimiVolumeConfiguration.class;
                break;
            case VolumeConfigurationCollection:
                service = new ArrayList<VolumeConfiguration>();
                cimiClass = CimiVolumeConfigurationCollection.class;
                break;
            case VolumeImage:
                service = new VolumeImage();
                ((Identifiable) service).setId(11);
                cimiClass = CimiVolumeImage.class;
                break;
            case VolumeImageCollection:
                service = new ArrayList<VolumeImage>();
                cimiClass = CimiVolumeImageCollection.class;
                break;
            case VolumeTemplate:
                service = new VolumeTemplate();
                ((Identifiable) service).setId(11);
                cimiClass = CimiVolumeTemplate.class;
                break;
            case VolumeTemplateCollection:
                service = new ArrayList<VolumeTemplate>();
                cimiClass = CimiVolumeTemplateCollection.class;
                break;
            case VolumeVolumeImage:
                service = new VolumeImage();
                ((Identifiable) service).setId(111);
                cimiClass = CimiVolumeVolumeImage.class;
                // Add idParent of request
                this.request.setIdParent("999");
                break;
            case VolumeVolumeImageCollection:
                service = new ArrayList<VolumeImage>();
                cimiClass = CimiVolumeVolumeImageCollection.class;
                // Add idParent of request
                this.request.setIdParent("999");
                break;
            default:
                Assert.fail(type.name());
                break;
            }
            if (null != service) {
                cimi = (CimiResource) this.context.convertToCimi(service, cimiClass);
                if (true == type.hasIdInReference()) {
                    if (true == type.hasParent()) {
                        Assert.assertEquals("in " + type, type.makeHref(this.request.getBaseUri(), "999", "111"), cimi.getId());
                    } else {
                        Assert.assertEquals("in " + type, type.makeHref(this.request.getBaseUri(), "11"), cimi.getId());
                    }
                } else {
                    if (true == type.hasParent()) {
                        Assert.assertEquals("in " + type, type.makeHref(this.request.getBaseUri(), "999"), cimi.getId());
                    } else {
                        Assert.assertEquals("in " + type, type.makeHref(this.request.getBaseUri()), cimi.getId());
                    }

                }
                Assert.assertEquals("in " + type, type.getResourceURI(), cimi.getResourceURI());
            }
        }
    }

    @Test
    public void testIdParentHierarchy() throws Exception {
        CimiMachine cimi;

        Machine sMachine = new Machine();
        sMachine.setId(9999);
        sMachine.setDisks(new ArrayList<MachineDisk>());

        MachineDisk sDiskOne = new MachineDisk();
        sDiskOne.setId(111);
        sMachine.getDisks().add(sDiskOne);
        MachineDisk sDiskTwo = new MachineDisk();
        sDiskTwo.setId(222);
        sMachine.getDisks().add(sDiskTwo);
        MachineDisk sDiskThree = new MachineDisk();
        sDiskThree.setId(333);
        sMachine.getDisks().add(sDiskThree);

        cimi = (CimiMachine) this.context.convertToCimi(sMachine, CimiMachine.class);

        Assert.assertEquals("in " + cimi.getExchangeType(), ExchangeType.Machine.makeHref(this.request.getBaseUri(), "9999"),
            cimi.getId());

        Assert.assertEquals("in " + ExchangeType.Disk, ExchangeType.Disk.makeHref(this.request.getBaseUri(), "9999", "111"),
            cimi.getDisks().getCollection().get(0).getId());
        Assert.assertEquals("in " + ExchangeType.Disk, ExchangeType.Disk.makeHref(this.request.getBaseUri(), "9999", "222"),
            cimi.getDisks().getCollection().get(1).getId());
        Assert.assertEquals("in " + ExchangeType.Disk, ExchangeType.Disk.makeHref(this.request.getBaseUri(), "9999", "333"),
            cimi.getDisks().getCollection().get(2).getId());

    }
}