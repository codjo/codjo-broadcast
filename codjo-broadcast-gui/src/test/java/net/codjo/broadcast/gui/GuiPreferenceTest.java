/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import net.codjo.mad.client.request.RequestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import junit.framework.TestCase;
/**
 */
public class GuiPreferenceTest extends TestCase {
    protected AbstractGuiPreference pref;


    @Override
    protected void setUp() throws Exception {
        pref = MockGuiPreference.createPreference();
    }


    public void test_getAllFunctions() throws Exception {
        List funcs = pref.getAllFunctions();
        assertTrue("Contient iif", funcs.contains("iif(condition, si-vrai, si-faux)"));
        assertTrue("Contient outil.format", funcs.contains("outil.format(Valeur)"));
    }


    public void test_determineTableName() throws Exception {
        assertEquals("AP_TABLE", pref.determineTableName("AP_TABLE"));
    }


    public void test_determineTableName_joinKey() throws Exception {
        assertEquals("AP_TABLE", pref.determineTableName("AP_TABLE as maTable"));
    }


    public void test_getGuiFieldsFor() throws Exception {
        String[] jkNames = pref.getJoinKeyLabels().keySet().toArray(new String[]{});

        for (String jkName : jkNames) {
            assertNotNull("Verification des champs de " + jkName, pref.getGuiFieldsFor(jkName));
        }
    }


    public void test_getGuiFieldFor_unknown() throws Exception {
        try {
            GuiField[] res = pref.getGuiFieldsFor("UNKNOWN");
            fail("La table ne fait pas partie des tables diffusable ! " + res);
        }
        catch (IllegalArgumentException ex) {
        }
    }


    public void test_getGuiFieldFor_Computed() throws Exception {
        GuiField[] fields = pref.getGuiFieldsFor(pref.getComputedTableName());
        assertTrue(fields.length > 0);
    }


    public void test_getTableLabels() throws Exception {
        Map<String, String> labels = pref.getTableLabels();
        String[] tables = pref.getTableNames();

        assertEquals("Toutes les tables sont dans la map getTableLabels",
                     sort(Arrays.asList(tables)), sort(labels.keySet()));

        for (String table : tables) {
            assertNotNull("verification du label de la table " + table, labels.get(table));
        }
    }


    public void test_getTableLabels_withAllias() throws Exception {
        Map<String, String> labels = pref.getTableLabels();
        String[] tables = pref.getTableNames();

        assertEquals("Toutes les tables sont dans la map getTableLabels",
                     sort(Arrays.asList(tables)), sort(labels.keySet()));

        for (String table : tables) {
            assertNotNull("verification du label de la table " + table, labels.get(table));
        }
    }


    public void test_buildSelectionComboBox() throws Exception {
        pref = new MockGuiPreference(MockGuiPreference.createStructureReader()) {

            @Override
            public JComboBox buildSelectionComboBox() throws RequestException {
                return createComboBox(usingBuilder()
                      .withSelector("A", "label A")
                      .withSelector("B", "label B"));
            }
        };
        JComboBox comboBox = pref.buildSelectionComboBox();
        assertNotNull(comboBox);
        assertEquals(2, comboBox.getItemCount());
        assertCombo(comboBox, 0, "A", "label A");
        assertCombo(comboBox, 1, "B", "label B");
    }


    private void assertCombo(JComboBox comboBox, int index, String expectedModel, String expectedLabel) {
        JLabel label = (JLabel)comboBox.getRenderer()
              .getListCellRendererComponent(new JList(), expectedModel, index, false, false);
        assertEquals(expectedLabel, label.getText());
        assertEquals(expectedModel, comboBox.getItemAt(index));
    }


    private List<String> sort(Collection<String> col) {
        List<String> list = new ArrayList<String>(col);
        Collections.sort(list);
        return list;
    }
}
