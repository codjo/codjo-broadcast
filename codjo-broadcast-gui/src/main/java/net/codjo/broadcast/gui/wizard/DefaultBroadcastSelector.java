package net.codjo.broadcast.gui.wizard;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.SelectRequest;
/**
 *
 */
public class DefaultBroadcastSelector implements BroadcastSelector {
    private MadConnectionOperations madConnection;
    private String selectHandlerId;


    public DefaultBroadcastSelector(MadConnectionOperations madConnection, String selectHandlerId) {
        this.madConnection = madConnection;
        this.selectHandlerId = selectHandlerId;
    }


    public Result selectBroadcastItems(String[] columns) throws RequestException {
        SelectRequest select = new SelectRequest();
        select.setPage(1, 1000);
        select.setId(selectHandlerId);
        select.setAttributes(columns);
        return madConnection.sendRequest(select);
    }
}
