package net.codjo.broadcast.server;
import net.codjo.broadcast.common.Broadcaster;
import net.codjo.broadcast.common.ConnectionProvider;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.PreferencesForTesting;
import net.codjo.broadcast.common.PreferencesManager;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.datagen.DatagenFixture;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 */
public class BroadcasterHomeTest {
    private static final DatagenFixture datagen = new DatagenFixture(BroadcasterHomeTest.class);
    private static final int FILE_ID = 1000;
    private static final String DESTINATION_SYSTEM_FOR_TEST = "JUNIT";
    private static final String TEMPORARY_DIRECTORY = System.getProperty("java.io.tmpdir");
    private Connection connection;
    private BroadcasterHome home;
    private PreferencesManager preferenceManager;
    private JdbcFixture jdbc = JdbcFixture.newFixture();


    @Test
    public void test_getAllAutomaticBroadcaster() throws Exception {
        insertIntoFile(FILE_ID, "Bobo_s_Parametrage");
        insertIntoFileContent(FILE_ID, 1);

        Broadcaster[] all = home.getAllAutomaticBroadcaster("TestTU", new Context());

        assertEquals("Un seul Broadcaster en base de type TestTU", 1, all.length);
        assertEquals(all[0].getDestinationFile(preferenceManager.getRootContext()).getName(),
                     "Bobo_s_Parametrage");
    }


    @Test
    public void test_getAllBroadcasterForSystem() throws Exception {
        insertIntoFile(FILE_ID, "Bobo_s_Parametrage", TEMPORARY_DIRECTORY, DESTINATION_SYSTEM_FOR_TEST);
        insertIntoFileContent(FILE_ID, 1);

        Broadcaster[] all = home.getAllBroadcasterForSystem(DESTINATION_SYSTEM_FOR_TEST, new Context());

        assertEquals(1, all.length);
        assertEquals(all[0].getDestinationFile(preferenceManager.getRootContext()).getName(),
                     "Bobo_s_Parametrage");
    }


    @Test
    public void test_getAllAutomaticBroadcaster_noBuffer() throws Exception {
        insertIntoFile(FILE_ID, "Bobo_s_Parametrage");
        insertIntoFileContent(FILE_ID, 1);

        Broadcaster[] all = home.getAllAutomaticBroadcaster("TestTU", new Context());

        Broadcaster[] bis = home.getAllAutomaticBroadcaster("TestTU", new Context());

        assertTrue("Les broadcaster ne sont pas bufferise", bis != all);
    }


    @Test
    public void test_getBroadcaster() throws Exception {
        insertIntoFile(FILE_ID, "Gex_s_Parametrage");
        Broadcaster broadcaster = home.getBroadcaster(FILE_ID, new Context());
        assertEquals(broadcaster.getDestinationFile(preferenceManager.getRootContext()).getName(),
                     "Gex_s_Parametrage");
        try {
            home.getBroadcaster(FILE_ID + 1, new Context());
            fail("le broadcaster n'existe pas");
        }
        catch (Exception e) {
            assertEquals("Impossible de trouver le paramétrage pour l'ID >" + (FILE_ID + 1) + "<",
                         e.getMessage());
        }
    }


    @Test
    public void test_getAllBroadcasterByFileName() throws Exception {
        String fileName = "Gex_s_Parametrage";
        insertIntoFile(FILE_ID, fileName);
        Broadcaster[] broadcasters = home.getAllBroadcasterByFileName(fileName, new Context());
        assertEquals("taille OK ", 1, broadcasters.length);
        assertEquals(fileName,
                     broadcasters[0].getDestinationFile(preferenceManager.getRootContext()).getName());
    }


