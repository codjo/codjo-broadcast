package net.codjo.broadcast.server;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import net.codjo.variable.TemplateInterpreter;
import net.codjo.variable.UnknownVariableException;
/**
 *
 */
public class SqlTemplate {
    private String sql;


    public SqlTemplate(String sql) {
        this.sql = sql;
    }


    public String buildQuery(TemplateInterpreter interpret, String sqlQuery) throws UnknownVariableException {
        return interpret.evaluate(sqlQuery);
    }


    public void executeUpdate(Connection con, TemplateInterpreter interpreter) throws SQLException {
        Statement stmt = con.createStatement();
        try {
            String sqlQuery = buildQuery(interpreter, sql);
            stmt.executeUpdate(sqlQuery);
        }
        catch (UnknownVariableException e) {
            throw new SQLException("La requête " + sql
                                   + " contient des variables qui ne peuvent être interprétées", e.getMessage());
        }
        finally {
            stmt.close();
        }
    }
}
