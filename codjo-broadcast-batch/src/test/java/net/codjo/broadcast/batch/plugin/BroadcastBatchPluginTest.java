/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.batch.plugin;
import net.codjo.plugin.batch.BatchCore;
import net.codjo.plugin.common.CommandLineArguments;
import net.codjo.workflow.common.batch.AbstractWorkflowBatchPlugin;
import net.codjo.workflow.common.batch.AbstractWorkflowBatchPluginTestCase;

public class BroadcastBatchPluginTest extends AbstractWorkflowBatchPluginTestCase {

    @Override
    protected String getExpectedType() {
        return "broadcast";
    }


    @Override
    protected String getExpectedLogContentAfterExecute() {
        return "job<broadcast>("
               + "broadcast.date=2004-01-25, "
               + "broadcast.dest.folder=directory, "
               + "broadcast.fileName=fileName, "
               + "myArg1=myArg1Value"
               + ")";
    }


    @Override
    protected CommandLineArguments buildCommandLineArguments() {
        return new CommandLineArguments(new String[]{
              "-" + BatchCore.BATCH_DATE, "2004-01-25",
              "-" + BatchCore.BATCH_ARGUMENT, "directory\\fileName",
              "-myArg1", "myArg1Value"
        });
    }


    @Override
    protected AbstractWorkflowBatchPlugin createPlugin() {
        return new BroadcastBatchPlugin();
    }
}
