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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.FrequencyUnit;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.domain.MemoryUnit;
import org.ow2.sirocco.apis.rest.cimi.domain.StorageUnit;

/**
 * Implementation test.
 */
public class MergeReferenceHelperImplTest {

    private class MyCimiObjectCommon extends CimiObjectCommonAbstract {
        private static final long serialVersionUID = 1L;

        @Override
        public ExchangeType getExchangeType() {
            return ExchangeType.CloudEntryPoint;
        }
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#mergeCommon(org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon, org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon)}
     * .
     */
    @Test
    public void testMergeCommon() {

        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        MyCimiObjectCommon cimi;
        MyCimiObjectCommon cimiRef;
        Map<String, String> refProps;

        // Destination without value : ID, Name, Description
        cimi = new MyCimiObjectCommon();
        cimiRef = new MyCimiObjectCommon();
        cimiRef.setId("/456");
        cimiRef.setName("refName");
        cimiRef.setDescription("refDescription");

        merger.mergeCommon(cimiRef, cimi);

        Assert.assertEquals("/456", cimi.getId());
        Assert.assertEquals("refName", cimi.getName());
        Assert.assertEquals("refDescription", cimi.getDescription());
        Assert.assertNull(cimi.getProperties());

        // Destination with values : ID, Name, Description
        cimi = new MyCimiObjectCommon();
        cimi.setName("name");
        cimi.setDescription("description");

        cimiRef = new MyCimiObjectCommon();
        cimiRef.setId("/456");
        cimiRef.setName("refName");
        cimiRef.setDescription("refDescription");

        merger.mergeCommon(cimiRef, cimi);

        Assert.assertEquals("/456", cimi.getId());
        Assert.assertEquals("name", cimi.getName());
        Assert.assertEquals("description", cimi.getDescription());
        Assert.assertNull(cimi.getProperties());

        // Destination without value : Properties empty
        refProps = new HashMap<String, String>();
        cimi = new MyCimiObjectCommon();
        cimiRef = new MyCimiObjectCommon();
        cimiRef.setProperties(refProps);

        merger.mergeCommon(cimiRef, cimi);

        Assert.assertNull(cimi.getProperties());

        // Destination without value : Properties full
        refProps = new HashMap<String, String>();
        refProps.put("refKeyOne", "refValueOne");
        refProps.put("refKeyTwo", "refValueTwo");
        refProps.put("refKeyThree", "refValueThree");

        cimi = new MyCimiObjectCommon();
        cimiRef = new MyCimiObjectCommon();
        cimiRef.setProperties(refProps);

        merger.mergeCommon(cimiRef, cimi);

        Assert.assertEquals(3, cimi.getProperties().size());
        Assert.assertEquals("refValueOne", cimi.getProperties().get("refKeyOne"));
        Assert.assertEquals("refValueTwo", cimi.getProperties().get("refKeyTwo"));
        Assert.assertEquals("refValueThree", cimi.getProperties().get("refKeyThree"));

        // Destination with values : Properties
        Map<String, String> props = new HashMap<String, String>();
        props.put("keyOne", "valueOne");
        props.put("keyTwo", "valueTwo");
        props.put("refKeyThree", "valueThree");
        cimi = new MyCimiObjectCommon();
        cimi.setProperties(props);

        refProps = new HashMap<String, String>();
        refProps.put("refKeyOne", "refValueOne");
        refProps.put("refKeyTwo", "refValueTwo");
        refProps.put("refKeyThree", "refValueThree");

        cimiRef = new MyCimiObjectCommon();
        cimiRef.setId("/456");
        cimiRef.setProperties(refProps);

        merger.mergeCommon(cimiRef, cimi);

        Assert.assertEquals(5, cimi.getProperties().size());
        Assert.assertEquals("valueOne", cimi.getProperties().get("keyOne"));
        Assert.assertEquals("valueTwo", cimi.getProperties().get("keyTwo"));
        Assert.assertEquals("refValueOne", cimi.getProperties().get("refKeyOne"));
        Assert.assertEquals("refValueTwo", cimi.getProperties().get("refKeyTwo"));
        Assert.assertEquals("valueThree", cimi.getProperties().get("refKeyThree"));
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu, org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu)}
     * .
     */
    @Test
    public void testMergeCimiCpu() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiCpu cimi;
        CimiCpu cimiRef;

