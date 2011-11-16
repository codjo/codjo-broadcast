/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.common.structure.DefaultStructureReader;
import java.io.FileReader;
import java.util.List;
import javax.swing.JComboBox;
/**
 * Preference IHM pour l'export des VL.
 */
public class MockGuiPreference extends AbstractGuiPreference {
    static final String FAMILY_NAME = "FAMILY_TU";
    static final String COMPUTED_TABLE = "#COMPUTED_CH";


    public MockGuiPreference(StructureReader structures) {
        super(FAMILY_NAME, COMPUTED_TABLE, structures);
    }


    public MockGuiPreference(String family, StructureReader structures) {
        super(family, COMPUTED_TABLE, structures);
    }


    @Override
    public List<String> getAllFunctions() {
        List<String> allFunction = super.getAllFunctions();
        allFunction.add("ref.getPersonAddressFor(Valeur)");
        allFunction.add("ref.getPersonFunctionFor(Valeur)");
        allFunction.add("ref.getPersonNameFor(Valeur)");
        allFunction.add("ref.getPersonVatSubjectedFor(Valeur)");
        allFunction.add("ref.getRefCodeFor(Valeur)");
        allFunction.add("ref.getRefLabelFor(Valeur)");
        allFunction.add("ref.subString(Valeur, [Longueur])");
        return allFunction;
    }


    @Override
    public void initJoinKeys() {
        addJoinKey(COMPUTED_TABLE);
        addJoinKey("AP_PORTFOLIO");
        addJoinKey("AP_REPORTING");
        addJoinKey("AP_VALUATION_TYPE");
        addJoinKey("AP_MANAGEMENT");
        addJoinKey("AP_LEGAL");
        addJoinKey("AP_BENCHMARK");
        addJoinKey("AP_BENCHMARK as previousBench", "AP_BENCHMARK as previousBench");
    }


    public JComboBox buildSelectionComboBox() throws RequestException {
        return createComboBox(usingBuilder()
              .withSelector("0", "Tous les portefeuilles"));
    }


    @Override
    protected GuiField[] getComputedFields(String computedTableName) {
        return new GuiField[]{
              new GuiField(computedTableName, "CTE_STRING", "Constante Chaîne")
        };
    }


    public static MockGuiPreference createPreference() {
        return new MockGuiPreference(createStructureReader());
    }


    static StructureReader createStructureReader() {
        String filename = MockGuiPreference.class.getResource("GuiPrefTestStructure.xml").getFile();
        try {
            return new DefaultStructureReader(new FileReader(filename));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
