package net.codjo.broadcast.gui.plugin;
import net.codjo.broadcast.gui.wizard.SelectPeriodStep;
import net.codjo.gui.toolkit.wizard.Wizard;
/**
 *
 */
class PeriodBroadcastWizardCustomizer implements BroadcastWizardCustomizer {
    public void customizeWizard(Wizard wizard) {
        wizard.addStep(new SelectPeriodStep());
    }
}
