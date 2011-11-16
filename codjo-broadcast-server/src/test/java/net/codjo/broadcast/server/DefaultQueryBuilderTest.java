package net.codjo.broadcast.server;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.PreferencesForTesting;
import net.codjo.broadcast.common.columns.FakeColumnGenerator;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
import net.codjo.sql.builder.FieldInfo;
import net.codjo.sql.builder.TableName;
import junit.framework.TestCase;
/**
 *
 */
public class DefaultQueryBuilderTest extends TestCase {
    private Preferences preference = PreferencesForTesting.buildPreferencesWithSlash();


    public void test_buildQuery() throws Exception {
        FileColumnGenerator[] fileColumnGenerator = {
              new FakeColumnGenerator(new FieldInfo(new TableName(preference.getSelectionTableName()),
                                                    "SHARE_PRICE_ID",
                                                    1)),
              new FakeColumnGenerator(new FieldInfo(new TableName(preference.getBroadcastTableName()),
                                                    "DB_TABLE_NAME",
                                                    2))};
        DefaultQueryBuilder dqb = new DefaultQueryBuilder(preference);

        String resultSelect =
              "select #COLUMNS_LIST.SHARE_PRICE_ID as COL_1 , PM_BROADCAST_COLUMNS.DB_TABLE_NAME as COL_2 from (PM_BROADCAST_COLUMNS inner join #COLUMNS_LIST on (PM_BROADCAST_COLUMNS.COLUMNS_ID  =  #COLUMNS_LIST.COLUMNS_ID) and (PM_BROADCAST_COLUMNS.SECTION_ID  =  #COLUMNS_LIST.SECTION_ID))";
        assertEquals(resultSelect, dqb.buildQuery(fileColumnGenerator));
        assertEquals("verification bis (bug)", resultSelect,
                     dqb.buildQuery(fileColumnGenerator));
    }
}
