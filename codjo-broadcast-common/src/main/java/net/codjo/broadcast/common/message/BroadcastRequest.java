/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.message;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.JobRequestWrapper;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 */
public class BroadcastRequest extends JobRequestWrapper {
    public static final String BROADCAST_JOB_TYPE = "broadcast";
    static final String FILE_NAME = "broadcast.fileName";
    static final String DEST_FOLDER = "broadcast.dest.folder";
    static final String DATE = "broadcast.date";


    public BroadcastRequest(JobRequest request) {
        super(BROADCAST_JOB_TYPE, request);
    }


    public BroadcastRequest() {
        this(new JobRequest());
    }


    public void setDestinationFile(File destinationFile) {
        setArgument(FILE_NAME, destinationFile.getName());
        setArgument(DEST_FOLDER, destinationFile.getParent());
    }


    public File getDestinationFile() {
        return new File(getDestinationFolder(), getArgument(FILE_NAME));
    }


    public File getDestinationFolder() {
        return new File(getArgument(DEST_FOLDER));
    }


    public void setBroadcastDate(Date date) {
        setArgument(DATE, new SimpleDateFormat("yyyy-MM-dd").format(date));
    }


    public Date getBroadcastDate() {
        return java.sql.Date.valueOf(getArgument(DATE));
    }
}
