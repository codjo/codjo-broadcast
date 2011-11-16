package net.codjo.broadcast.server.api;
import net.codjo.util.file.FileUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
/**
 *
 */
public class SqlUtil {
    private static final Logger LOG = Logger.getLogger(SqlUtil.class);


    private SqlUtil() {
    }


    public static String loadQuery(URL url) throws SQLException {
        try {
            return FileUtil.loadContent(url);
        }
        catch (IOException cause) {
            SQLException exception = new SQLException("Impossible de charger la requete");
            exception.initCause(cause);
            throw exception;
        }
    }


    public static void deleteTable(Connection con, String tableName)
          throws SQLException {
        Statement stmt = con.createStatement();
        try {
            LOG.debug("vidage de la table : " + tableName);
            stmt.executeUpdate("delete " + tableName);
        }
        finally {
            stmt.close();
        }
    }


    public static void dropTable(Connection con, String tableName)
          throws SQLException {
        Statement stmt = con.createStatement();
        try {
            LOG.debug("supression de la table : " + tableName);
            stmt.executeUpdate("drop table " + tableName);
        }
        catch (SQLException ex) {
            LOG.info("La table temporaire : " + tableName + " n'a pas été supprimé "
                     + ex.getMessage());
        }
        finally {
            stmt.close();
        }
    }
}
