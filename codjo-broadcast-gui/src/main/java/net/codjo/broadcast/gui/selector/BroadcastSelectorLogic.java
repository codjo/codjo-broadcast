package net.codjo.broadcast.gui.selector;
import net.codjo.broadcast.gui.GuiPreference;
import net.codjo.broadcast.gui.GuiPreferencesManager;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.GuiLogic;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
/**
 *
 */
public class BroadcastSelectorLogic implements GuiLogic<BroadcastSelectorDetailWindow> {

    private BroadcastSelectorDetailWindow gui;

    private ButtonPanelLogic buttonPanelLogic;

    private Map<String, String> selectorPreferences;


    public BroadcastSelectorLogic(DetailDataSource dataSource) throws RequestException {
        gui = new BroadcastSelectorDetailWindow();
        buttonPanelLogic = new ButtonPanelLogic(gui.getButtonPanelGui());

        fillSelectorPreferences();
        fillFamilyCombobox();
        initDatasource(dataSource);

        dataSource.getSaveFactory().setExcludedFieldList(new String[]{"selectorId"});

        if (dataSource.getLoadFactory() != null) {
            dataSource.load();
            gui.switchToUpdateMode();
        }
    }


    private void initDatasource(DetailDataSource dataSource) {
        dataSource.declare("selectorId");
        dataSource.declare("selectorName", gui.getSelectorName());
        dataSource.declare("selectorQuery", gui.getSelectorQuery());
        dataSource.declare("selectorFamily", gui.getSelectorFamily());
        buttonPanelLogic.setMainDataSource(dataSource);
    }


    private void fillFamilyCombobox() {
        final JComboBox familyCombo = gui.getSelectorFamily();
        familyCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    setFamilySelectorDescription((String)familyCombo.getSelectedItem());
                }
            }
        });

        Set<String> familySet = selectorPreferences.keySet();
        String[] selectorFamilies = familySet.toArray(new String[familySet.size()]);
        familyCombo.setModel(new DefaultComboBoxModel(selectorFamilies));

        setFamilySelectorDescription((String)familyCombo.getItemAt(0));
    }


    private void setFamilySelectorDescription(String family) {
        gui.getSelectorColumns().setText(selectorPreferences.get(family));
    }


    private void fillSelectorPreferences() {
        GuiPreferencesManager preferencesManager = GuiPreferencesManager.getGuiPreferencesManager();
        String[] families = preferencesManager.getFamilies();
        selectorPreferences = new HashMap<String, String>();
        for (String family : families) {
            GuiPreference preference = preferencesManager.getPreferenceFor(family);
            if (preference instanceof AbstractSelectorGuiPreference) {
                AbstractSelectorGuiPreference selectorPreference = (AbstractSelectorGuiPreference)preference;
                selectorPreferences.put(family, selectorPreference.getSelectorColumnsDescription());
            }
        }

        if (selectorPreferences.isEmpty()) {
            throw new IllegalStateException("Aucune préférence selector définie");
        }
    }


    public BroadcastSelectorDetailWindow getGui() throws Exception {
        return gui;
    }
}
