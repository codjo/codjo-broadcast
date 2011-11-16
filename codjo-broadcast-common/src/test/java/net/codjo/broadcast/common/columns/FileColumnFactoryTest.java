/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
import net.codjo.sql.builder.TableName;
import fakedb.FakeResultSet;
import java.sql.Types;
import junit.framework.TestCase;

public class FileColumnFactoryTest extends TestCase {
    FileColumnFactory factory;
    private static final FieldInfo fieldFinfo = new FieldInfo(new TableName("COCO"), "TOTO", -1);


    public FileColumnFactoryTest(String s) {
        super(s);
    }


    public void test_newExpression() throws Exception {
        FakeResultSet rs = new FakeResultSet(new Object[][]{{"EXPRESSION"}, {"Valeur"}});
        rs.next();
        assertNotNull("Construction d'une expression", factory.newExpression(rs, Types.VARCHAR, null));

        rs = new FakeResultSet(new Object[][]{{"EXPRESSION"}, {null}});
        rs.next();
        assertNull("Pas d'expression", factory.newExpression(rs, Types.VARCHAR, null));
    }


    public void test_newExpression_noColumn() throws Exception {
        FakeResultSet rs = new FakeResultSet(new Object[][]{{"TOTO"}, {null}});
        rs.next();
        assertNull("Pas de colonne d'expression", factory.newExpression(rs, Types.VARCHAR, null));
    }


    public void test_newFileColumnGenerator_Boolean() throws Exception {
        FakeResultSet rs = new FakeResultSet(new Object[][]{
              {"COLUMN_NAME", "PADDING_CARACTER", "FIXED_LENGTH", "BREAK_FIELD"},
              {"bobo", null, Boolean.FALSE, Boolean.FALSE}
        });
        rs.next();

        FileColumnGenerator generator = factory.newFileColumnGenerator(rs, fieldFinfo, Types.BIT);

        assertTrue(generator instanceof BooleanColumnGenerator);
    }


    public void test_newFileColumnGenerator_Date() throws Exception {
        FakeResultSet rs = new FakeResultSet(new Object[][]{
              {"COLUMN_NAME", "COLUMN_DATE_FORMAT", "PADDING_CARACTER", "FIXED_LENGTH", "BREAK_FIELD"},
              {"bobo", "dd/MM/yyyy", null, Boolean.FALSE, Boolean.FALSE}
        });
        rs.next();

        FileColumnGenerator generator = factory.newFileColumnGenerator(rs, fieldFinfo, Types.DATE);

        assertTrue(generator instanceof DateColumnGenerator);

        generator = factory.newFileColumnGenerator(rs, fieldFinfo, Types.TIMESTAMP);
        assertTrue(generator instanceof DateColumnGenerator);
    }


    public void test_newFileColumnGenerator_Numeric() throws Exception {
        FakeResultSet rs = new FakeResultSet(new Object[][]{
              {"COLUMN_NAME", "DECIMAL_SEPARATOR", "COLUMN_NUMBER_FORMAT", "PADDING_CARACTER",
               "FIXED_LENGTH", "BREAK_FIELD"},
              {"bobo", ".", "0.00", null, Boolean.FALSE, Boolean.FALSE}
        });
        rs.next();

        FileColumnGenerator generator = factory.newFileColumnGenerator(rs, fieldFinfo, Types.INTEGER);

        assertTrue(generator instanceof NumberColumnGenerator);

        generator = factory.newFileColumnGenerator(rs, fieldFinfo, Types.NUMERIC);
        assertTrue(generator instanceof NumberColumnGenerator);
    }


    public void test_newFileColumnGenerator_String()
          throws Exception {
        FakeResultSet rs = new FakeResultSet(new Object[][]{
              {"COLUMN_NAME", "PADDING_CARACTER", "FIXED_LENGTH", "BREAK_FIELD"},
              {"bobo", null, Boolean.FALSE, Boolean.TRUE}
        });
        rs.next();

        FileColumnGenerator generator = factory.newFileColumnGenerator(rs, fieldFinfo, Types.VARCHAR);

        assertTrue(generator instanceof StringColumnGenerator);

        generator = factory.newFileColumnGenerator(rs, fieldFinfo, Types.CHAR);
        assertTrue(generator instanceof StringColumnGenerator);
    }


    @Override
    protected void setUp() throws Exception {
        factory = new FileColumnFactory();
    }
}
