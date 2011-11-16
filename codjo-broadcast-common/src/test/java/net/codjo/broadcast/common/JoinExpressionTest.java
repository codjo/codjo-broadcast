package net.codjo.broadcast.common;
import junit.framework.TestCase;
/**
 *
 */
public class JoinExpressionTest extends TestCase {
    public void test_builders() throws Exception {
        JoinExpression expression =
              JoinExpression.create()
                    .extraOnClause("and LABEL like 'hell%'")
                    .where("LABEL is null");

        assertEquals("and LABEL like 'hell%'", expression.getExtraOnClause());
        assertEquals("LABEL is null", expression.getWhereClause());
    }
}
