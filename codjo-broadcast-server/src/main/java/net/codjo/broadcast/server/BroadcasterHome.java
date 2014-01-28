/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import net.codjo.broadcast.common.Broadcaster;
import net.codjo.broadcast.common.ConnectionProvider;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.PostBroadcaster;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.PreferencesManager;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
import net.codjo.broadcast.common.diffuser.Diffuser;
/**
 * Maison mère des <code>Broadcaster</code>.
 */
public class BroadcasterHome {
    private PreferencesManager prefManager;
    private FileColumnHome columnHome;
    private ConnectionProvider connectionProvider;


    /**
     * @throws NullPointerException cp ou prefManager null
     */
    public BroadcasterHome(ConnectionProvider cp, PreferencesManager prefManager) throws SQLException {
        if (cp == null || prefManager == null) {
            throw new NullPointerException();
        }

        this.prefManager = prefManager;
        this.connectionProvider = cp;
        this.columnHome = new FileColumnHome();
        Connection con = cp.getConnection();
        try {
            columnHome.init(con, prefManager);
        }
        finally {
            cp.releaseConnection(con);
        }
    }


    /**
     * Retourne tous les broadcaster automatique du home pour une famille donnée.
     *
     * @return La valeur de allBroadcaster
     *
     * @throws NullPointerException family null
     */
    public Broadcaster[] getAllAutomaticBroadcaster(String family, Context context) throws SQLException {
        if (family == null) {
            throw new NullPointerException();
        }
        String query =
              "select * from $broadcast.fileTable$ " + " where FILE_ID in ("
              + "   select $broadcast.fileTable$.FILE_ID "
              + "   from $broadcast.fileTable$ "
              + "    inner join $broadcast.fileContentsTable$ "
              + "    on $broadcast.fileTable$.FILE_ID =  $broadcast.fileContentsTable$.FILE_ID "
              + "    inner join $broadcast.sectionTable$ "
              + "    on $broadcast.fileContentsTable$.SECTION_ID = $broadcast.sectionTable$.SECTION_ID "
              + "   where AUTO_DISTRIBUTION = 1 and FAMILY = ? "
              + "   group by $broadcast.fileTable$.FILE_ID " + ")";
        return buildAllBroadcaster(query, family, context);
    }


    /**
     * Retourne tous les broadcaster pour un nom de fichier. NB : les noms de fichiers peuvent contenir des variables
     * definies dans le Context.
     *
     * @param fileName Le nom de fichier
     *
     * @return Les broadcasters.
     *
     * @throws SQLException         Erreur SQL
     * @throws NullPointerException filename null
     */
    public Broadcaster[] getAllBroadcasterByFileName(String fileName, Context context) throws SQLException {
        if (fileName == null) {
            throw new NullPointerException();
        }
        String query = "select * from $broadcast.fileTable$ where FILE_NAME = ?";
        return buildAllBroadcaster(query, fileName, context);
    }


    /**
     * Retourne tous les broadcaster pour un systeme de destination.
     *
     * @param destinationSystem Le systeme de destination
     *
     * @return Les broadcasters.
     *
     * @throws SQLException         Erreur SQL
     * @throws NullPointerException destinationSystem null
     */
    public Broadcaster[] getAllBroadcasterForSystem(String destinationSystem, Context context)
          throws SQLException {
        if (destinationSystem == null) {
            throw new NullPointerException();
        }
        String query = "select * from $broadcast.fileTable$ where DESTINATION_SYSTEM = ?";
        return buildAllBroadcaster(query, destinationSystem, context);
    }


    public Broadcaster getBroadcaster(long distributedFileId, Context context)
          throws SQLException {
        Connection con = connectionProvider.getConnection();
        try {
            Statement stmt = con.createStatement();

            try {
                ResultSet fileRs =
                      stmt.executeQuery("select * from " + prefManager.getFileTableName()
                                        + " where FILE_ID = " + distributedFileId);
                if (fileRs.next()) {
                    return buildBroadcaster(con, fileRs, context);
                }
                else {
                    throw new IllegalArgumentException(
                          "Impossible de trouver le paramétrage pour l'ID >"
                          + distributedFileId + "<");
                }
            }
            finally {
                stmt.close();
            }
        }
        finally {
            connectionProvider.releaseConnection(con);
        }
    }


    private Context getRootContext() {
        return prefManager.getRootContext();
    }


    /**
     * Avec BCP on ne peut pas inserer le caractere tabulation dans une colonne.
     *
     * @return le separateur.
     */
    private String bugBcp(String columnSeparator) {
        if ("\\t".equals(columnSeparator)) {
            return "\t";
        }
        return columnSeparator;
    }


    private Broadcaster[] buildAllBroadcaster(String query, String argument, Context context)
          throws SQLException {
        if (query == null) {
            throw new NullPointerException("query parameter is null");
        }
        List<Broadcaster> allBroadcaster = new ArrayList<Broadcaster>();

        Connection con = connectionProvider.getConnection();
        try {
            PreparedStatement stmt =
                  con.prepareStatement(getRootContext().replaceVariables(query));
            try {
                stmt.setString(1, argument);
                ResultSet fileRs = stmt.executeQuery();

                while (fileRs.next()) {
                    allBroadcaster.add(buildBroadcaster(con, fileRs, context));
                }
            }
            finally {
                stmt.close();
            }
        }
        finally {
            connectionProvider.releaseConnection(con);
        }

        return allBroadcaster.toArray(new Broadcaster[allBroadcaster.size()]);
    }


