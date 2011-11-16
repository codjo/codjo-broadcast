/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * DOCUMENT ME!
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public interface ConnectionProvider {
    public Connection getConnection() throws SQLException;


    public void releaseConnection(Connection con)
            throws SQLException;
}
