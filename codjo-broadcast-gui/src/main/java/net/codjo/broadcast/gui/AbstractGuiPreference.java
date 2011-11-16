/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.common.structure.FieldStructure;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.common.structure.TableStructure;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.sql.builder.TableName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JPanel;
/**
 * Classe de base pour les preferences d'une famille.
 */
public abstract class AbstractGuiPreference implements GuiPreference {
    private StructureReader structures;
    private String family = null;
    private String computedTableName = null;
    private Map<String, String> joinKeyLabels;


    protected AbstractGuiPreference(String family, String computedTable, StructureReader structures) {
        this.family = family;
        this.computedTableName = computedTable;
        this.structures = structures;
    }


    protected StructureReader getStructure() {
        return structures;
    }


    public List<String> getAllFunctions() {
        List<String> functions = new ArrayList<String>();
        functions.add("iif(condition, si-vrai, si-faux)");
        functions.add("outil.format(Valeur)");
        return functions;
    }


    public String getFamily() {
        return family;
    }


    public String getFamilyLabel() {
        return getFamily();
    }


    public GuiField[] getGuiFieldsFor(String joinKeyName) {
        if (getComputedTableName().equals(joinKeyName)) {
            return getComputedFields(getComputedTableName());
        }
        else if (isBroadcastable(joinKeyName)) {
            return determineTableGuiField(joinKeyName).toArray(new GuiField[]{});
        }

        throw new IllegalArgumentException("La Table >" + joinKeyName
                                           + "< ne fait pas partie des tables diffusables "
                                           + " ou n'est pas un lien vers un referentiel");
    }


    /**
     * @deprecated Méthode à ne plus utiliser dans un projet. Initialiser les tables de jointures avec la
     *             méthode initJoinKeys().
     */
    @Deprecated
    public Map<String, String> getTableLabels() {
        Map<String, String> labels = new HashMap<String, String>();
        String[] tables = getTableNames();
        for (String table : tables) {
            if (computedTableName.equals(table)) {
                labels.put(computedTableName, "Calcul");
            }
            else {
                labels.put(table, structures.getTableBySqlName(determineTableName(table)).getLabel());
            }
        }
        return labels;
    }


    public String determineTableName(String joinKeyName) {
        return new TableName(joinKeyName).getDBTableName();
    }


    public JPanel buildContentOptionPanel(DetailDataSource contentDataSource)
          throws RequestException {
        return null;
    }


    public void saveContentOptionPanel(DetailDataSource contentDataSource, JPanel panel)
          throws RequestException {
    }


    public Map<String, String> getJoinKeyLabels() {
        if (joinKeyLabels == null) {
            joinKeyLabels = new HashMap<String, String>();
            initJoinKeys();
        }
        return joinKeyLabels;
    }


    /**
     * Retourne la liste des tables exportables.
     *
     * @return liste des tables.
     *
     * @deprecated Utiliser initJoinKeys() pour initialiser les tables en jointure.
     */
    @Deprecated
    public String[] getTableNames() {
        return getJoinKeyLabels().keySet().toArray(new String[0]);
    }


    protected boolean isBroadcastable(String dbTableName) {
        for (int i = 0; i < getTableNames().length; i++) {
            if (getTableNames()[i].equals(dbTableName)) {
                return true;
            }
        }
        return false;
    }


    protected abstract GuiField[] getComputedFields(String aComputedTableName);


    protected void initJoinKeys() {
    }


    protected void addJoinKey(String tableName) {
        String tableLabel = "Calcul";
        if (!computedTableName.equals(tableName)) {
            tableLabel = getStructure().getTableBySqlName(determineTableName(tableName)).getLabel();
        }
        addJoinKey(tableName, tableLabel);
    }


    protected void addJoinKey(String tableName, String tableLabel) {
        getJoinKeyLabels().put(tableName, tableLabel);
    }


    protected List<GuiField> determineTableGuiField(String joinKeyName) {
        TableStructure table = structures.getTableBySqlName(determineTableName(joinKeyName));
        List<GuiField> fields = new ArrayList<GuiField>();

        for (Object object : table.getFieldsBySqlKey().values()) {
            FieldStructure item = (FieldStructure)object;
            declareField(fields, table, item);
        }

        Collections.sort(fields);

        return fields;
    }


    protected void declareField(final List<GuiField> fields, final TableStructure table,
                                final FieldStructure field) {
        fields.add(new GuiField(table.getSqlName(), field));
    }


    protected JComboBox createComboBox(SelectionComboBoxBuilder builder) {
        return builder.create();
    }


    protected SelectionComboBoxBuilder usingBuilder() {
        return new SelectionComboBoxBuilder();
    }


    protected String getComputedTableName() {
        return computedTableName;
    }


    public static class SelectionComboBoxBuilder {
        private DefaultComboBoxModel model = new DefaultComboBoxModel();
        private Map<String, String> renderer = new HashMap<String, String>();


        public SelectionComboBoxBuilder withSelector(String selectionID, String label) {
            model.addElement(selectionID);
            renderer.put(selectionID, label);
            return this;
        }


        public JComboBox create() {
            JComboBox comboBox = new JComboBox(model);
            comboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public void setText(String text) {
                    super.setText(renderer.get(text));
                }
            });
            return comboBox;
        }
    }
}
