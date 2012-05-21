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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation;
import org.ow2.sirocco.apis.rest.cimi.domain.FrequencyUnit;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.domain.MemoryUnit;
import org.ow2.sirocco.apis.rest.cimi.domain.StorageUnit;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu.Frequency;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.Type;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;

// TODO All Collections, CloudEntryPoint
public class ConverterTest {

    private CimiRequest request;

    private CimiContext context;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setContext(new CimiContextImpl(this.request));
        this.request.setBaseUri("http://www.test.org/");
        this.context = this.request.getContext();
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
        CimiConverter converter;
        CimiCpu cimi;
        Cpu service;

        converter = this.context.getConverter(CimiCpu.class);

        // Empty Cimi -> Service
        service = (Cpu) converter.toService(this.context, new CimiCpu());
        Assert.assertNull(service.getCpuSpeedUnit());
        Assert.assertNull(service.getNumberCpu());
        Assert.assertNull(service.getQuantity());

        // Empty Service -> Cimi
        cimi = (CimiCpu) converter.toCimi(this.context, new Cpu());
        Assert.assertNull(cimi.getFrequency());
        Assert.assertNull(cimi.getNumberVirtualCpus());
        Assert.assertNull(cimi.getUnits());

        // Full Cimi -> Service
        cimi = new CimiCpu();
        cimi.setFrequency(2f);
        cimi.setNumberVirtualCpus(3);
        cimi.setUnits(FrequencyUnit.GIGAHERTZ.getLabel());

        service = (Cpu) converter.toService(this.context, cimi);
        Assert.assertEquals(2, service.getQuantity().longValue());
        Assert.assertEquals(3, service.getNumberCpu().intValue());
        Assert.assertEquals(Cpu.Frequency.GIGA, service.getCpuSpeedUnit());

        // Full Service -> Cimi
        service = new Cpu();
        service.setCpuSpeedUnit(Frequency.MEGA);
        service.setNumberCpu(3);
        service.setQuantity(4f);

