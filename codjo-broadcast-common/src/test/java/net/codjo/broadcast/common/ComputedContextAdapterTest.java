/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ComputedContextAdapter}.
 */
public class ComputedContextAdapterTest extends TestCase {
    private ComputedContextAdapter adapter;

    public void test_joinToBroadcastTable() throws Exception {
        String result = adapter.joinToBroadcastTable();
        assertEquals(
            "COMPUTED inner join COLUMNS_LIST on COMPUTED.SELECTION_ID = COLUMNS_LIST.SELECTION_ID "
            + "inner join PM_BROADCAST_COLUMNS on PM_BROADCAST_COLUMNS.COLUMNS_ID  =  COLUMNS_LIST.COLUMNS_ID and PM_BROADCAST_COLUMNS.SECTION_ID  =  COLUMNS_LIST.SECTION_ID",
            result);
    }


    protected void setUp() throws Exception {
        adapter = new ComputedContextAdapter(new PreferencesForTesting(), new Context());
    }
}
