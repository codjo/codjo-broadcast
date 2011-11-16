package net.codjo.broadcast.gui.selector;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.action.DeleteAction;
import net.codjo.mad.gui.request.factory.SelectFactory;
import java.util.HashMap;
/**
 *
 */
public class DeleteSelectorAction extends DeleteAction {
    public DeleteSelectorAction(GuiContext ctxt, RequestTable table) {
        super(ctxt, table);
    }


    @Override
    protected void sendDeleteRequest() throws RequestException {
        checkBeforeDelete();
        super.sendDeleteRequest();
    }


    private void checkBeforeDelete() throws RequestException {
        SelectFactory selectFactory = new SelectFactory("selectBroadcastSectionBySelectionId");
        Row firstSelectedRow = getTable().getFirstSelectedDataRow();
        FieldsList selector = new FieldsList("selectionId",
                                             "-" + firstSelectedRow.getFieldValue("selectorId"));
        selectFactory.init(selector);
        Result result = getGuiContext().getSender().send(selectFactory.buildRequest(new HashMap()));

        if (result.getRowCount()>0) {
            throw new RequestException("Vous ne pouvez pas supprimer cette requête de sélection car elle est utilisée dans un export.");
        }
    }
}
