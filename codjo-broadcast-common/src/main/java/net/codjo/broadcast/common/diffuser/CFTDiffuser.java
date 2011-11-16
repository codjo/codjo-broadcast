/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.diffuser;
import net.codjo.util.system.WindowsExec;
import java.io.File;
/**
 * Diffusion d'un fichier par CFT
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.2 $
 */
public class CFTDiffuser implements Diffuser {
    private File cftBatchFile;
    private WindowsExec executor = new WindowsExec();


    public CFTDiffuser(String cftBatchFileName) {
        this(new File(cftBatchFileName));
    }


    public CFTDiffuser(File cftBatchFile) {
        if (cftBatchFile == null) {
            throw new IllegalArgumentException();
        }

        this.cftBatchFile = cftBatchFile;
    }


    public void diffuse(DiffuserContext ctxt, File file)
          throws DiffuserException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException();
        }
        int result =
              executor.exec(ctxt.replaceVariables(cftBatchFile).getAbsolutePath() + " "
                            + file.getName());
        if (result != 0) {
            throw new DiffuserException("Echec de la diffusion par CFT "
                                        + "du fichier : " + file);
        }
    }


    public File getCFTBatchFile() {
        return cftBatchFile;
    }
}