    private FileColumnGenerator[] buildAllColumnGenerator(Connection con,
                                                          ResultSet sectionRs,
                                                          Preferences sectionPreferences,
                                                          Context context) throws SQLException {
        return columnHome
              .loadFileColumns(con, sectionRs.getBigDecimal("CONTENT_ID"), sectionPreferences, context);
    }


    private FileSectionGenerator[] buildAllSectionGenerator(Connection con,
                                                            ResultSet fileRs,
                                                            List<PostBroadcaster> postBroadcasterList,
                                                            Context context)
          throws SQLException {
        List<FileSectionGenerator> allSections = new ArrayList<FileSectionGenerator>();

        Statement stmt = con.createStatement();
        try {
            ResultSet sectionRs =
                  stmt.executeQuery(getRootContext().replaceVariables("select * "
                                                                      + " from $broadcast.fileContentsTable$ inner join $broadcast.sectionTable$ "
                                                                      + " on $broadcast.fileContentsTable$.SECTION_ID = $broadcast.sectionTable$.SECTION_ID "
                                                                      + " where FILE_ID = "
                                                                      + fileRs.getBigDecimal("FILE_ID")
                                                                      + " order by SECTION_POSITION "));
            while (sectionRs.next()) {
                postBroadcasterList.clear();
                allSections.add(buildSectionGenerator(con,
                                                      fileRs.getBigDecimal("FILE_ID"), sectionRs,
                                                      postBroadcasterList, context));
            }
        }
        finally {
            stmt.close();
        }
        return allSections.toArray(new FileSectionGenerator[allSections
              .size()]);
    }


    private Broadcaster buildBroadcaster(Connection con, ResultSet fileRs, Context context)
          throws SQLException {
        List<PostBroadcaster> postBroadcasterList = new ArrayList<PostBroadcaster>();

        DefaultBroadcaster broadcaster =
              new DefaultBroadcaster(prefManager.getRootContext(),
                                     buildFileGenerator(con, fileRs, postBroadcasterList, context),
                                     new File(fileRs.getString("FILE_DESTINATION_LOCATION"),
                                              fileRs.getString("FILE_NAME")),
                                     buildDiffuser(fileRs.getString("DISTRIBUTION_METHOD"),
                                                   fileRs.getString("CFT_BATCH_FILE")),
                                     connectionProvider);

        broadcaster.setHistoriseFile(fileRs.getBoolean("HISTORISE_FILE"));

        // PostBroadCast
        broadcaster.setPostBroadcaster(postBroadcasterList);

        return broadcaster;
    }


    /**
     * Construit le <code>Diffuser</code> approprié.
     *
     * @return un diffuseur
     */
    private Diffuser buildDiffuser(String diffuserCode, String batchFile) {
        return prefManager.buildDiffuser(diffuserCode, batchFile);
    }


    private FileGenerator buildFileGenerator(Connection con,
                                             ResultSet fileRs,
                                             List<PostBroadcaster> postBroadcasterList,
                                             Context context) throws SQLException {
        return new DefaultFileGenerator(buildAllSectionGenerator(con, fileRs, postBroadcasterList, context),
                                        fileRs.getBoolean("FILE_HEADER"),
                                        fileRs.getString("FILE_HEADER_TEXT"),
                                        fileRs.getBoolean("SECTION_SEPARATOR"));
    }


    private FileSectionGenerator buildSectionGenerator(Connection con,
                                                       BigDecimal fileId,
                                                       ResultSet sectionRs,
                                                       List<PostBroadcaster> postBroadcasterList,
                                                       Context context)
          throws SQLException {
        Preferences sectionPreferences =
              prefManager.getPreferences(sectionRs.getString("FAMILY"));

        // Construction du postBroadcaster
        PostBroadcaster postBroad =
              sectionPreferences.buildPostBroadcaster(con, fileId,
                                                      sectionRs.getBigDecimal("CONTENT_ID"),
                                                      sectionRs.getBigDecimal("SECTION_ID"));
        if (postBroad != null) {
            postBroadcasterList.add(postBroad);
        }

        // Construction de la section
        DefaultFileSectionGenerator section =
              new DefaultFileSectionGenerator(sectionPreferences,
                                              sectionRs.getString("SECTION_NAME"),
                                              sectionPreferences.buildSelector(con,
                                                                               sectionRs.getBigDecimal(
                                                                                     "CONTENT_ID"),
                                                                               sectionRs.getBigDecimal(
                                                                                     "SECTION_ID"),
                                                                               sectionRs.getBigDecimal(
                                                                                     "SELECTION_ID")),
                                              new DefaultComputedFieldGenerator(sectionPreferences),
                                              new DefaultQueryBuilder(sectionPreferences),
                                              buildAllColumnGenerator(con, sectionRs, sectionPreferences,
                                                                      context)
              );
        section.setColumnHeader(sectionRs.getBoolean("COLUMN_HEADER"));
        section.setColumnSeparator(bugBcp(sectionRs.getString("COLUMN_SEPARATOR")));
        if (sectionRs.getBoolean("SECTION_HEADER")) {
            section.setSectionHeader(sectionRs.getString("SECTION_HEADER_TEXT"));
        }
        return section;
    }
}