        cimi = (CimiCpu) converter.toCimi(this.context, service);
        Assert.assertEquals(4, cimi.getFrequency().longValue());
        Assert.assertEquals(3, cimi.getNumberVirtualCpus().intValue());
        Assert.assertEquals(FrequencyUnit.MEGAHERTZ.getLabel(), cimi.getUnits());
    }

    @Test
    public void testCimiMemory() throws Exception {
        CimiConverter converter;
        CimiMemory cimi;
        Memory service;

        converter = this.context.getConverter(CimiMemory.class);

        // Empty Cimi -> Service
        service = (Memory) converter.toService(this.context, new CimiMemory());
        Assert.assertNull(service.getQuantity());
        Assert.assertNull(service.getUnit());

        // Empty Service -> Cimi
        cimi = (CimiMemory) converter.toCimi(this.context, new Memory());
        Assert.assertNull(cimi.getQuantity());
        Assert.assertNull(cimi.getUnits());

        // Full Cimi -> Service
        cimi = new CimiMemory();
        cimi.setQuantity(5);
        cimi.setUnits(MemoryUnit.GibiBYTE.getLabel());

        service = (Memory) converter.toService(this.context, cimi);
        Assert.assertEquals(5, service.getQuantity().longValue());
        Assert.assertEquals(Memory.MemoryUnit.GIGIBYTE, service.getUnit());

        // Full Service -> Cimi
        service = new Memory();
        service.setQuantity(4f);
        service.setUnit(Memory.MemoryUnit.MEGIBYTE);

        cimi = (CimiMemory) converter.toCimi(this.context, service);
        Assert.assertEquals(4, cimi.getQuantity().longValue());
        Assert.assertEquals(MemoryUnit.MebiBYTE.getLabel(), cimi.getUnits());
    }

    @Test
    public void testCimiCapacity() throws Exception {
        CimiConverter converter;
        CimiCapacity cimi;
        Object service;

        converter = this.context.getConverter(CimiCapacity.class);

        // Empty Cimi (CimiDiskConfiguration) -> Service
        service = converter.toService(this.context, new CimiDiskConfiguration());
        Assert.assertEquals(DiskTemplate.class, service.getClass());
        Assert.assertNull(((DiskTemplate) service).getQuantity());
        Assert.assertNull(((DiskTemplate) service).getUnit());

        // Empty Cimi (CimiDisk) -> Service
        service = converter.toService(this.context, new CimiDisk());
        Assert.assertEquals(Disk.class, service.getClass());
        Assert.assertNull(((Disk) service).getQuantity());
        Assert.assertNull(((Disk) service).getUnits());

        // Empty Service (DiskTemplate) -> Cimi
        cimi = (CimiCapacity) converter.toCimi(this.context, new DiskTemplate());
        Assert.assertNull(cimi.getQuantity());
        Assert.assertNull(cimi.getUnits());

        // Empty Service (Disk) -> Cimi
        cimi = (CimiCapacity) converter.toCimi(this.context, new Disk());
        Assert.assertNull(cimi.getQuantity());
        Assert.assertNull(cimi.getUnits());

        // Full Cimi (CimiDiskConfiguration) -> Service
        CimiDiskConfiguration cimiDiskConf = new CimiDiskConfiguration();
        cimiDiskConf.setCapacity(new CimiCapacity(5, StorageUnit.GIGABYTE.getLabel()));
        service = converter.toService(this.context, cimiDiskConf);
        Assert.assertEquals(5, ((DiskTemplate) service).getQuantity().longValue());
        Assert.assertEquals(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.GIGABYTE, ((DiskTemplate) service).getUnit());

        // Full Cimi (CimiDisk) -> Service
        CimiDisk cimiDisk = new CimiDisk();
        cimiDisk.setCapacity(new CimiCapacity(7, StorageUnit.EXABYTE.getLabel()));
        service = converter.toService(this.context, cimiDisk);
        Assert.assertEquals(7, ((Disk) service).getQuantity().longValue());
        Assert.assertEquals(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.EXABYTE, ((Disk) service).getUnits());

        // Full Service (DiskTemplate) -> Cimi
        DiskTemplate serviceDiskTemplate = new DiskTemplate();
        serviceDiskTemplate.setQuantity(44f);
        serviceDiskTemplate.setUnit(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.KILOBYTE);

        cimi = (CimiCapacity) converter.toCimi(this.context, serviceDiskTemplate);
        Assert.assertEquals(44, cimi.getQuantity().longValue());
        Assert.assertEquals(StorageUnit.KILOBYTE.getLabel(), cimi.getUnits());

        // Full Service (Disk) -> Cimi
        Disk serviceDisk = new Disk();
        serviceDisk.setQuantity(17f);
        serviceDisk.setUnit(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.TERABYTE);

        cimi = (CimiCapacity) converter.toCimi(this.context, serviceDisk);
        Assert.assertEquals(17, cimi.getQuantity().longValue());
        Assert.assertEquals(StorageUnit.TERABYTE.getLabel(), cimi.getUnits());

    }

    @Test
    public void testCimiDiskConfiguration() throws Exception {
        CimiConverter converter;
        CimiDiskConfiguration cimi;
        DiskTemplate service;

        converter = this.context.getConverter(CimiDiskConfiguration.class);

        // Empty Cimi -> Service
        service = (DiskTemplate) converter.toService(this.context, new CimiDiskConfiguration());
        Assert.assertNull(service.getAttachmentPoint());
        Assert.assertNull(service.getFormat());
        Assert.assertNull(service.getQuantity());
        Assert.assertNull(service.getUnit());

        // Empty Service -> Cimi
        cimi = (CimiDiskConfiguration) converter.toCimi(this.context, new DiskTemplate());
        Assert.assertNull(cimi.getAttachmentPoint());
        Assert.assertNull(cimi.getFormat());
        Assert.assertNotNull(cimi.getCapacity());
        Assert.assertNull(cimi.getCapacity().getQuantity());
        Assert.assertNull(cimi.getCapacity().getUnits());

        // Full Cimi -> Service
        cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("attachmentPoint");
        cimi.setFormat("format");
        cimi.setCapacity(new CimiCapacity(5, StorageUnit.PETABYTE.getLabel()));

        service = (DiskTemplate) converter.toService(this.context, cimi);
        Assert.assertEquals("attachmentPoint", service.getAttachmentPoint());
        Assert.assertEquals("format", service.getFormat());
        Assert.assertEquals(5, service.getQuantity().longValue());
        Assert.assertEquals(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.PETABYTE, service.getUnit());

        // Full Service -> Cimi
        service = new DiskTemplate();
        service.setAttachmentPoint("attachmentPoint");
        service.setFormat("format");
        service.setQuantity(7f);
        service.setUnit(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.MEGABYTE);

        cimi = (CimiDiskConfiguration) converter.toCimi(this.context, service);
        Assert.assertEquals("attachmentPoint", cimi.getAttachmentPoint());
        Assert.assertEquals("format", cimi.getFormat());
        Assert.assertEquals(7, cimi.getCapacity().getQuantity().intValue());
        Assert.assertEquals(StorageUnit.MEGABYTE.getLabel(), cimi.getCapacity().getUnits());
    }

    @Test
    public void testCimiDisk() throws Exception {
        CimiConverter converter;
        CimiDisk cimi;
        Disk service;

        converter = this.context.getConverter(CimiDisk.class);

        // Empty Cimi -> Service
        service = (Disk) converter.toService(this.context, new CimiDisk());
        Assert.assertNull(service.getQuantity());
        Assert.assertNull(service.getUnits());

        // Empty Service -> Cimi
        cimi = (CimiDisk) converter.toCimi(this.context, new Disk());
        Assert.assertNotNull(cimi.getCapacity());
        Assert.assertNull(cimi.getCapacity().getQuantity());
        Assert.assertNull(cimi.getCapacity().getUnits());

        // Full Cimi -> Service
        cimi = new CimiDisk();
        cimi.setCapacity(new CimiCapacity(5, StorageUnit.BYTE.getLabel()));

        service = (Disk) converter.toService(this.context, cimi);
        Assert.assertEquals(5, service.getQuantity().longValue());
        Assert.assertEquals(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.BYTE, service.getUnits());

        // Full Service -> Cimi
        service = new Disk();
        service.setQuantity(7f);
        service.setUnit(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.MEGABYTE);

        cimi = (CimiDisk) converter.toCimi(this.context, service);
        Assert.assertEquals(7, cimi.getCapacity().getQuantity().intValue());
        Assert.assertEquals(StorageUnit.MEGABYTE.getLabel(), cimi.getCapacity().getUnits());
    }

    @Test
    public void testCimiCommonFill() throws Exception {
        CimiCommon cimi;
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

    @Test
    public void testCimiCommonIdFill() throws Exception {
        CimiMachineImage cimi;
        MachineImage service;
        CommonIdConverter converter = new CommonIdConverter();

        // Force to test ID and HREF
        this.context.getRootConverter(CimiEntityType.MachineImage);

        // Empty
        cimi = new CimiMachineImage();
        service = new MachineImage();

        converter.fill(this.context, cimi, service);
        Assert.assertNull(service.getId());
        Assert.assertNull(service.getCreated());
        Assert.assertNull(service.getUpdated());

        converter.fill(this.context, service, cimi);
        // FIXME with new collection
        // Assert.assertNull(cimi.getId());
        Assert.assertNull(cimi.getHref());
        Assert.assertNull(cimi.getCreated());
        Assert.assertNull(cimi.getUpdated());

        // Full
        cimi = new CimiMachineImage();
        cimi.setId(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/13");
        cimi.setHref(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/13");
        cimi.setCreated(new Date());
        cimi.setUpdated(new Date());
        cimi.setOperations(new CimiOperation[] {new CimiOperation("rel", "href")});
        service = new MachineImage();

        converter.fill(this.context, cimi, service);
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

        converter.fill(this.context, service, cimi);
        Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/29",
            cimi.getId());
        Assert.assertNull(cimi.getHref());
        Assert.assertEquals(created, cimi.getCreated());
        Assert.assertEquals(updated, cimi.getUpdated());

        // Force to test ID and HREF with mustBeReferenced = true
        this.context.getRootConverter(CimiEntityType.MachineImageCollection);
        // Full with mustBeReferenced = true
        converter.fill(this.context, service, cimi);
        Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/29",
            cimi.getId());
        Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/29",
            cimi.getHref());
        Assert.assertEquals(created, cimi.getCreated());
        Assert.assertEquals(updated, cimi.getUpdated());
    }

    @Test
    public void testCimiCredentials() throws Exception {
        CimiConverter converter;
        CimiCredentials cimi;
        Credentials service;

        converter = this.context.getRootConverter(CimiEntityType.Credentials);

        // Empty Cimi -> Service
        service = (Credentials) converter.toService(this.context, new CimiCredentials());
        Assert.assertNull(service.getPassword());
        Assert.assertNull(service.getUserName());
        Assert.assertNull(service.getPublicKey());

        // Empty Service -> Cimi
        cimi = (CimiCredentials) converter.toCimi(this.context, new Credentials());
        Assert.assertNull(cimi.getPassword());
        Assert.assertNull(cimi.getUserName());
        Assert.assertNull(cimi.getKey());

        // Full Cimi -> Service
        cimi = new CimiCredentials();
        cimi.setPassword("password");
        cimi.setUserName("userName");
        cimi.setKey(new byte[] {1, 2, 3, 4, 5});

        service = (Credentials) converter.toService(this.context, cimi);
        Assert.assertEquals("password", service.getPassword());
        Assert.assertEquals("userName", service.getUserName());
        Assert.assertArrayEquals(cimi.getKey(), service.getPublicKey());

        // Full Service -> Cimi
        service = new Credentials();
        service.setPassword("password");
        service.setUserName("userName");
        service.setPublicKey(new byte[] {6, 7, 8, 9, 10, 11});

        cimi = (CimiCredentials) converter.toCimi(this.context, service);
        Assert.assertNull(cimi.getPassword());
        Assert.assertEquals("userName", cimi.getUserName());
        Assert.assertNull(cimi.getKey());

        converter = this.context.getRootConverter(CimiEntityType.Credentials, true);
        cimi = (CimiCredentials) converter.toCimi(this.context, service);
        Assert.assertEquals("password", cimi.getPassword());
        Assert.assertEquals("userName", cimi.getUserName());
        Assert.assertArrayEquals(service.getPublicKey(), cimi.getKey());
    }

    @Test
    public void testCimiCredentialsTemplate() throws Exception {
        CimiConverter converter;
        CimiCredentialsTemplate cimi;
        CredentialsTemplate service;

        converter = this.context.getRootConverter(CimiEntityType.CredentialsTemplate);

        // Empty Cimi -> Service
        service = (CredentialsTemplate) converter.toService(this.context, new CimiCredentialsTemplate());
        Assert.assertNull(service.getPassword());
        Assert.assertNull(service.getUserName());
        Assert.assertNull(service.getPublicKey());

        // Empty Service -> Cimi
        cimi = (CimiCredentialsTemplate) converter.toCimi(this.context, new CredentialsTemplate());
        Assert.assertNull(cimi.getPassword());
        Assert.assertNull(cimi.getUserName());
        Assert.assertNull(cimi.getKey());

        // Full Cimi -> Service
        cimi = new CimiCredentialsTemplate();
        cimi.setPassword("password");
        cimi.setUserName("userName");
        cimi.setKey(new byte[] {1, 2, 3, 4, 5});

        service = (CredentialsTemplate) converter.toService(this.context, cimi);
        Assert.assertEquals("password", service.getPassword());
        Assert.assertEquals("userName", service.getUserName());
        Assert.assertArrayEquals(cimi.getKey(), service.getPublicKey());

        // Full Service -> Cimi
        service = new CredentialsTemplate();
        service.setPassword("password");
        service.setUserName("userName");
        service.setPublicKey(new byte[] {6, 7, 8, 9, 10, 11});

        converter = this.context.getRootConverter(CimiEntityType.CredentialsTemplate, true);
        cimi = (CimiCredentialsTemplate) converter.toCimi(this.context, service);
        Assert.assertEquals("password", cimi.getPassword());
        Assert.assertEquals("userName", cimi.getUserName());
        Assert.assertArrayEquals(service.getPublicKey(), cimi.getKey());
    }

    @Test
    public void testCimiCredentialsCreate() throws Exception {
        CimiConverter converter;
        CimiCredentialsCreate cimi;
        CredentialsCreate service;

        converter = this.context.getRootConverter(CimiEntityType.CredentialsCreate);

        // Empty Cimi -> Service
        service = (CredentialsCreate) converter.toService(this.context, new CimiCredentialsCreate());
        Assert.assertNull(service.getCredentialsTemplate());

        // Full Cimi -> Service
        cimi = new CimiCredentialsCreate();
        cimi.setCredentialsTemplate(new CimiCredentialsTemplate());

        service = (CredentialsCreate) converter.toService(this.context, cimi);
        Assert.assertEquals(CredentialsTemplate.class, service.getCredentialsTemplate().getClass());
    }

    @Test
    public void testCimiJob() throws Exception {
        CimiConverter converter;
        CimiJob cimi;
        Job service;

        converter = this.context.getRootConverter(CimiEntityType.Job);

        // Empty Service -> Cimi
        cimi = (CimiJob) converter.toCimi(this.context, new Job());
        Assert.assertNull(cimi.getAction());
        Assert.assertNull(cimi.getIsCancellable());
        Assert.assertNull(cimi.getNestedJobs());
        Assert.assertNull(cimi.getParentJob());
        Assert.assertNull(cimi.getProgress());
        Assert.assertNull(cimi.getReturnCode());
        Assert.assertNull(cimi.getStatus());
        Assert.assertNull(cimi.getStatusMessage());
        Assert.assertNull(cimi.getTargetEntity());
        Assert.assertNull(cimi.getTimeOfStatusChange());

        // Full Service -> Cimi
        CloudResource targetResource = new Machine();
        targetResource.setId(321);
        Date timeOfStatusChange = new Date();
        Job parentJob = new Job();
        parentJob.setId(789);

        service = new Job();
        service.setAction("action");
        service.setIsCancellable(Boolean.TRUE);
        service.setParentJob(parentJob);
        service.setProgress(13);
        service.setReturnCode(11);
        service.setStatus(Job.Status.RUNNING);
        service.setStatusMessage("statusMessage");
        service.setTargetEntity(targetResource);
        service.setTimeOfStatusChange(timeOfStatusChange);

        cimi = (CimiJob) converter.toCimi(this.context, service);
        Assert.assertEquals("action", cimi.getAction());
        Assert.assertEquals(Boolean.TRUE, cimi.getIsCancellable());
        Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.Job.getPathType().getPathname() + "/789", cimi
            .getParentJob().getHref());
        Assert.assertEquals(13, cimi.getProgress().intValue());
        Assert.assertEquals(11, cimi.getReturnCode().intValue());
        Assert.assertEquals(Job.Status.RUNNING.toString(), cimi.getStatus());
        Assert.assertEquals("statusMessage", cimi.getStatusMessage());
        Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.Machine.getPathType().getPathname() + "/321",
            cimi.getTargetEntity());
        Assert.assertEquals(timeOfStatusChange, cimi.getTimeOfStatusChange());

        // Full Service -> Cimi
        service = new Job();
        service.setNestedJobs(new ArrayList<Job>());

        cimi = (CimiJob) converter.toCimi(this.context, service);
        Assert.assertNull(cimi.getNestedJobs());

        // Full Service -> Cimi
        List<Job> listJob = new ArrayList<Job>();
        for (int i = 0; i < 3; i++) {
            Job job = new Job();
            job.setId(i + 100);
            listJob.add(job);
        }
        service = new Job();
        service.setNestedJobs(listJob);

        cimi = (CimiJob) converter.toCimi(this.context, service);
        Assert.assertNotNull(cimi.getNestedJobs());
        Assert.assertEquals(3, cimi.getNestedJobs().length);
        for (int i = 0; i < cimi.getNestedJobs().length; i++) {
            Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.Job.getPathType().getPathname() + "/" + (i + 100),
                cimi.getNestedJobs()[i].getHref());
        }
    }

    @Test
    public void testCimiMachineConfiguration() throws Exception {
        CimiConverter converter;
        CimiMachineConfiguration cimi;
        MachineConfiguration service;

        converter = this.context.getRootConverter(CimiEntityType.MachineConfiguration);

        // Empty Cimi -> Service
        service = (MachineConfiguration) converter.toService(this.context, new CimiMachineConfiguration());
        Assert.assertNull(service.getCpu());
        Assert.assertNull(service.getDiskTemplates());
        Assert.assertNull(service.getMemory());

        // Empty Service -> Cimi
        cimi = (CimiMachineConfiguration) converter.toCimi(this.context, new MachineConfiguration());
        Assert.assertNull(cimi.getCpu());
        Assert.assertNull(cimi.getDisks());
        Assert.assertNull(cimi.getMemory());

        // Full Cimi -> Service
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu());
        cimi.setMemory(new CimiMemory());

        service = (MachineConfiguration) converter.toService(this.context, cimi);
        Assert.assertEquals(Cpu.class, service.getCpu().getClass());
        Assert.assertEquals(Memory.class, service.getMemory().getClass());
        Assert.assertNull(service.getDiskTemplates());

        // Full Cimi -> Service
        cimi = new CimiMachineConfiguration();
        cimi.setDisks(new CimiDiskConfiguration[] {});

        service = (MachineConfiguration) converter.toService(this.context, cimi);
        Assert.assertNull(service.getDiskTemplates());

        // Full Cimi -> Service
        cimi = new CimiMachineConfiguration();
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(), new CimiDiskConfiguration()});

        service = (MachineConfiguration) converter.toService(this.context, cimi);
        Assert.assertEquals(2, service.getDiskTemplates().size());

        // Full Service -> Cimi
        service = new MachineConfiguration();
        service.setCpu(new Cpu());
        service.setMemory(new Memory());

        cimi = (CimiMachineConfiguration) converter.toCimi(this.context, service);
        Assert.assertEquals(CimiCpu.class, cimi.getCpu().getClass());
        Assert.assertEquals(CimiMemory.class, cimi.getMemory().getClass());
        Assert.assertNull(cimi.getDisks());

        // Full Service -> Cimi
        service = new MachineConfiguration();
        service.setDiskTemplates(new ArrayList<DiskTemplate>());

        cimi = (CimiMachineConfiguration) converter.toCimi(this.context, service);
        Assert.assertNull(cimi.getDisks());

        // Full Service -> Cimi
        service = new MachineConfiguration();
        service.setDiskTemplates(new ArrayList<DiskTemplate>(Arrays.asList(new DiskTemplate[] {new DiskTemplate(),
            new DiskTemplate(), new DiskTemplate()})));

        cimi = (CimiMachineConfiguration) converter.toCimi(this.context, service);
        Assert.assertEquals(3, cimi.getDisks().length);
    }

    @Test
    public void testCimiMachineImage() throws Exception {
        CimiConverter converter;
        CimiMachineImage cimi;
        MachineImage service;

        converter = this.context.getRootConverter(CimiEntityType.MachineImage);

        // Empty Cimi -> Service
        service = (MachineImage) converter.toService(this.context, new CimiMachineImage());
        Assert.assertNull(service.getImageLocation());
        Assert.assertNull(service.getState());
        Assert.assertNull(service.getType());

        // Empty Service -> Cimi
        cimi = (CimiMachineImage) converter.toCimi(this.context, new MachineImage());
        Assert.assertNull(cimi.getImageLocation());
        Assert.assertNull(cimi.getState());
        Assert.assertNull(cimi.getType());

        // Full Cimi -> Service
        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation("href"));
        cimi.setState("state");
        cimi.setType("type");

        service = (MachineImage) converter.toService(this.context, cimi);
        Assert.assertEquals("href", service.getImageLocation());
        Assert.assertNull(service.getState());
        Assert.assertNull(service.getType());

        // Full Service -> Cimi
        service = new MachineImage();
        service.setImageLocation("hrefImageLocation");
        service.setState(State.AVAILABLE);
        service.setType(Type.IMAGE);

        cimi = (CimiMachineImage) converter.toCimi(this.context, service);
        Assert.assertEquals("hrefImageLocation", cimi.getImageLocation().getHref());
        Assert.assertEquals("AVAILABLE", cimi.getState());
        Assert.assertEquals("IMAGE", cimi.getType());
    }

    @Test
    public void testCimiMachineImageCollection() throws Exception {
        CimiConverter converter;
        CimiMachineImageCollection cimi;
        MachineImageCollection service;

        converter = this.context.getRootConverter(CimiEntityType.MachineImageCollection);

        // Empty Cimi -> Service
        service = (MachineImageCollection) converter.toService(this.context, new CimiMachineImageCollection());
        Assert.assertNull(service.getImages());

        // Empty Service -> Cimi
        cimi = (CimiMachineImageCollection) converter.toCimi(this.context, new MachineImageCollection());
        Assert.assertNull(cimi.getMachineImages());

        // Full Cimi -> Service
        cimi = new CimiMachineImageCollection();
        cimi.setMachineImages(new CimiMachineImage[] {new CimiMachineImage(), new CimiMachineImage()});

        service = (MachineImageCollection) converter.toService(this.context, cimi);
        Assert.assertEquals(2, service.getImages().size());

        // Full Service -> Cimi
        MachineImage machineImage1 = new MachineImage();
        machineImage1.setId(1);
        machineImage1.setName("nameOne");
        MachineImage machineImage2 = new MachineImage();
        machineImage2.setId(2);
        machineImage2.setName("nameTwo");
        MachineImage machineImage3 = new MachineImage();
        machineImage3.setId(3);
        machineImage3.setName("nameThree");

        service = new MachineImageCollection();
        service.setImages(Arrays.asList(new MachineImage[] {machineImage1, machineImage2, machineImage3}));

        cimi = (CimiMachineImageCollection) converter.toCimi(this.context, service);
        Assert.assertEquals(3, cimi.getMachineImages().length);
        Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/1",
            cimi.getMachineImages()[0].getHref());
        Assert.assertNull(cimi.getMachineImages()[0].getId());
        Assert.assertNull(cimi.getMachineImages()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/2",
            cimi.getMachineImages()[1].getHref());
        Assert.assertNull(cimi.getMachineImages()[1].getId());
        Assert.assertNull(cimi.getMachineImages()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/3",
            cimi.getMachineImages()[2].getHref());
        Assert.assertNull(cimi.getMachineImages()[2].getId());
        Assert.assertNull(cimi.getMachineImages()[2].getName());

        cimi = (CimiMachineImageCollection) converter.toCimi(this.context,
            Arrays.asList(new MachineImage[] {machineImage3, machineImage1}));
        Assert.assertEquals(2, cimi.getMachineImages().length);
        Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/3",
            cimi.getMachineImages()[0].getHref());
        Assert.assertNull(cimi.getMachineImages()[0].getId());
        Assert.assertNull(cimi.getMachineImages()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/1",
            cimi.getMachineImages()[1].getHref());
        Assert.assertNull(cimi.getMachineImages()[1].getId());
        Assert.assertNull(cimi.getMachineImages()[1].getName());
    }

    // TODO Volumes, Network, ...
    @Test
    public void testCimiMachine() throws Exception {
        CimiConverter converter;
        CimiMachine cimi;
        Machine service;

        converter = this.context.getRootConverter(CimiEntityType.Machine);

        // Empty Cimi -> Service
        service = (Machine) converter.toService(this.context, new CimiMachine());
        Assert.assertNull(service.getCpu());
        Assert.assertNull(service.getMemory());
        Assert.assertNull(service.getState());
        // FIXME Disk collection
        // Assert.assertEquals(0, service.getDisks().size());

        // Empty Service -> Cimi
        cimi = (CimiMachine) converter.toCimi(this.context, new Machine());
        Assert.assertNull(cimi.getCpu());
        Assert.assertNull(cimi.getDisks());
        Assert.assertNull(cimi.getMemory());
        Assert.assertNull(cimi.getState());

        // Full Cimi -> Service
        cimi = new CimiMachine();
        cimi.setCpu(new CimiCpu());
        cimi.setMemory(new CimiMemory());
        cimi.setState("state");

        service = (Machine) converter.toService(this.context, cimi);
        Assert.assertEquals(Cpu.class, service.getCpu().getClass());
        Assert.assertEquals(Memory.class, service.getMemory().getClass());
        Assert.assertNull(service.getState());

        // Full Cimi -> Service
        cimi = new CimiMachine();
        cimi.setDisks(new CimiDisk[] {});

        service = (Machine) converter.toService(this.context, cimi);
        // FIXME Disk collection
        // Assert.assertEquals(0, service.getDisks().size());

        // Full Cimi -> Service
        cimi = new CimiMachine();
        cimi.setDisks(new CimiDisk[] {new CimiDisk(), new CimiDisk()});

        service = (Machine) converter.toService(this.context, cimi);
        // FIXME Disk collection
        // Assert.assertEquals(2, service.getDisks().size());

        // Full Service -> Cimi
        service = new Machine();
        service.setCpu(new Cpu());
        service.setMemory(new Memory());
        service.setState(Machine.State.CREATING);

        cimi = (CimiMachine) converter.toCimi(this.context, service);
        Assert.assertEquals(CimiCpu.class, cimi.getCpu().getClass());
        Assert.assertEquals(CimiMemory.class, cimi.getMemory().getClass());
        Assert.assertEquals(Machine.State.CREATING.toString(), cimi.getState());
        Assert.assertNull(cimi.getDisks());

        // Full Service -> Cimi
        service = new Machine();
        // FIXME Disk collection
        // service.setDisks(new ArrayList<Disk>());

        cimi = (CimiMachine) converter.toCimi(this.context, service);
        Assert.assertNull(cimi.getDisks());

        // Full Service -> Cimi
        service = new Machine();
        // FIXME Disk collection
        // service.setDisks(new ArrayList<Disk>(Arrays.asList(new Disk[] {new
        // Disk(), new Disk(), new Disk()})));

        cimi = (CimiMachine) converter.toCimi(this.context, service);
        // FIXME Disk collection
        // Assert.assertEquals(3, cimi.getDisks().length);
    }

    // TODO Volumes, Network, ...
    @Test
    public void testCimiMachineTemplate() throws Exception {
        CimiConverter converter;
        CimiMachineTemplate cimi;
        MachineTemplate service;

        converter = this.context.getRootConverter(CimiEntityType.MachineTemplate);

        // Empty Cimi -> Service
        service = (MachineTemplate) converter.toService(this.context, new CimiMachineTemplate());
        Assert.assertNull(service.getCredentials());
        Assert.assertNull(service.getMachineConfiguration());
        Assert.assertNull(service.getMachineImage());

        // Empty Service -> Cimi
        cimi = (CimiMachineTemplate) converter.toCimi(this.context, new MachineTemplate());
        Assert.assertNull(cimi.getCredentials());
        Assert.assertNull(cimi.getMachineConfig());
        Assert.assertNull(cimi.getMachineImage());

        // Full Cimi -> Service
        cimi = new CimiMachineTemplate();
        cimi.setCredentials(new CimiCredentials());
        cimi.setMachineConfig(new CimiMachineConfiguration());
        cimi.setMachineImage(new CimiMachineImage());

        service = (MachineTemplate) converter.toService(this.context, cimi);
        Assert.assertEquals(Credentials.class, service.getCredentials().getClass());
        Assert.assertEquals(MachineConfiguration.class, service.getMachineConfiguration().getClass());
        Assert.assertEquals(MachineImage.class, service.getMachineImage().getClass());

        // Full Service -> Cimi
        service = new MachineTemplate();
        service.setCredentials(new Credentials());
        service.setMachineConfiguration(new MachineConfiguration());
        service.setMachineImage(new MachineImage());

        cimi = (CimiMachineTemplate) converter.toCimi(this.context, service);
        Assert.assertEquals(CimiCredentials.class, cimi.getCredentials().getClass());
        Assert.assertEquals(CimiMachineConfiguration.class, cimi.getMachineConfig().getClass());
        Assert.assertEquals(CimiMachineImage.class, cimi.getMachineImage().getClass());
    }

    // TODO Volumes, Network, ...
    @Test
    public void testCimiMachineCreate() throws Exception {
        CimiConverter converter;
        CimiMachineCreate cimi;
        MachineCreate service;

        converter = this.context.getRootConverter(CimiEntityType.MachineCreate);

        // Empty Cimi -> Service
        service = (MachineCreate) converter.toService(this.context, new CimiMachineCreate());
        Assert.assertNull(service.getMachineTemplate());

        // Full Cimi -> Service
        cimi = new CimiMachineCreate();
        cimi.setMachineTemplate(new CimiMachineTemplate());

        service = (MachineCreate) converter.toService(this.context, cimi);
        Assert.assertEquals(MachineTemplate.class, service.getMachineTemplate().getClass());
    }
}