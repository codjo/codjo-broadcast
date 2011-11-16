/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.diffuser;
import java.io.File;
import java.util.Arrays;
import junit.framework.TestCase;
/**
 */
public class DiffuserManagerTest extends TestCase {
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private DiffuserManager manager;


    public void test_defaultDiffuser() throws Exception {
        assertEquals("[CFT, NONE]", Arrays.asList(manager.getDiffusersCode()).toString());
        Diffuser dif = manager.buildDiffuser("CFT", TEMP_DIR + "/BOBO");

        assertNotNull(dif);
        assertTrue(dif instanceof CFTDiffuser);
        assertEquals(TEMP_DIR + "BOBO", ((CFTDiffuser)dif).getCFTBatchFile().toString());

        assertNull(manager.buildDiffuser("NONE", null));
    }


    public void test_buildDiffuser_error() throws Exception {
        try {
            manager.buildDiffuser("TOTO", TEMP_DIR + "/BOBO");
            fail("TOTO est un type de diffuseur inconnu");
        }
        catch (IllegalArgumentException ex) {
        }
    }


    public void test_declare_and_build() throws Exception {
        manager.declareDiffuser("MOCK", MockDiffuser.class);
        assertEquals("[CFT, MOCK, NONE]", Arrays.asList(manager.getDiffusersCode()).toString());

        Diffuser dif = manager.buildDiffuser("MOCK", TEMP_DIR + "/BOBO");

        assertNotNull(dif);
        assertTrue(dif instanceof MockDiffuser);
    }


    public void test_declare_error() throws Exception {
        try {
            manager.declareDiffuser("BAD", BadMockDiffuser.class);
            fail("La declaration echoue car la classe ne possede pas de constructeur valide");
        }
        catch (NoSuchMethodException ex) {
        }

        try {
            manager.declareDiffuser("CFT", MockDiffuser.class);
            fail("La declaration echoue car le diffuseur est deja defini");
        }
        catch (IllegalArgumentException ex) {
        }
    }


    @Override
    protected void setUp() throws Exception {
        manager = new DiffuserManager();
    }


    public static class BadMockDiffuser implements Diffuser {
        public BadMockDiffuser() {
        }


        public void diffuse(DiffuserContext ctxt, File file) {
        }
    }

    public static class MockDiffuser implements Diffuser {
        public MockDiffuser(String arg) {
        }


        public void diffuse(DiffuserContext ctxt, File file) {
        }
    }
}
