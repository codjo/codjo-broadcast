package net.codjo.broadcast.releasetest;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.computed.ComputedField;
import net.codjo.broadcast.gui.GuiField;
import net.codjo.broadcast.gui.GuiPreference;
import net.codjo.broadcast.gui.plugin.BroadcastGuiPlugin;
import net.codjo.broadcast.gui.plugin.BroadcastGuiPluginConfiguration;
import net.codjo.broadcast.server.plugin.BroadcastServerPluginConfiguration;
import net.codjo.expression.help.DefaultFunctionHolderHelp;
import net.codjo.expression.help.FunctionHelp;
import net.codjo.mad.common.structure.DefaultStructureReader;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.gui.base.GuiConfigurationMock;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public abstract class BroadcastConfigurationTestCase extends TestCase {
    private BroadcastServerPluginConfiguration serverConfiguration;
    private BroadcastGuiPluginConfiguration guiConfiguration;
    private GuiConfigurationMock madGuiConfiguration;


    protected abstract BroadcastGuiPluginConfiguration createGuiConfiguration();


    protected abstract BroadcastServerPluginConfiguration createServerConfiguration();


    public void test_families() throws Exception {
        Set<String> server = new TreeSet<String>();
        for (Preferences preferences : serverConfiguration.getPreferences()) {
            server.add(preferences.getFamily());
        }

        Set<String> client = new TreeSet<String>();
        for (GuiPreference preferences : guiConfiguration.createGuiPreferenceList(madGuiConfiguration)) {
            client.add(preferences.getFamily());
        }

        assertFalse("Aucune famille server déclarée", server.isEmpty());
        assertFalse("Aucune famille client déclarée", client.isEmpty());
        assertListEquals("Incohérence sur le nombre de familles déclarées", server, client);
    }


    /**
     * Test la coherence du paramétrage serveur et client pour les jointures accessibles à la diffusion.
     */
    public void test_joinKeyNames() throws Exception {
        for (String family : getFamilies()) {

            Preferences prefServeur = getServerPreferences(family);
            Set<String> serverDefinedTables = new TreeSet<String>(prefServeur.getTableList());
            serverDefinedTables.remove(prefServeur.getSelectionTableName());

            GuiPreference preference = getGuiPreferences(family);
            Set<String> clientDefinedTables = new TreeSet<String>(preference.getJoinKeyLabels().keySet());

            assertListEquals("Incohérence des jointures pour '" + family + "'",
                             serverDefinedTables, clientDefinedTables);
        }
    }


    public void test_computedField() throws Exception {
        for (String family : getFamilies()) {
            Set<String> serveur = new TreeSet<String>(getComputedFieldNames(family));

            Set<String> client = new TreeSet<String>();
            GuiField[] guiFields = getGuiPreferences(family).getGuiFieldsFor(getComputedTableName(family));

            assertNotNull("[GUI] Dans " + family + " aucune configuration pour "
                          + getComputedTableName(family),
                          guiFields);

            for (GuiField guiField : guiFields) {
                client.add(guiField.getFieldName());
            }

            assertEquals(serveur, client);
        }
    }


    public void test_functions() throws Exception {
        for (String family : getFamilies()) {
            Set<String> server = new TreeSet<String>(getFunctionsHelp(family));
            server.add("iif(condition, si-vrai, si-faux)");
            server.add("outil.format(Valeur)");
            Set<String> client = new TreeSet<String>(getGuiPreferences(family).getAllFunctions());
            assertListEquals("Incohérence des fonctions déclarées pour '" + family + "'", server, client);
        }
    }


    public void test_buildSelectionComboBox() throws Exception {
        for (String family : getFamilies()) {
            assertNotNull(getGuiPreferences(family).buildSelectionComboBox());
        }
    }


    private Set<String> getFunctionsHelp(String family) {
        Preferences preferences = getServerPreferences(family);
        if (preferences.getFunctionHolder() != null) {
            DefaultFunctionHolderHelp functionHolderHelp =
                  new DefaultFunctionHolderHelp(preferences.getFunctionHolder());
            Set<String> result = new TreeSet<String>();
            for (FunctionHelp functionHelp : functionHolderHelp.getFunctionHelpList()) {
                result.add(functionHelp.getHelp());
            }
            return result;
        }
        return Collections.emptySet();
    }


    @Override
    protected void setUp() throws Exception {
        serverConfiguration = createServerConfiguration();
        guiConfiguration = createGuiConfiguration();
        madGuiConfiguration = new MadGuiConfigurationMock();
    }


    private StructureReader createStructureReader() throws Exception {
        return new DefaultStructureReader(getClass().getResourceAsStream("/conf/structure.xml"));
    }


    private String[] getFamilies() {
        List<String> server = new ArrayList<String>();
        for (Preferences preferences : serverConfiguration.getPreferences()) {
            server.add(preferences.getFamily());
        }
        return server.toArray(new String[server.size()]);
    }


    private Set<String> getComputedFieldNames(String family) {
        Preferences vl = getServerPreferences(family);
        ComputedField[] fields = vl.getComputedFields();
        Set<String> names = new TreeSet<String>();
        for (ComputedField field : fields) {
            names.add(field.getName());
        }
        return names;
    }


    private String getComputedTableName(String family) {
        return getServerPreferences(family).getComputedTableName();
    }


    private Preferences getServerPreferences(String family) {
        for (Preferences preferences : serverConfiguration.getPreferences()) {
            if (family.equalsIgnoreCase(preferences.getFamily())) {
                return preferences;
            }
        }
        throw new IllegalArgumentException("Famille '" + family + "' inexistante côté serveur.");
    }


    private GuiPreference getGuiPreferences(String family) {
        for (GuiPreference preferences : guiConfiguration.createGuiPreferenceList(madGuiConfiguration)) {
            if (family.equalsIgnoreCase(preferences.getFamily())) {
                return preferences;
            }
        }
        throw new IllegalArgumentException("Famille '" + family + "' inexistante côté GUI.");
    }


    private void assertListEquals(String message, Set<String> serveur, Set<String> client) {
        // Diff Serveur -> Client
        List<String> diffServeurClient = new ArrayList<String>();
        diffServeurClient.addAll(serveur);
        diffServeurClient.removeAll(client);

        // Diff Client -> Serveur
        List<String> diffClientServeur = new ArrayList<String>();
        diffClientServeur.addAll(client);
        diffClientServeur.removeAll(serveur);

        // Presentation du resultat
        String error = message + "\n";
        if (diffServeurClient.size() != 0) {
            error += "\tDéfinit sur le serveur mais pas sur le client ! \n\t\t" + diffServeurClient + "\n";
        }

        if (diffClientServeur.size() != 0) {
            error += "\tDéfinit sur le client mais pas sur le serveur ! \n\t\t" + diffClientServeur + "\n";
        }

        if (diffClientServeur.size() != 0 || diffServeurClient.size() != 0) {
            throw new AssertionFailedError(error + "\nSERVEUR: " + serveur + "\nCLIENT: " + client);
        }
    }


    private class MadGuiConfigurationMock extends GuiConfigurationMock {
        private StructureReader structureReader;


        MadGuiConfigurationMock() throws Exception {
            this.structureReader = createStructureReader();
            DefaultGuiContext guiContext = (DefaultGuiContext)getGuiContext();
            guiContext.putProperty(BroadcastGuiPlugin.BROADCAST_VTOM_PARAMETER, "to_export.txt");
        }


        @Override
        public StructureReader getStructureReader() {
            return structureReader;
        }
    }
}
