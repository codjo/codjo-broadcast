/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.PreferencesManager;
import net.codjo.broadcast.common.columns.FileColumnFactory;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
import net.codjo.broadcast.common.computed.ComputedField;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.SQLFieldList;
import net.codjo.database.common.impl.sqlfield.DefaultSQLFieldList;
import net.codjo.sql.builder.FieldInfo;
import net.codjo.sql.builder.JoinKey;
import net.codjo.sql.builder.TableName;
import net.codjo.variable.TemplateInterpreter;
import net.codjo.variable.UnknownVariableException;
import org.apache.log4j.Logger;
/**
 * Classe Home des colonnes de diffusions.
 *
 * @author $Author: galaber $
 * @version $Revision: 1.6 $
 */
class FileColumnHome {
    private static final Logger APP = Logger.getLogger(FileColumnHome.class);
    private static final String QUERY_SELECT =
          "select * " + "from $column$ broadcast_column " + "inner join $section$ section "
          + "  on broadcast_column.SECTION_ID = section.SECTION_ID " + "inner join $fileContents$ broadcast_contents "
          + "  on broadcast_contents.SECTION_ID = section.SECTION_ID " + "where broadcast_contents.CONTENT_ID = ? "
          + "order by broadcast_column.COLUMN_NUMBER";

    private final DatabaseFactory databaseFactory = new DatabaseFactory();
    private FileColumnFactory factory = new FileColumnFactory();
    private Map<String, SQLFieldList> tableDefinition = new HashMap<String, SQLFieldList>();
    private PreferencesManager preferenceManager;
    private String loadQuery;


    /**
     * Positionne le <code>PreferencesManager</code> pour ce home, et pre-calcul la table de definition.
     *
     * @throws IllegalArgumentException si un<code>Preferences</code> est incoherent
     */
    public void init(Connection connection, PreferencesManager preferencesManager) {
        this.preferenceManager = preferencesManager;
        Map<String, String> arguments = new HashMap<String, String>();
        arguments.put("column", preferenceManager.getColumnsTableName());
        arguments.put("section", preferenceManager.getSectionTableName());
        arguments.put("fileContents", preferenceManager.getFileContentsTableName());
        arguments.put("file", preferenceManager.getFileTableName());

        loadQuery = formatQuery(QUERY_SELECT, arguments);

        try {
            tableDefinition = determineTableDefinition(connection);
        }
        catch (SQLException error) {
            APP.error("Impossible d'initialiser le HOME des colonnes", error);
            throw new IllegalArgumentException(error.getLocalizedMessage());
        }
    }


    /**
     * DOCUMENT ME!
     */
    protected FieldInfo buildFieldInfo(Map<FieldInfo, FieldInfo> definedFieldInfo, ResultSet rs)
          throws SQLException {
        FieldInfo fieldInfo = new FieldInfo(new TableName(rs.getString("DB_TABLE_NAME")),
                                            rs.getString("DB_FIELD_NAME"),
                                            rs.getInt("COLUMN_NUMBER"));

        if (definedFieldInfo.containsKey(fieldInfo)) {
            fieldInfo = definedFieldInfo.get(fieldInfo);
        }
        else {
            definedFieldInfo.put(fieldInfo, fieldInfo);
        }

        return fieldInfo;
    }


    /**
     * Charge les FileColumnGenerator d'une section.
     *
     * @param connection la connection JDBC
     * @param contentId  Identifiant d'un File content
     *
     * @return les FileColumnGenerator d'une section
     */
    public FileColumnGenerator[] loadFileColumns(Connection connection,
                                                 BigDecimal contentId,
                                                 Preferences sectionPreferences,
                                                 Context context) throws SQLException {
        Map<FieldInfo, FieldInfo> definedFieldInfo = new HashMap<FieldInfo, FieldInfo>();
        List<FileColumnGenerator> list = new ArrayList<FileColumnGenerator>();
        PreparedStatement loadStatment = connection.prepareStatement(loadQuery);

        loadStatment.setBigDecimal(1, contentId);
        ResultSet rs = loadStatment.executeQuery();

        try {
            while (rs.next()) {
                list.add(newFileColumnGenerator(rs, definedFieldInfo, sectionPreferences, context));
            }
        }
        finally {
            rs.close();
        }
        return list.toArray(new FileColumnGenerator[list.size()]);
    }


