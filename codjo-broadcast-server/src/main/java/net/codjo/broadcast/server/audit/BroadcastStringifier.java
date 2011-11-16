package net.codjo.broadcast.server.audit;
import net.codjo.broadcast.common.message.BroadcastRequest;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.server.plugin.StringifierImpl;
/**
 *
 */
public class BroadcastStringifier extends StringifierImpl {

    public BroadcastStringifier() {
        super(BroadcastRequest.BROADCAST_JOB_TYPE);
    }


    public String toString(JobRequest jobRequest) {
        return new BroadcastRequest(jobRequest).getDestinationFile().getName();
    }
}
