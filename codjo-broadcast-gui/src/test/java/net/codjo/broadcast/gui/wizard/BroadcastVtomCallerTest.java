/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui.wizard;
import net.codjo.test.common.LogString;
import net.codjo.workflow.gui.wizard.CommandFile;
import net.codjo.workflow.gui.wizard.CommandFileMock;
import net.codjo.workflow.gui.wizard.WizardUtil;
import java.util.Map;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import org.junit.Test;

public class BroadcastVtomCallerTest {
    private LogString log = new LogString();


    @Test
    public void test_call() throws Exception {
        BroadcastVtomCaller vtomCaller = new BroadcastVtomCaller(new CommandFileMock(log));

        Map wizardState = WizardUtil.createBroadcastState("to_export.txt", "2006-01-30");

        System.setProperty("user.name", "fede");

        vtomCaller.call(wizardState);

        log.assertContent("setTimeout(3600000), execute([fede, to_export.txt, 2006-01-30])");
    }


    @Test
    public void test_callWithExtraArguments() throws Exception {
        BroadcastVtomCaller vtomCaller = new BroadcastVtomCaller(new CommandFileMock(log));

        Map state = WizardUtil.createBroadcastState("to_export.txt", "2006-01-30");
        state.put("myExtraArgument1", "myValue1");
        state.put("myExtraArgument2", "myValue2");
        state.put("anotherExtraArgument", "anotherValue");
        state.put("nullExtraArgument", null);
        state.put("nullStringExtraArgument", "null");

        System.setProperty("user.name", "fede");

        vtomCaller.call(state);

        log.assertContent(
              "setTimeout(3600000), execute([fede, to_export.txt, 2006-01-30, "
              + "-anotherExtraArgument, anotherValue, -myExtraArgument1, myValue1, -myExtraArgument2, myValue2])"
        );
    }


    @Test
    public void test_callError() throws Exception {
        CommandFileMock mock = new CommandFileMock(log);
        CommandFile.ExecuteException executeException =
              new CommandFile.ExecuteException("erreur", "", -1);
        mock.mockExecuteFailure(executeException);
        BroadcastVtomCaller vtomCaller = new BroadcastVtomCaller(mock);

        try {
            vtomCaller.call(WizardUtil.createBroadcastState("to_export.txt", "2006-01-30"));
            fail();
        }
        catch (CommandFile.ExecuteException ex) {
            assertSame(executeException, ex);
        }
    }
}