    private SQLFieldList getComputedFieldList(Preferences pref) {
        SQLFieldList fieldList = new DefaultSQLFieldList();
        ComputedField[] fields = pref.getComputedFields();
        for (ComputedField field : fields) {
            fieldList.addField(field.getName(), field.getSqlType());
        }
        return fieldList;
    }


    /**
     * Determine la définition des tables utilisables par la diffusion.
     *
     * @param connection la connection JDBC
     *
     * @return Table de hash (key=TableName, Value=SQLFieldList)
     *
     * @throws SQLException Erreur MetaData
     */
    private Map<String, SQLFieldList> determineTableDefinition(Connection connection) throws SQLException {
        Map<String, SQLFieldList> def = new HashMap<String, SQLFieldList>();

        for (Iterator<Preferences> iter = preferenceManager.iterator(); iter.hasNext();) {
            Preferences prefs = iter.next();
            def.putAll(determineTableDefinition(connection, prefs));
        }

        return def;
    }


    private Map<String, SQLFieldList> determineTableDefinition(Connection connection, Preferences pref)
          throws SQLException {
        HashMap<String, SQLFieldList> def = new HashMap<String, SQLFieldList>();

        // Ajoute les computed field
        def.put(pref.getFamily() + "_" + pref.getComputedTableName(), getComputedFieldList(pref));

        // Ajoute de la table maitres
        def.put(pref.getFamily() + "_" + pref.getBroadcastTableName(),
                databaseFactory.createSQLFieldList(connection,
                                                   null,
                                                   pref.getBroadcastTableName()));

        // Ajoute les tables non temporaire
        for (String joinKeyName : pref.getConfig().getJoinKeyMap().keySet()) {
            if (joinKeyName.startsWith("#")) {
                continue;
            }
            // Pas une Table temporaire
            JoinKey joinKey = pref.getConfig().getJoinKeyMap().get(joinKeyName);
            if (joinKey != null) {
                TableName tab = new TableName(joinKey.getLeftTableName());
                if (!def.containsKey(tab.getDBTableName())) {
                    APP.debug("determineTableDefinition " + tab.getDBTableName());
                    def.put(tab.getDBTableName(),
                            databaseFactory.createSQLFieldList(connection,
                                                               null,
                                                               tab.getDBTableName()));
                }
                String name = pref.getFamily() + "_" + joinKeyName;
                if (!def.containsKey(name)) {
                    def.put(name, def.get(tab.getDBTableName()));
                }
            }
        }

        return def;
    }


    private int findSqlType(String joinKeyName, String fieldName, String family) {
        SQLFieldList fieldList = tableDefinition.get(family + "_" + joinKeyName);
        if (fieldList == null) {
            throw new NoSuchElementException("La table >" + joinKeyName
                                             + "< ne fait pas partie des tables utilisable "
                                             + "pour la diffusion");
        }
        try {
            return fieldList.getFieldType(fieldName);
        }
        catch (NoSuchElementException ex) {
            throw new NoSuchElementException("La colonne >" + fieldName + "< n'existe pas dans la jointure >"
                                             + joinKeyName + "<");
        }
    }


    private String formatQuery(String queryPattern, Map<String, String> properties) {
        TemplateInterpreter interpreter = new TemplateInterpreter();
        interpreter.addAsVariable(properties);
        try {
            return interpreter.evaluate(queryPattern);
        }
        catch (UnknownVariableException ex) {
            throw new IllegalArgumentException(ex.toString());
        }
    }


    private FileColumnGenerator newFileColumnGenerator(ResultSet rs,
                                                       Map<FieldInfo, FieldInfo> definedFieldInfo,
                                                       Preferences section,
                                                       Context context) throws SQLException {
        FieldInfo fieldInfo = buildFieldInfo(definedFieldInfo, rs);

        int sqlType =
              findSqlType(fieldInfo.getFullDBTableName(), fieldInfo.getDBFieldName(), section.getFamily());

        return factory.newFileColumnGenerator(rs, fieldInfo, sqlType, section.createFunctionHolder(context));
    }
}
