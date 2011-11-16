package net.codjo.broadcast.server.audit;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobRequest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
/**
 *
 */
public class BroadcastStringifierTest {
    private BroadcastStringifier stringifier = new BroadcastStringifier();


    @Test
    public void test_toString() throws Exception {
        Arguments arguments = new Arguments("broadcast.fileName", "toto.txt");
        arguments.put("broadcast.dest.folder", "dest-folder");
        arguments.put("broadcast.date", "20100101");

        assertEquals("toto.txt", stringifier.toString(new JobRequest("broadcast", arguments)));
    }
}
