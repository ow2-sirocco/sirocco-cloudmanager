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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;

public class CimiSelectTest {

    /**
     * Test for {@link CimiSelect#splitByComma(List)}.
     */
    @Test
    public void testSplitByComma() {
        List<String> results;

        List<String> selects = new ArrayList<String>();
        selects.add("one,two,three");
        selects.add("  four  , five  ,  six ,  seven   ,");
        selects.add("");
        selects.add("  ");
        selects.add(" eight, ,");
        selects.add(" ,nine ,ten,,  , eleven,");
        selects.add("twelve");
        selects.add("thirteen[az > 65], fourteen[2]");
        selects.add("fifteen[1-20]");
        results = CimiSelect.splitByComma(selects);

        Assert.assertEquals(15, results.size());
        Assert
            .assertEquals(
                "[one, two, three, four, five, six, seven, eight, nine, ten, eleven, twelve, thirteen[az > 65], fourteen[2], fifteen[1-20]]",
                results.toString());
    }

    /**
     * Test for {@link CimiSelect#extractBefore(String, char)}.
     */
    @Test
    public void testExtractBefore() {
        Assert.assertNull(CimiSelect.extractBefore(null, '['));

        Assert.assertEquals("", CimiSelect.extractBefore("", '['));
        Assert.assertEquals("", CimiSelect.extractBefore("[", '['));
        Assert.assertEquals("", CimiSelect.extractBefore("[extract]", '['));

        Assert.assertEquals("before", CimiSelect.extractBefore("before", '['));
        Assert.assertEquals("before", CimiSelect.extractBefore("before[", '['));
        Assert.assertEquals("before", CimiSelect.extractBefore("   before   [    ", '['));
        Assert.assertEquals("before", CimiSelect.extractBefore("before[extract]after", '['));
        Assert.assertEquals("before", CimiSelect.extractBefore("   before   [extract]after", '['));
    }

    /**
     * Test for {@link CimiSelect#extractBetween(String, char, char)}.
     */
    @Test
    public void testExtractBetween() {
        Assert.assertNull(CimiSelect.extractBetween(null, '[', ']'));
        Assert.assertNull(CimiSelect.extractBetween("", '[', ']'));
        Assert.assertNull(CimiSelect.extractBetween("][", '[', ']'));
        Assert.assertNull(CimiSelect.extractBetween("aa]bb[cc", '[', ']'));

        Assert.assertEquals("", CimiSelect.extractBetween("[]", '[', ']'));
        Assert.assertEquals("extract", CimiSelect.extractBetween("[extract]", '[', ']'));
        Assert.assertEquals("extract", CimiSelect.extractBetween("before[extract]after", '[', ']'));
        Assert.assertEquals("extract", CimiSelect.extractBetween("before[   extract   ]after", '[', ']'));
        Assert.assertEquals("107", CimiSelect.extractBetween("[107]", '[', ']'));
        Assert.assertEquals("19-67", CimiSelect.extractBetween("[19-67]", '[', ']'));
    }

    /**
     * Test for {@link CimiSelect#extractNumericArray(String)}.
     */
    @Test
    public void testExtractNumericArray() {
        Assert.assertEquals(0, CimiSelect.extractNumericArray(null).size());
        Assert.assertEquals(0, CimiSelect.extractNumericArray("").size());
        Assert.assertEquals(0, CimiSelect.extractNumericArray("    ").size());
        Assert.assertEquals(0, CimiSelect.extractNumericArray("lazkerl azelmkjr azelmk  almzerk     lmaker aze").size());
        Assert.assertEquals(0, CimiSelect.extractNumericArray("aze  -  amlkj").size());
        Assert.assertEquals(0, CimiSelect.extractNumericArray("10-amlkj").size());

        Assert.assertEquals(2, CimiSelect.extractNumericArray("99").size());
        Assert.assertEquals(99, CimiSelect.extractNumericArray("99").get(0).intValue());
        Assert.assertEquals(99, CimiSelect.extractNumericArray("99").get(1).intValue());

        Assert.assertEquals(2, CimiSelect.extractNumericArray(" 23 ").size());
        Assert.assertEquals(23, CimiSelect.extractNumericArray(" 23 ").get(0).intValue());
        Assert.assertEquals(23, CimiSelect.extractNumericArray(" 23 ").get(1).intValue());

        Assert.assertEquals(2, CimiSelect.extractNumericArray("5-9").size());
        Assert.assertEquals(5, CimiSelect.extractNumericArray("5-9").get(0).intValue());
        Assert.assertEquals(9, CimiSelect.extractNumericArray("5-9").get(1).intValue());

        Assert.assertEquals(2, CimiSelect.extractNumericArray("7-100").size());
        Assert.assertEquals(7, CimiSelect.extractNumericArray("7-100").get(0).intValue());
        Assert.assertEquals(100, CimiSelect.extractNumericArray("7-100").get(1).intValue());

        Assert.assertEquals(2, CimiSelect.extractNumericArray("19-6").size());
        Assert.assertEquals(19, CimiSelect.extractNumericArray("19-6").get(0).intValue());
        Assert.assertEquals(6, CimiSelect.extractNumericArray("19-6").get(1).intValue());

        Assert.assertEquals(2, CimiSelect.extractNumericArray("17-99-123").size());
        Assert.assertEquals(17, CimiSelect.extractNumericArray("17-99-123").get(0).intValue());
        Assert.assertEquals(99, CimiSelect.extractNumericArray("17-99-123").get(1).intValue());

        Assert.assertEquals(2, CimiSelect.extractNumericArray("  8   -   125   -   369   ").size());
        Assert.assertEquals(8, CimiSelect.extractNumericArray("  8   -   125   -   369   ").get(0).intValue());
        Assert.assertEquals(125, CimiSelect.extractNumericArray("  8   -   125   -   369   ").get(1).intValue());
    }

