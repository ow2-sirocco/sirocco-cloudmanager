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

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHelper;

public class IdentifierValidatorTest {

    @Test
    public void testIdentifier() {

        class MyTest {
            @Identifier
            String name;

            public void setName(final String name) {
                this.name = name;
            }
        }
        MyTest toTest = new MyTest();
        Assert.assertNull(toTest.name);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("a");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("z");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("Z");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("0");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("9");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName(".");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("-");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("/");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_abcdefghijklmnopqrstuvwxyz");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_0123456789");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_AAA+BBB");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_AAA-BBB");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_AAA/BBB");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));
    }

}
