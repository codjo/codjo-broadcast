package net.codjo.broadcast.server;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import net.codjo.broadcast.common.computed.AbstractComputedField;
import net.codjo.broadcast.common.computed.ComputedContext;
import net.codjo.broadcast.server.api.SqlUtil;
/**
 *
 */
public abstract class AbstractSqlResourceComputedField extends AbstractComputedField {
    protected AbstractSqlResourceComputedField(String name, int sqlType, String sqlDefinition) {
        super(name, sqlType, sqlDefinition);
    }


    protected String loadQuery(String queryName) throws SQLException {
        return SqlUtil.loadQuery(this, queryName);
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


    protected void initParameters(ComputedContext computedContext) {
    }


    public void compute(ComputedContext computedContext, Connection con) throws SQLException {
        computedContext.putParameter("selectionTable", computedContext.getSelectionTableName());
        computedContext.putParameter("computedTable", computedContext.getComputedTableName());

        initParameters(computedContext);

        executeUpdate(con, computedContext.replaceVariables(loadQuery(getClass().getSimpleName() + ".sql")));
    }
}
