package net.codjo.broadcast.server;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.Selector;
import net.codjo.broadcast.server.api.SqlUtil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractSelector implements Selector {

    protected AbstractSelector() {
    }


    public final void proceed(Context context,
                              Connection connection,
                              String selectionTableName,
                              Date broadcastDate) throws SQLException {
        if (context != null) {
            context.putParameter("selectionTable", selectionTableName);
            if (broadcastDate != null) {
                context.putParameter("broadcastDate", broadcastDate.toString());
            }
        }
        proceedImpl(context, connection, selectionTableName, broadcastDate);
    }


    protected abstract void proceedImpl(Context context,
                                        Connection connection,
                                        String selectionTableName,
                                        Date broadcastDate) throws SQLException;


    public void cleanup(Context ctxt,
                        Connection connection,
                        String selectionTableName,
                        Date broadcastDate) throws SQLException {
        SqlUtil.dropTable(connection, selectionTableName);
    }


    protected String loadQuery(String queryName) throws SQLException {
        return SqlUtil.loadQuery(getClass().getResource(queryName));
    }


    protected void executeUpdate(Connection connection, String sqlQuery) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate(sqlQuery);
        }
        finally {
            statement.close();
        }
    }


    protected void createTempTable(Connection connection, String tableName, String body) throws SQLException {
        SqlUtil.dropTable(connection, tableName);
        executeUpdate(connection, "create table " + tableName + " (" + body + " )");
    }


    protected String loadQuery(String queryName, Context context) throws SQLException {
        return context.replaceVariables(loadQuery(queryName));
    }
}
