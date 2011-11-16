package net.codjo.broadcast.gui.plugin;
import net.codjo.broadcast.gui.wizard.SelectDateStep;
import net.codjo.gui.toolkit.wizard.Wizard;
/**
 *
 */
public class DateBroadcastWizardCustomizer implements BroadcastWizardCustomizer {
    public void customizeWizard(Wizard wizard) {
        wizard.addStep(new SelectDateStep());
    }
}
