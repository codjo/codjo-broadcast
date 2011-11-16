/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.broadcast.common.columns.FunctionHolder;
import net.codjo.sql.builder.OrderByField;
import net.codjo.test.common.AssertUtil;
import java.util.List;
import junit.framework.TestCase;
/**
 * Tests de la classe <code>Preferences</code>.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class PreferencesTest extends TestCase {
    private MyFunctionHolder myFunctionHolder = new MyFunctionHolder();


    public void test_getConfig() throws Exception {
        PreferencesForTesting pref = new PreferencesForTesting();
        assertNotNull(pref.getConfig());
        assertSame(pref.getConfig(), pref.getConfig());
    }


    public void test_getOrderByFields() throws Exception {
        Preferences pref = PreferencesForTesting.buildPreferences();
        assertEquals(0, pref.getOrderByFields().length);

        PreferencesForTesting prefWithOrderBy = new PreferencesForTesting() {
            @Override
            public OrderByField[] getOrderByFields() {
                return new OrderByField[]{new OrderByField("AP_FOO", "COL1"),
                                          new OrderByField("PM_TOTO", "COL2"),
                                          new OrderByField("AP_PIPO", "COLOSCOPIE")};
            }
        };

        AssertUtil.assertEquals(new OrderByField[]{new OrderByField("AP_FOO", "COL1"),
                                                   new OrderByField("PM_TOTO", "COL2"),
                                                   new OrderByField("AP_PIPO", "COLOSCOPIE")},
                                prefWithOrderBy.getConfig().getOrderByFields());
    }


    public void test_createFunctionHolder() throws Exception {
        PreferencesForTesting pref = new PreferencesForTesting() {

            @Override
            public FunctionHolder getFunctionHolder() {
                return myFunctionHolder;
            }
        };
        assertSame(pref.getFunctionHolder(), pref.createFunctionHolder(new Context()));
    }


    private static class MyFunctionHolder implements FunctionHolder {
        public String getName() {
            return null;
        }


        public List<String> getAllFunctions() {
            return null;
        }
    }
}
