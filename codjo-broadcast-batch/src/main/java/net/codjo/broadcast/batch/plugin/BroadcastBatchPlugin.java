/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.batch.plugin;
import net.codjo.broadcast.common.message.BroadcastRequest;
import net.codjo.plugin.batch.BatchCore;
import net.codjo.plugin.common.CommandLineArguments;
import net.codjo.workflow.common.batch.AbstractWorkflowBatchPlugin;
import net.codjo.workflow.common.message.JobRequestWrapper;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class BroadcastBatchPlugin extends AbstractWorkflowBatchPlugin {

    private static final Set<String> REQUIRED_PARAMS = new HashSet<String>();


    static {
        Collections.addAll(REQUIRED_PARAMS,
                           BatchCore.BATCH_DATE, BatchCore.BATCH_ARGUMENT);
    }


    @Override
    public String getType() {
        return "broadcast";
    }


    @Override
    protected JobRequestWrapper createRequest(CommandLineArguments arguments) {
        BroadcastRequest request = new BroadcastRequest();
        request.setBroadcastDate(arguments.getDateArgument(BatchCore.BATCH_DATE));
        request.setDestinationFile(arguments.getFileArgument(BatchCore.BATCH_ARGUMENT));

        Iterator<String> it = arguments.getAllArguments();
        while (it.hasNext()) {
            String argName = it.next();
            if (!REQUIRED_PARAMS.contains(argName)) {
                request.getArguments().put(argName, arguments.getArgument(argName));
            }
        }

        return request;
    }
}
