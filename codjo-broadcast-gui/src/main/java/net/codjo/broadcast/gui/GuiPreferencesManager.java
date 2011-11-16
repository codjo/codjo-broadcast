/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import java.util.Map;
import java.util.TreeMap;
/**
 * Preference pour les IHM broadcast.
 *
 * @author $Author: galaber $
 * @version $Revision: 1.3 $
 */
public class GuiPreferencesManager {
    private static GuiPreferencesManager manager = null;
    private Map<String, GuiPreference> preferences = new TreeMap<String, GuiPreference>();


    private GuiPreferencesManager() {
    }


    public void initPrefs(GuiPreference[] pref) {
        for (GuiPreference aPref : pref) {
            addPreference(aPref);
        }
    }


    public static GuiPreferencesManager getGuiPreferencesManager() {
        if (manager == null) {
            manager = new GuiPreferencesManager();
        }
        return manager;
    }


    /**
     * TODO public a virer !!!
     */
    public static void cancelSingleton() {
        manager = null;
    }


    void addPreference(GuiPreference pref) {
        preferences.put(pref.getFamily(), pref);
    }


    public String[] getBroadcastLocations() {
        return new String[]{};
    }


    public String[] getDiffuserCode() {
        return new String[]{"NONE", "CFT"};
    }


    public String[] getFamilies() {
        return preferences.keySet().toArray(new String[]{});
    }


    public GuiPreference getPreferenceFor(String familyName) {
        GuiPreference pref = preferences.get(familyName);
        if (pref == null) {
            throw new IllegalArgumentException("Famille d'export '" + familyName + "' inexistante!");
        }
        return pref;
    }


    public String[] getVtomBatchFilesNames() {
        return new String[]{};
    }
}
