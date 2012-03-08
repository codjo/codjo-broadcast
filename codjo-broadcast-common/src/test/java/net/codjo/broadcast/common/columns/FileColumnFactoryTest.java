/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import fakedb.FakeResultSet;
import java.sql.ResultSet;
import java.sql.Types;
import junit.framework.TestCase;
import net.codjo.sql.builder.FieldInfo;
import net.codjo.sql.builder.TableName;

public class FileColumnFactoryTest extends TestCase {
    private FileColumnFactory factory;
    private static final FieldInfo FIELDF_INFO = new FieldInfo(new TableName("COCO"), "TOTO", -1);


    public void test_newExpression() throws Exception {
        ResultSet rs = new FakeResultSet(new Object[][]{{"EXPRESSION"}, {"Valeur"}}).getStub();
        rs.next();
        assertNotNull("Construction d'une expression", factory.newExpression(rs, Types.VARCHAR, null));

        rs = new FakeResultSet(new Object[][]{{"EXPRESSION"}, {null}}).getStub();
        rs.next();
        assertNull("Pas d'expression", factory.newExpression(rs, Types.VARCHAR, null));
    }


    public void test_newExpression_noColumn() throws Exception {
        ResultSet rs = new FakeResultSet(new Object[][]{{"TOTO"}, {null}}).getStub();
        rs.next();
        assertNull("Pas de colonne d'expression", factory.newExpression(rs, Types.VARCHAR, null));
    }


    public void test_newFileColumnGenerator_Boolean() throws Exception {
        ResultSet rs = new FakeResultSet(new Object[][]{
              {"COLUMN_NAME", "PADDING_CARACTER", "FIXED_LENGTH", "BREAK_FIELD"},
              {"bobo", null, Boolean.FALSE, Boolean.FALSE}
        }).getStub();
        rs.next();

        FileColumnGenerator generator = factory.newFileColumnGenerator(rs, FIELDF_INFO, Types.BIT);

        assertTrue(generator instanceof BooleanColumnGenerator);
    }


    public void test_newFileColumnGenerator_Date() throws Exception {
        ResultSet rs = new FakeResultSet(new Object[][]{
              {"COLUMN_NAME", "COLUMN_DATE_FORMAT", "PADDING_CARACTER", "FIXED_LENGTH", "BREAK_FIELD"},
              {"bobo", "dd/MM/yyyy", null, Boolean.FALSE, Boolean.FALSE}
        }).getStub();
        rs.next();

        FileColumnGenerator generator = factory.newFileColumnGenerator(rs, FIELDF_INFO, Types.DATE);

        assertTrue(generator instanceof DateColumnGenerator);

        generator = factory.newFileColumnGenerator(rs, FIELDF_INFO, Types.TIMESTAMP);
        assertTrue(generator instanceof DateColumnGenerator);
    }


    public void test_newFileColumnGenerator_Numeric() throws Exception {
        ResultSet rs = new FakeResultSet(new Object[][]{
              {"COLUMN_NAME", "DECIMAL_SEPARATOR", "COLUMN_NUMBER_FORMAT", "PADDING_CARACTER",
               "FIXED_LENGTH", "BREAK_FIELD"},
              {"bobo", ".", "0.00", null, Boolean.FALSE, Boolean.FALSE}
        }).getStub();
        rs.next();

        FileColumnGenerator generator = factory.newFileColumnGenerator(rs, FIELDF_INFO, Types.INTEGER);

        assertTrue(generator instanceof NumberColumnGenerator);

        generator = factory.newFileColumnGenerator(rs, FIELDF_INFO, Types.NUMERIC);
        assertTrue(generator instanceof NumberColumnGenerator);
    }


    public void test_newFileColumnGenerator_String() throws Exception {
        ResultSet rs = new FakeResultSet(new Object[][]{
              {"COLUMN_NAME", "PADDING_CARACTER", "FIXED_LENGTH", "BREAK_FIELD"},
              {"bobo", null, Boolean.FALSE, Boolean.TRUE}
        }).getStub();
        rs.next();

        FileColumnGenerator generator = factory.newFileColumnGenerator(rs, FIELDF_INFO, Types.VARCHAR);

        assertTrue(generator instanceof StringColumnGenerator);

        generator = factory.newFileColumnGenerator(rs, FIELDF_INFO, Types.CHAR);
        assertTrue(generator instanceof StringColumnGenerator);
    }


    @Override
    protected void setUp() throws Exception {
        factory = new FileColumnFactory();
    }
}