    /**
     * Test for {@link CimiSelect#dispatchAttributesValues(Object)}.
     */
    @Test
    public void testDispatchAttributesValues() {
        List<String> selects;
        CimiSelect cimiSelect;
        Map<String, Object> dispatched;

        // CimiSelect with a 'unknwon' property
        selects = new ArrayList<String>();
        selects.add("name");
        selects.add("description");
        selects.add("unknown");

        cimiSelect = new CimiSelect();
        cimiSelect.setSelects(selects);

        // Dispatching select on MachineImage
        MachineImage image = new MachineImage();
        image.setName("imageNameValue");
        image.setDescription("imageDescriptionValue");

        dispatched = cimiSelect.dispatchAttributesValues(image);

        Assert.assertEquals(2, dispatched.size());
        Assert.assertEquals("imageNameValue", dispatched.get("name"));
        Assert.assertEquals("imageDescriptionValue", dispatched.get("description"));

        // Same dispatching on MachineConfiguration with only one property
        // attributed
        MachineConfiguration config = new MachineConfiguration();
        config.setName("configNameValue");

        dispatched = cimiSelect.dispatchAttributesValues(config);
        Assert.assertEquals(1, dispatched.size());
        Assert.assertEquals("configNameValue", dispatched.get("name"));
    }

