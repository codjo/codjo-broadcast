package net.codjo.broadcast.server;
import fakedb.FakeDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.PreferencesMock;
import net.codjo.broadcast.common.columns.FakeColumnGenerator;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
import net.codjo.broadcast.common.computed.AbstractComputedField;
import net.codjo.broadcast.common.computed.ComputedContext;
import net.codjo.broadcast.common.computed.ComputedField;
import net.codjo.sql.builder.FieldInfo;
import net.codjo.sql.builder.TableName;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertTrue;
import static net.codjo.broadcast.common.computed.ComputedField.WARNINGS;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class DefaultComputedFieldGeneratorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void testCreateComputedTable_useOfReservedName() throws Exception {
        Preferences preferences = new PreferencesMock("", "", "", "") {
            @Override
            protected ComputedField[] initComputedFields() {
                return new ComputedField[]{new AbstractComputedField(WARNINGS, 0, "") {
                    public void compute(ComputedContext ctxt, Connection con) throws SQLException {
                    }
                }};
            }
        };
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(WARNINGS + " is a reserved column name");

        new DefaultComputedFieldGenerator(preferences);
    }


    @Test
    public void testCreateComputedTable_oneComputedField() throws Exception {
        testCreateComputedTable(true);
    }


    @Test
    public void testCreateComputedTable_noComputedField() throws Exception {
        testCreateComputedTable(false);
    }


    private void testCreateComputedTable(final boolean oneComputedField) throws Exception {
        // prepare context
        final String computedTableName = "aComputedTable";
        final String fieldName = "aField";
        final String fieldDef = "number(5,2) aField";
        Preferences preferences = new PreferencesMock("", "", "", computedTableName) {
            @Override
            protected ComputedField[] initComputedFields() {
                return new ComputedField[]{new AbstractComputedField(fieldName, 0, fieldDef) {
                    public void compute(ComputedContext ctxt, Connection con) throws SQLException {
                    }
                }};
            }
        };

        Context ctxt = new Context();
        FileColumnGenerator[] fileColumnGenerator = new FileColumnGenerator[]{
              new FakeColumnGenerator(new FieldInfo(new TableName(oneComputedField ?
                                                                  computedTableName :
                                                                  "anotherTable"), fieldName, 0)) {
              }
        };

        if (oneComputedField) {
            // Note : the order of push is the reverse of expected order
            FakeDriver.getDriver()
                  .pushUpdateConstraint("create table #" + computedTableName + " ( SELECTION_ID numeric(18) not null, "
                                        + ComputedField.WARNINGS + " CLOB null,  "
                                        + fieldDef + " null, "
                                        + "constraint PK_TMP_COMPUTED primary key (SELECTION_ID) )");
            FakeDriver.getDriver().pushUpdateConstraint("drop table " + computedTableName);
        }

        Connection connection = DriverManager.getConnection("jdbc:fakeDriver");
        DefaultComputedFieldGenerator generator = new DefaultComputedFieldGenerator(preferences);

        // test
        generator.createComputedTable(ctxt, fileColumnGenerator, connection);

        // assertions
        if (!FakeDriver.getDriver().isUpdateConstraintEmpty()) {
            FakeDriver.getDriver().spoolQueryNotUsed();
        }
        assertTrue(FakeDriver.getDriver().isUpdateConstraintEmpty());
        assertEquals("Context doesn't contain creation state of computed table",
                     oneComputedField,
                     ctxt.getComputedTableWasCreated());
    }
}
