/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server.plugin;
import java.io.File;
import java.sql.SQLException;
import net.codjo.agent.DFService;
import net.codjo.broadcast.common.Broadcaster;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.message.BroadcastRequest;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobAudit;
import net.codjo.workflow.common.message.JobAudit.Anomaly;
import net.codjo.workflow.common.message.JobException;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
import net.codjo.workflow.server.api.JobAgent;
/**
 *
 */
class BroadcastJobAgent extends JobAgent {
    public static final String RESULT_FILE = "result-file";


    BroadcastJobAgent(JobConfig jobConfig) {
        this(jobConfig, MODE.NOT_DELEGATE);
    }


    BroadcastJobAgent(JobConfig jobConfig, MODE mode) {
        super(new BroadcastParticipant(jobConfig),
              new DFService.AgentDescription(
                    new DFService.ServiceDescription(BroadcastRequest.BROADCAST_JOB_TYPE,
                                                     "broadcast-service")), mode);
    }


    private static class BroadcastParticipant extends JobProtocolParticipant {
        private final JobConfig jobConfig;
        private Anomaly anomaly;


        BroadcastParticipant(JobConfig jobConfig) {
            this.jobConfig = jobConfig;
        }


        @Override
        protected void executeJob(JobRequest jobRequest)
              throws JobException {
            jobConfig.init(getAgent(), getRequestMessage());

            BroadcastRequest request = new BroadcastRequest(jobRequest);
            File destinationFile = request.getDestinationFile();
            try {
                Context context = jobConfig.buildContext(request.getInitiatorLogin(),
                                                         destinationFile.getName(),
                                                         request.getDate(),
                                                         request.getBroadcastDate(),
                                                         request.getDestinationFolder());

                context.putParameter("userId", getRequestMessage().decodeUserId());
                context.putParameter("jobRequest", jobRequest);
                context.putParameter("agentContainer", getAgent().getAgentContainer());

                Broadcaster broadcaster = getBroadcaster(destinationFile.getName(), context);
                broadcaster.setDiffuser(null);
                destinationFile.delete();
                broadcaster.setDestinationFile(destinationFile);

                broadcaster.broadcast(context);

                if (context.getWarnings() != null) {
                    anomaly = new Anomaly(
                          String.format("Warnings durant l'export de %s", destinationFile.getName()),
                          context.getWarnings());
                }
            }
            catch (JobException exception) {
                throw exception;
            }
            catch (Exception exception) {
                throw new JobException("Erreur durant l'export de "
                                       + destinationFile.getName() + " : " + exception.getLocalizedMessage(),
                                       exception);
            }
        }


        private Broadcaster getBroadcaster(String fileName, Context context)
              throws SQLException, JobException {
            Broadcaster[] broadcasters = jobConfig.getBroadcastersFor(fileName, context);

            if (broadcasters == null || broadcasters.length != 1) {
                throw new JobException("Erreur durant l'export de " + fileName
                                       + " : Broadcaster introuvable.");
            }
            return broadcasters[0];
        }


        @Override
        protected void handlePOST(JobRequest jobRequest, JobException failure) {
            JobAudit audit = new JobAudit(JobAudit.Type.POST);
            if (failure != null) {
                audit.setError(new JobAudit.Anomaly(failure.getMessage(), failure));
            }
            else {
                BroadcastRequest request = new BroadcastRequest(jobRequest);
                audit.setArguments(new Arguments(RESULT_FILE,
                                                 request.getDestinationFile().getPath()));

                if (anomaly != null) {
                    audit.setWarning(anomaly);
                }
            }
            sendAudit(audit);
        }
    }
}
