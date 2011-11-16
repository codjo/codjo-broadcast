package net.codjo.broadcast.server.api;
import java.io.File;
import java.util.Date;
import net.codjo.broadcast.common.Context;
/**
 *
 */
public class DefaultBroadcastContextBuilder implements BroadcastContextBuilder {
    private String user;
    private String fileName;
    private Date generationDate;
    private Date broadcastDate;
    private File outFolder;


    public Context buildContext() {
        return new Context(new java.sql.Date(broadcastDate.getTime()));
    }


    public Date getBroadcastDate() {
        return broadcastDate;
    }


    public void setBroadcastDate(Date broadcastDate) {
        this.broadcastDate = broadcastDate;
    }


    public String getUser() {
        return user;
    }


    public void setUser(String user) {
        this.user = user;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public Date getGenerationDate() {
        return generationDate;
    }


    public void setGenerationDate(Date generationDate) {
        this.generationDate = generationDate;
    }


    public File getOutFolder() {
        return outFolder;
    }


    public void setOutFolder(File outFolder) {
        this.outFolder = outFolder;
    }
}
