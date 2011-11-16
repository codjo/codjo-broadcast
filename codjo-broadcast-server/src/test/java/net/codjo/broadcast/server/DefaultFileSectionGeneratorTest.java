package net.codjo.broadcast.server;
import net.codjo.broadcast.common.ComputedFieldGenerator;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.PreferencesForTesting;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
import net.codjo.sql.builder.FieldInfo;
import net.codjo.sql.builder.TableName;
import fakedb.FakeDriver;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
/**
 *
 */
public class DefaultFileSectionGeneratorTest extends TestCase {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private FakeColumnGenerator columnA;
    private FakeColumnGenerator columnB;
    private FakeComputedField computedField;
    private Context context;
    private FakeQueryBuilder queryBuilder;
    private DefaultFileSectionGenerator sectionGenerator;
    private PreferencesForTesting.FakeSelector selector;
    private Preferences preference;
    private Connection connection;
    private StringWriter result = new StringWriter();
    private PrintWriter writer = new PrintWriter(result);


    /**
     * Verifie la generation : Pas d'en-tete, avec separateur.
     */
    public void test_generate_content_ColumnSeparator() throws Exception {
        sectionGenerator.setColumnSeparator("-");
        sectionGenerator.generate(context, connection, writer);

        assertEquals("A1-B1" + LINE_SEPARATOR + "A2-B2" + LINE_SEPARATOR, result.toString());
    }


    public void test_generate_content_ColumnSeparator_header() throws Exception {
        sectionGenerator.setColumnSeparator("-");
        sectionGenerator.setColumnHeader(true);
        sectionGenerator.generate(context, connection, writer);

        assertEquals("A-B" + LINE_SEPARATOR + "A1-B1" + LINE_SEPARATOR + "A2-B2" + LINE_SEPARATOR,
                     result.toString());
    }


    /**
     * Verifie la generation : en-tete de colonne, pas de separateur.
     */
    public void test_generate_content_columnHeader() throws Exception {
        sectionGenerator.setColumnHeader(true);
        sectionGenerator.generate(context, connection, writer);

        assertEquals("AB" + LINE_SEPARATOR + "A1B1" + LINE_SEPARATOR + "A2B2" + LINE_SEPARATOR,
                     result.toString());
    }


    /**
     * Verifie la generation : en-tete de section, pas de separateur.
     */
    public void test_generate_content_sectionHeader() throws Exception {

        sectionGenerator.setSectionHeader("eau de $name$");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "cristaline");
        context = new Context(map);
        sectionGenerator.generate(context, connection, writer);

