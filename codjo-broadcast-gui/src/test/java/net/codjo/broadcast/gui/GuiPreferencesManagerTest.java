/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import java.util.Arrays;
import junit.framework.TestCase;
/**
 */
public class GuiPreferencesManagerTest extends TestCase {
    private GuiPreferencesManager pref;
    private MockGuiPreference mockPref;


    public void test_getPreferenceFor() throws Exception {
        pref.addPreference(mockPref);
        GuiPreference family = pref.getPreferenceFor(MockGuiPreference.FAMILY_NAME);

        assertEquals(mockPref, family);
    }


    public void test_getPreferenceFor_error() throws Exception {
        try {
            pref.getPreferenceFor("BadBAd");
            fail("La famille BadBAd n'est pas une famille definit");
        }
        catch (Exception ex) {
        }
    }


    public void test_getFamilies() throws Exception {
        pref.addPreference(new MockGuiPreference("AA_FAMILY", MockGuiPreference.createStructureReader()));
        pref.addPreference(mockPref);
        pref.addPreference(new MockGuiPreference("A_FAMILY", MockGuiPreference.createStructureReader()));
        assertEquals("[AA_FAMILY, A_FAMILY, FAMILY_TU]", Arrays.asList(pref.getFamilies()).toString());
    }


    @Override
    protected void setUp() throws Exception {
        GuiPreferencesManager.cancelSingleton();
        pref = GuiPreferencesManager.getGuiPreferencesManager();
        mockPref = MockGuiPreference.createPreference();
    }
}
