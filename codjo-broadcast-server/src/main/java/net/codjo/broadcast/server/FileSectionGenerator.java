/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import net.codjo.broadcast.common.BroadcastException;
import net.codjo.broadcast.common.Context;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

interface FileSectionGenerator {
    /**
     * @return nombre de lignes générées
     */
    public int generate(Context context, Connection connection, PrintWriter writer)
          throws IOException, SQLException, BroadcastException;
}