        assertEquals("eau de cristaline" + LINE_SEPARATOR
                     + "A1B1" + LINE_SEPARATOR
                     + "A2B2" + LINE_SEPARATOR, result.toString());
    }


    /**
     * Verifie la generation : Pas d'en-tete, pas de separateur.
     */
    public void test_generate_content_simple() throws Exception {
        sectionGenerator.generate(context, connection, writer);

        assertEquals("A1B1" + LINE_SEPARATOR + "A2B2" + LINE_SEPARATOR, result.toString());
    }


    /**
     * Verifie que les appels aux accointances sont faits.
     */
    public void test_generate_section_calls() throws Exception {
        FakeDriver.getDriver().pushUpdateConstraint("drop table "
                                                    + preference.getComputedTableName());

        sectionGenerator.generate(context, connection, writer);

        assertTrue(selector.isProceedHasBeenCalled());
        assertTrue(selector.isCleanupHasBeenCalled());
        assertTrue(computedField.hasBeenCalled);
        assertEquals(2, columnA.calledNumber);
        assertEquals(2, columnB.calledNumber);
        assertTrue(queryBuilder.hasBeenCalled);
        assertTrue("Drop de la table des champs calculé",
                   FakeDriver.getDriver().isUpdateConstraintEmpty());
    }


    public void test_generate_withTwoBreakForAllLines() throws Exception {
        DefaultFileSectionGenerator sectionWithBreakGenerator = initFileGenerator(true, true);
        sectionWithBreakGenerator.generate(context, connection, writer);

        assertEquals("AB" + LINE_SEPARATOR
                     + "CD" + LINE_SEPARATOR
                     + "A1B1" + LINE_SEPARATOR
                     + "C1D1" + LINE_SEPARATOR
                     + "A3B2" + LINE_SEPARATOR
                     + "C2D2" + LINE_SEPARATOR, result.toString());
    }


    public void test_generate_withFirstFieldBreakForAllLines() throws Exception {
        DefaultFileSectionGenerator sectionWithBreakGenerator = initFileGenerator(true, false);
        sectionWithBreakGenerator.generate(context, connection, writer);

        assertEquals("AB" + LINE_SEPARATOR
                     + "CD" + LINE_SEPARATOR
                     + "A1B1" + LINE_SEPARATOR
                     + "C1D1" + LINE_SEPARATOR
                     + "A3B1" + LINE_SEPARATOR
                     + "C2D2" + LINE_SEPARATOR, result.toString());
    }


    public void test_generate_withSecondFieldBreakForAllLines() throws Exception {

        DefaultFileSectionGenerator sectionWithBreakGenerator = initFileGenerator(false, true);
        sectionWithBreakGenerator.generate(context, connection, writer);

        assertEquals("AB" + LINE_SEPARATOR
                     + "CD" + LINE_SEPARATOR
                     + "A1B1" + LINE_SEPARATOR
                     + "C1D1" + LINE_SEPARATOR
                     + "A1B3" + LINE_SEPARATOR
                     + "C2D2" + LINE_SEPARATOR, result.toString());
    }


    public void test_generate_withOneBreakForTwoLines() throws Exception {
        DefaultFileSectionGenerator sectionWithBreakGenerator = initFileGenerator(false, false);
        sectionWithBreakGenerator.setColumnSeparator("-");
        sectionWithBreakGenerator.generate(context, connection, writer);

        assertEquals("A-B" + LINE_SEPARATOR
                     + "C-D" + LINE_SEPARATOR
                     + "A1-B1" + LINE_SEPARATOR
                     + "C1-D1" + LINE_SEPARATOR
                     + "C2-D2" + LINE_SEPARATOR, result.toString());
    }


    private DefaultFileSectionGenerator initFileGenerator(boolean isFirstBreakColumnChange,
                                                          boolean isSecondBreakColumnChange) {
        columnA = new FakeColumnGenerator("A", true, isFirstBreakColumnChange);
        columnB = new FakeColumnGenerator("B", true, isSecondBreakColumnChange);
        FakeColumnGenerator columnC = new FakeColumnGenerator("C", false, true);
        FakeColumnGenerator columnD = new FakeColumnGenerator("D", false, true);
        DefaultFileSectionGenerator sectionWithBreakGenerator = new DefaultFileSectionGenerator(preference,
                                                                                                "TU with break field",
                                                                                                selector,
                                                                                                computedField,
                                                                                                queryBuilder,
                                                                                                new FileColumnGenerator[]{
                                                                                                      columnA,
                                                                                                      columnB,
                                                                                                      columnC,
                                                                                                      columnD});

        sectionWithBreakGenerator.setColumnHeader(true);
        return sectionWithBreakGenerator;
    }


    @Override
    protected void setUp() throws SQLException {
        preference = PreferencesForTesting.buildPreferences();
        columnA = new FakeColumnGenerator("A", false, true);
        columnB = new FakeColumnGenerator("B", false, true);

        String sectionName = "section pour TU";
        selector = new PreferencesForTesting.FakeSelector();
        queryBuilder = new FakeQueryBuilder(preference);
        computedField = new FakeComputedField();

        sectionGenerator = new DefaultFileSectionGenerator(preference, sectionName, selector, computedField,
                                                           queryBuilder,
                                                           new FileColumnGenerator[]{columnA, columnB});

        fakeResultSetWith2Rows();
        context = new Context();

        connection = FakeDriver.getDriver().connect("jdbc:fakeDriver", null);
    }


    private void fakeResultSetWith2Rows() {
        Object[][] rs = {{}, {"ligne 1"}, {"ligne 2"}};
        FakeDriver.getDriver().pushResultSet(rs);
    }


    private static final class FakeColumnGenerator implements FileColumnGenerator {
        int calledNumber = 0;
        String value;
        boolean isBreakField;
        boolean isValueChange;


        /**
         * Constructeur de FakeColumnGenerator
         *
         * @param value Description of the Parameter
         */
        FakeColumnGenerator(String value, boolean isBreakField, boolean isValueChange) {
            this.value = value;
            this.isBreakField = isBreakField;
            this.isValueChange = isValueChange;
        }


        public String buildColumnHeader() {
            return value;
        }


        public String proceedField(ResultSet rs) throws SQLException {
            if (isValueChange) {
                calledNumber++;
            } else {
                calledNumber = 1;
            }
            return value + calledNumber;
        }


        public FieldInfo getFieldInfo() {
            return new FieldInfo(new TableName("TABLE"), value, 0);
        }


        public boolean isBreakField() {
            return isBreakField;
        }
    }

    private static final class FakeComputedField implements ComputedFieldGenerator {
        boolean hasBeenCalled = false;


        FakeComputedField() {
        }


        public void generateComputedTable(Context ctxt,
                                          FileColumnGenerator[] fileColumnGenerator,
                                          Connection con) {
            hasBeenCalled = true;
        }
    }

    private static final class FakeQueryBuilder implements QueryBuilder {
        boolean hasBeenCalled = false;
        Preferences preference;


        FakeQueryBuilder(Preferences preference) {
            this.preference = preference;
        }


        public String buildQuery(FileColumnGenerator[] columns) {
            hasBeenCalled = true;
            return "select * from " + preference.getSelectionTableName();
        }
    }
}
