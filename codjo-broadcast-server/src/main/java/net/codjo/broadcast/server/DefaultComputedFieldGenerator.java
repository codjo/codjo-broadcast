/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.codjo.broadcast.common.ComputedContextAdapter;
import net.codjo.broadcast.common.ComputedFieldGenerator;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
import net.codjo.broadcast.common.computed.ComputedField;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseQueryHelper;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.variable.TemplateInterpreter;
import net.codjo.variable.UnknownVariableException;
/**
 * DOCUMENT ME!
 *
 * @author $Author: galaber $
 * @version $Revision: 1.3 $
 */
class DefaultComputedFieldGenerator implements ComputedFieldGenerator {
    private Map computedField = new HashMap();
    private Preferences preference;
    private Set fieldList;


    DefaultComputedFieldGenerator(Preferences preference) {
        this(preference, preference.getComputedFields());
    }


    protected DefaultComputedFieldGenerator(Preferences preference, ComputedField[] fields) {
        if (fields == null || fields.length == 0) {
            throw new IllegalArgumentException("Le tableau de ComputedField n'a pas ete initialise");
        }
        for (ComputedField field : fields) {
            computedField.put(field.getName(), field);
        }
        this.preference = preference;
    }


    public void createComputedTable(Context ctxt, FileColumnGenerator[] fileColumnGenerator, Connection con)
          throws SQLException {
        if (fileColumnGenerator == null || fileColumnGenerator.length == 0) {
            throw new IllegalArgumentException("Le tableau de 'FileColumnGenerator'"
                                               + " n'a pas ete initialise");
        }
        if (con == null) {
            throw new IllegalArgumentException("La connexion n'a pas ete initialise");
        }

        fieldList = determineFieldList(fileColumnGenerator);
        if (!fieldList.isEmpty()) {
            createComputedTable(ctxt, con, fieldList);
        }
    }


