/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.DFService;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.PreferencesManager;
import net.codjo.broadcast.common.message.BroadcastRequest;
import net.codjo.broadcast.server.api.BroadcastContextBuilder;
import net.codjo.broadcast.server.audit.BroadcastStringifier;
import net.codjo.plugin.server.ServerPlugin;
import net.codjo.sql.server.JdbcServiceUtil;
import net.codjo.workflow.server.api.JobAgent;
import net.codjo.workflow.server.api.JobAgent.MODE;
import net.codjo.workflow.server.api.ResourcesManagerAgent;
import net.codjo.workflow.server.api.ResourcesManagerAgent.AgentFactory;
import net.codjo.workflow.server.plugin.WorkflowServerPlugin;
import java.io.File;
import java.util.Date;
/**
 *
 */
public final class BroadcastServerPlugin implements ServerPlugin {
    public static final String BROADCAST_JOB_TYPE = BroadcastRequest.BROADCAST_JOB_TYPE;
    private final PreferencesManager preferenceManager;
    private BroadcastServerPluginConfiguration configuration = new BroadcastServerPluginConfiguration();


    public BroadcastServerPlugin() {
        this(null);
    }


    public BroadcastServerPlugin(WorkflowServerPlugin workflowServerPlugin) {
        this.preferenceManager = new DefaultPreferencesManager();

        new BroadcastStringifier().install(workflowServerPlugin);
    }


    public void initContainer(ContainerConfiguration aConfiguration) {
        for (Object serverPreference : getConfiguration().getPreferences()) {
            Preferences preference = (Preferences)serverPreference;
            preferenceManager.addPreferences(preference);
        }
    }


    public void start(AgentContainer agentContainer)
          throws Exception {

        agentContainer.acceptNewAgent("broadcast-drh-agent",
                                      new ResourcesManagerAgent(new BroadcastAgentFactory(),
                                                                DFService.createAgentDescription(
                                                                      BROADCAST_JOB_TYPE))).start();
        agentContainer.acceptNewAgent("broadcast-job-agent", createBroadcastAgent(MODE.NOT_DELEGATE)).start();
    }


    private BroadcastJobAgent createBroadcastAgent(MODE mode) {
        return new BroadcastJobAgent(new DefaultJobConfig(preferenceManager, new JdbcServiceUtil()), mode);
    }


    public BroadcastServerPluginConfiguration getConfiguration() {
        return configuration;
    }


    public void stop() {
    }


    PreferencesManager getPreferenceManager() {
        return preferenceManager;
    }


    private class DefaultPreferencesManager extends PreferencesManager {
        @Override
        public Context buildContext(final String user,
                                    final String fileName,
                                    final Date generationDate,
                                    final Date broadcastDate,
                                    final File outFolder) {
            BroadcastContextBuilder builder = getConfiguration().getBroadcastContextBuilder();
            synchronized (builder) {
                builder.setBroadcastDate(broadcastDate);
                builder.setGenerationDate(generationDate);
                builder.setFileName(fileName);
                builder.setOutFolder(outFolder);
                builder.setUser(user);
                return builder.buildContext();
            }
        }
    }

    private class BroadcastAgentFactory implements AgentFactory {
        public JobAgent create() throws Exception {
            return createBroadcastAgent(MODE.DELEGATE);
        }
    }
}
