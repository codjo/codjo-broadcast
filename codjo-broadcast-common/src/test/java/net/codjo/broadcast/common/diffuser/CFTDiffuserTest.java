/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.diffuser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import junit.framework.TestCase;
/**
 * Test de la classe CFTDiffuser
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class CFTDiffuserTest extends TestCase {
    private File cftFile;
    private Diffuser diffuser;
    private File fileToDiffuse;
    private File expectedFile;
    private String destDir;


    public void test_diffuse() throws Exception {
        buildFileToDiffuse(fileToDiffuse);
        buildCftBat(destDir);

        diffuser.diffuse(new MockDiffuserContext(), fileToDiffuse);

        assertTrue("présence du fichier : ", expectedFile.exists());
    }


    @Override
    protected void setUp() throws Exception {
        destDir = System.getProperty("java.io.tmpdir");
        cftFile = new File("CFT.bat");
        fileToDiffuse = new File("testDiffusion.txt");
        expectedFile = new File(destDir, fileToDiffuse.getName());

        diffuser = new CFTDiffuser(cftFile);
    }


    @Override
    protected void tearDown() throws Exception {
        delete(expectedFile);
        delete(fileToDiffuse);
        delete(cftFile);
    }


    private void delete(File file) {
        if (file.exists()) {
            assertTrue("Suppression du fichier " + file.getAbsolutePath(), file.delete());
        }
    }


    private void buildCftBat(String dest) throws IOException {
        FileWriter out;
        out = new FileWriter(cftFile);
        out.write("copy %1 " + dest);
        out.close();
    }


    private void buildFileToDiffuse(File file) throws IOException {
        FileWriter out = new FileWriter(file);
        out.write("test");
        out.close();
    }
}
