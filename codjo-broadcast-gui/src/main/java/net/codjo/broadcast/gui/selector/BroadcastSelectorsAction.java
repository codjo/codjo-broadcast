package net.codjo.broadcast.gui.selector;
import static net.codjo.broadcast.gui.plugin.BroadcastGuiPlugin.BROADCAST_SELECTOR_PREFERENCE_ID;
import net.codjo.mad.gui.framework.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.SimpleListGui;
import net.codjo.mad.gui.framework.SimpleListLogic;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
import javax.swing.ListSelectionModel;
/**
 *
 */
public class BroadcastSelectorsAction extends AbstractAction {

    public BroadcastSelectorsAction(GuiContext guiContext) {
        super(guiContext, "Sélecteurs génériques", "Liste des sélecteurs génériques");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext guiContext) throws Exception {
        SimpleListGui simpleListGui = new SimpleListLogic(guiContext,
                                                          "Sélecteurs génériques",
                                                          BROADCAST_SELECTOR_PREFERENCE_ID,
                                                          new Dimension(500, 350)).getGui();

        final RequestTable requestTable = simpleListGui.getTable();
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestTable.getDataSource().setPageSize(10000);
        simpleListGui.getToolBar()
              .replace(RequestToolBar.ACTION_DELETE, new DeleteSelectorAction(guiContext, requestTable));

        return simpleListGui;
    }
}
