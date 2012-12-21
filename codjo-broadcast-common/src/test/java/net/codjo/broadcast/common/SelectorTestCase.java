package net.codjo.broadcast.common;
import java.sql.Connection;
import java.sql.Date;
import net.codjo.database.common.api.TransactionManager;
import net.codjo.tokio.TokioTestCase;
/**
 * TODO[segolene][a valider]
 */
public class SelectorTestCase extends TokioTestCase {

    public void assertProceed(Selector selector,
                              final String tempTableName,
                              String storyName) throws Exception {
        assertProceed(selector, tempTableName, storyName, new Context(), null);
    }


    public void assertProceed(final Selector selector,
                              final String tempTableName,
                              final String storyName,
                              final Context context, final Date today) throws Exception {
        final Connection connection = tokioFixture.getConnection();
        TransactionManager<Void> transactionManager = new TransactionManager<Void>(connection) {

            @Override
            public Void runSql(Connection connection) throws Exception {
                tokioFixture.insertInputInDb(storyName);
                selector.proceed(context, connection, tempTableName, today);
                tokioFixture.assertAllOutputs(storyName);
                return null;
            }
        };

        transactionManager.run(connection);
    }
}