    @Test
    public void testCases() {
        List<String> selects;
        CimiSelect cimi = new CimiSelect();

        // No select
        Assert.assertTrue(cimi.isEmpty());
        Assert.assertFalse(cimi.isArrayPresent());
        Assert.assertFalse(cimi.isExpressionArrayPresent());
        Assert.assertFalse(cimi.isNumericArrayPresent());
        Assert.assertNull(cimi.getAttributes());
        Assert.assertNull(cimi.getIndexFirstArray());
        Assert.assertNull(cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertNull(cimi.getNumericArray(cimi.getIndexFirstArray()));

        // Single select
        selects = new ArrayList<String>();
        selects.add("attrOne");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertFalse(cimi.isArrayPresent());
        Assert.assertFalse(cimi.isExpressionArrayPresent());
        Assert.assertFalse(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(1, cimi.getAttributes().size());
        Assert.assertNull(cimi.getIndexFirstArray());
        Assert.assertNull(cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertNull(cimi.getNumericArray(cimi.getIndexFirstArray()));

        // Duo select in single line
        selects = new ArrayList<String>();
        selects.add("attrOne, attrTwo");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertFalse(cimi.isArrayPresent());
        Assert.assertFalse(cimi.isExpressionArrayPresent());
        Assert.assertFalse(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(2, cimi.getAttributes().size());
        Assert.assertNull(cimi.getIndexFirstArray());
        Assert.assertNull(cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertNull(cimi.getNumericArray(cimi.getIndexFirstArray()));

        // Duo select in multi lines
        selects = new ArrayList<String>();
        selects.add("attrOne");
        selects.add("attrTwo");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertFalse(cimi.isArrayPresent());
        Assert.assertFalse(cimi.isExpressionArrayPresent());
        Assert.assertFalse(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(2, cimi.getAttributes().size());
        Assert.assertNull(cimi.getIndexFirstArray());
        Assert.assertNull(cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertNull(cimi.getNumericArray(cimi.getIndexFirstArray()));

        // Single select with a numeric array
        selects = new ArrayList<String>();
        selects.add("attrOne[10]");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertTrue(cimi.isArrayPresent());
        Assert.assertFalse(cimi.isExpressionArrayPresent());
        Assert.assertTrue(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(1, cimi.getAttributes().size());
        Assert.assertEquals(0, cimi.getIndexFirstArray().intValue());
        Assert.assertNull(cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertNotNull(cimi.getNumericArray(cimi.getIndexFirstArray()));
        Assert.assertEquals(2, cimi.getNumericArray(cimi.getIndexFirstArray()).size());
        Assert.assertEquals(10, cimi.getNumericArray(cimi.getIndexFirstArray()).get(0).intValue());
        Assert.assertEquals(10, cimi.getNumericArray(cimi.getIndexFirstArray()).get(1).intValue());

        // Single select with range numeric array
        selects = new ArrayList<String>();
        selects.add("attrOne[7-13]");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertTrue(cimi.isArrayPresent());
        Assert.assertFalse(cimi.isExpressionArrayPresent());
        Assert.assertTrue(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(1, cimi.getAttributes().size());
        Assert.assertEquals(0, cimi.getIndexFirstArray().intValue());
        Assert.assertNull(cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertNotNull(cimi.getNumericArray(cimi.getIndexFirstArray()));
        Assert.assertEquals(2, cimi.getNumericArray(cimi.getIndexFirstArray()).size());
        Assert.assertEquals(7, cimi.getNumericArray(cimi.getIndexFirstArray()).get(0).intValue());
        Assert.assertEquals(13, cimi.getNumericArray(cimi.getIndexFirstArray()).get(1).intValue());

        // Single select with expression array
        selects = new ArrayList<String>();
        selects.add("attrOne[expOne > 13]");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertTrue(cimi.isArrayPresent());
        Assert.assertTrue(cimi.isExpressionArrayPresent());
        Assert.assertFalse(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(1, cimi.getAttributes().size());
        Assert.assertEquals(0, cimi.getIndexFirstArray().intValue());
        Assert.assertNotNull(cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertEquals("expOne > 13", cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertNull(cimi.getNumericArray(cimi.getIndexFirstArray()));

        // Multi select with expression array
        selects = new ArrayList<String>();
        selects.add("attrOne");
        selects.add("attrTwo[expTwo = 7]");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertTrue(cimi.isArrayPresent());
        Assert.assertTrue(cimi.isExpressionArrayPresent());
        Assert.assertFalse(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(2, cimi.getAttributes().size());
        Assert.assertEquals(1, cimi.getIndexFirstArray().intValue());
        Assert.assertNotNull(cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertEquals("expTwo = 7", cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertNull(cimi.getNumericArray(cimi.getIndexFirstArray()));

        // Multi select with expression and numeric array
        selects = new ArrayList<String>();
        selects.add("attrOne");
        selects.add("attrTwo[expTwo = 7]");
        selects.add("attrThree[25-103]");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertTrue(cimi.isArrayPresent());
        Assert.assertTrue(cimi.isExpressionArrayPresent());
        Assert.assertFalse(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(3, cimi.getAttributes().size());
        Assert.assertEquals(1, cimi.getIndexFirstArray().intValue());
        Assert.assertEquals("expTwo = 7", cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertNull(cimi.getExpressionArray(0));
        Assert.assertEquals("expTwo = 7", cimi.getExpressionArray(1));
        Assert.assertNull(cimi.getExpressionArray(2));
        Assert.assertNull(cimi.getNumericArray(cimi.getIndexFirstArray()));
        Assert.assertNull(cimi.getNumericArray(0));
        Assert.assertNull(cimi.getNumericArray(1));
        Assert.assertEquals(2, cimi.getNumericArray(2).size());
        Assert.assertEquals(25, cimi.getNumericArray(2).get(0).intValue());
        Assert.assertEquals(103, cimi.getNumericArray(2).get(1).intValue());

        // Multi select with expression and numeric array
        selects = new ArrayList<String>();
        selects.add("attrOne");
        selects.add("attrTwo");
        selects.add("attrThree[19 - 79]");
        selects.add("attrFour[expThree=25]");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertTrue(cimi.isArrayPresent());
        Assert.assertFalse(cimi.isExpressionArrayPresent());
        Assert.assertTrue(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(4, cimi.getAttributes().size());
        Assert.assertEquals(2, cimi.getIndexFirstArray().intValue());
        Assert.assertNull(cimi.getExpressionArray(cimi.getIndexFirstArray()));
        Assert.assertNull(cimi.getExpressionArray(0));
        Assert.assertNull(cimi.getExpressionArray(1));
        Assert.assertNull(cimi.getExpressionArray(2));
        Assert.assertEquals("expThree=25", cimi.getExpressionArray(3));

        Assert.assertNotNull(cimi.getNumericArray(cimi.getIndexFirstArray()));
        Assert.assertEquals(2, cimi.getNumericArray(cimi.getIndexFirstArray()).size());
        Assert.assertEquals(19, cimi.getNumericArray(cimi.getIndexFirstArray()).get(0).intValue());
        Assert.assertEquals(79, cimi.getNumericArray(cimi.getIndexFirstArray()).get(1).intValue());

        Assert.assertNull(cimi.getNumericArray(0));
        Assert.assertNull(cimi.getNumericArray(1));
        Assert.assertEquals(2, cimi.getNumericArray(2).size());
        Assert.assertEquals(19, cimi.getNumericArray(2).get(0).intValue());
        Assert.assertEquals(79, cimi.getNumericArray(2).get(1).intValue());
        Assert.assertNull(cimi.getNumericArray(3));
    }

}
