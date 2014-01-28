/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server.plugin;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.broadcast.common.Broadcaster;
import net.codjo.broadcast.common.BroadcasterMock;
import net.codjo.broadcast.common.Context;
import net.codjo.test.common.LogString;
/**
 * Classe de mock de {@link JobConfig}.
 */
class JobConfigMock implements JobConfig {
    private LogString log;
    private Broadcaster[] broadcasters;
    private SQLException getBroadcastersForFailure;
    private static final String BROADCAST_DATE = "broadcastDate";


    JobConfigMock(LogString log) {
        this.log = log;
        broadcasters =
              new Broadcaster[]{new BroadcasterMock(new LogString("Broadcaster", log))};
    }


    public void init(Agent agent, AclMessage message) {
        log.call("init", "agent:" + agent.getAID().getLocalName(),
                 "message:" + AclMessage.performativeToString(message.getPerformative()));
    }


    public Broadcaster[] getBroadcastersFor(String fileName, Context context)
          throws SQLException {
        log.call("getBroadcastersFor", fileName, "context(" + context.getParameter(BROADCAST_DATE) + ")");
        if (getBroadcastersForFailure != null) {
            getBroadcastersForFailure.fillInStackTrace();
            throw getBroadcastersForFailure;
        }
        return broadcasters;
    }


    public Context buildContext(final String user, final String fileName,
                                final Date generationDate, final Date broadcastDate, final File outFolder) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        log.call("buildContext", user, fileName, format.format(generationDate),
                 format.format(broadcastDate) + ", " + outFolder);
        Context context = new Context();
        context.putParameter(BROADCAST_DATE, format.format(broadcastDate));
        return context;
    }


    public void mockGetBroadcastersFor(Broadcaster[] broadcastersMock) {
        broadcasters = broadcastersMock;
    }


    public void mockGetBroadcastersForFailure(SQLException error) {
        getBroadcastersForFailure = error;
    }


    public void mockWarnings(String warnings) {
        for (Broadcaster b : broadcasters) {
            if (b instanceof BroadcasterMock) {
                ((BroadcasterMock)b).mockWarnings(warnings);
            }
        }
    }
}
