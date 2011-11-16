package net.codjo.broadcast.server.api;
import java.io.File;
import java.util.Date;
import net.codjo.broadcast.common.Context;
/**
 *
 */
public interface BroadcastContextBuilder {

    Context buildContext();


    Date getBroadcastDate();


    void setBroadcastDate(Date broadcastDate);


    String getUser();


    void setUser(String user);


    String getFileName();


    void setFileName(String fileName);


    Date getGenerationDate();


    void setGenerationDate(Date generationDate);


    File getOutFolder();


    void setOutFolder(File outFolder);
}
