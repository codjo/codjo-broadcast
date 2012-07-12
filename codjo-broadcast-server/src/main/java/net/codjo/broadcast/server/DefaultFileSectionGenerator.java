/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import net.codjo.broadcast.common.BroadcastException;
import net.codjo.broadcast.common.ComputedFieldGenerator;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.Selector;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
import net.codjo.broadcast.common.columns.GenerationException;
import net.codjo.database.common.api.DatabaseFactory;
import org.apache.log4j.Logger;
/**
 * Générateur de section par défaut.
 *
 * @author $Author: galaber $
 * @version $Revision: 1.3 $
 */
class DefaultFileSectionGenerator implements FileSectionGenerator {
    private static final Logger APP = Logger.getLogger(DefaultFileSectionGenerator.class);
    private boolean columnHeader = false;
    private String columnSeparator = null;
    private FileColumnGenerator[] columns;
    private ComputedFieldGenerator computedField;
    private Preferences preference;
    private QueryBuilder queryBuilder;
    private String sectionHeader = null;
    private Selector selector;


    /**
     * @param sectionName  Le nom de la section
     * @param selector     Le selecteur de ligne a diffuser
     * @param queryBuilder Le constructeur de requete
     * @param columns      Les generateur de colonnes
     */
    DefaultFileSectionGenerator(Preferences preference,
                                String sectionName,
                                Selector selector,
                                ComputedFieldGenerator computedField,
                                QueryBuilder queryBuilder,
                                FileColumnGenerator[] columns) {
        assertNotNull("preference null", preference);
        assertNotNull("Nom de section null", sectionName);
        assertNotNull("Type de selection null", selector);
        assertNotNull("Constructeur de requête null", queryBuilder);
        assertNotNull("Colonnes de diffusion null", columns);
        assertNotNull("Generateur de champ calculé", computedField);

        this.columns = columns;
        this.queryBuilder = queryBuilder;
        this.selector = selector;
        this.computedField = computedField;
        this.preference = preference;
    }


    public int generate(Context context, Connection connection, PrintWriter writer)
          throws IOException, SQLException, BroadcastException {
        java.sql.Date today = context.getToday();

        TransactionManager transactionManager = new TransactionManager(connection);
        try {
            computedField.createComputedTable(context, columns, connection);
            selector.proceed(context, connection, preference.getSelectionTableName(), today);
            computedField.fillComputedTable(context, connection);
            String query = queryBuilder.buildQuery(columns);

            int contentResult = generateContent(context, connection, query, writer);
            transactionManager.commit(connection);
            return contentResult;
        }
        catch (GenerationException ex) {
            transactionManager.rollback(connection);
            throw new BroadcastException(ex);
        }
        finally {
            transactionManager.release(connection);
            cleanupTemporaryTables(context, connection, today);
        }
    }


    void setColumnHeader(boolean displayHeader) {
        this.columnHeader = displayHeader;
    }


    void setColumnSeparator(String separator) {
        this.columnSeparator = separator;
    }


    void setSectionHeader(String displayHeader) {
        this.sectionHeader = displayHeader;
    }


    private void assertNotNull(String errorMsg, Object pointer) {
        if (pointer == null) {
            throw new IllegalArgumentException(errorMsg);
        }
    }


    private void cleanupTemporaryTables(Context ctxt, Connection con, java.sql.Date today)
          throws SQLException {
        try {
            selector.cleanup(ctxt, con, ctxt.replaceVariables(preference.getSelectionTableName()), today);
        }
        finally {
            dropComputedTable(con, ctxt.replaceVariables(preference.getComputedTableName()));
        }
    }


