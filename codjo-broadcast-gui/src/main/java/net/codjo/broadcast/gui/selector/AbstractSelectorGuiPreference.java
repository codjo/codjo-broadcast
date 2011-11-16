package net.codjo.broadcast.gui.selector;
import net.codjo.broadcast.gui.AbstractGuiPreference;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.gui.request.factory.SelectFactory;
import java.util.HashMap;
import javax.swing.JComboBox;
/**
 *
 */
public abstract class AbstractSelectorGuiPreference extends AbstractGuiPreference {

    protected AbstractSelectorGuiPreference(String family,
                                            String computedTable,
                                            StructureReader structures) {
        super(family, computedTable, structures);
    }


    protected abstract String getSelectorColumnsDescription();


    protected abstract SelectionComboBoxBuilder addStaticSelectors(SelectionComboBoxBuilder comboBoxBuilder);


    public JComboBox buildSelectionComboBox() throws RequestException {
        return createComboBox(usingBuilder());
    }


    @Override
    protected JComboBox createComboBox(SelectionComboBoxBuilder builder) {
        SelectionComboBoxBuilder selectionComboBoxBuilder = addStaticSelectors(builder);
        try {
            Result result = selectAllQueries();
            for (int index = 0; index < result.getRowCount(); index++) {
                final Row row = result.getRow(index);
                String selectorId = "-" + row.getFieldValue("selectorId");
                String selectorName = "<Générique> " + row.getFieldValue("selectorName");
                selectionComboBoxBuilder = selectionComboBoxBuilder.withSelector(selectorId,
                                                                                 selectorName);
            }
        }
        catch (RequestException e) {
            throw new RuntimeException(
                  "Une erreur s'est produite durant l'initialisation de la combo des sélections.");
        }

        return selectionComboBoxBuilder.create();
    }


    private Result selectAllQueries() throws RequestException {
        SelectFactory selectFactory = new SelectFactory("selectAllBroadcastSelectorByFamily");
        FieldsList selector = new FieldsList("family", this.getFamily());
        selectFactory.init(selector);
        RequestSender requestSender = new RequestSender();
        ResultManager resultManager = requestSender.send(selectFactory.buildRequest(new HashMap()));
        return (Result)resultManager.getResults().iterator().next();
    }
}
