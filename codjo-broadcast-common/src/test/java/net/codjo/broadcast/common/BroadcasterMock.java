/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.broadcast.common.diffuser.Diffuser;
import net.codjo.test.common.LogString;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
/**
 *
 */
public class BroadcasterMock implements Broadcaster {
    private LogString logString;


    public BroadcasterMock(LogString logString) {
        this.logString = logString;
    }


    public File getDestinationFile(Context currentContext) {
        return null;
    }


    public void setDestinationFile(File destFile) {
        logString.call("setDestinationFile", destFile);
    }


    public void broadcast(Context currentContext)
          throws IOException, SQLException, BroadcastException {
        logString.call("broadcast", currentContext.getParameters());
    }


    public void setDiffuser(Diffuser diffuser) {
    }


    public Diffuser getDiffuser() {
        return null;
    }
}
