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
package org.ow2.sirocco.apis.rest.cimi.validator.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHelper;

public class NotEmptyIfNotNullValidatorTest {

    @Test
    public void testString() {

        class MyTest {
            @NotEmptyIfNotNull
            String field;
        }
        MyTest toTest = new MyTest();
        Assert.assertNull(toTest.field);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.field = "A";
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.field = "";
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.field = "   ";
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));
    }

    @Test
    public void testArray() {

        class MyTest {
            @NotEmptyIfNotNull
            byte[] byteArray;

            @NotEmptyIfNotNull
            String[] stringArray;
        }

        MyTest toTest;

        // byte[]
        toTest = new MyTest();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.byteArray = new byte[1];
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.byteArray = new byte[9];
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.byteArray = new byte[2];
        toTest.byteArray[0] = 1;
        toTest.byteArray[1] = 99;
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.byteArray = new byte[0];
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        // String[]
        toTest = new MyTest();
        toTest.stringArray = new String[1];
        toTest.stringArray[0] = "A";
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.stringArray = new String[2];
        toTest.stringArray[0] = "A";
        toTest.stringArray[1] = "B";
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.stringArray = new String[2];
        toTest.stringArray[0] = "A";
        toTest.stringArray[1] = null;
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.stringArray = new String[2];
        toTest.stringArray[0] = null;
        toTest.stringArray[1] = "A";
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.stringArray = new String[0];
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.stringArray = new String[2];
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));
    }

    @Test
    public void testCollection() {

        class MyTest {
            @NotEmptyIfNotNull
            List<String> list;

            @NotEmptyIfNotNull
            Set<String> set;
        }

        MyTest toTest;

        // List
        toTest = new MyTest();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.list = new ArrayList<String>();
        toTest.list.add("A");
        toTest.list.add("B");
        toTest.list.add("C");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.list = new ArrayList<String>();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.list = new ArrayList<String>();
        toTest.list.add("A");
        toTest.list.add(null);
        toTest.list.add("C");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        // Set
        toTest = new MyTest();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.set = new HashSet<String>();
        toTest.set.add("A");
        toTest.set.add("B");
        toTest.set.add("C");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.set = new HashSet<String>();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.set = new HashSet<String>();
        toTest.set.add("A");
        toTest.set.add(null);
        toTest.set.add("C");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));
    }

    @Test
    public void testMap() {

        class MyTest {
            @NotEmptyIfNotNull
            Map<String, String> map;
        }

        MyTest toTest;

        toTest = new MyTest();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.map = new HashMap<String, String>();
        toTest.map.put("A", "a");
        toTest.map.put("B", "b");
        toTest.map.put("C", "c");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.map = new HashMap<String, String>();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.map = new HashMap<String, String>();
        toTest.map.put("A", null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.map = new HashMap<String, String>();
        toTest.map.put(null, null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.map = new HashMap<String, String>();
        toTest.map.put(null, "A");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.map = new HashMap<String, String>();
        toTest.map.put("A", "a");
        toTest.map.put("B", "b");
        toTest.map.put("C", null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.map = new HashMap<String, String>();
        toTest.map.put("A", "a");
        toTest.map.put("B", "b");
        toTest.map.put(null, "c");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));
    }

}
