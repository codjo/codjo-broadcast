package net.codjo.broadcast.common.computed;
import net.codjo.broadcast.common.ComputedContextAdapter;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.Preferences;
import net.codjo.tokio.TokioFixture;
import net.codjo.database.common.api.structure.SqlTable;
import java.sql.SQLException;
import junit.framework.TestCase;
/**
 *
 */
public abstract class ComputedFieldTestCase<C extends ComputedField, P extends Preferences> extends TestCase {
    protected TokioFixture tokio = new TokioFixture(getClass());
    protected ComputedContextAdapter computedContext;
    protected C computedField = createComputedField();
    protected P preferences = createPreferences();
    protected Context context;


    protected abstract C createComputedField();


    protected abstract P createPreferences();


    protected abstract void createSelectionTable() throws SQLException;


    protected void assertCase(String storyName) throws SQLException {
        createSelectionTable();
        createComputedTable();

        tokio.insertInputInDb(storyName);

        computedField.compute(computedContext, tokio.getConnection());

        tokio.assertAllOutputs(storyName);
    }


    private void createComputedTable() {
        tokio.getJdbcFixture().create(SqlTable.table(preferences.getComputedTableName()),
                                      "SELECTION_ID numeric(18) not null, "
                                      + computedField.getSqlDefinition() + " null ");
    }


    @Override
    protected void setUp() throws Exception {
        tokio.doSetUp();
        context = new Context();
        computedContext = new ComputedContextAdapter(preferences, context);
    }


    @Override
    protected void tearDown() throws Exception {
        tokio.doTearDown();
    }
}
