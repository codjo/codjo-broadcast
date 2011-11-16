package net.codjo.broadcast.server.plugin;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.diffuser.DiffuserManager;
import net.codjo.broadcast.server.api.BroadcastContextBuilder;
import net.codjo.broadcast.server.api.DefaultBroadcastContextBuilder;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class BroadcastServerPluginConfiguration {
    private List<Preferences> serverPreferences = new ArrayList<Preferences>();
    private BroadcastContextBuilder broadcastContextBuilder = new DefaultBroadcastContextBuilder();
    private DiffuserManager diffuserManager = new DiffuserManager();


    public void addServerPreference(Preferences serverPreference) {
        this.serverPreferences.add(serverPreference);
    }


    public List<Preferences> getPreferences() {
        return serverPreferences;
    }


    public void setBroadcastContextBuilder(BroadcastContextBuilder broadcastContextBuilder) {
        this.broadcastContextBuilder = broadcastContextBuilder;
    }


    public BroadcastContextBuilder getBroadcastContextBuilder() {
        return broadcastContextBuilder;
    }


    public final String[] getDiffusersCode() {
        return diffuserManager.getDiffusersCode();
    }
}
