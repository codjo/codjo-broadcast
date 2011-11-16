package net.codjo.broadcast.server;
import net.codjo.broadcast.common.ConnectionProvider;
import net.codjo.broadcast.common.ConnectionProviderMock;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.PostBroadcaster;
import net.codjo.broadcast.common.diffuser.Diffuser;
import net.codjo.broadcast.common.diffuser.DiffuserContext;
import net.codjo.test.common.fixture.DirectoryFixture;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
/**
 *
 */
public class DefaultBroadcasterTest extends TestCase {
    private DirectoryFixture directoryFixture = DirectoryFixture.newTemporaryDirectoryFixture();
    private File destinationFile;
    private FakeDiffuser diffuser;
    private FakeFileGenerator fileGenerator;
    private File firstHistorisedFile;
    private File secondHistorisedFile;
    private Context context;
    private Context initialContext;
    private ConnectionProvider connectionProvider;


    public void test_broadcast_NotHistorise() throws Exception {
        DefaultBroadcaster broadcaster = new DefaultBroadcaster(initialContext,
                                                                fileGenerator,
                                                                destinationFile,
                                                                null,
                                                                connectionProvider);

        broadcaster.broadcast(context);

        assertTrue("Le fichier existe", destinationFile.exists());
        assertFalse(firstHistorisedFile.exists());
        assertFalse(secondHistorisedFile.exists());
    }


    public void test_broadcast_NotHistorise_replaced()
          throws Exception {
        DefaultBroadcaster broadcaster = new DefaultBroadcaster(initialContext,
                                                                fileGenerator,
                                                                destinationFile,
                                                                null,
                                                                connectionProvider);

        broadcaster.broadcast(context);
        long first = destinationFile.lastModified();

        Thread.sleep(100);

        broadcaster.broadcast(context);
        long second = destinationFile.lastModified();

        assertTrue("Le fichier existe", destinationFile.exists());
        assertTrue("Le premier fichier est ecrasé", first != second);
    }


    public void test_broadcast_calls_PostBroadcast()
          throws Exception {
        DefaultBroadcaster broadcaster = new DefaultBroadcaster(initialContext,
                                                                fileGenerator,
                                                                destinationFile,
                                                                diffuser,
                                                                connectionProvider);

        FakePostBroadcast postA = new FakePostBroadcast();
        FakePostBroadcast postB = new FakePostBroadcast();
        List<PostBroadcaster> list = toList(postA, postB);

        broadcaster.setPostBroadcaster(list);

        broadcaster.broadcast(context);

        assertTrue("postA", postA.isProceedCalled);
        assertFalse("postA(undo)", postA.isUndoProceedCalled);
        assertTrue("postB", postB.isProceedCalled);
        assertFalse("postB(undo)", postB.isUndoProceedCalled);
    }


    public void test_broadcast_calls_PostBroadcast_Error()
          throws Exception {
        DefaultBroadcaster broadcaster = new DefaultBroadcaster(initialContext,
                                                                fileGenerator,
                                                                destinationFile,
                                                                diffuser,
                                                                connectionProvider);

        FakePostBroadcast postA = new FakePostBroadcast();
        FakePostBroadcast postB = new FakePostBroadcast(new SQLException());
        List<PostBroadcaster> list = toList(postA, postB);

        broadcaster.setPostBroadcaster(list);
        try {
            broadcaster.broadcast(context);
        }
        catch (Exception e) {
            // Normal car un postB echoue.
        }

        assertTrue("postA", postA.isProceedCalled);
        assertTrue("postA(undo)", postA.isUndoProceedCalled);
        assertTrue("postB", postB.isProceedCalled);
        assertFalse("postB(undo)", postB.isUndoProceedCalled);
    }


    public void test_broadcast_calls_withDiffuser()
          throws Exception {
        DefaultBroadcaster broadcaster = new DefaultBroadcaster(initialContext,
                                                                fileGenerator,
                                                                destinationFile,
                                                                diffuser,
                                                                connectionProvider);

        broadcaster.broadcast(context);

        assertEquals(fileGenerator.isCalled, true);
        assertEquals(diffuser.isCalled, true);
    }


    public void test_broadcast_calls_withoutDiffuser()
          throws Exception {
        DefaultBroadcaster broadcaster = new DefaultBroadcaster(initialContext,
                                                                fileGenerator,
                                                                destinationFile,
                                                                null,
                                                                connectionProvider);

        broadcaster.broadcast(context);

        assertEquals(fileGenerator.isCalled, true);
    }


