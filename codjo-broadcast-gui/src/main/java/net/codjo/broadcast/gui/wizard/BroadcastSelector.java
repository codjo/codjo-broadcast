package net.codjo.broadcast.gui.wizard;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
/**
 *
 */
public interface BroadcastSelector {
    Result selectBroadcastItems(String[] columns) throws RequestException;
}
