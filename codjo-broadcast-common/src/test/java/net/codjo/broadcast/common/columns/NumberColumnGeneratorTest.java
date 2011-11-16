/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
import net.codjo.sql.builder.TableName;
import fakedb.FakeResultSet;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;

public class NumberColumnGeneratorTest extends TestCase {
    private static final FieldInfo fieldInfo = new FieldInfo(new TableName("TABLE_A"), "FIELD_A", 1);


    public NumberColumnGeneratorTest(String name) {
        super(name);
    }


    public void test_with_Expression() throws Exception {
        // Construction du generator
        Padder padder = new Padder("0", 10, false);
        NumberColumnGenerator ncg =
              new NumberColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_A", 1),
                                        "DEST_FIELD", ".", "#,###.00", padder,
                                        new GeneratorExpression(
                                              "iif(Valeur_nulle, \"NA\", outil.format(Valeur * 2) )",
                                              Types.NUMERIC), false);

        // Simulation acces BD
        Object[][] matrix = {{"COL_1", "FIELD_B", "FIELD_C"},
                             {new BigDecimal("5.00"), null, "FININF"},
                             {null, null, "FININF"}};
        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        // Lancement du test
        assertEquals("0000010.00", ncg.proceedField(rs));
        rs.next();
        assertEquals("00000000NA", ncg.proceedField(rs));
    }


    public void test_convertField_fieldNotFound() throws Exception {
        Object[][] matrix = {{"FIELD_A", "FIELD_B", "FIELD_C"},
                             {Date.valueOf("1966-10-10"), null, "FININF"}};

        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        NumberColumnGenerator ncg =
              new NumberColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_Z", 1),
                                        "DEST_FIELD", ".", "#,###.##", null);

        try {
            ncg.proceedField(rs);
            fail("Le test doit echouer ! le champ FIELD_Z n'existe pas");
        }
        catch (SQLException e) {
            // c'est normal !
        }
    }


    public void test_convertField_getFieldAlias() throws Exception {
        NumberColumnGenerator ncg =
              new NumberColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_Z", 1),
                                        "DEST_FIELD", ",", "#,###.##", null);

        assertEquals(ncg.getFieldAlias(), "COL_1");
    }


    public void test_convertField_getFullDbTableName() throws Exception {
        NumberColumnGenerator ncg =
              new NumberColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_Z", 1),
                                        "DEST_FIELD", ",", "#,###.##", null);

        assertEquals(ncg.getFieldInfo().getAlias(), ncg.getFieldAlias());
    }


    public void test_convertField_nullArguments() throws Exception {
        try {

            new NumberColumnGenerator(null, "DEST_FIELD", ".", "#,###.##", null);
            fail("Le test doit echouer : l'argument p0 ne peut pas etre null");
        }
        catch (IllegalArgumentException e) {
            assertEquals("Parametres invalides", e.getMessage());
        }
    }


    public void test_convertField_nullNumber() throws Exception {
        Object[][] matrix = {{"TABLE_A_FIELD_A", "COL_1", "TABLE_A_FIELD_C"},
                             {null, null, "FININF"}};

        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        Padder padder = new Padder(" ", 10, false);

        NumberColumnGenerator ncg =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ".", "#,###.##", null);

        NumberColumnGenerator ncgTwin =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ".", "#,###.##", padder);

        assertEquals(ncg.proceedField(rs), "");
        assertEquals(ncgTwin.proceedField(rs), "          ");
    }


    public void test_proceedFieldBigDecimal() throws Exception {
        Object[][] matrix = {{"COL_1", "TABLE_A_FIELD_B", "TABLE_A_FIELD_C"},
                             {new BigDecimal("12233.2500"), null, "FININF"}};

        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        Padder padder = new Padder("0", 12, false);

        NumberColumnGenerator ncg =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ",", "0.0000", padder);
        NumberColumnGenerator ncgTwin =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ",", "0.0000", null);

        assertEquals(ncg.proceedField(rs), "0012233,2500");
        assertEquals(ncgTwin.proceedField(rs), "12233,2500");
    }


    public void test_proceedFieldBigDecimal_OnlyDecimal() throws Exception {
        Object[][] matrix = {{"COL_1", "TABLE_A_FIELD_B", "TABLE_A_FIELD_C"},
                             {new BigDecimal("0.2500"), null, "FININF"}};

        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        Padder padder = new Padder("0", 12, false);

        NumberColumnGenerator ncg =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ",", "0.0000", padder);
        NumberColumnGenerator ncgTtwin =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ",", "0.0000", null);

        assertEquals(ncg.proceedField(rs), "0000000,2500");
        assertEquals(ncgTtwin.proceedField(rs), "0,2500");
    }


    public void test_proceedFieldDouble() throws Exception {
        Object[][] matrix = {{"COL_1", "TABLE_A_FIELD_B", "TABLE_A_FIELD_C"},
                             {new Double("122.25"), null, "FININF"}};

        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        Padder padder = new Padder("0", 10, false);

        NumberColumnGenerator ncg =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ",", "#,###.##", padder);
        NumberColumnGenerator ncgTwin =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ",", "#,###.##", null);

        assertEquals(ncg.proceedField(rs), "0000122,25");
        assertEquals(ncgTwin.proceedField(rs), "122,25");
    }


    public void test_proceedFieldDouble_NoDecimal() throws Exception {
        Object[][] matrix = {{"COL_1", "TABLE_A_FIELD_B", "TABLE_A_FIELD_C"},
                             {new Double("122"), null, "FININF"}};

        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        Padder padder = new Padder("0", 10, false);

        NumberColumnGenerator ncg =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ".", "#,###.##", padder);
        NumberColumnGenerator ncgTwin =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ".", "#,###.##", null);

        assertEquals(ncg.proceedField(rs), "0000000122");
        assertEquals(ncgTwin.proceedField(rs), "122");
    }


    public void test_proceedFieldDouble_Point() throws Exception {
        Object[][] matrix = {{"COL_1", "TABLE_A_FIELD_B", "TABLE_A_FIELD_C"},
                             {new Double("122.25"), null, "FININF"}};

        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        Padder padder = new Padder("0", 10, false);

        NumberColumnGenerator ncg =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ".", "#,###.##", padder);
        NumberColumnGenerator ncgTwin =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ".", "#,###.##", null);

        assertEquals(ncg.proceedField(rs), "0000122.25");
        assertEquals(ncgTwin.proceedField(rs), "122.25");
    }


    public void test_formatInteger() throws Exception {
        Object[][] matrix = {{"COL_1", "TABLE_A_FIELD_B", "TABLE_A_FIELD_C"},
                             {new Integer("12225"), null, "FININF"}};

        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        Padder padder = new Padder("0", 10, false);

        NumberColumnGenerator ncg =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ",", "#", padder);
        NumberColumnGenerator ncgTwin =
              new NumberColumnGenerator(fieldInfo, "DEST_FIELD", ",", "#", null);

        assertEquals(ncg.proceedField(rs), "0000012225");
        assertEquals(ncgTwin.proceedField(rs), "12225");
    }


    @Override
    protected void setUp() throws java.lang.Exception {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
    }
}
