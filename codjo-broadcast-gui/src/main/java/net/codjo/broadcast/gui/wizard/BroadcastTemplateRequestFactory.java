/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui.wizard;
import net.codjo.broadcast.common.message.BroadcastRequest;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.JobRequestTemplate;
import net.codjo.workflow.gui.wizard.RequestTemplateFactory;
import net.codjo.workflow.gui.wizard.WizardConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
/**
 *
 */
public class BroadcastTemplateRequestFactory implements RequestTemplateFactory {
    public JobRequestTemplate createTemplate(final Map wizardState) {
        JobRequestTemplate matchType = JobRequestTemplate.matchType("broadcast");
        JobRequestTemplate matchInitiator =
              JobRequestTemplate.matchInitiator(System.getProperty("user.name"));
        JobRequestTemplate matchRequest =
              JobRequestTemplate.matchCustom(new BroadcastMatchExpression(wizardState));

        return JobRequestTemplate.and(matchType, JobRequestTemplate.and(matchInitiator, matchRequest));
    }


    private class BroadcastMatchExpression implements JobRequestTemplate.MatchExpression {
        private String selectionFileName;
        private Date date;


        BroadcastMatchExpression(Map wizardState) {
            selectionFileName = (String)wizardState.get(WizardConstants.BROADCAST_FILE_NAME);
            date = removeHoursToDate((Date)wizardState.get(WizardConstants.BROADCAST_DATE));
        }


        public boolean match(JobRequest jobRequest) {
            BroadcastRequest request = new BroadcastRequest(jobRequest);
            return selectionFileName.equals(request.getDestinationFile().getName())
                   && date.equals(request.getBroadcastDate());
        }


        private Date removeHoursToDate(Date dateWithHour) {
            return java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(dateWithHour));
        }
    }
}
