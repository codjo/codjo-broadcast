/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import net.codjo.broadcast.common.BroadcastException;
import net.codjo.broadcast.common.Broadcaster;
import net.codjo.broadcast.common.ConnectionProvider;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.DiffuserContextAdapter;
import net.codjo.broadcast.common.PostBroadcaster;
import net.codjo.broadcast.common.diffuser.Diffuser;
import net.codjo.broadcast.common.diffuser.DiffuserException;
import net.codjo.util.file.FileUtil;
import org.apache.log4j.Logger;
/**
 * Génère et diffuse un fichier.
 */
class DefaultBroadcaster implements Broadcaster {
    private static final Logger APP = Logger.getLogger(DefaultBroadcaster.class);
    private ConnectionProvider connectionProvider = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private File destinationFileTemplate;
    private Diffuser diffuser;
    private FileGenerator fileGenerator;
    private boolean historiseFile = false;
    private List<PostBroadcaster> postBroadcasterList = new ArrayList<PostBroadcaster>();
    private Context rootContext;


    DefaultBroadcaster(Context rootContext, FileGenerator fileGenerator,
                       File destinationFile, Diffuser diffuser, ConnectionProvider connectionProvider) {
        if (fileGenerator == null || destinationFile == null) {
            throw new IllegalArgumentException();
        }
        this.diffuser = diffuser;
        this.fileGenerator = fileGenerator;
        setDestinationFile(destinationFile);
        this.rootContext = rootContext;
        this.connectionProvider = connectionProvider;
    }


    public void broadcast(Context currentContext)
          throws IOException, SQLException, BroadcastException {
        currentContext.connectTo(rootContext);

        Connection connection = connectionProvider.getConnection();
        String databaseUserName = connection.getMetaData().getUserName();
        if (databaseUserName != null) {
            currentContext.putParameter("dbApplicationUser", databaseUserName);
        }

        File tmpFile = null;
        try {
            tmpFile = fileGenerator.generate(currentContext, connection);
            if (tmpFile == null) {
                return;
            }

            File generatedFile = moveToDestination(currentContext, tmpFile);

            executeAllPostBroadcaster(currentContext, generatedFile, connection);

            executeDiffuser(currentContext, generatedFile);
        }
        finally {
            cleanUp(connection, tmpFile);
        }
    }


    public File getDestinationFile(Context ctxt) {
        return ctxt.replaceVariables(destinationFileTemplate);
    }


    public Diffuser getDiffuser() {
        return diffuser;
    }


    public void setDestinationFile(File destFile) {
        this.destinationFileTemplate = destFile;
    }


    public void setDiffuser(Diffuser diffuser) {
        this.diffuser = diffuser;
    }


    /**
     * Positionne l'attribut historiseFile de DefaultBroadcaster
     *
     * @param newHistoriseFile La nouvelle valeur de historiseFile
     */
    public void setHistoriseFile(boolean newHistoriseFile) {
        historiseFile = newHistoriseFile;
    }


    /**
     * Positionne l'attribut postBroadcaster de DefaultBroadcaster
     *
     * @param pbList La nouvelle valeur de postBroadcaster
     *
     * @throws NullPointerException pbList n'est pas definit
     */
    public void setPostBroadcaster(List<PostBroadcaster> pbList) {
        if (pbList == null) {
            throw new NullPointerException();
        }
        postBroadcasterList = new ArrayList<PostBroadcaster>(pbList);
    }


    String realDestinationFileName(File destinationFile) {
        if (historiseFile) {
            String fileName = destinationFile.getName();
            int insertPos = fileName.indexOf(".");
            if (insertPos == -1) {
                insertPos = fileName.length();
            }
            return fileName.substring(0, insertPos) + "_"
                   + dateFormat.format(new java.util.Date())
                   + fileName.substring(insertPos, fileName.length());
        }
        else {
            return destinationFile.getName();
        }
    }


    private void cancelPostBroadcast(List<PostBroadcaster> executedPostList, Context currentCtxt,
                                     Connection con) {
        for (Object anExecutedPostList : executedPostList) {
            PostBroadcaster post = (PostBroadcaster)anExecutedPostList;
            try {
                post.undoProceed(currentCtxt, con);
            }
            catch (Exception exception) {
                APP.error(exception);
            }
        }
    }


    private void executeAllPostBroadcaster(Context currentCtxt, File generatedFile, Connection connection)
          throws SQLException, BroadcastException {
        if (postBroadcasterList.isEmpty()) {
            return;
        }

        List<PostBroadcaster> executedPostList = new ArrayList<PostBroadcaster>();
        try {
            for (PostBroadcaster postBroadcaster : postBroadcasterList) {
                postBroadcaster.proceed(currentCtxt, connection);
                executedPostList.add(postBroadcaster);
            }
        }
        catch (Exception exception) {
            APP.error(exception);
            generatedFile.delete();
            cancelPostBroadcast(executedPostList, currentCtxt, connection);
            throw new BroadcastException("Erreur durant les post-traitements "
                                         + "de diffusion : La diffusion est annulée", exception);
        }
    }


    private void executeDiffuser(Context currentContext, File generatedFile)
          throws BroadcastException {
        if (diffuser == null) {
            return;
        }

        try {
            diffuser.diffuse(new DiffuserContextAdapter(currentContext), generatedFile);
        }
        catch (DiffuserException ex) {
            throw new BroadcastException(ex);
        }
    }


    /**
     * Deplace le fichier genere dans le repertoire destination (avec historisation).
     *
     * @return Le fichier deplacé
     *
     * @throws IOException Impossible de deplacer (ou historiser) le fichier
     * @see net.codjo.util.file.FileUtil#moveFileTo
     */
    private File moveToDestination(Context ctxt, File generatedFile)
          throws IOException {
        File destinationFile = getDestinationFile(ctxt);

        File tmp =
              new File(generatedFile.getParent(), realDestinationFileName(destinationFile));
        tmp.delete();
        boolean state = generatedFile.renameTo(tmp);
        if (!state) {
            APP.debug("Impossible de renommer le fichier " + generatedFile);
            APP.debug(" en " + tmp);
            APP.debug("source exists ? " + generatedFile.exists());
            APP.debug("destination exists ? " + tmp.exists());
            generatedFile.delete();
            throw new IOException("Impossible de renommer le fichier " + generatedFile);
        }

        try {
            return FileUtil.moveFileTo(destinationFile.getParent(), tmp, historiseFile);
        }
        catch (IOException ioe) {
            tmp.delete();
            throw ioe;
        }
    }


    private void cleanUp(Connection connection, File tmpFile) throws SQLException {
        try {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
        finally {
            connectionProvider.releaseConnection(connection);
        }
    }
}
