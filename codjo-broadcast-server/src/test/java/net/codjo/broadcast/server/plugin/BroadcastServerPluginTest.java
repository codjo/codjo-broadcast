/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server.plugin;
import net.codjo.agent.ContainerConfigurationMock;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.broadcast.common.Context;
import static net.codjo.broadcast.common.message.BroadcastRequest.BROADCAST_JOB_TYPE;
import net.codjo.broadcast.server.api.BroadcastContextBuilder;
import net.codjo.broadcast.server.api.DefaultBroadcastContextBuilder;
import net.codjo.test.common.LogString;
import java.io.File;
import java.util.Date;
import junit.framework.TestCase;
/**
 * Classe de test de {@link BroadcastServerPlugin}.
 */
public class BroadcastServerPluginTest extends TestCase {
    private BroadcastServerPlugin plugin;
    private LogString log = new LogString();
    private AgentContainerFixture fixture = new AgentContainerFixture();


    public void test_other() throws Exception {
        plugin.initContainer(new ContainerConfigurationMock(log));
        plugin.stop();
        log.assertContent("");
    }


    public void test_start() throws Exception {
        plugin.start(fixture.getContainer());

        fixture.assertNumberOfAgentWithService(2, BROADCAST_JOB_TYPE);
        fixture.assertAgentWithService(new String[]{"broadcast-drh-agent", "broadcast-job-agent"},
                                       BROADCAST_JOB_TYPE);
    }


    public void test_configuration_broadcastContextBuilder() throws Exception {
        BroadcastContextBuilder builder = plugin.getConfiguration().getBroadcastContextBuilder();
        assertNotNull(builder);

        DefaultBroadcastContextBuilder contextBuilder = new DefaultBroadcastContextBuilder() {
            @Override
            public Context buildContext() {
                log.call("buildContext");
                return super.buildContext();
            }
        };
        plugin.getConfiguration().setBroadcastContextBuilder(contextBuilder);

        Context context = plugin.getPreferenceManager()
              .buildContext("oggy", "ooo.txt", new Date(0), new Date(1), new File("/bobo"));

        log.assertContent("buildContext()");
        assertNotNull(context);
        assertEquals(0, contextBuilder.getGenerationDate().getTime());
        assertEquals(1, contextBuilder.getBroadcastDate().getTime());
        assertEquals("oggy", contextBuilder.getUser());
        assertEquals("ooo.txt", contextBuilder.getFileName());
        assertEquals(new File("/bobo"), contextBuilder.getOutFolder());
    }


    @Override
    protected void setUp() throws Exception {
        fixture.doSetUp();
        plugin = new BroadcastServerPlugin();
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.doTearDown();
    }
}
