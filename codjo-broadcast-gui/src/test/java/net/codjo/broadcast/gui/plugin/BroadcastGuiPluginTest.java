/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui.plugin;
import net.codjo.broadcast.gui.AnotherPreferenceMock;
import net.codjo.broadcast.gui.GuiPreference;
import net.codjo.broadcast.gui.GuiPreferencesManager;
import net.codjo.broadcast.gui.PreferenceMock;
import net.codjo.mad.client.plugin.MadConnectionPluginMock;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.mad.gui.base.GuiConfigurationMock;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.plugin.MadGuiPlugin;
import net.codjo.mad.gui.plugin.MadGuiPluginMock;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.test.common.AssertUtil;
import net.codjo.workflow.gui.plugin.WorkflowGuiPlugin;
import junit.framework.TestCase;
/**
 *
 */
public class BroadcastGuiPluginTest extends TestCase {
    private static final String PREFERENCE_CONFIG =
          "<?xml version='1.0' encoding='ISO-8859-1'?>                                             "
          + "<preferenceList xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'                 "
          + "                xsi:noNamespaceSchemaLocation='preference.xsd'>                       "
          + "    <preference id='CountryList'                                                      "
          + "                detailWindowClassName='"
          + BroadcastGuiPluginTest.class.getName() + "'>  "
          + "        <selectAll>selectAllCountry</selectAll>                                       "
          + "        <selectByPk>selectCountryById</selectByPk>                                    "
          + "        <update>updateCountry</update>                                                "
          + "        <delete>deleteCountry</delete>                                                "
          + "        <insert>newCountry</insert>                                                   "
          + "        <requetor>allCountry</requetor>                                               "
          + "        <column fieldName='countryCode' label='Code' preferredSize='20'/>             "
          + "        <column fieldName='countryName' label='Libellé' preferredSize='450'/>         "
          + "    </preference>                                                                     "
          + "</preferenceList>";


    public void test_initPlugin() throws Exception {
        GuiPreferencesManager.cancelSingleton();
        Class[] preferenceClasses = new Class[]{PreferenceMock.class, AnotherPreferenceMock.class};
        assertEquals(0, GuiPreferencesManager.getGuiPreferencesManager().getFamilies().length);
        //noinspection unchecked
        BroadcastGuiPlugin plugin = new BroadcastGuiPlugin(preferenceClasses,
                                                           new MadConnectionPluginMock(),
                                                           new WorkflowGuiPlugin());

        plugin.initGui(createGuiConfiguration());

        String[] families = GuiPreferencesManager.getGuiPreferencesManager().getFamilies();
        AssertUtil.assertUnorderedEquals(new String[]{"Fonky Family", "Addams Family"}, families);
    }


    public void test_loadBroadcastPreference() throws Exception {
        BroadcastGuiPlugin broadcastGuiPlugin = new BroadcastGuiPlugin(createMadGuiPlugin(),
                                                                       new MadConnectionPluginMock(),
                                                                       new WorkflowGuiPlugin());

        broadcastGuiPlugin.loadBroadcastPreferences();
        Preference preference = PreferenceFactory.getPreference("BroadcastSectionsWindow");
        assertNotNull(preference);
        assertEquals(3, preference.getColumns().size());
        preference = PreferenceFactory.getPreference("BroadcastFilesWindow");
        assertNotNull(preference);
        assertEquals(3, preference.getColumns().size());
        preference = PreferenceFactory.getPreference("BroadcastFileContentsWindow");
        assertNotNull(preference);
        assertEquals(3, preference.getColumns().size());
        preference = PreferenceFactory.getPreference("BroadcastColumnsWindow");
        assertNotNull(preference);
        assertEquals(6, preference.getColumns().size());

        preference = PreferenceFactory.getPreference("CountryList");
        assertNotNull(preference);
        assertEquals(2, preference.getColumns().size());
    }


    public void test_loadBroadcastPreference_withBuilder() throws Exception {
        BroadcastGuiPlugin broadcastGuiPlugin = new BroadcastGuiPlugin(createMadGuiPlugin(),
                                                                       new MadConnectionPluginMock(),
                                                                       new WorkflowGuiPlugin());

        broadcastGuiPlugin.getConfiguration().addGuiPreference(PreferenceMock.class);
        broadcastGuiPlugin.getConfiguration().addGuiPreference(new GuiPreferenceBuilder() {

            public GuiPreference createPreference(GuiConfiguration guiConfiguration) throws Exception {
                return new AnotherPreferenceMock(guiConfiguration.getStructureReader());
            }
        });

        broadcastGuiPlugin.initGui(createGuiConfiguration());

        String[] families = GuiPreferencesManager.getGuiPreferencesManager().getFamilies();
        AssertUtil.assertUnorderedEquals(new String[]{"Fonky Family", "Addams Family"}, families);
    }


    private GuiConfigurationMock createGuiConfiguration() {
        GuiConfigurationMock configuration = new GuiConfigurationMock();
        DefaultGuiContext guiContext = (DefaultGuiContext)configuration.getGuiContext();
        guiContext.putProperty(BroadcastGuiPlugin.BROADCAST_VTOM_PARAMETER, "to_export.txt");
        return configuration;
    }


    private MadGuiPlugin createMadGuiPlugin() throws Exception {
        MadGuiPlugin madGuiPlugin = new MadGuiPluginMock(PREFERENCE_CONFIG);
        madGuiPlugin.initGui(new GuiConfigurationMock());
        return madGuiPlugin;
    }
}