        cimiRef = new CimiCpu();
        cimiRef.setFrequency(1.5f);
        cimiRef.setNumberVirtualCpus(2);
        cimiRef.setUnits(FrequencyUnit.GIGAHERTZ.getLabel());

        // Source null
        cimi = new CimiCpu();
        merger.merge(null, cimi);

        // Destination without value
        cimi = new CimiCpu();

        merger.merge(cimiRef, cimi);

        Assert.assertEquals(1.5f, cimi.getFrequency());
        Assert.assertEquals(2, cimi.getNumberVirtualCpus().intValue());
        Assert.assertEquals(FrequencyUnit.GIGAHERTZ.getLabel(), cimi.getUnits());

        // Destination with values
        cimi = new CimiCpu();
        cimi.setFrequency(3.18f);
        cimi.setNumberVirtualCpus(5);
        cimi.setUnits(FrequencyUnit.KILOHERTZ.getLabel());

        merger.merge(cimiRef, cimi);

        Assert.assertEquals(3.18f, cimi.getFrequency());
        Assert.assertEquals(5, cimi.getNumberVirtualCpus().intValue());
        Assert.assertEquals(FrequencyUnit.KILOHERTZ.getLabel(), cimi.getUnits());
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory, org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory)}
     * .
     */
    @Test
    public void testMergeCimiMemory() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiMemory cimi;
        CimiMemory cimiRef;

        cimiRef = new CimiMemory();
        cimiRef.setQuantity(3);
        cimiRef.setUnits(MemoryUnit.GibiBYTE.getLabel());

        // Source null
        cimi = new CimiMemory();
        merger.merge(null, cimi);

        // Destination without value
        cimi = new CimiMemory();

        merger.merge(cimiRef, cimi);

        Assert.assertEquals(3, cimi.getQuantity().intValue());
        Assert.assertEquals(MemoryUnit.GibiBYTE.getLabel(), cimi.getUnits());

        // Destination with values
        cimi = new CimiMemory();
        cimi.setQuantity(200);
        cimi.setUnits(MemoryUnit.KibiBYTE.getLabel());

        merger.merge(cimiRef, cimi);

        Assert.assertEquals(200, cimi.getQuantity().intValue());
        Assert.assertEquals(MemoryUnit.KibiBYTE.getLabel(), cimi.getUnits());
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration, org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration)}
     * .
     */
    @Test
    public void testMergeCimiDiskConfiguration() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiDiskConfiguration cimi;
        CimiDiskConfiguration cimiRef;

        CimiCapacity refCapacity = new CimiCapacity();
        cimiRef = new CimiDiskConfiguration();
        cimiRef.setAttachmentPoint("refAttachmentPoint");
        cimiRef.setCapacity(refCapacity);
        cimiRef.setFormat("refFormat");

        // Source null
        cimi = new CimiDiskConfiguration();
        merger.merge(null, cimi);

        // Destination without value
        cimi = new CimiDiskConfiguration();
        merger.merge(cimiRef, cimi);

        Assert.assertEquals("refAttachmentPoint", cimi.getAttachmentPoint());
        Assert.assertSame(refCapacity, cimi.getCapacity());
        Assert.assertEquals("refFormat", cimi.getFormat());

        // Destination with values
        CimiCapacity cimiCapacity = new CimiCapacity();
        cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("attachmentPoint");
        cimi.setCapacity(cimiCapacity);
        cimi.setFormat("format");
        merger.merge(cimiRef, cimi);

        Assert.assertEquals("attachmentPoint", cimi.getAttachmentPoint());
        Assert.assertSame(cimiCapacity, cimi.getCapacity());
        Assert.assertEquals("format", cimi.getFormat());
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk, org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk)}
     * .
     */
    @Test
    public void testMergeCimiDisk() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiDisk cimi;
        CimiDisk cimiRef;

        CimiCapacity refCapacity = new CimiCapacity();
        cimiRef = new CimiDisk();
        cimiRef.setCapacity(refCapacity);

        // Source null
        cimi = new CimiDisk();
        merger.merge(null, cimi);

        // Destination without value
        cimi = new CimiDisk();
        merger.merge(cimiRef, cimi);

        Assert.assertSame(refCapacity, cimi.getCapacity());

        // Destination with values
        CimiCapacity cimiCapacity = new CimiCapacity();
        cimi = new CimiDisk();
        cimi.setCapacity(cimiCapacity);
        merger.merge(cimiRef, cimi);

        Assert.assertSame(cimiCapacity, cimi.getCapacity());
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity, org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity)}
     * .
     */
    @Test
    public void testMergeCimiCapacity() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiCapacity cimi;
        CimiCapacity cimiRef;

        cimiRef = new CimiCapacity();
        cimiRef.setQuantity(3);
        cimiRef.setUnits(StorageUnit.GIGABYTE.getLabel());

        // Source null
        cimi = new CimiCapacity();
        merger.merge(null, cimi);

        // Destination without value
        cimi = new CimiCapacity();

        merger.merge(cimiRef, cimi);

        Assert.assertEquals(3, cimi.getQuantity().intValue());
        Assert.assertEquals(StorageUnit.GIGABYTE.getLabel(), cimi.getUnits());

        // Destination with values
        cimi = new CimiCapacity();
        cimi.setQuantity(200);
        cimi.setUnits(StorageUnit.KILOBYTE.getLabel());

        merger.merge(cimiRef, cimi);

        Assert.assertEquals(200, cimi.getQuantity().intValue());
        Assert.assertEquals(StorageUnit.KILOBYTE.getLabel(), cimi.getUnits());
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials, org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials)}
     * .
     */
    @Test
    public void testMergeCimiCredentials() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiCredentials cimi;
        CimiCredentials cimiRef;

        byte[] refKey = new byte[10];
        cimiRef = new CimiCredentials();
        cimiRef.setId("refId");
        cimiRef.setKey(refKey);
        cimiRef.setPassword("refPassword");
        cimiRef.setUserName("refUserName");

        // Source null
        cimi = new CimiCredentials();
        merger.merge((CimiCredentials) null, cimi);

        // Destination without value
        cimi = new CimiCredentials();
        merger.merge(cimiRef, cimi);

        Assert.assertEquals("refId", cimi.getId());
        Assert.assertSame(refKey, cimi.getKey());
        Assert.assertEquals("refPassword", cimi.getPassword());
        Assert.assertEquals("refUserName", cimi.getUserName());

        // Destination with values
        byte[] cimiKey = new byte[7];
        cimi = new CimiCredentials();
        cimi.setKey(cimiKey);
        cimi.setPassword("password");
        cimi.setUserName("userName");
        merger.merge(cimiRef, cimi);

        Assert.assertSame(cimiKey, cimi.getKey());
        Assert.assertEquals("password", cimi.getPassword());
        Assert.assertEquals("userName", cimi.getUserName());
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage, org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage)}
     * .
     */
    @Test
    public void testMergeCimiMachineImage() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiMachineImage cimi;
        CimiMachineImage cimiRef;

        ImageLocation refImageLocation = new ImageLocation();
        cimiRef = new CimiMachineImage();
        cimiRef.setId("refId");
        cimiRef.setImageLocation(refImageLocation);

        // Source null
        cimi = new CimiMachineImage();
        merger.merge((CimiMachineImage) null, cimi);

        // Destination without value
        cimi = new CimiMachineImage();
        merger.merge(cimiRef, cimi);

        Assert.assertEquals("refId", cimi.getId());
        Assert.assertSame(refImageLocation, cimi.getImageLocation());

        // Destination with values
        ImageLocation cimiImageLocation = new ImageLocation();
        cimi = new CimiMachineImage();
        cimi.setImageLocation(cimiImageLocation);
        merger.merge(cimiRef, cimi);

        Assert.assertSame(cimiImageLocation, cimi.getImageLocation());
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration, org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration)}
     * .
     */
    @Test
    public void testMergeCimiMachineConfiguration() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiMachineConfiguration cimi;
        CimiMachineConfiguration cimiRef;

        CimiCpu refCpu = new CimiCpu();
        CimiMemory refMemory = new CimiMemory();
        cimiRef = new CimiMachineConfiguration();
        cimiRef.setId("refId");
        cimiRef.setCpu(refCpu);
        cimiRef.setMemory(refMemory);

        // Source null
        cimi = new CimiMachineConfiguration();
        merger.merge((CimiMachineConfiguration) null, cimi);

        // Destination without value
        cimi = new CimiMachineConfiguration();
        merger.merge(cimiRef, cimi);

        Assert.assertEquals("refId", cimi.getId());
        Assert.assertSame(refCpu, cimi.getCpu());
        Assert.assertSame(refMemory, cimi.getMemory());
        Assert.assertNull(cimi.getDisks());

        // Destination with values
        CimiCpu cimiCpu = new CimiCpu();
        CimiMemory cimiMemory = new CimiMemory();
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(cimiCpu);
        cimi.setMemory(cimiMemory);
        merger.merge(cimiRef, cimi);

        Assert.assertSame(cimiCpu, cimi.getCpu());
        Assert.assertSame(cimiMemory, cimi.getMemory());

        // Destination without value : disk empty
        cimiRef = new CimiMachineConfiguration();
        cimiRef.setDisks(new CimiDiskConfiguration[0]);
        cimi = new CimiMachineConfiguration();
        merger.merge(cimiRef, cimi);

        Assert.assertNull(cimi.getDisks());

        // Destination without value : disk full
        CimiDiskConfiguration refDiskOne = new CimiDiskConfiguration();
        CimiDiskConfiguration refDiskTwo = new CimiDiskConfiguration();
        cimiRef = new CimiMachineConfiguration();
        cimiRef.setDisks(new CimiDiskConfiguration[] {refDiskOne, refDiskTwo});
        cimi = new CimiMachineConfiguration();
        merger.merge(cimiRef, cimi);

        Assert.assertEquals(2, cimi.getDisks().length);
        Assert.assertSame(refDiskOne, cimi.getDisks()[0]);
        Assert.assertSame(refDiskTwo, cimi.getDisks()[1]);

        // Destination with values : disk empty
        cimiRef = new CimiMachineConfiguration();
        cimiRef.setDisks(new CimiDiskConfiguration[0]);

        CimiDiskConfiguration cimiDisk = new CimiDiskConfiguration();
        cimi = new CimiMachineConfiguration();
        cimi.setDisks(new CimiDiskConfiguration[] {cimiDisk});
        merger.merge(cimiRef, cimi);

        Assert.assertEquals(1, cimi.getDisks().length);

        // Destination with values : disk full
        cimiRef = new CimiMachineConfiguration();
        cimiRef.setDisks(new CimiDiskConfiguration[] {refDiskOne, refDiskTwo});
        cimi = new CimiMachineConfiguration();
        cimi.setDisks(new CimiDiskConfiguration[] {cimiDisk});
        merger.merge(cimiRef, cimi);

        Assert.assertEquals(3, cimi.getDisks().length);
        Assert.assertSame(refDiskOne, cimi.getDisks()[0]);
        Assert.assertSame(refDiskTwo, cimi.getDisks()[1]);
        Assert.assertSame(cimiDisk, cimi.getDisks()[2]);
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate, org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate)}
     * .
     */
    @Test
    // TODO Volumes, Network, ...
    public void testMergeCimiMachineTemplate() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiMachineTemplate cimi;
        CimiMachineTemplate cimiRef;

        CimiCredentials refCredentials = new CimiCredentials();
        CimiMachineConfiguration refMachineConfiguration = new CimiMachineConfiguration();
        CimiMachineImage refMachineImage = new CimiMachineImage();
        cimiRef = new CimiMachineTemplate();
        cimiRef.setId("refId");
        cimiRef.setCredentials(refCredentials);
        cimiRef.setMachineConfig(refMachineConfiguration);
        cimiRef.setMachineImage(refMachineImage);

        // Source null
        cimi = new CimiMachineTemplate();
        merger.merge((CimiMachineTemplate) null, cimi);

        // Destination without value
        cimi = new CimiMachineTemplate();
        merger.merge(cimiRef, cimi);

        Assert.assertEquals("refId", cimi.getId());
        Assert.assertSame(refCredentials, cimi.getCredentials());
        Assert.assertSame(refMachineConfiguration, cimi.getMachineConfig());
        Assert.assertSame(refMachineImage, cimi.getMachineImage());

        // Destination with values
        CimiCredentials cimiCredentials = new CimiCredentials();
        CimiMachineConfiguration cimiMachineConfiguration = new CimiMachineConfiguration();
        CimiMachineImage cimiMachineImage = new CimiMachineImage();
        cimi = new CimiMachineTemplate();
        cimi.setCredentials(cimiCredentials);
        cimi.setMachineConfig(cimiMachineConfiguration);
        cimi.setMachineImage(cimiMachineImage);
        merger.merge(cimiRef, cimi);

        Assert.assertSame(cimiCredentials, cimi.getCredentials());
        Assert.assertSame(cimiMachineConfiguration, cimi.getMachineConfig());
        Assert.assertSame(cimiMachineImage, cimi.getMachineImage());
    }

}