    @Test
    public void test_getAllBroadcasterByFileName_VariableFileName() throws Exception {
        String fileName = "Gex_s__$period$_Parametrage";
        insertIntoFile(FILE_ID, fileName);
        Broadcaster[] broadcasters = home.getAllBroadcasterByFileName(fileName, new Context());
        assertEquals("taille OK ", 1, broadcasters.length);
        assertEquals(broadcasters[0].getDestinationFile(preferenceManager.getRootContext()).getName(),
                     "Gex_s__200412_Parametrage");
    }


    /**
     * Verifie que le contexte initial est correctement donne a un broadcaster.
     */
    @Test
    public void test_getBroadcaster_InitialContext() throws Exception {
        insertIntoFile(FILE_ID, "Gex_s_Parametrage", TEMPORARY_DIRECTORY + "\\$broadcast.fileTable$");
        Broadcaster broadcaster = home.getBroadcaster(FILE_ID, new Context());
        assertEquals("Gex_s_Parametrage",
                     broadcaster.getDestinationFile(preferenceManager.getRootContext()).getName());
        assertEquals(TEMPORARY_DIRECTORY + preferenceManager.getFileTableName(),
                     broadcaster.getDestinationFile(preferenceManager.getRootContext()).getParentFile()
                           .toString());
    }


    @BeforeClass
    public static void setUpGlobal() throws Exception {
        datagen.doSetUp();
        datagen.generate();
    }


    @AfterClass
    public static void tearDownGlobal() throws Exception {
        datagen.doTearDown();
    }


    @Before
    public void setUp() throws Exception {
        jdbc.doSetUp();
        jdbc.advanced().dropAllObjects();

        create("PM_BROADCAST_FILES");
        create("PM_BROADCAST_FILE_CONTENTS");
        create("PM_BROADCAST_SECTION");
        create("PM_BROADCAST_COLUMNS");

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("period", "200412");
        preferenceManager = new PreferencesManager("PM_BROADCAST_FILES",
                                                   "PM_BROADCAST_FILE_CONTENTS",
                                                   "PM_BROADCAST_SECTION",
                                                   "PM_BROADCAST_COLUMNS",
                                                   variables);
        preferenceManager.addPreferences(new PreferencesForTesting());

        home = new BroadcasterHome(new MyProvider(), preferenceManager);

        connection = jdbc.getConnection();
    }


    @After
    public void tearDown() throws Exception {
        jdbc.doTearDown();
    }


    private void insertIntoFile(int id, String fileName) throws Exception {
        insertIntoFile(id, fileName, ".");
    }


    private void insertIntoFile(int id, String fileName, String filePath) throws Exception {
        insertIntoFile(id, fileName, filePath, DESTINATION_SYSTEM_FOR_TEST);
    }


    private void insertIntoFile(int id, String fileName, String filePath, String destinationSystem)
          throws Exception {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("insert into " + preferenceManager.getFileTableName()
                           + " values (" + id + ", '" + fileName + "', '" + destinationSystem + "', "
                           + "'" + filePath + "', 0, null, 1" + ",'NONE', 0, null, 0)");
    }


    private void insertIntoFileContent(int fileId, int position) throws Exception {
        Statement stmt = connection.createStatement();
        int contentId = (fileId + position);
        int sectionId = (fileId + position);
        stmt.executeUpdate("insert into " + preferenceManager.getSectionTableName()
                           + " values (" + sectionId + ", 'sectionName', 0, 'TestTU', 0"
                           + ", null, '.' )");
        stmt.executeUpdate("insert into " + preferenceManager.getFileContentsTableName()
                           + " values (" + contentId + "," + fileId + "," + sectionId + "," + position
                           + ", 0, null, null, 0 )");
    }


    private void create(String tableName) {
        jdbc.advanced().executeCreateTableScriptFile(new File(datagen.getSqlPath(), tableName + ".tab"));
    }


    private class MyProvider implements ConnectionProvider {
        public Connection getConnection() throws SQLException {
            return jdbc.getConnection();
        }


        public void releaseConnection(Connection con) throws SQLException {
        }
    }
}
