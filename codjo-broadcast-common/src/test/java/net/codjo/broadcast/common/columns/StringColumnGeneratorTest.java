/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import fakedb.FakeResultSet;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import junit.framework.TestCase;
import net.codjo.sql.builder.FieldInfo;
import net.codjo.sql.builder.TableName;

public class StringColumnGeneratorTest extends TestCase {
    public void test_convertField_fieldNotFound()
          throws Exception {
        Object[][] matrix =
              {
                    {"FIELD_A", "FIELD_B", "FIELD_C"},
                    {Date.valueOf("1966-10-10"), null, "FININF"}
              };

        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();

        StringColumnGenerator scg =
              new StringColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_Z", 1),
                                        "DEST_FIELD", null);

        try {
            scg.proceedField(rs);
            fail("Le test doit echouer ! le champ FIELD_Z n'existe pas");
        }
        catch (SQLException e) {
            // c'est normal !
        }
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_convertField_getDbTableName()
          throws Exception {
        StringColumnGenerator scg =
              new StringColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_Z", 1),
                                        "DEST_FIELD", null);

        assertEquals(scg.getFieldAlias(), scg.getFieldInfo().getAlias());
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_convertField_getFieldAlias()
          throws Exception {
        StringColumnGenerator scg =
              new StringColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_Z", 1),
                                        "DEST_FIELD", null);

        assertEquals(scg.getFieldAlias(), "COL_1");
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_convertField_nullArguments() throws Exception {
        try {
            new StringColumnGenerator(new FieldInfo(new TableName(null), "FIELD_Z", 1), "DEST_FIELD", null);
            fail("Le test doit echouer : l'argument p0 ne peut pas etre null");
        }
        catch (IllegalArgumentException e) {
            // c'est normal !
        }
        try {
            new StringColumnGenerator(new FieldInfo(new TableName("TABLE_A"), null, 1), "DEST_FIELD", null);
            fail("Le test doit echouer : l'argument p1 ne peut pas etre null");
        }
        catch (IllegalArgumentException e) {
            // c'est normal !
        }
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_convertField_nullValue() throws Exception {
        Object[][] matrix =
              {
                    {"TABLE_A_FIELD_A", "COL_1", "TABLE_A_FIELD_C"},
                    {null, null, "FININF"}
              };

        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();

        Padder padder = new Padder(" ", 5, false);

        StringColumnGenerator scg =
              new StringColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_B", 1), "DEST_FIELD", null);

        StringColumnGenerator scgTwin =
              new StringColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_B", 1), "DEST_FIELD", padder);

        assertEquals(scg.proceedField(rs), "");
        assertEquals(scgTwin.proceedField(rs), "     ");
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_proceedField() throws Exception {
        Object[][] matrix =
              {
                    {"COL_1", "TABLE_A_FIELD_B", "TABLE_A_FIELD_C"},
                    {"coucou c'est moi", null, null}
              };

        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();

        Padder padder = new Padder(" ", 20, false);

        StringColumnGenerator scg =
              new StringColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_A", 1), "DEST_FIELD", null);
        StringColumnGenerator scgTwin =
              new StringColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_A", 1), "DEST_FIELD", padder);

        assertEquals(scg.proceedField(rs), "coucou c'est moi");
        assertEquals(scgTwin.proceedField(rs), "    coucou c'est moi");
    }


    public void test_with_Expression() throws Exception {
        // Construction du generator
        Padder padder = new Padder("0", 10, false);
        StringColumnGenerator ncg =
              new StringColumnGenerator(new FieldInfo(new TableName("TABLE_A"), "FIELD_A", 1),
                                        "DEST_FIELD", padder,
                                        new GeneratorExpression(
                                              "iif(Valeur_nulle, \"NA\", outil.format(Valeur + \"a\" ) )",
                                              Types.VARCHAR), false);

        // Simulation acces BD
        Object[][] matrix =
              {
                    {"COL_1", "FIELD_B", "FIELD_C"},
                    {"bobo", null, "FININF"},
                    {null, null, "FININF"}
              };
        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();

        // Lancement du test
        assertEquals("00000boboa", ncg.proceedField(rs));
        rs.next();
        assertEquals("00000000NA", ncg.proceedField(rs));
    }
}
