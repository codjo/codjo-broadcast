/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.agent.Aid;
import net.codjo.agent.ContainerFailureException;
import net.codjo.agent.MessageTemplate;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.agent.test.SubStep;
import net.codjo.agent.test.TesterAgent;
import net.codjo.broadcast.common.Broadcaster;
import net.codjo.broadcast.common.Context;
import net.codjo.broadcast.common.message.BroadcastRequest;
import net.codjo.test.common.LogString;
import net.codjo.workflow.common.message.JobAudit;
import net.codjo.workflow.common.protocol.JobProtocol;
import java.io.File;
import java.sql.SQLException;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
/**
 * Classe de test de {@link BroadcastJobAgent}.
 */
public class BroadcastJobAgentTest extends TestCase {
    private static final String BROADCAST_AID = "broadcast-broadcastAgent";
    private BroadcastJobAgent broadcastAgent;
    private AgentContainerFixture agentContainerFixture = new AgentContainerFixture();
    private LogString logString = new LogString();
    private JobConfigMock jobConfig;


    public void test_broadcast() throws Exception {
        agentContainerFixture.startNewAgent(BROADCAST_AID, broadcastAgent);

        AclMessage broadcastMessage = createBroadcastRequest("r24",
                                                             "2006-01-01",
                                                             "1998-07-12",
                                                             "destfolder",
                                                             "result.txt");
        TesterAgent tester = new TesterAgent();
        tester.record().sendMessage(broadcastMessage);
        tester.record().receiveMessage(hasAuditType(JobAudit.Type.PRE));
        tester.record().receiveMessage(hasAuditType(JobAudit.Type.POST))
              .add(new AssertAuditArguments("destfolder\\result.txt")).die();

        runAndAssertNoError(tester);

        Context context = new Context();
        context.putParameter("broadcastDate", "1998-07-12");
        context.putParameter("userId", null);
        context.putParameter("jobRequest", broadcastMessage.getContentObject());
        context.putParameter("agentContainer", tester.getAgentContainer());

        logString.assertContent(
              "jobConfig.init(agent:broadcast-broadcastAgent, message:REQUEST), "
              + "jobConfig.buildContext(r24, result.txt, 2006-01-01, 1998-07-12, destfolder), "
              + "jobConfig.getBroadcastersFor(result.txt, context(1998-07-12)), "
              + "Broadcaster.setDestinationFile(destfolder\\result.txt), "
              + "Broadcaster.broadcast(" + context.getParameters() + ")");
    }


    public void test_broadcast_withoutParameter()
          throws Exception {
        jobConfig.mockGetBroadcastersFor(new Broadcaster[0]);

        agentContainerFixture.startNewAgent(BROADCAST_AID, broadcastAgent);

        TesterAgent tester = new TesterAgent();
        tester.record().sendMessage(createBroadcastRequest());
        tester.record().receiveMessage(hasAuditType(JobAudit.Type.PRE))
              .assertReceivedMessage(hasNoAuditError());
        tester.record().receiveMessage(hasAuditType(JobAudit.Type.POST))
              .assertReceivedMessage(hasAuditError(true,
                                                   "Erreur durant l'export de broadcast-result.txt : Broadcaster introuvable."))
              .die();

        runAndAssertNoError(tester);
    }


    public void test_broadcast_error() throws Exception {
        jobConfig.mockGetBroadcastersForFailure(new SQLException("Erreur SQL"));

        agentContainerFixture.startNewAgent(BROADCAST_AID, broadcastAgent);

        TesterAgent tester = new TesterAgent();
        tester.record().sendMessage(createBroadcastRequest());
        tester.record().receiveMessage(hasAuditType(JobAudit.Type.PRE))
              .assertReceivedMessage(hasNoAuditError());
        tester.record().receiveMessage(hasAuditType(JobAudit.Type.POST))
              .assertReceivedMessage(hasAuditError(true,
                                                   "Erreur durant l'export de broadcast-result.txt : Erreur SQL"))
              .die();

        runAndAssertNoError(tester);
    }


    public void test_agentDescription() throws Exception {
        agentContainerFixture.startNewAgent(BROADCAST_AID, broadcastAgent);

        agentContainerFixture.assertAgentWithService(new String[]{BROADCAST_AID},
                                                     BroadcastRequest.BROADCAST_JOB_TYPE);
    }


    @Override
    protected void setUp() throws Exception {
        jobConfig = new JobConfigMock(new LogString("jobConfig", logString));
        broadcastAgent = new BroadcastJobAgent(jobConfig);
        agentContainerFixture.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        agentContainerFixture.doTearDown();
    }


    private MessageTemplate hasAuditType(JobAudit.Type type) {
        return MessageTemplate.and(MessageTemplate.matchPerformative(AclMessage.Performative.INFORM),
                                   matchType(type));
    }


    private MessageTemplate matchType(final JobAudit.Type auditType) {
        return MessageTemplate.matchWith(new MessageTemplate.MatchExpression() {
            public boolean match(AclMessage aclMessage) {
                JobAudit jobAudit = (JobAudit)aclMessage.getContentObject();
                return auditType == jobAudit.getType();
            }
        });
    }


    private MessageTemplate hasNoAuditError() {
        return hasAuditError(false, "");
    }


    private MessageTemplate hasAuditError(final boolean hasError, final String message) {
        return MessageTemplate.matchWith(new MessageTemplate.MatchExpression() {
            public boolean match(AclMessage aclMessage) {
                JobAudit jobAudit = (JobAudit)aclMessage.getContentObject();
                if (jobAudit.hasError() && hasError) {
                    return message.equals(jobAudit.getErrorMessage());
                }
                return jobAudit.hasError() == hasError;
            }
        });
    }


    private AclMessage createBroadcastRequest() {
        return createBroadcastRequest("r24(comme le metier)", "2006-01-01", "2006-01-20",
                                      "destfolder", "broadcast-result.txt");
    }


    private AclMessage createBroadcastRequest(String user, String generationDate,
                                              String broadcastDate, String destFolderValue, String fileName) {
        AclMessage requestMessage = new AclMessage(AclMessage.Performative.REQUEST);
        requestMessage.setConversationId("conversation-id");
        requestMessage.setProtocol(JobProtocol.ID);
        requestMessage.addReceiver(new Aid(BROADCAST_AID));

        BroadcastRequest request = new BroadcastRequest();
        request.setId(requestMessage.getConversationId());
        request.setInitiatorLogin(user);
        request.setDate(java.sql.Date.valueOf(generationDate));
        request.setBroadcastDate(java.sql.Date.valueOf(broadcastDate));
        request.setDestinationFile(new File(destFolderValue, fileName));
        requestMessage.setContentObject(request.toRequest());
        return requestMessage;
    }


    private void runAndAssertNoError(TesterAgent tester)
          throws ContainerFailureException {
        agentContainerFixture.startNewAgent("tester", tester);
        try {
            agentContainerFixture.waitForAgentDeath("tester");
        }
        catch (AssertionFailedError e) {
            tester.getErrorManager().assertNoError();
        }

        assertFalse(tester.getErrorManager().hasError());
    }


    private static class AssertAuditArguments implements SubStep {
        private String expected;


        AssertAuditArguments(String expectedResultFile) {
            expected = expectedResultFile;
        }


        public void run(Agent agent, AclMessage message)
              throws AssertionFailedError {
            JobAudit audit = (JobAudit)message.getContentObject();
            Assert.assertNotNull(audit.getArguments());
            Assert.assertEquals(expected,
                                audit.getArguments().get(BroadcastJobAgent.RESULT_FILE));
        }
    }
}