    public void fillComputedTable(Context ctxt, Connection con)
          throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("La connexion n'a pas ete initialise");
        }
        if (!fieldList.isEmpty()) {
            fillComputedTableKey(ctxt, con);
            updateComputedField(ctxt, con, fieldList);
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param ctxt   -
     * @param con    -
     * @param fields -
     *
     * @throws SQLException -
     */
    protected void createComputedTable(Context ctxt, Connection con, Set fields)
          throws SQLException {
        dropComputedTable(ctxt, con);
        Statement stmt = con.createStatement();
        String createTableQueryString = "SELECTION_ID numeric(18) not null, "
                                        + " $computedTable.fields$"
                                        + " constraint PK_TMP_COMPUTED primary key (SELECTION_ID)";
        try {
            TemplateInterpreter interpreter = new TemplateInterpreter();
            interpreter.add("computedTable.name", preference.getComputedTableName());
            interpreter.add("computedTable.fields", getComputedFieldDef(fields));
            interpreter.addAsVariable(ctxt.getParameters());

            String sql = buildCreateTableQuery(interpreter, createTableQueryString);
            String createTableQuery = interpreter.evaluate(sql);
            stmt.executeUpdate(createTableQuery);
        }
        catch (UnknownVariableException e) {
            throw new SQLException("La requête " + createTableQueryString
                                   + " contient des variables qui ne peuvent être interprétées", e.getMessage());
        }
        finally {
            stmt.close();
        }
    }


    /**
     * Drop de la table de calcul.
     *
     * @param ctxt -
     * @param con  -
     *
     * @throws SQLException -
     */
    protected void dropComputedTable(Context ctxt, Connection con)
          throws SQLException {
        String dropQuery = "drop table " + preference.getComputedTableName();

        TemplateInterpreter interpreter = new TemplateInterpreter();
        interpreter.add("computedTable.name", preference.getComputedTableName());
        interpreter.addAsVariable(ctxt.getParameters());

        String sqlQuery;
        try {
            sqlQuery = interpreter.evaluate(dropQuery);
        }
        catch (UnknownVariableException e) {
            throw new SQLException("La requête " + dropQuery
                                   + " contient des variables qui ne peuvent être interprétées", e.getMessage());
        }

        Statement stmt = con.createStatement();
        try {
            stmt.executeUpdate(sqlQuery);
        }
        catch (SQLException error) {
            ; // Erreur sans incidence
        }
        finally {
            stmt.close();
        }
    }


    /**
     * Mise à jour des champs calcules.
     *
     * @param ctxt   Le contexte
     * @param con    La connexion
     * @param fields Description of the Parameter
     *
     * @throws SQLException Erreur d'acces a la base de données
     */
    protected void updateComputedField(Context ctxt, Connection con, Set fields)
          throws SQLException {
        ComputedContextAdapter adapter = new ComputedContextAdapter(preference, ctxt);

        for (Object aFieldList : fields) {
            ComputedField field = (ComputedField)aFieldList;
            field.compute(adapter, con);
        }
    }


    private String buildCreateTableQuery(TemplateInterpreter interpreter, String createTableQueryString) {
        try {
            DatabaseQueryHelper queryHelper = new DatabaseFactory().getDatabaseQueryHelper();
            String tableName = interpreter.evaluate("$computedTable.name$");
            String tableScript = queryHelper.buildCreateTableQuery(SqlTable.temporaryTable(tableName),
                                                                   createTableQueryString);
            return interpreter.evaluate(tableScript);
        }
        catch (UnknownVariableException error) {
            throw new IllegalArgumentException("La chaine " + createTableQueryString
                                               + " contient des variables inconnues : " + error.getMessage());
        }
    }


    /**
     * Determine la liste des champs calcules.
     *
     * @param fileColumnGenerator La liste des colonnes du fichier a generer
     *
     * @return La liste des champs
     *
     * @throws IllegalArgumentException Nom de champ inconnue.
     */
    private Set determineFieldList(FileColumnGenerator[] fileColumnGenerator) {
        Set set = new HashSet();
        for (FileColumnGenerator aFileColumnGenerator : fileColumnGenerator) {
            if (isComputedField(aFileColumnGenerator)) {
                if (isKnownComputedField(aFileColumnGenerator)) {
                    set.add(computedField.get(aFileColumnGenerator.getFieldInfo().getDBFieldName()));
                }
                else {
                    throw new IllegalArgumentException("Le nom du champ calcule : "
                                                       + aFileColumnGenerator.getFieldInfo() + " est inconnu");
                }
            }
        }
        return set;
    }


    /**
     * Insertion de la cle de selection dans la table de calcul
     *
     * @param context -
     * @param con     -
     */
    private void fillComputedTableKey(Context context, Connection con)
          throws SQLException {
        Statement stmt = con.createStatement();
        try {
            stmt.executeUpdate(getFillTableKeyQuery(context));
        }
        finally {
            stmt.close();
        }
    }


    private String getComputedFieldDef(Set fields) {
        StringBuilder buffer = new StringBuilder();
        for (Object aFieldList : fields) {
            ComputedField field = (ComputedField)aFieldList;
            buffer.append(field.getSqlDefinition()).append(" null,");
        }
        return buffer.toString();
    }


    private String getFillTableKeyQuery(Context context) {
        String insertDef =
              "insert into $computed.table.name$" + " (SELECTION_ID) select SELECTION_ID"
              + " from $selection.table.name$";

        TemplateInterpreter interpreter = new TemplateInterpreter();
        interpreter.add("computed.table.name", preference.getComputedTableName());
        interpreter.add("selection.table.name", preference.getSelectionTableName());
        try {
            return context.replaceVariables(interpreter.evaluate(insertDef));
        }
        catch (UnknownVariableException error) {
            throw new IllegalArgumentException("La chaine " + insertDef
                                               + " contient des variables inconnues : " + error.getMessage());
        }
    }


    private boolean isComputedField(final FileColumnGenerator columnGenerator) {
        return preference.getComputedTableName().equals(columnGenerator.getFieldInfo()
                                                              .getDBTableName());
    }


    private boolean isKnownComputedField(final FileColumnGenerator fileColumnGenerator) {
        return computedField.containsKey(fileColumnGenerator.getFieldInfo()
                                               .getDBFieldName());
    }
}
