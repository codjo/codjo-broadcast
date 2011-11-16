package net.codjo.broadcast.gui;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.factory.InsertFactory;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.security.common.api.UserMock;
import net.codjo.test.common.LogString;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import org.uispec4j.Button;
import org.uispec4j.ComboBox;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;

public class BroadcastColumnsDetailWindowTest extends UISpecTestCase {

    private static final String DB_TABLE_NAME = "dbTableName";
    private static final String DB_FIELD_NAME = "dbFieldName";

    private Window window;


    public void test_insertColumnDetails() throws Exception {
        BroadcastGuiContext guiContext = new BroadcastGuiContext();
        guiContext.setUser(new UserMock().mockIsAllowedTo(true));
        EmptyDetailDataSourceMock datasource = new EmptyDetailDataSourceMock(guiContext);
        window = new Window(
              new BroadcastColumnsDetailWindow(datasource, buildSelectedSectionRow("Famille 1")));

        ComboBox tableComboBox = window.getComboBox(DB_TABLE_NAME);
        checkTableNames(tableComboBox);
        tableComboBox.selectionEquals("#COMPUTED_TAB").check();

        ComboBox fieldComboBox = window.getComboBox(DB_FIELD_NAME);
        fieldComboBox.contains(new String[]{"Colonne a", "Colonne b"}).check();
        fieldComboBox.selectionEquals("Colonne a").check();

        tableComboBox.select("TABLE_2");
        fieldComboBox.contains(new String[]{"Colonne e", "Colonne f"}).check();
        fieldComboBox.selectionEquals("Colonne e").check();

        fieldComboBox.select("Colonne f");

        tableComboBox.select("TABLE_1");
        fieldComboBox.contains(new String[]{"Colonne c", "Colonne d"}).check();
        fieldComboBox.selectionEquals("Colonne c").check();

        Button okButton = window.getButton("ButtonPanelGui.okButton");

        okButton.click();
        datasource.log.assertContent("save(TABLE_1, TABLE_1_COLUMN_1)");

        fieldComboBox.select("Colonne d");

        datasource.log.clear();
        okButton.click();
        datasource.log.assertContent("save(TABLE_1, TABLE_1_COLUMN_2)");
    }


    public void test_updateColumnDetails() throws Exception {
        DetailDataSource datasource = new DetailDataSourceMock(new BroadcastGuiContext());
        window = new Window(
              new BroadcastColumnsDetailWindow(datasource, buildSelectedSectionRow("Famille 1")));

        ComboBox tableComboBox = window.getComboBox(DB_TABLE_NAME);
        checkTableNames(tableComboBox);
        tableComboBox.selectionEquals("TABLE_2").check();

        ComboBox fieldComboBox = window.getComboBox(DB_FIELD_NAME);
        fieldComboBox.contains("Colonne e").check();
        fieldComboBox.contains("Colonne f").check();
        fieldComboBox.selectionEquals("Colonne f").check();
    }


    private void checkTableNames(ComboBox tableComboBox) throws Exception {
        tableComboBox.contains(new String[]{"#COMPUTED_TAB", "TABLE_1", "TABLE_2"}).check();
    }


    private Row buildSelectedSectionRow(String familyValue) {
        Row row = new Row();
        row.addField("sectionId", "0");
        row.addField("family", familyValue);
        return row;
    }


    @Override
    protected void setUp() throws Exception {
        GuiPreferencesManager guiPreferenceManager = GuiPreferencesManager.getGuiPreferencesManager();
        guiPreferenceManager.addPreference(new TestGuiPreference(null));
    }


    private class DetailDataSourceMock extends DetailDataSource {

        private DetailDataSourceMock(GuiContext guiContext) {
            super(guiContext);
            setLoadFactory(new SelectFactory("idSelect"));
        }


        @Override
        public void load() throws RequestException {
            setFieldValue(DB_TABLE_NAME, "TABLE_2");
            setFieldValue(DB_FIELD_NAME, "TABLE_2_COLUMN_2");
        }
    }

    private class EmptyDetailDataSourceMock extends DetailDataSource {

        LogString log = new LogString();


        private EmptyDetailDataSourceMock(GuiContext guiContext) {
            super(guiContext);
            setSaveFactory(new InsertFactory("save"));
        }


        @Override
        public void load() throws RequestException {

        }


        public void save() throws RequestException {
            String tableName = getFieldValue(DB_TABLE_NAME);
            String fieldName = getFieldValue(DB_FIELD_NAME);
            log.call("save", tableName, fieldName);
        }
    }

    public class TestGuiPreference extends AbstractGuiPreference {

        Map<String, GuiField[]> tableGuiFields = new HashMap<String, GuiField[]>();


        public TestGuiPreference(StructureReader structures) {
            super("Famille 1", "#COMPUTED_TAB", structures);
            tableGuiFields.put("#COMPUTED_TAB",
                               new GuiField[]{
                                     new GuiField("#COMPUTED_TAB", "TABLE_1_COLUMN_1", "Colonne a"),
                                     new GuiField("#COMPUTED_TAB", "TABLE_1_COLUMN_2", "Colonne b")
                               });
            tableGuiFields.put("TABLE_1",
                               new GuiField[]{
                                     new GuiField("TABLE_1", "TABLE_1_COLUMN_1", "Colonne c"),
                                     new GuiField("TABLE_1", "TABLE_1_COLUMN_2", "Colonne d")
                               });
            tableGuiFields.put("TABLE_2",
                               new GuiField[]{
                                     new GuiField("TABLE_2", "TABLE_2_COLUMN_1", "Colonne e"),
                                     new GuiField("TABLE_2", "TABLE_2_COLUMN_2", "Colonne f")
                               });
        }


        @Override
        protected GuiField[] getComputedFields(String computedTableName) {
            return new GuiField[]{
                  new GuiField(computedTableName, "CHAMP_1", "Label CHAMP 1"),
                  new GuiField(computedTableName, "CHAMP_2", "Label CHAMP 2"),
                  new GuiField(computedTableName, "CHAMP_3", "Label CHAMP 3"),
                  new GuiField(computedTableName, "CHAMP_4", "Label CHAMP 4")
            };
        }


        public JComboBox buildSelectionComboBox() throws RequestException {
            return createComboBox(usingBuilder().withSelector("1", "Ma selection"));
        }


        @Override
        protected void initJoinKeys() {
            addJoinKey(getComputedTableName());
            addJoinKey("TABLE_1");
            addJoinKey("TABLE_2");
        }


        @Override
        public GuiField[] getGuiFieldsFor(String joinKeyName) {
            return tableGuiFields.get(joinKeyName);
        }


        @Override
        protected void addJoinKey(String tableName) {
            addJoinKey(tableName, tableName);
        }
    }
}
