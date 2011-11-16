/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import junit.framework.TestCase;
/**
 * Classe de test de PreferencesManager.
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
public class PreferencesManagerTest extends TestCase {
    PreferencesManager prefMan;


    public PreferencesManagerTest(String name) {
        super(name);
    }


    /**
     * Test que l'objet PreferencesManager sait construire toutes les methodes de diffusion qu'il connait.
     *
     * @throws Exception
     */
    public void test_buildDiffuser() throws Exception {
        String[] codes = prefMan.getDiffusersCode();
        int index = 0;
        try {
            for (; index < codes.length; index++) {
                prefMan.buildDiffuser(codes[index], "toto.bat");
            }
        }
        catch (Throwable e) {
            fail("Inconsistance L'objet preference ne sait pas construire : " + codes[index]);
        }
    }


    @Override
    protected void setUp() {
        //noinspection deprecation
        prefMan = new PreferencesManager("FILE", "CONTENT", "SECTION", "COLUMNS");
    }
}
