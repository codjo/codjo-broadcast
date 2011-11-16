package net.codjo.broadcast.gui;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.util.InternationalizableGuiContext;

public class BroadcastGuiContext extends InternationalizableGuiContext {
    public BroadcastGuiContext() {
        TranslationManager translationManager = InternationalizationUtil.retrieveTranslationManager(this);
        translationManager.addBundle("net.codjo.broadcast.gui.i18n", Language.FR);
        translationManager.addBundle("net.codjo.broadcast.gui.i18n", Language.EN);
    }
}
