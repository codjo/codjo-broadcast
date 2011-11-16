/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.computed;
import fakedb.FakeDriver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import junit.framework.TestCase;
/**
 * Tests de la classe <code>ConstantField</code>.
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
public class ConstantFieldTest extends TestCase {
    private MockComputedContext ctxt;

    public ConstantFieldTest(String name) {
        super(name);
    }

    public void test_compute() throws Exception {
        ConstantField field =
            new ConstantField("COL_CST", Types.VARCHAR, "", "constante");

        FakeDriver.getDriver().pushUpdateConstraint("update "
            + ctxt.getComputedTableName() + " set COL_CST = constante");

        field.compute(ctxt, DriverManager.getConnection("jdbc:fakeDriver"));

        assertTrue(FakeDriver.getDriver().isUpdateConstraintEmpty());
    }


    public void test_compute_null() throws Exception {
        ConstantField field = new ConstantField("COL_CST", Types.VARCHAR, "", null);

        FakeDriver.getDriver().pushUpdateConstraint("update "
            + ctxt.getComputedTableName() + " set COL_CST = null(sqlType=12)");

        field.compute(ctxt, DriverManager.getConnection("jdbc:fakeDriver"));

        assertTrue(FakeDriver.getDriver().isUpdateConstraintEmpty());
    }


    protected void setUp() throws SQLException {
        ctxt = new MockComputedContext();
    }
}
