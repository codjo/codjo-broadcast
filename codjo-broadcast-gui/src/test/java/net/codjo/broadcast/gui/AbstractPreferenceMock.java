package net.codjo.broadcast.gui;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.common.structure.StructureReader;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
/**
 *
 */
public abstract class AbstractPreferenceMock extends AbstractGuiPreference {
    protected AbstractPreferenceMock(String family, String computedTable, StructureReader structures) {
        super(family, computedTable, structures);
    }


    @Override
    protected GuiField[] getComputedFields(String computedTableName) {
        return new GuiField[0];
    }


    @Override
    public Map<String, String> getJoinKeyLabels() {
        return new HashMap<String, String>();
    }


    public JComboBox buildSelectionComboBox() throws RequestException {
        return null;
    }
}
