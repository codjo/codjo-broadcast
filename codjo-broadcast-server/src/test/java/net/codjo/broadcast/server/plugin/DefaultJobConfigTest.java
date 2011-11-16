/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.test.DummyAgent;
import net.codjo.broadcast.common.Broadcaster;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.PreferencesManagerMock;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.sql.server.JdbcServiceUtilMock;
import net.codjo.test.common.LogString;
import java.io.File;
import junit.framework.TestCase;
/**
 * Classe de test de {@link DefaultJobConfig}.
 */
public class DefaultJobConfigTest extends TestCase {
    private JdbcFixture fixture;
    private LogString log = new LogString();
    private DefaultJobConfig config;


    public void test_init() throws Exception {
        fixture.executeUpdate("create table fileTable (FILE_NAME varchar(10))");

        config.init(new DummyAgent(), new AclMessage(AclMessage.Performative.AGREE));
        Broadcaster[] broadcasters = config.getBroadcastersFor("result.txt", new Context());

        assertEquals(0, broadcasters.length);
        assertTrue(log.getContent().contains("getConnectionPool(DummyAgent, message:AGREE)"));
    }


    public void test_buildContext() throws Exception {
        config.buildContext("user", "result.txt", java.sql.Date.valueOf("2006-01-01"),
                            java.sql.Date.valueOf("1998-07-12"), new File("destfolder"));
        log.assertContent(
              "preference.buildContext(user, result.txt, 2006-01-01, 1998-07-12, destfolder)");
    }


    @Override
    protected void setUp() throws Exception {
        PreferencesManagerMock preferencesManager =
              new PreferencesManagerMock(new LogString("preference", log));
        fixture = new DatabaseFactory().createJdbcFixture();
        config =
              new DefaultJobConfig(preferencesManager, new JdbcServiceUtilMock(log, fixture));
        fixture.doSetUp();
        fixture.advanced().dropAllObjects();
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.advanced().dropAllObjects();
        fixture.doTearDown();
    }
}