    private void dropComputedTable(Connection connection, String computedTableName) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate("drop table " + computedTableName);
        }
        catch (SQLException ex) {
            ; // Erreur sans incidence
        }
        finally {
            statement.close();
        }
    }


    private ResultSet executeQuery(final Statement stmt, final String query)
          throws SQLException {
        try {
            return stmt.executeQuery(query);
        }
        catch (SQLException ex) {
            APP.error("Erreur durant l'execution de la requête : \n\t " + query);
            throw ex;
        }
    }


    private void generateColumnHeader(PrintWriter writer) {
        boolean isBreak = false;

        if (!columnHeader) {
            return;
        }
        for (int i = 0; i < columns.length; i++) {
            if (!columns[i].isBreakField() && isBreak) {
                writer.println();
            }
            else if (columnSeparator != null && i > 0) {
                writer.print(columnSeparator);
            }

            isBreak = columns[i].isBreakField();
            writer.print(columns[i].buildColumnHeader());
        }
        writer.println();
    }


    private int generateContent(Context context,
                                Connection connection,
                                String query,
                                PrintWriter writer) throws SQLException, GenerationException {
        boolean canWriteValue;
        boolean isPreviousColumnBreak = false;
        boolean applyBreakLine;
        Map<Integer, String> breakFields = new HashMap<Integer, String>();
        int sectionLines = 0;

        generateSectionHeader(context, writer);
        generateColumnHeader(writer);
        Statement statement = connection.createStatement();
        try {
            String query1 = context.replaceVariables(query);
            ResultSet rs = executeQuery(statement, query1);
            while (rs.next()) {
                sectionLines++;
                canWriteValue = false;
                applyBreakLine = checkBreak(breakFields, rs);

                for (int i = 0; i < columns.length; i++) {
                    if (!columns[i].isBreakField() && isPreviousColumnBreak && applyBreakLine) {
                        writer.println();
                    }
                    else if (columnSeparator != null && canWriteValue) {
                        writer.print(columnSeparator);
                    }

                    String value = columns[i].proceedField(rs);
                    canWriteValue = checkWriteValue(value, applyBreakLine, breakFields, i);
                    if (canWriteValue) {
                        writer.print(value);
                    }

                    isPreviousColumnBreak = columns[i].isBreakField();
                }

                writer.println();
            }
        }
        finally {
            statement.close();
        }
        return sectionLines;
    }


    private boolean checkWriteValue(String value, boolean applyBreakLine, Map<Integer, String> breakFields,
                                    int indexColumn) throws SQLException, GenerationException {

        boolean isWritable = false;
        if (columns[indexColumn].isBreakField()) {
            if (applyBreakLine) {
                isWritable = true;
            }
        }
        else {
            isWritable = true;
        }
        if (columns[indexColumn].isBreakField()) {
            breakFields.put(indexColumn, value);
        }
        return isWritable;
    }


    private boolean checkBreak(Map<Integer, String> breakFields, ResultSet rs)
          throws SQLException, GenerationException {
        Integer index = 0;
        for (FileColumnGenerator column : columns) {
            if (column.isBreakField()) {
                if (breakFields.isEmpty() || !breakFields.get(index).equals(column.proceedField(rs))) {
                    return true;
                }
            }
            index++;
        }
        return false;
    }


    private void generateSectionHeader(Context ctxt, PrintWriter os) {
        if (sectionHeader == null) {
            return;
        }
        os.println(ctxt.replaceVariables(sectionHeader));
    }


    private class TransactionManager {
        private boolean previousAutoCommitMode;
        private boolean hasToChangeAutoCommit;


        TransactionManager(Connection connection) throws SQLException {
            previousAutoCommitMode = connection.getAutoCommit();
            hasToChangeAutoCommit = hasToChangeAutoCommit();
            if (hasToChangeAutoCommit) {
                APP.debug("changing autocommit mode to false");
                connection.setAutoCommit(false);
            }
        }


        private boolean hasToChangeAutoCommit() {
            return new DatabaseFactory().getDatabaseQueryHelper().hasDeleteRowStrategyOnTemporaryTable();
        }


        void release(Connection connection) throws SQLException {
            if (hasToChangeAutoCommit) {
                APP.debug("restoring autocommit to:" + previousAutoCommitMode);
                if (connection != null) {
                    connection.setAutoCommit(previousAutoCommitMode);
                }
            }
        }


        public void commit(Connection connection) throws SQLException {
            if (hasToChangeAutoCommit && !connection.getAutoCommit()) {
                APP.debug("Commiting datas");
                connection.commit();
            }
        }


        public void rollback(Connection connection) throws SQLException {
            if (hasToChangeAutoCommit && !connection.getAutoCommit()) {
                APP.debug("Rollbacking datas");
                connection.rollback();
            }
        }
    }
}
