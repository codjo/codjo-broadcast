/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui.plugin;
import net.codjo.broadcast.common.message.BroadcastRequest;
import net.codjo.broadcast.gui.BroadcastFilesAction;
import net.codjo.broadcast.gui.BroadcastSectionsAction;
import net.codjo.broadcast.gui.GuiPreference;
import net.codjo.broadcast.gui.TemporaryWrapper;
import net.codjo.broadcast.gui.selector.BroadcastSelectorsAction;
import net.codjo.broadcast.gui.wizard.BroadcastSelectionStep;
import net.codjo.broadcast.gui.wizard.BroadcastTemplateRequestFactory;
import net.codjo.broadcast.gui.wizard.BroadcastVtomCaller;
import net.codjo.broadcast.gui.wizard.BroadcastWizardSummaryGui;
import net.codjo.gui.toolkit.wizard.Wizard;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.mad.client.plugin.MadConnectionPlugin;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.i18n.AbstractInternationalizableGuiPlugin;
import net.codjo.mad.gui.plugin.MadGuiPlugin;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.workflow.gui.plugin.WorkflowGuiPlugin;
import net.codjo.workflow.gui.wizard.CommandFile;
import net.codjo.workflow.gui.wizard.DefaultJobGui;
import net.codjo.workflow.gui.wizard.FinalStep;
import net.codjo.workflow.gui.wizard.WizardAction;
import net.codjo.workflow.gui.wizard.WizardBuilder;
import java.io.File;
import java.util.Arrays;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.xml.sax.InputSource;
/**
 * Plugin permettant d'enregistrer les préférences des familles d'export.
 */
public final class BroadcastGuiPlugin extends AbstractInternationalizableGuiPlugin {
    public static final String BROADCAST_VTOM_PARAMETER = "broadcast.vtom";
    private static final String BROADCAST_PREFERENCE_FILE_NAME = "broadcastPreference.xml";

    public static final String BROADCAST_SELECTOR_PREFERENCE_ID = "BroadcastSelectorsWindow";
    private static final String BROADCAST_SELECTOR_PREFERENCE_FILE
          = "selector/broadcastSelectorPreference.xml";

    private static final String BROADCAST_SECTIONS_PREFERENCE_ID = "BroadcastSectionsWindow";
    private static final String BROADCAST_COLUMNS_PREFERENCE_ID = "BroadcastColumnsWindow";
    private static final String BROADCAST_FILES_PREFERENCE_ID = "BroadcastFilesWindow";
    private static final String BROADCAST_CONTENTS_PREFERENCE_ID = "BroadcastFileContentsWindow";
    private static final String WIZARD_IMAGE = "wizard.broadcast.gif";
    private static final String WIZARD_TITLE = "Assistant Diffusion";
    private static final String WIZARD_DESCRIPTION = "Assistant diffusion manuelle";
    private static final String WIZARD_ACTION = "BroadcastWizard";
    private static final String WIZARD_ICON = "/net/codjo/broadcast/gui/wizard/broadcast.gif";
    private static final String BROADCAST_FILES_ACTION = "BroadcastFilesAction";
    private static final String BROADCAST_SECTIONS_ACTION = "BroadcastSectionsAction";
    private static final String BROADCAST_SELECTORS_ACTION = "BroadcastSelectorsAction";
    private final BroadcastGuiPluginConfiguration configuration;
    private MadGuiPlugin madGuiPlugin;


    public BroadcastGuiPlugin(Class<? extends GuiPreference>[] preferenceClasses,
                              MadConnectionPlugin madConnectionPlugin,
                              WorkflowGuiPlugin workflowGuiPlugin) {
        configuration = new BroadcastGuiPluginConfiguration(madConnectionPlugin.getOperations());
        configuration.addAllGuiPreferences(Arrays.asList(preferenceClasses));
        workflowGuiPlugin.getConfiguration()
              .setTaskManagerJobIcon(BroadcastRequest.BROADCAST_JOB_TYPE,
                                     new ImageIcon(getClass().getResource("/images/job.broadcast.png")));
    }


    public BroadcastGuiPlugin(MadGuiPlugin madGuiPlugin,
                              MadConnectionPlugin madConnectionPlugin,
                              WorkflowGuiPlugin workflowGuiPlugin) {
        this.madGuiPlugin = madGuiPlugin;
        configuration = new BroadcastGuiPluginConfiguration(madConnectionPlugin.getOperations());
        workflowGuiPlugin.getConfiguration()
              .setTaskManagerJobIcon(BroadcastRequest.BROADCAST_JOB_TYPE,
                                     new ImageIcon(getClass().getResource("/images/job.broadcast.png")));
    }


