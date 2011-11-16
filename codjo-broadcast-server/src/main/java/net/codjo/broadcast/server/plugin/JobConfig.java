/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.broadcast.common.Broadcaster;
import net.codjo.broadcast.common.Context;
import java.io.File;
import java.sql.SQLException;
import java.util.Date;
/**
 *
 */
interface JobConfig {
    public void init(Agent agent, AclMessage message);


    public Broadcaster[] getBroadcastersFor(String fileName, Context context)
          throws SQLException;


    public Context buildContext(final String user, final String fileName,
                                final Date generationDate, final Date broadcastDate, final File outFolder);
}
