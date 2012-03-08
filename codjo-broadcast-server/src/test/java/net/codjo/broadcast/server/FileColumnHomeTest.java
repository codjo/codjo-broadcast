package net.codjo.broadcast.server;
import fakedb.FakeResultSet;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.PreferencesForTesting;
import net.codjo.broadcast.common.PreferencesManager;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.datagen.DatagenFixture;
import net.codjo.sql.builder.FieldInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.math.BigDecimal.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/**
 *
 */
public class FileColumnHomeTest {
    private static final DatagenFixture DATAGEN = new DatagenFixture(BroadcasterHomeTest.class);
    private static final int CONTENT_ID = 1000;
    private static final int FILE_ID = 1000;
    private static final int SECTION_ID = 1000;
    private Connection connection;
    private FileColumnHome fileColumnHome;
    private JdbcFixture jdbc = JdbcFixture.newFixture();
    private PreferencesManager prefManager;
    private Preferences preference = new PreferencesForTesting();


    @Test
    public void test_buildFieldInfo() throws Exception {
        ResultSet rs = new FakeResultSet(new Object[][]{
              {"COLUMN_NUMBER", "DB_FIELD_NAME", "DB_TABLE_NAME"},
              {new Integer("10"), "CODE", "AP_VALUATION_TYPE as VALUATION_FRE_VALUATION_TY_REF"},
              {new Integer("70"), "VALUATION_DATE", "AP_FUND_PRICE"},
              {new Integer("20"), "LABEL", "AP_COMMERCIAL as FUND_TYPE_COMMERCIAL_REF"},
              {new Integer("30"), "SICOVAM_CODE", "AP_PORTFOLIO_CODIFICATION"},
              {new Integer("40"), "LABEL", "AP_PORTFOLIO_CODIFICATION"},
              {new Integer("50"), "CODE", "AP_LEGAL as MASTER_SON_LEGAL_REF"},
              {new Integer("60"), "NET_FUND_PRICE", "AP_FUND_PRICE"},
              {new Integer("80"), "PERF_YTD", "#COMPUTED_VL"},
              {new Integer("90"), "NET_DIVIDEND", "AP_DIVIDEND"},
              {new Integer("100"), "CODE", "AP_VALUATION_TYPE as BALANCE_CURRE_VALUATION_TY_REF"},
              {new Integer("110"), "DIVIDEND_DATE", "AP_DIVIDEND"},
              {new Integer("120"), "LABEL", "AP_COMMERCIAL as RISK_LEVEL_COMMERCIAL_REF"}
        }).getStub();
        Map<FieldInfo, FieldInfo> map = new HashMap<FieldInfo, FieldInfo>();
        for (int i = 0; i < 12; i++) {
            rs.next();
            fileColumnHome.buildFieldInfo(map, rs);
        }

        assertEquals(12, map.size());
    }


    @Test
    public void test_loadFileColumns() throws Exception {
        Statement statement = connection.createStatement();

        // Creation de la section
        insertSection(statement, SECTION_ID);
        initDataCols(statement, SECTION_ID);

        // Creation du fichier
        insertIntoFile(statement, FILE_ID, "File_TestTU");
        insertIntoFileContent(statement, FILE_ID, SECTION_ID, CONTENT_ID);

        //chargement des colonnes
        FileColumnGenerator[] cols =
              fileColumnHome.loadFileColumns(connection, valueOf(CONTENT_ID), preference, new Context());

        //vérification du contenu
        FieldInfo info = cols[0].getFieldInfo();
        assertEquals("ligne 1 string (COL)", "DB_FIELD_NAME", info.getDBFieldName());
        assertEquals("ligne 1 string (TABLE)", prefManager.getColumnsTableName(), info.getDBTableName());

        info = cols[1].getFieldInfo();
        assertEquals("ligne 2 number (COL)", "COLUMNS_ID", info.getDBFieldName());
        assertEquals("ligne 2 number (TABLE)", prefManager.getColumnsTableName(), info.getDBTableName());

        info = cols[2].getFieldInfo();
        assertEquals("ligne 3 date (COL)(COMPUTED)", "DATE_HEURE", info.getDBFieldName());
        assertEquals("ligne 3 date (TABLE)(COMPUTED)",
                     preference.getComputedTableName(),
                     info.getDBTableName());

        info = cols[3].getFieldInfo();
        assertEquals("ligne 4 date (COL)(COMPUTED)", "DATE_HEURE", info.getDBFieldName());
        assertEquals("ligne 4 date (TABLE)(COMPUTED)",
                     preference.getComputedTableName(),
                     info.getDBTableName());

        assertTrue("Verifie que le HOME detecte que Les 2 colonnes utilisent le meme FI",
                   cols[2].getFieldInfo() == cols[3].getFieldInfo());
    }


    @BeforeClass
    public static void setUpGlobal() throws Exception {
        DATAGEN.doSetUp();
        DATAGEN.generate();
    }


    @AfterClass
    public static void tearDownGlobal() throws Exception {
        DATAGEN.doTearDown();
    }


