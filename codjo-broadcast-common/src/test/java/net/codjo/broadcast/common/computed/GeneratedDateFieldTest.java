/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.computed;
import fakedb.FakeDriver;
import java.sql.DriverManager;
import java.sql.SQLException;
import junit.framework.TestCase;
/**
 * Tests de la classe <code>GeneratedDateField</code>.
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
public class GeneratedDateFieldTest extends TestCase {
    private MockComputedContext ctxt;

    public GeneratedDateFieldTest(String name) {
        super(name);
    }

    public void test_compute() throws Exception {
        GeneratedDateField field = new GeneratedDateField();

        FakeDriver.getDriver().pushUpdateConstraint("update "
            + ctxt.getComputedTableName() + " set DATE_HEURE = getDate()");

        field.compute(ctxt, DriverManager.getConnection("jdbc:fakeDriver"));

        assertTrue(FakeDriver.getDriver().isUpdateConstraintEmpty());
    }


    protected void setUp() throws SQLException {
        ctxt = new MockComputedContext();
    }
}