    public BroadcastGuiPluginConfiguration getConfiguration() {
        return configuration;
    }


    @Override
    public void initGui(GuiConfiguration guiConfiguration) throws Exception {
        super.initGui(guiConfiguration);

        for (GuiPreference guiPreference : configuration.createGuiPreferenceList(guiConfiguration)) {
            TemporaryWrapper.addPreferences(guiPreference);
        }

        if (madGuiPlugin != null) {
            loadBroadcastPreferences();
            if (configuration.isGenericSelectorInstalled()) {
                loadBroadcastGenericSelectorPreference();
            }
        }

        registerActions(guiConfiguration, guiConfiguration.getGuiContext());
    }


    @Override
    protected void registerLanguageBundles(TranslationManager translationManager) {
        translationManager.addBundle("net.codjo.broadcast.gui.i18n", Language.FR);
        translationManager.addBundle("net.codjo.broadcast.gui.i18n", Language.EN);
    }


    void registerActions(GuiConfiguration guiConfiguration, MutableGuiContext guiContext) {
        WizardAction wizardAction = createWizardAction(guiConfiguration);
        guiConfiguration.registerAction(this, WIZARD_ACTION, wizardAction);

        Action filesAction = new BroadcastFilesAction(guiContext);
        guiConfiguration.registerAction(this, BROADCAST_FILES_ACTION, filesAction);

        Action sectionsAction = new BroadcastSectionsAction(guiContext);
        guiConfiguration.registerAction(this, BROADCAST_SECTIONS_ACTION, sectionsAction);

        if (configuration.isGenericSelectorInstalled()) {
            Action selectorsAction = new BroadcastSelectorsAction(guiContext);
            guiConfiguration.registerAction(this, BROADCAST_SELECTORS_ACTION, selectorsAction);
        }
    }


    void loadBroadcastGenericSelectorPreference() {
        if (PreferenceFactory.containsPreferenceId(BROADCAST_SELECTOR_PREFERENCE_ID)) {
            return;
        }
        InputSource inputSource =
              new InputSource(getClass().getResourceAsStream(BROADCAST_SELECTOR_PREFERENCE_FILE));
        madGuiPlugin.getConfiguration().addPreferenceMapping(inputSource);
    }


    void loadBroadcastPreferences() {
        if (PreferenceFactory.containsPreferenceId(BROADCAST_COLUMNS_PREFERENCE_ID)
            && PreferenceFactory.containsPreferenceId(BROADCAST_FILES_PREFERENCE_ID)
            && PreferenceFactory.containsPreferenceId(BROADCAST_SECTIONS_PREFERENCE_ID)
            && PreferenceFactory.containsPreferenceId(BROADCAST_CONTENTS_PREFERENCE_ID)) {
            return;
        }
        InputSource inputSource =
              new InputSource(getClass().getResourceAsStream(BROADCAST_PREFERENCE_FILE_NAME));
        madGuiPlugin.getConfiguration().addPreferenceMapping(inputSource);
    }


    private WizardAction createWizardAction(GuiConfiguration guiConfiguration) {
        String file =
              (String)guiConfiguration.getGuiContext().getProperty(BROADCAST_VTOM_PARAMETER);
        return new WizardAction(guiConfiguration.getGuiContext(),
                                WIZARD_TITLE,
                                WIZARD_DESCRIPTION,
                                new DefaultWizardBuilder(new File(file), guiConfiguration.getGuiContext()),
                                WIZARD_ACTION,
                                WIZARD_ICON,
                                new ImageIcon(getClass().getResource(WIZARD_IMAGE)));
    }


    private class DefaultWizardBuilder implements WizardBuilder {
        private final File file;
        private MutableGuiContext guiContext;


        DefaultWizardBuilder(File file, MutableGuiContext guiContext) {
            this.file = file;
            this.guiContext = guiContext;
        }


        public Wizard createWizard() {
            FinalStep finalStep =
                  new FinalStep("Exporter...",
                                new BroadcastVtomCaller(new CommandFile(file)),
                                new BroadcastWizardSummaryGui(guiContext),
                                new DefaultJobGui(guiContext, "Traitement VTOM"),
                                new FinalStep.JobGuiData[]{
                                      new FinalStep.JobGuiData(
                                            new DefaultJobGui(guiContext, "Export des données"),
                                            new BroadcastTemplateRequestFactory())
                                });

            Wizard wizard = new Wizard();
            wizard.addStep(new BroadcastSelectionStep(getConfiguration().getWizardBroadcastSelector()));
            configuration.getBroadcastWizardCustomizer().customizeWizard(wizard);
            wizard.setFinalStep(finalStep);

            return wizard;
        }
    }
}