    @Before
    public void setUp() throws Exception {
        jdbc.doSetUp();
        jdbc.advanced().dropAllObjects();

        create("PM_BROADCAST_FILES");
        create("PM_BROADCAST_FILE_CONTENTS");
        create("PM_BROADCAST_SECTION");
        create("PM_BROADCAST_COLUMNS");

        prefManager = new PreferencesManager("PM_BROADCAST_FILES",
                                             "PM_BROADCAST_FILE_CONTENTS",
                                             "PM_BROADCAST_SECTION",
                                             "PM_BROADCAST_COLUMNS",
                                             Collections.<String, Object>emptyMap());
        prefManager.addPreferences(preference);

        connection = jdbc.getConnection();
        connection.setAutoCommit(false);

        fileColumnHome = new FileColumnHome();
        fileColumnHome.init(connection, prefManager);
    }


    @After
    public void tearDown() throws Exception {
        jdbc.doTearDown();
    }


    private void initDataCols(Statement stmt, int sectionID)
          throws SQLException {
        //column string
        stmt.executeUpdate("insert into " + prefManager.getColumnsTableName()
                           + " (COLUMNS_ID, SECTION_ID, DB_TABLE_NAME, DB_FIELD_NAME, COLUMN_NAME,"
                           + " RIGHT_COLUMN_PADDING, PADDING_CARACTER, COLUMN_DATE_FORMAT,COLUMN_NUMBER,"
                           + " BREAK_FIELD)"
                           + " values(1, " + sectionID + ",'PM_BROADCAST_COLUMNS','DB_FIELD_NAME','Champ'"
                           + ", 0, null,null,1,1)");

        //column number
        stmt.executeUpdate("insert into " + prefManager.getColumnsTableName()
                           + " (COLUMNS_ID, SECTION_ID, DB_TABLE_NAME, DB_FIELD_NAME, COLUMN_NAME,"
                           + " RIGHT_COLUMN_PADDING, PADDING_CARACTER, COLUMN_DATE_FORMAT,COLUMN_NUMBER,"
                           + " COLUMN_NUMBER_FORMAT, BREAK_FIELD)"
                           + " values(2, " + sectionID + ",'PM_BROADCAST_COLUMNS','COLUMNS_ID','Id'"
                           + ", 0, null,null,3, '0',1)");

        //column date : COMPUTED
        stmt.executeUpdate("insert into " + prefManager.getColumnsTableName()
                           + " (COLUMNS_ID, SECTION_ID," + " DB_TABLE_NAME, DB_FIELD_NAME, COLUMN_NAME,"
                           + " RIGHT_COLUMN_PADDING, PADDING_CARACTER, COLUMN_DATE_FORMAT,COLUMN_NUMBER,"
                           + " BREAK_FIELD)"
                           + " values(3, " + sectionID + ",'" + preference.getComputedTableName() + "'"
                           + ",'DATE_HEURE','Date de génération'" + ", 1, ' ','dd-mm-yy',4,0)");

        // Insertion une deuxieme fois de (column date : COMPUTED) pour verifier
        // la gestion des FieldInfo
        stmt.executeUpdate("insert into " + prefManager.getColumnsTableName()
                           + " (COLUMNS_ID, SECTION_ID," + " DB_TABLE_NAME, DB_FIELD_NAME, COLUMN_NAME,"
                           + " RIGHT_COLUMN_PADDING, PADDING_CARACTER, COLUMN_DATE_FORMAT,COLUMN_NUMBER,"
                           + " BREAK_FIELD)"
                           + " values(4, " + sectionID + ",'" + preference.getComputedTableName() + "'"
                           + ",'DATE_HEURE','Date de génération'" + ", 1, ' ','dd-mm-yy',5,0)");
    }


    private void insertIntoFile(Statement stmt, int id, String fileName) throws Exception {
        stmt.executeUpdate("insert into " + prefManager.getFileTableName() + " values ("
                           + id + ", '" + fileName + "', 'GCP', '.', 0, null, 1"
                           + ",'NONE', 0, null, 0 )");
    }


    private void insertIntoFileContent(Statement stmt, int fileId, int sectionId, int contentId)
          throws Exception {
        int position = (fileId + contentId);
        stmt.executeUpdate("insert into " + prefManager.getFileContentsTableName()
                           + " values (" + contentId + "," + fileId + "," + sectionId + "," + position
                           + ", 0, null, null, 0 )");
    }


    private void insertSection(Statement stmt, int sectionId) throws Exception {
        stmt.executeUpdate("insert into " + prefManager.getSectionTableName()
                           + " ( SECTION_ID, SELECTION_ID, FAMILY, FIXED_LENGTH, DECIMAL_SEPARATOR)"
                           + " values(" + sectionId + ", 0, 'TestTU', 0 , '.')");
    }


    private void create(String tableName) {
        jdbc.advanced().executeCreateTableScriptFile(new File(DATAGEN.getSqlPath(), tableName + ".tab"));
    }
}
