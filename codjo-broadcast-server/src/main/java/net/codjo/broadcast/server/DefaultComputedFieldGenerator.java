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
import java.util.Iterator;
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


    public DefaultComputedFieldGenerator(Preferences preference) {
        this(preference, preference.getComputedFields());
    }


    protected DefaultComputedFieldGenerator(Preferences preference, ComputedField[] fields) {
        if (fields == null || fields.length == 0) {
            throw new IllegalArgumentException("Le tableau de ComputedField n'a pas ete initialise");
        }
        for (int i = 0; i < fields.length; i++) {
            computedField.put(fields[i].getName(), fields[i]);
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
        if (fieldList.size() > 0) {
            createComputedTable(ctxt, con, fieldList);
        }
    }


    public void fillComputedTable(Context ctxt, Connection con)
          throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("La connexion n'a pas ete initialise");
        }
        if (fieldList.size() > 0) {
            fillComputedTableKey(ctxt, con);
            updateComputedField(ctxt, con, fieldList);
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param ctxt      -
     * @param con       -
     * @param fieldList -
     *
     * @throws SQLException -
     */
    protected void createComputedTable(Context ctxt, Connection con, Set fieldList)
          throws SQLException {
        dropComputedTable(ctxt, con);
        Statement stmt = con.createStatement();
        String createTableQueryString = "SELECTION_ID numeric(18) not null, "
                                        + " $computedTable.fields$"
                                        + " constraint PK_TMP_COMPUTED primary key (SELECTION_ID)";
        try {
            TemplateInterpreter interpreter = new TemplateInterpreter();
            interpreter.add("computedTable.name", preference.getComputedTableName());
            interpreter.add("computedTable.fields", getComputedFieldDef(fieldList));
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
        TemplateInterpreter interpreter = new TemplateInterpreter();
        interpreter.add("computedTable.name", preference.getComputedTableName());
        interpreter.addAsVariable(ctxt.getParameters());

        String dropQuery = "drop table " + preference.getComputedTableName();
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
     * @param ctxt      Le contexte
     * @param con       La connexion
     * @param fieldList Description of the Parameter
     *
     * @throws SQLException Erreur d'acces a la base de données
     */
    protected void updateComputedField(Context ctxt, Connection con, Set fieldList)
          throws SQLException {
        ComputedContextAdapter adapter = new ComputedContextAdapter(preference, ctxt);

        for (Iterator iter = fieldList.iterator(); iter.hasNext(); ) {
            ComputedField field = (ComputedField)iter.next();
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
        HashSet fieldList = new HashSet();
        for (int i = 0; i < fileColumnGenerator.length; i++) {
            if (isComputedField(fileColumnGenerator[i])) {
                if (isKnownComputedField(fileColumnGenerator[i])) {
                    fieldList.add(computedField.get(fileColumnGenerator[i].getFieldInfo().getDBFieldName()));
                }
                else {
                    throw new IllegalArgumentException("Le nom du champ calcule : "
                                                       + fileColumnGenerator[i].getFieldInfo() + " est inconnu");
                }
            }
        }
        return fieldList;
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


    private String getComputedFieldDef(Set fieldList) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator iter = fieldList.iterator(); iter.hasNext(); ) {
            ComputedField field = (ComputedField)iter.next();
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
