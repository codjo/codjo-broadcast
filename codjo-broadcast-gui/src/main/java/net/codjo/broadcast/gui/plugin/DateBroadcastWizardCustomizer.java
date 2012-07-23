package net.codjo.broadcast.gui.plugin;
import net.codjo.broadcast.gui.wizard.SelectDateStep;
import net.codjo.gui.toolkit.wizard.Wizard;
import net.codjo.mad.gui.framework.GuiContext;
/**
 *
 */
public class DateBroadcastWizardCustomizer implements BroadcastWizardCustomizer {
    private GuiContext guiContext;


    public DateBroadcastWizardCustomizer(GuiContext guiContext) {
        this.guiContext = guiContext;
    }


    public void customizeWizard(Wizard wizard) {
        wizard.addStep(new SelectDateStep(guiContext));
    }
}
