/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import net.codjo.broadcast.common.BroadcastException;
import net.codjo.broadcast.common.Context;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

interface FileGenerator {
    public File generate(Context context, Connection connection)
          throws IOException, SQLException, BroadcastException;
}
