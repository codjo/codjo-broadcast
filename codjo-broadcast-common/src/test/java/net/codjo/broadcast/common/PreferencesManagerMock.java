/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.test.common.LogString;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Mock de {@link PreferencesManager}.
 */
public class PreferencesManagerMock extends PreferencesManager {
    private LogString log;


    public PreferencesManagerMock() {
        this(new LogString());
    }


    public PreferencesManagerMock(LogString logString) {
        //noinspection deprecation
        super("fileTable", "fileContentsTable", "sectionTable", "columnTable");
        this.log = logString;
    }


    @Override
    public Context buildContext(final String user, final String fileName,
                                final Date generationDate, final Date broadcastDate, final File outFolder) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        log.call("buildContext", user, fileName, format.format(generationDate),
                 format.format(broadcastDate) + ", " + outFolder);
        Context context =
              super.buildContext(user, fileName, generationDate, broadcastDate, outFolder);
        context.putParameter("broadcastDate", format.format(broadcastDate));
        return context;
    }
}
