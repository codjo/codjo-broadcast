package net.codjo.broadcast.gui.plugin;
import net.codjo.broadcast.gui.GuiPreference;
import net.codjo.mad.gui.base.GuiConfiguration;
/**
 *
 */
public interface GuiPreferenceBuilder {
    GuiPreference createPreference(GuiConfiguration guiConfiguration) throws Exception;
}
