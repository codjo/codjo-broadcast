package net.codjo.broadcast.gui.plugin;
import net.codjo.broadcast.gui.GuiPreference;
import net.codjo.broadcast.gui.wizard.BroadcastSelector;
import net.codjo.broadcast.gui.wizard.DefaultBroadcastSelector;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.gui.base.GuiConfiguration;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class BroadcastGuiPluginConfiguration {
    private List<GuiPreferenceBuilder> guiPreferenceBuilders = new ArrayList<GuiPreferenceBuilder>();
    private BroadcastWizardCustomizer broadcastWizardCustomizer = new PeriodBroadcastWizardCustomizer();
    private boolean genericSelectorInstalled = false;
    private BroadcastSelector wizardBroadcastSelector;


    public BroadcastGuiPluginConfiguration(MadConnectionOperations madConnectionOperations) {
        wizardBroadcastSelector = new DefaultBroadcastSelector(madConnectionOperations,
                                                               "selectAllBroadcastFiles");
    }


    public void setWizardBroadcastSelector(BroadcastSelector wizardBroadcastSelector) {
        this.wizardBroadcastSelector = wizardBroadcastSelector;
    }


    public BroadcastSelector getWizardBroadcastSelector() {
        return wizardBroadcastSelector;
    }


    public void addGuiPreference(final Class<? extends GuiPreference> guiPreference) {
        guiPreferenceBuilders.add(new DefaultGuiPreferenceBuilder(guiPreference));
    }


    public void addGuiPreference(GuiPreferenceBuilder guiPreference) {
        guiPreferenceBuilders.add(guiPreference);
    }


    public BroadcastWizardCustomizer getBroadcastWizardCustomizer() {
        return broadcastWizardCustomizer;
    }


    public void setBroadcastWizardCustomizer(BroadcastWizardCustomizer broadcastWizardCustomizer) {
        this.broadcastWizardCustomizer = broadcastWizardCustomizer;
    }


    public boolean isGenericSelectorInstalled() {
        return genericSelectorInstalled;
    }


    public void installGenericSelector() {
        this.genericSelectorInstalled = true;
    }


    public List<GuiPreference> createGuiPreferenceList(GuiConfiguration guiConfiguration) {
        try {
            List<GuiPreference> guiPreferences = new ArrayList<GuiPreference>(guiPreferenceBuilders.size());
            for (GuiPreferenceBuilder builder : guiPreferenceBuilders) {
                guiPreferences.add(builder.createPreference(guiConfiguration));
            }
            return guiPreferences;
        }
        catch (Exception e) {
            throw new IllegalStateException("Impossible de construire des preferences.", e);
        }
    }


    void addAllGuiPreferences(List<Class<? extends GuiPreference>> classes) {
        for (Class<? extends GuiPreference> guiPreferenceClass : classes) {
            addGuiPreference(guiPreferenceClass);
        }
    }


    private static class DefaultGuiPreferenceBuilder implements GuiPreferenceBuilder {
        private final Class<? extends GuiPreference> guiPreference;


        DefaultGuiPreferenceBuilder(Class<? extends GuiPreference> guiPreference) {
            this.guiPreference = guiPreference;
        }


        public GuiPreference createPreference(GuiConfiguration guiConfiguration) throws Exception {
            return createInstance(guiPreference, guiConfiguration.getStructureReader());
        }


        private GuiPreference createInstance(Class<? extends GuiPreference> preferenceClass,
                                             StructureReader structureReader) throws Exception {
            Constructor<? extends GuiPreference> constructor =
                  preferenceClass.getConstructor(StructureReader.class);
            return constructor.newInstance(structureReader);
        }
    }
}
