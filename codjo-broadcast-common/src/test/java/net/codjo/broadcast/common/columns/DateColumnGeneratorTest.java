/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import java.sql.ResultSet;
import net.codjo.sql.builder.FieldInfo;
import net.codjo.sql.builder.TableName;
import fakedb.FakeResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import junit.framework.TestCase;

public class DateColumnGeneratorTest extends TestCase {
    private static final FieldInfo FIELD_INFO = new FieldInfo(new TableName("TABLE_A"), "FIELD_A", 1);

    public void test_formatField() throws Exception {
        Object[][] matrix = {{"COL_1", "COL_2", "TABLE_A_FIELD_B", "TABLE_A_FIELD_C"},
                             {Timestamp.valueOf("1966-10-10 12:00:00"),
                              Timestamp.valueOf("1966-10-10 12:00:00"), null,
                              "FININF"}};

        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();

        Padder padder = new Padder(" ", 18, false);

        DateColumnGenerator dcg = new DateColumnGenerator(FIELD_INFO, "DEST_FIELD", "dd/MM/yyyy", null);
        DateColumnGenerator dcgTwin = new DateColumnGenerator(FIELD_INFO, "DEST_FIELD", "dd/MM/yyyy", padder);

        assertEquals(dcg.proceedField(rs), "10/10/1966");
        assertEquals(dcgTwin.proceedField(rs), "        10/10/1966");
    }


    public void test_proceedField_DateAndTime() throws Exception {
        ResultSet rs =
              new FakeResultSet(new Object[][]{
                    {"COL_1"},
                    {java.sql.Timestamp.valueOf("2001-01-18 16:20:00")}
              }).getStub();

        FieldInfo fieldInfo2 = new FieldInfo(new TableName("#BOBO"), "DATE_HEURE", 1);
        DateColumnGenerator dcg = new DateColumnGenerator(fieldInfo2, "XXX", "dd/MM/yyyy HH:mm", null);

        rs.next();
        assertEquals("18/01/2001 16:20", dcg.proceedField(rs));
    }


    public void test_proceedField_buildColumnHeader() throws Exception {

        DateColumnGenerator dcg = new DateColumnGenerator(FIELD_INFO, "DEST_FIELD", "dd/MM/yyyy", null);

        assertEquals(dcg.buildColumnHeader(), "DEST_FIELD");
    }


    public void test_proceedField_fieldNotFound() throws Exception {
        Object[][] matrix = {{"FIELD_A", "FIELD_B", "FIELD_C"}, {Date.valueOf("1966-10-10"), null, "FININF"}};

        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();

        DateColumnGenerator dcg = new DateColumnGenerator(FIELD_INFO, "DEST_FIELD", "dd/MM/yyyy", null);

        try {
            dcg.proceedField(rs);
            fail("Le test doit echouer ! le champ FIELD_Z n'existe pas");
        }
        catch (SQLException e) {
            assertEquals("Unknown column name : COL_1", e.getMessage());
        }
    }


    public void test_proceedField_getFieldAlias() throws Exception {
        DateColumnGenerator dcg = new DateColumnGenerator(FIELD_INFO, "DEST_FIELD", "dd/MM/yyyy", null);

        assertEquals(dcg.getFieldAlias(), dcg.getFieldInfo().getAlias());
    }


    public void test_proceedField_getFullDBFieldName() throws Exception {
        DateColumnGenerator dcg = new DateColumnGenerator(FIELD_INFO, "DEST_FIELD", "dd/MM/yyyy", null);

        assertEquals(dcg.getFieldInfo().getAlias(), "COL_1");
    }


    public void test_proceedField_nullArguments() throws Exception {
        try {
            new DateColumnGenerator(null, "DEST_FIELD", "dd/MM/yyyy", null);
            fail("Le test doit echouer : l'argument p0 ne peut pas etre null");
        }
        catch (IllegalArgumentException e) {
            assertEquals("Parametres invalides", e.getMessage());
        }
    }


    public void test_proceedField_nullDate() throws Exception {
        Object[][] matrix = {{"TABLE_A_FIELD_A", "COL_1", "TABLE_A_FIELD_C"}, {null, null, "FININF"}};

        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();

        Padder padder = new Padder(" ", 18, false);

        DateColumnGenerator dcg = new DateColumnGenerator(FIELD_INFO, "DEST_FIELD", "dd/MM/yyyy", null);

        DateColumnGenerator dcgTwin =
              new DateColumnGenerator(FIELD_INFO, "DEST_FIELD", "dd/MM/yyyy", padder);

        assertEquals(dcg.proceedField(rs), "");
        assertEquals(dcgTwin.proceedField(rs), "                  ");
    }


    public void test_with_Expression() throws Exception {
        // Construction du generator
        Padder padder = new Padder(" ", 11, false);
        GeneratorExpression expression =
              new GeneratorExpression("iif(Valeur_nulle, \"NA\", outil.format(Valeur ) )", Types.DATE);

        DateColumnGenerator ncg = new DateColumnGenerator(FIELD_INFO, "DEST_FIELD", "dd/MM/yyyy", padder,
                                                          expression, false);

        // Simulation acces BD
        Object[][] matrix = {{"COL_1", "FIELD_B", "FIELD_C"},
                             {java.sql.Date.valueOf("2003-03-18"), null, "FININF"},
                             {null, null, "FININF"}};
        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();

        // Lancement du test
        assertEquals(" 18/03/2003", ncg.proceedField(rs));
        rs.next();
        assertEquals("         NA", ncg.proceedField(rs));
    }
}