    public void test_broadcast_historise() throws Exception {
        DefaultBroadcaster broadcaster = new DefaultBroadcaster(initialContext,
                                                                fileGenerator,
                                                                destinationFile,
                                                                null,
                                                                connectionProvider);
        broadcaster.setHistoriseFile(true);

        broadcaster.broadcast(context);
        broadcaster.broadcast(context);

        assertEquals("Le premier fichier existe", firstHistorisedFile.exists(), true);
        assertEquals("Le deuxieme fichier existe", secondHistorisedFile.exists(), true);
        assertEquals("Deuxieme fichier est historise", secondHistorisedFile.getName(),
                     "TU_BOBO_" + toDay() + "001" + ".txt");
    }


    public void test_realDestinationFileName_withHistoric()
          throws Exception {
        DefaultBroadcaster broadcaster = new DefaultBroadcaster(initialContext,
                                                                fileGenerator,
                                                                destinationFile,
                                                                null,
                                                                connectionProvider);
        broadcaster.setHistoriseFile(true);
        assertEquals("Historise, donc avec la date dans le nom",
                     broadcaster.realDestinationFileName(broadcaster.getDestinationFile(context)),
                     "TU_BOBO_" + toDay() + ".txt");
    }


    public void test_realDestinationFileName_withHistoric_NoSuffix()
          throws Exception {
        DefaultBroadcaster broadcaster = new DefaultBroadcaster(initialContext,
                                                                fileGenerator,
                                                                new File("Bobo"),
                                                                null,
                                                                connectionProvider);
        broadcaster.setHistoriseFile(true);
        assertEquals("Historise, donc avec la date dans le nom",
                     broadcaster.realDestinationFileName(broadcaster.getDestinationFile(context)),
                     "Bobo_" + toDay());
    }


    public void test_realDestinationFileName_withoutHistoric()
          throws Exception {
        DefaultBroadcaster broadcaster = new DefaultBroadcaster(initialContext,
                                                                fileGenerator,
                                                                destinationFile,
                                                                null,
                                                                connectionProvider);
        assertEquals("Non historiser, donc le meme nom",
                     broadcaster.realDestinationFileName(broadcaster.getDestinationFile(context)),
                     "TU_BOBO.txt");
    }


    @Override
    protected void setUp() throws Exception {
        fileGenerator = new FakeFileGenerator();
        diffuser = new FakeDiffuser();

        directoryFixture.doSetUp();
        destinationFile = new File(directoryFixture, "TU_BOBO.txt");
        firstHistorisedFile = new File(directoryFixture, "TU_BOBO_" + toDay() + ".txt");
        secondHistorisedFile = new File(directoryFixture, "TU_BOBO_" + toDay() + "001" + ".txt");

        context = new Context();
        initialContext = new Context();
        connectionProvider = new ConnectionProviderMock();
    }


    private String toDay() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
    }


    @Override
    protected void tearDown() throws java.lang.Exception {
        directoryFixture.doTearDown();
    }


    private List<PostBroadcaster> toList(FakePostBroadcast postA, FakePostBroadcast postB) {
        List<PostBroadcaster> list = new ArrayList<PostBroadcaster>();
        list.add(postA);
        list.add(postB);
        return list;
    }


    private static final class FakeDiffuser implements Diffuser {
        boolean isCalled = false;


        public void diffuse(DiffuserContext ctxt, File file) {
            isCalled = true;
        }
    }

    private static final class FakeFileGenerator implements FileGenerator {
        File generatedFile;
        boolean isCalled = false;


        public File generate(Context context, Connection connection) throws IOException {
            generatedFile = File.createTempFile("ORBIS_TU_", null);
            generatedFile.deleteOnExit();
            isCalled = true;
            return generatedFile;
        }
    }

    private static final class FakePostBroadcast implements PostBroadcaster {
        SQLException exception = null;
        boolean isProceedCalled = false;
        boolean isUndoProceedCalled = false;


        FakePostBroadcast() {
        }


        FakePostBroadcast(SQLException exception) {
            this.exception = exception;
        }


        public void proceed(Context context, Connection connection)
              throws SQLException {
            isProceedCalled = true;
            if (exception != null) {
                throw exception;
            }
        }


        public void undoProceed(Context ctxt, Connection connection) {
            isUndoProceedCalled = true;
        }
    }
}
