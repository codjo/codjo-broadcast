/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PadderTest extends TestCase {
    public PadderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(PadderTest.class);
    }


    public void test_constructor_failed() throws Exception {
        try {
            new Padder("0", 0, true);
            fail("la largeur de la colonne est invalide");
        }
        catch (IllegalArgumentException e) {}
        try {
            new Padder("0", -8, true);
            fail("la largeur de la colonne est invalide");
        }
        catch (IllegalArgumentException e) {}
        try {
            new Padder(null, 8, true);
            fail("la largeur de la colonne est invalide");
        }
        catch (IllegalArgumentException e) {}
        try {
            new Padder("ee", 8, true);
            fail("la largeur de la colonne est invalide");
        }
        catch (IllegalArgumentException e) {}
    }


    public void test_doPadding_Left() throws Exception {
        Padder padder = new Padder("0", 3, false);
        assertEquals(padder.doPadding("1"), "001");
    }


    public void test_doPadding_Right() throws Exception {
        Padder padder = new Padder("0", 3, true);
        assertEquals(padder.doPadding("1"), "100");
    }


    public void test_doPadding_nop() throws Exception {
        Padder padder = new Padder("0", 3, true);
        assertEquals(padder.doPadding("123"), "123");
    }


    public void test_doPadding_nullValue() throws Exception {
        Padder padder = new Padder("0", 3, true);
        assertEquals(padder.doPadding(null), "000");
    }


    public void test_doPadding_tooGrand() throws Exception {
        Padder padder = new Padder("0", 3, true);
        try {
            padder.doPadding("1234");
            fail("la chaine '1234' depasse la largeur de la colonne");
        }
        catch (IllegalArgumentException e) {}
    }
}
