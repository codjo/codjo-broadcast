/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
import net.codjo.sql.builder.TableName;
import fakedb.FakeResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import junit.framework.TestCase;
public class BooleanColumnGeneratorTest extends TestCase {
    FieldInfo fieldInfo = new FieldInfo(new TableName("TABLE_A"), "FIELD_A", 1);


    public BooleanColumnGeneratorTest(String name) {
        super(name);
    }


    public void test_convertField_fieldNotFound() throws Exception {
        Object[][] matrix = {{"FIELD_A", "FIELD_B", "FIELD_C"}, {Date.valueOf("1966-10-10"), null, "FININF"}};

        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        BooleanColumnGenerator bcg = new BooleanColumnGenerator(fieldInfo, "DEST_FIELD", null);

        try {
            bcg.proceedField(rs);
            fail("Le test doit echouer ! le champ FIELD_Z n'existe pas");
        }
        catch (SQLException e) {
            assertEquals("Unknown column name : COL_1", e.getMessage());
        }
    }


    public void test_convertField_getFieldAlias() throws Exception {
        BooleanColumnGenerator bcg = new BooleanColumnGenerator(fieldInfo, "DEST_FIELD", null);

        assertEquals(bcg.getFieldAlias(), fieldInfo.getAlias());
    }


    public void test_convertField_getFullDbFieldName() throws Exception {
        BooleanColumnGenerator bcg = new BooleanColumnGenerator(fieldInfo, "DEST_FIELD", null);

        assertEquals(bcg.getFieldInfo().getFullDBName(), "TABLE_A.FIELD_A");
    }


    public void test_convertField_nullArguments() throws Exception {
        try {
            new BooleanColumnGenerator(null, "DEST_FIELD", null);
            fail("Le test doit echouer : l'argument p0 ne peut pas etre null");
        }
        catch (IllegalArgumentException e) {
            assertEquals("Parametres invalides", e.getMessage());
        }
    }


    public void test_proceedField() throws Exception {
        Object[][] matrix = {{"COL_1", "XXX", "COL_2"}, {Boolean.TRUE, Boolean.TRUE, Boolean.FALSE}};

        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        Padder padder = new Padder(" ", 20, false);

        BooleanColumnGenerator bcg = new BooleanColumnGenerator(fieldInfo, "DEST_FIELD", null);
        
        FieldInfo fieldInfo2 = new FieldInfo(new TableName("TABLE_A"), "FIELD_C", 2);
        BooleanColumnGenerator bcgTwin = new BooleanColumnGenerator(fieldInfo2, "DEST_FIELD", padder);

        assertEquals(bcg.proceedField(rs), "Vrai");
        assertEquals(bcgTwin.proceedField(rs), "                Faux");
    }


    public void test_with_Expression() throws Exception {

        Padder padder = new Padder(" ", 11, false);
        GeneratorExpression expression = new GeneratorExpression(
              "iif(Valeur_nulle, \"NA\", iif(Valeur,\"BOBO\",\"Vrai\"))",
              Types.BIT);
        BooleanColumnGenerator ncg = new BooleanColumnGenerator(fieldInfo, "DEST_FIELD", padder, expression,
                                                                false);

        // Simulation acces BD
        Object[][] matrix = {{"COL_1", "FIELD_B", "FIELD_C"},
                             {Boolean.TRUE, null, "FININF"},
                             {null, null, "FININF"}};
        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();

        // Lancement du test
        assertEquals("       BOBO", ncg.proceedField(rs));
        rs.next();
        assertEquals("         NA", ncg.proceedField(rs));
    }
}
