/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.broadcast.common.Broadcaster;
import net.codjo.broadcast.common.ConnectionProvider;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.PreferencesManager;
import net.codjo.broadcast.server.BroadcasterHome;
import net.codjo.sql.server.JdbcServiceUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
/**
 *
 */
class DefaultJobConfig implements JobConfig {
    private final PreferencesManager preferencesManager;
    private final AgentConnectionProvider connectionProvider;
    private BroadcasterHome broadcasterHome;


    DefaultJobConfig(PreferencesManager preferencesManager,
                     JdbcServiceUtil jdbcServiceUtil) {
        this.preferencesManager = preferencesManager;
        connectionProvider = new AgentConnectionProvider(jdbcServiceUtil);
    }


    public void init(Agent anAgent, AclMessage aMessage) {
        connectionProvider.init(anAgent, aMessage);
    }


    public Broadcaster[] getBroadcastersFor(String fileName, Context context)
          throws SQLException {
        return getBroadcasterHome().getAllBroadcasterByFileName(fileName, context);
    }


    public Context buildContext(final String user, final String fileName,
                                final Date generationDate, final Date broadcastDate, final File outFolder) {
        return preferencesManager.buildContext(user, fileName, generationDate,
                                               broadcastDate, outFolder);
    }


    BroadcasterHome getBroadcasterHome() throws SQLException {
        if (broadcasterHome == null) {
            broadcasterHome = new BroadcasterHome(connectionProvider, preferencesManager);
        }
        return broadcasterHome;
    }


    private static class AgentConnectionProvider implements ConnectionProvider {
        private final JdbcServiceUtil jdbcServiceUtil;
        private Agent agent;
        private AclMessage message;


        AgentConnectionProvider(JdbcServiceUtil jdbcServiceUtil) {
            this.jdbcServiceUtil = jdbcServiceUtil;
        }


        public void init(Agent anAgent, AclMessage aMessage) {
            agent = anAgent;
            message = aMessage;
        }


        public Connection getConnection() throws SQLException {
            return jdbcServiceUtil.getConnectionPool(agent, message).getConnection();
        }


        public void releaseConnection(Connection con)
              throws SQLException {
            jdbcServiceUtil.getConnectionPool(agent, message).releaseConnection(con);
        }
    }
}
