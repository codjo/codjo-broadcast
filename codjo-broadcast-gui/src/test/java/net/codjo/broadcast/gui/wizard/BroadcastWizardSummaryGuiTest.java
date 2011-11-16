package net.codjo.broadcast.gui.wizard;
import net.codjo.broadcast.gui.BroadcastGuiContext;
import net.codjo.workflow.gui.wizard.WizardUtil;
import java.util.Map;
import org.uispec4j.Panel;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;

public class BroadcastWizardSummaryGuiTest extends UISpecTestCase {
    private BroadcastGuiContext broadcastGuiContext = new BroadcastGuiContext();


    public void test_displayStart() throws Exception {
        final Map displayStart = WizardUtil.createBroadcastState("to_export.txt", "2006-01-30");

        final BroadcastWizardSummaryGui summaryPanel =
              new BroadcastWizardSummaryGui(broadcastGuiContext);

        summaryPanel.display(displayStart);

        final Panel mainPanel = new Panel(summaryPanel.getGui());

        final TextBox file = mainPanel.getInputTextBox("file");
        assertTrue(file.textEquals("to_export.txt"));

        final TextBox date = mainPanel.getInputTextBox("date");
        assertTrue(date.textEquals("30/01/2006"));

        final TextBox startTime = mainPanel.getInputTextBox("startTime");

        final TextBox extraArguments = mainPanel.getInputTextBox("extraArguments");
        assertTrue(extraArguments.textEquals(""));

        assertVisibleEnabledButNotEditable(file, date, startTime);
        assertFalse(extraArguments.isVisible().isTrue());
    }


    public void test_displayStartWithExtraArguments() throws Exception {
        final Map state = WizardUtil.createBroadcastState("to_export.txt", "2006-01-30");
        state.put("myExtraArgument1", "myValue1");
        state.put("myExtraArgument2", "myValue2");
        state.put("anotherExtraArgument", "anotherValue");
        state.put("nullExtraArgument", null);
        state.put("nullStringExtraArgument", "null");

        final BroadcastWizardSummaryGui summaryPanel =
              new BroadcastWizardSummaryGui(broadcastGuiContext);

        summaryPanel.display(state);

        final Panel mainPanel = new Panel(summaryPanel.getGui());

        final TextBox file = mainPanel.getInputTextBox("file");
        assertTrue(file.textEquals("to_export.txt"));

        final TextBox date = mainPanel.getInputTextBox("date");
        assertTrue(date.textEquals("30/01/2006"));

        final TextBox startTime = mainPanel.getInputTextBox("startTime");

        final TextBox extraArguments = mainPanel.getInputTextBox("extraArguments");
        assertTrue(extraArguments.textEquals(
              "anotherExtraArgument=anotherValue, myExtraArgument1=myValue1, myExtraArgument2=myValue2"));

        assertVisibleEnabledButNotEditable(file, date, startTime, extraArguments);

        state.remove("myExtraArgument1");
        state.remove("myExtraArgument2");
        state.remove("anotherExtraArgument");
        state.remove("nullExtraArgument");
        state.remove("nullStringExtraArgument");

        summaryPanel.display(state);

        assertTrue(file.textEquals("to_export.txt"));
        assertTrue(date.textEquals("30/01/2006"));
        assertTrue(extraArguments.textEquals(""));

        assertTrue(mainPanel.getInputTextBox("file").isVisible().isTrue());
        assertTrue(mainPanel.getInputTextBox("date").isVisible().isTrue());
        assertFalse(mainPanel.getInputTextBox("extraArguments").isVisible().isTrue());

        assertVisibleEnabledButNotEditable(file, date, startTime);
        assertFalse(extraArguments.isVisible().isTrue());
    }


    private static void assertVisibleEnabledButNotEditable(TextBox... textBoxes) {
        for (TextBox textBox : textBoxes) {
            assertTrue(textBox.isVisible().isTrue());
            assertTrue(textBox.isEnabled().isTrue());
            assertFalse(textBox.isEditable().isTrue());
        }
    }
}
