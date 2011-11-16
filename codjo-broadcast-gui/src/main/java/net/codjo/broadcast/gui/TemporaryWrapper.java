package net.codjo.broadcast.gui;
/**
 *
 */
public class TemporaryWrapper {
    private TemporaryWrapper() {
    }


    public static void addPreferences(GuiPreference preference) {
        // TODO a virer lors du merge dans agf-broadcast
        GuiPreferencesManager.getGuiPreferencesManager().addPreference(preference);
    }
}
