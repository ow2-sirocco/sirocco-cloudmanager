package org.ow2.sirocco.apis.rest.cimi.request;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertNull(cimi.getLastAttribute());
        Assert.assertNull(cimi.getLastExpressionArray());
        Assert.assertNull(cimi.getLastNumericArray());

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
        Assert.assertEquals("attrOne", cimi.getLastAttribute());
        Assert.assertNull(cimi.getLastExpressionArray());
        Assert.assertNull(cimi.getLastNumericArray());

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
        Assert.assertEquals("attrTwo", cimi.getLastAttribute());
        Assert.assertNull(cimi.getLastExpressionArray());
        Assert.assertNull(cimi.getLastNumericArray());

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
        Assert.assertEquals("attrTwo", cimi.getLastAttribute());
        Assert.assertNull(cimi.getLastExpressionArray());
        Assert.assertNull(cimi.getLastNumericArray());

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
        Assert.assertEquals("attrOne", cimi.getLastAttribute());
        Assert.assertNull(cimi.getLastExpressionArray());
        Assert.assertNotNull(cimi.getLastNumericArray());
        Assert.assertEquals(2, cimi.getLastNumericArray().size());
        Assert.assertEquals(10, cimi.getLastNumericArray().get(0).intValue());
        Assert.assertEquals(10, cimi.getLastNumericArray().get(1).intValue());

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
        Assert.assertEquals("attrOne", cimi.getLastAttribute());
        Assert.assertNull(cimi.getLastExpressionArray());
        Assert.assertNotNull(cimi.getLastNumericArray());
        Assert.assertEquals(2, cimi.getLastNumericArray().size());
        Assert.assertEquals(7, cimi.getLastNumericArray().get(0).intValue());
        Assert.assertEquals(13, cimi.getLastNumericArray().get(1).intValue());

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
        Assert.assertEquals("attrOne", cimi.getLastAttribute());
        Assert.assertNotNull(cimi.getLastExpressionArray());
        Assert.assertEquals("expOne > 13", cimi.getLastExpressionArray());
        Assert.assertNull(cimi.getLastNumericArray());

        // Multi select with expression array
        selects = new ArrayList<String>();
        selects.add("attrOne[expOne > 13]");
        selects.add("attrTwo[expTwo = 7]");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertTrue(cimi.isArrayPresent());
        Assert.assertTrue(cimi.isExpressionArrayPresent());
        Assert.assertFalse(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(2, cimi.getAttributes().size());
        Assert.assertEquals("attrTwo", cimi.getLastAttribute());
        Assert.assertNotNull(cimi.getLastExpressionArray());
        Assert.assertEquals("expTwo = 7", cimi.getLastExpressionArray());
        Assert.assertNull(cimi.getLastNumericArray());

        // Multi select with expression and numeric array
        selects = new ArrayList<String>();
        selects.add("attrOne[expOne > 13]");
        selects.add("attrTwo[expTwo = 7]");
        selects.add("attrThree[25-103]");

        cimi.setSelects(selects);
        Assert.assertFalse(cimi.isEmpty());
        Assert.assertTrue(cimi.isArrayPresent());
        Assert.assertFalse(cimi.isExpressionArrayPresent());
        Assert.assertTrue(cimi.isNumericArrayPresent());
        Assert.assertNotNull(cimi.getAttributes());
        Assert.assertEquals(3, cimi.getAttributes().size());
        Assert.assertEquals("attrThree", cimi.getLastAttribute());
        Assert.assertNull(cimi.getLastExpressionArray());
        Assert.assertNotNull(cimi.getLastNumericArray());
        Assert.assertEquals(2, cimi.getLastNumericArray().size());
        Assert.assertEquals(25, cimi.getLastNumericArray().get(0).intValue());
        Assert.assertEquals(103, cimi.getLastNumericArray().get(1).intValue());
    }

    @Test
    public void testPattern() {
        String regex = "^[a-zA-Z_0-9]+$";
        Pattern p = Pattern.compile(regex);

        Assert.assertTrue(p.matcher("aaaaab").matches());
        Assert.assertTrue(p.matcher("aaa999aab").matches());
        Assert.assertTrue(p.matcher("aaa9_9_9aab").matches());
        Assert.assertTrue(p.matcher("____").matches());
        Assert.assertTrue(p.matcher("0123456789").matches());
        Assert.assertTrue(p.matcher("ABCDEF_XYZ").matches());
        Assert.assertTrue(p.matcher("abcdef_xyz").matches());

        Assert.assertFalse(p.matcher("ab.cdef_xyz").matches());
    }
}
