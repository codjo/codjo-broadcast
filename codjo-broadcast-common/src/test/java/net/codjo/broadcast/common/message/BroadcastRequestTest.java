/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.message;
import java.io.File;
import net.codjo.workflow.common.message.JobRequestWrapperTestCase;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.JobRequestWrapper;
/**
 * Classe de test de {@link BroadcastRequest}.
 */
public class BroadcastRequestTest extends JobRequestWrapperTestCase {
    public void test_createFromJobRequest() {
        Arguments requestArguments = new Arguments();
        requestArguments.put(BroadcastRequest.FILE_NAME, "fileName.txt");
        requestArguments.put(BroadcastRequest.DEST_FOLDER, "/opt/tmp");
        requestArguments.put(BroadcastRequest.DATE, "2001-12-31");

        BroadcastRequest request =
              new BroadcastRequest(new JobRequest("", requestArguments));

        assertEquals(new File("/opt/tmp/fileName.txt"), request.getDestinationFile());
        assertEquals(new File("/opt/tmp"), request.getDestinationFolder());
        assertEquals(java.sql.Date.valueOf("2001-12-31"), request.getBroadcastDate());

        assertEquals(requestArguments.encode(),
                     request.toRequest().getArguments().encode());
        assertSame(requestArguments, request.getArguments());
    }


    public void test_setters() throws Exception {
        Arguments arguments = new Arguments();
        arguments.put(BroadcastRequest.FILE_NAME, "fileName.txt");
        arguments.put(BroadcastRequest.DEST_FOLDER, "\\opt\\tmp");
        arguments.put(BroadcastRequest.DATE, "2001-12-31");

        BroadcastRequest broadcastRequest = new BroadcastRequest();
        broadcastRequest.setDestinationFile(new File("/opt/tmp/fileName.txt"));
        broadcastRequest.setBroadcastDate(java.sql.Date.valueOf("2001-12-31"));

        JobRequest jobRequest = broadcastRequest.toRequest();
        assertEquals(arguments.encode(), jobRequest.getArguments().encode());
        assertEquals(BroadcastRequest.BROADCAST_JOB_TYPE, jobRequest.getType());
    }


    @Override
    protected String getJobRequestType() {
        return BroadcastRequest.BROADCAST_JOB_TYPE;
    }


    @Override
    protected JobRequestWrapper createWrapper(JobRequest jobRequest) {
        return new BroadcastRequest(jobRequest);
    }
}
