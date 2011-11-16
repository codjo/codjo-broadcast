package net.codjo.broadcast.server.selector;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.server.AbstractSelector;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 */
public abstract class AbstractGenericSelector extends AbstractSelector {
    private int selectorId;


    protected AbstractGenericSelector(int selectorId) {
        this.selectorId = selectorId;
    }


    @Override
    public final void proceedImpl(Context context,
                                  Connection connection,
                                  String selectionTableName,
                                  Date broadcastDate) throws SQLException {
        beforeProceed(context, connection, selectionTableName, broadcastDate);
        if (selectorId < 0) {
            executeGenericSelector(context, connection, selectionTableName, broadcastDate, selectorId);
        }
        else {
            executeStaticSelector(context, connection, selectionTableName, broadcastDate, selectorId);
        }
        afterProceed(context, connection, selectionTableName, broadcastDate);
    }


    protected void beforeProceed (Context context,
                                 Connection connection,
                                 String selectionTableName,
                                 Date broadcastDate)  throws SQLException{
    }


    protected void afterProceed(Context context,
                                Connection connection,
                                String selectionTableName,
                                Date broadcastDate)  throws SQLException{
    }


    protected void executeStaticSelector(Context context,
                                         Connection connection,
                                         String selectionTableName,
                                         Date broadcastDate, int selectorId) throws SQLException {

    }


    protected abstract void executeGenericSelector(Context context,
                                                   Connection connection,
                                                   String selectionTableName,
                                                   Date broadcastDate, int selectorId) throws SQLException;


    protected String getSelectorQuery(Connection con, int selectorId)
          throws SQLException {
        PreparedStatement stmt = con.prepareStatement(
              "select SELECTOR_QUERY from PM_BROADCAST_SELECTOR where SELECTOR_ID = ?");
        String selectQuery = "";
        try {
            stmt.setInt(1, -selectorId);
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();
            if (resultSet.next()) {
                selectQuery = resultSet.getString("SELECTOR_QUERY");
            }
        }
        finally {
            stmt.close();
        }
        return selectQuery;
    }


    protected void executeQueryWithVariables(Context context, Connection connection, String query)
          throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(context.replaceVariables(query));
        try {
            stmt.executeUpdate();
        }
        finally {
            stmt.close();
        }
    }
}
