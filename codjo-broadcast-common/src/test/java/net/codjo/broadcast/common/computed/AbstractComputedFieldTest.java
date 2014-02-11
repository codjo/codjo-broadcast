package net.codjo.broadcast.common.computed;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Test;

import static net.codjo.broadcast.common.computed.ComputedField.WARNINGS;
import static org.junit.Assert.assertEquals;
/**
 *
 */
public class AbstractComputedFieldTest {
    @Test
    public void testAppendWarning_withoutPrefix() {
        testAppendWarning(null, false);
    }


    @Test
    public void testAppendWarning_withNullPrefix() {
        testAppendWarning(null, true);
    }


    @Test
    public void testAppendWarning_withPrefix() {
        testAppendWarning("For line 123:\n", true);
    }


    private void testAppendWarning(String prefix, boolean withPrefix) {
        String fieldName = "fieldName";
        final String normalCondition = fieldName + " IS NOT NULL";
        final String warningMessage = "a warning message";
        AbstractComputedField field = new AbstractComputedField(fieldName, 0, fieldName + " NUMBER(10,2)") {
            public void compute(ComputedContext ctxt, Connection con) throws SQLException {
            }
        };

        boolean usePrefix = withPrefix && (prefix != null);
        String expectedSql = ", comp." + WARNINGS + " ="
                             + " case when NOT (" + normalCondition + ")"
                             + " then"
                             + " NVL(comp." + WARNINGS + ", " + (usePrefix ? "'" + prefix + "'" : "null")
                             + ") || '    - " + warningMessage + "'"
                             + " else null"
                             + " end ";

        String actualSql;
        if (withPrefix) {
            actualSql = field.appendWarning(normalCondition, warningMessage, prefix);
        }
        else {
            actualSql = field.appendWarning(normalCondition, warningMessage);
        }
        assertEquals(expectedSql, actualSql);
    }
}
