/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import net.codjo.broadcast.common.Broadcaster;
import net.codjo.broadcast.common.BroadcasterMock;
import net.codjo.broadcast.common.ConnectionProviderMock;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.PreferencesManagerMock;
import net.codjo.test.common.LogString;
import java.sql.SQLException;
/**
 *
 */
public class BroadcasterHomeMock extends BroadcasterHome {
    private LogString logString;
    private Broadcaster[] broadcasters;
    private SQLException getAllBroadcasterByFileNameFailure;


    public BroadcasterHomeMock(LogString logString)
          throws SQLException {
        super(new ConnectionProviderMock(), new PreferencesManagerMock());
        this.logString = logString;
        broadcasters =
              new Broadcaster[]{new BroadcasterMock(new LogString("Broadcaster", logString))};
    }


    public Broadcaster[] getAllBroadcasterByFileName(String fileName, Context context)
          throws SQLException {
        logString.call("getAllBroadcasterByFileName", fileName);
        if (getAllBroadcasterByFileNameFailure != null) {
            getAllBroadcasterByFileNameFailure.fillInStackTrace();
            throw getAllBroadcasterByFileNameFailure;
        }
        return broadcasters;
    }


    public void mockGetAllBroadcasterByFileName(Broadcaster[] broadcastersMock) {
        this.broadcasters = broadcastersMock;
    }


    public void mockGetAllBroadcasterByFileNameFailure(SQLException error) {
        getAllBroadcasterByFileNameFailure = error;
    }
}
