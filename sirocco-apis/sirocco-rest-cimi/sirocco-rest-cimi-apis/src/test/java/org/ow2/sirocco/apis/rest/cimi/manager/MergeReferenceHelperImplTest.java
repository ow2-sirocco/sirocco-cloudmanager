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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;

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
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#mergeObjectCommon(org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon, org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon)}
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

        merger.mergeObjectCommon(cimiRef, cimi);

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

        merger.mergeObjectCommon(cimiRef, cimi);

        Assert.assertEquals("/456", cimi.getId());
        Assert.assertEquals("name", cimi.getName());
        Assert.assertEquals("description", cimi.getDescription());
        Assert.assertNull(cimi.getProperties());

        // Destination without value : Properties empty
        refProps = new HashMap<String, String>();
        cimi = new MyCimiObjectCommon();
        cimiRef = new MyCimiObjectCommon();
        cimiRef.setProperties(refProps);

        merger.mergeObjectCommon(cimiRef, cimi);

        Assert.assertNull(cimi.getProperties());

        // Destination without value : Properties full
        refProps = new HashMap<String, String>();
        refProps.put("refKeyOne", "refValueOne");
        refProps.put("refKeyTwo", "refValueTwo");
        refProps.put("refKeyThree", "refValueThree");

        cimi = new MyCimiObjectCommon();
        cimiRef = new MyCimiObjectCommon();
        cimiRef.setProperties(refProps);

        merger.mergeObjectCommon(cimiRef, cimi);

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

        merger.mergeObjectCommon(cimiRef, cimi);

        Assert.assertEquals(5, cimi.getProperties().size());
        Assert.assertEquals("valueOne", cimi.getProperties().get("keyOne"));
        Assert.assertEquals("valueTwo", cimi.getProperties().get("keyTwo"));
        Assert.assertEquals("refValueOne", cimi.getProperties().get("refKeyOne"));
        Assert.assertEquals("refValueTwo", cimi.getProperties().get("refKeyTwo"));
        Assert.assertEquals("valueThree", cimi.getProperties().get("refKeyThree"));
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk, org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk)}
     * .
     */
    @Test
    public void testMergeCimiMachineDisk() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiMachineDisk cimi;
        CimiMachineDisk cimiRef;

        cimiRef = new CimiMachineDisk();
        cimiRef.setCapacity(10);
        cimiRef.setInitialLocation("refInitialLocation");

        // Source null
        cimi = new CimiMachineDisk();
        merger.merge((CimiMachineDisk) null, cimi);

        // Destination without value
        cimi = new CimiMachineDisk();
        merger.merge(cimiRef, cimi);

        Assert.assertEquals(10, cimi.getCapacity().intValue());
        Assert.assertEquals("refInitialLocation", cimi.getInitialLocation());

        // Destination with values
        cimi = new CimiMachineDisk();
        cimi.setCapacity(25);
        cimi.setInitialLocation("initialLocation");
        merger.merge(cimiRef, cimi);

        Assert.assertEquals(25, cimi.getCapacity().intValue());
        Assert.assertEquals("initialLocation", cimi.getInitialLocation());
    }

    /**
     * Test method for
     * {@link org.ow2.sirocco.apis.rest.cimi.manager.MergeReferenceHelperImpl#merge(org.ow2.sirocco.apis.rest.cimi.domain.CimiCredential, org.ow2.sirocco.apis.rest.cimi.domain.CimiCredential)}
     * .
     */
    @Test
    public void testMergeCimiCredential() {
        MergeReferenceHelperImpl merger = new MergeReferenceHelperImpl();
        CimiCredential cimi;
        CimiCredential cimiRef;

        byte[] refKey = new byte[10];
        cimiRef = new CimiCredential();
        cimiRef.setId("refId");
        cimiRef.setKey(refKey);
        cimiRef.setPassword("refPassword");
        cimiRef.setUserName("refUserName");

        // Source null
        cimi = new CimiCredential();
        merger.merge((CimiCredential) null, cimi);

        // Destination without value
        cimi = new CimiCredential();
        merger.merge(cimiRef, cimi);

        Assert.assertEquals("refId", cimi.getId());
        Assert.assertSame(refKey, cimi.getKey());
        Assert.assertEquals("refPassword", cimi.getPassword());
        Assert.assertEquals("refUserName", cimi.getUserName());

        // Destination with values
        byte[] cimiKey = new byte[7];
        cimi = new CimiCredential();
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

        cimiRef = new CimiMachineConfiguration();
        cimiRef.setId("refId");
        cimiRef.setCpu(11);
        cimiRef.setMemory(22);

        // Source null
        cimi = new CimiMachineConfiguration();
        merger.merge((CimiMachineConfiguration) null, cimi);

        // Destination without value
        cimi = new CimiMachineConfiguration();
        merger.merge(cimiRef, cimi);

        Assert.assertEquals("refId", cimi.getId());
        Assert.assertEquals(11, cimi.getCpu().intValue());
        Assert.assertEquals(22, cimi.getMemory().intValue());
        Assert.assertNull(cimi.getDisks());

        // Destination with values
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(50);
        cimi.setMemory(100);
        merger.merge(cimiRef, cimi);

        Assert.assertEquals(50, cimi.getCpu().intValue());
        Assert.assertEquals(100, cimi.getMemory().intValue());

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

        CimiCredential refCredential = new CimiCredential();
        CimiMachineConfiguration refMachineConfiguration = new CimiMachineConfiguration();
        CimiMachineImage refMachineImage = new CimiMachineImage();
        cimiRef = new CimiMachineTemplate();
        cimiRef.setId("refId");
        cimiRef.setCredential(refCredential);
        cimiRef.setMachineConfig(refMachineConfiguration);
        cimiRef.setMachineImage(refMachineImage);

        // Source null
        cimi = new CimiMachineTemplate();
        merger.merge((CimiMachineTemplate) null, cimi);

        // Destination without value
        cimi = new CimiMachineTemplate();
        merger.merge(cimiRef, cimi);

        Assert.assertEquals("refId", cimi.getId());
        Assert.assertSame(refCredential, cimi.getCredential());
        Assert.assertSame(refMachineConfiguration, cimi.getMachineConfig());
        Assert.assertSame(refMachineImage, cimi.getMachineImage());

        // Destination with values
        CimiCredential cimiCredential = new CimiCredential();
        CimiMachineConfiguration cimiMachineConfiguration = new CimiMachineConfiguration();
        CimiMachineImage cimiMachineImage = new CimiMachineImage();
        cimi = new CimiMachineTemplate();
        cimi.setCredential(cimiCredential);
        cimi.setMachineConfig(cimiMachineConfiguration);
        cimi.setMachineImage(cimiMachineImage);
        merger.merge(cimiRef, cimi);

        Assert.assertSame(cimiCredential, cimi.getCredential());
        Assert.assertSame(cimiMachineConfiguration, cimi.getMachineConfig());
        Assert.assertSame(cimiMachineImage, cimi.getMachineImage());
    }

}
