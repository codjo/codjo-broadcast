/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui.wizard;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Map;
import net.codjo.broadcast.gui.BroadcastGuiContext;
import net.codjo.gui.toolkit.wizard.Step;
import net.codjo.gui.toolkit.wizard.StepPanel;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.test.common.LogString;
import net.codjo.workflow.gui.wizard.WizardConstants;
import org.uispec4j.ComboBox;
import org.uispec4j.Panel;
import org.uispec4j.UISpecTestCase;
/**
 * Classe de test de {@link BroadcastSelectionStep}.
 */
public class BroadcastSelectionStepTest extends UISpecTestCase {
    private MadServerFixture madServerFixture = new MadServerFixture();
    private LogString log = new LogString();
    private BroadcastGuiContext guiContext;


    public void test_noSubStep() throws Exception {
        BroadcastSelectionStep step
              = new BroadcastSelectionStep(guiContext,
                                           new DefaultBroadcastSelector(madServerFixture.getOperations(),
                                                                        "selectAllBroadcastFiles"));
        Panel panel = new Panel(step);

        assertEquals("Sélection du type d'export :", InternationalizationUtil.translate(step.getName(), guiContext));

        mockStart(step);

        ComboBox typeCombo = panel.getComboBox();
        assertEquals("wizard.typeComboBox", typeCombo.getName());
        assertTrue(typeCombo.contentEquals(new String[]{"import1", "import2"}));

        assertFalse(step.isFulfilled());

        typeCombo.select("import1");
        assertTrue(step.isFulfilled());
        assertSelectionStepStates(step, "import1", new Date());
    }


    public void test_oneSubStep() throws Exception {
        StepPanelMock subStep = new StepPanelMock(new LogString("sub", log));
        subStep.setName("hahaa");
        BroadcastSelectionStep step = new BroadcastSelectionStep(guiContext,
                                                                 subStep,
                                                                 new DefaultBroadcastSelector(madServerFixture.getOperations(),
                                                                                              "selectAllBroadcastFiles"));
        Panel panel = new Panel(step);

        assertEquals("hahaa", panel.findSwingComponent(StepPanelMock.class).getName());

        mockStart(step);
        log.assertContent("sub.start(null)");
        log.clear();

        panel.getComboBox().select("import1");
        subStep.setFulfilled(false);
        assertFalse(step.isFulfilled());

        subStep.setValue(WizardConstants.BROADCAST_DATE, new Date(0));

        subStep.setFulfilled(true);
        assertTrue(step.isFulfilled());
        assertSelectionStepStates(step, "import1", new Date(0));
        log.clear();

        step.cancel();
        log.assertContent("sub.cancel()");
        log.clear();

        step.getState();
        log.assertContent("sub.getState()");
        log.clear();

        step.addPropertyChangeListener("props", null);
        log.assertContent("sub.addPropertyChangeListener(props, null)");
        log.clear();

        step.removePropertyChangeListener("props", null);
        log.assertContent("sub.removePropertyChangeListener(props, null)");
        log.clear();
    }


    public void test_event() throws Exception {
        BroadcastSelectionStep step
              = new BroadcastSelectionStep(guiContext,
                                           new DefaultBroadcastSelector(madServerFixture.getOperations(),
                                                                        "selectAllBroadcastFiles"));
        Panel panel = new Panel(step);

        mockStart(step);

        ComboBox typeCombo = panel.getComboBox();

        step.addPropertyChangeListener(Step.FULFILLED_PROPERTY,
                                       new PropertyChangeListener() {
                                           public void propertyChange(PropertyChangeEvent evt) {
                                               log.call("propertyChange", evt.getNewValue());
                                           }
                                       });

        typeCombo.select("import1");

        log.assertContent("propertyChange(true)");
    }


    @Override
    protected void setUp() throws Exception {
        madServerFixture.doSetUp();
        guiContext = new BroadcastGuiContext();
    }


    @Override
    protected void tearDown() throws Exception {
        madServerFixture.doTearDown();
    }


    private void mockStart(BroadcastSelectionStep step) {
        madServerFixture.mockServerResult(new String[]{"fileName"},
                                          new String[][]{
                                                {"import1"},
                                                {"import2"}
                                          });

        step.start(null);
    }


    private void assertSelectionStepStates(BroadcastSelectionStep step, String expectedFile,
                                           Date expectedDate) {
        final String fileName =
              (String)step.getState().get(WizardConstants.BROADCAST_FILE_NAME);
        assertEquals(expectedFile, fileName);
        final Date actualDate = (Date)step.getState().get(WizardConstants.BROADCAST_DATE);
        assertNotNull(actualDate);
        assertEquals(new java.sql.Date(expectedDate.getTime()).toString(),
                     new java.sql.Date(actualDate.getTime()).toString());
    }


    private static class StepPanelMock extends StepPanel {
        private LogString log = new LogString();


        StepPanelMock() {
        }


        StepPanelMock(LogString log) {
            this.log = log;
        }


        @Override
        public void start(Map previousStepState) {
            log.call("start", previousStepState);
        }


        @Override
        public void cancel() {
            log.call("cancel");
        }


        @Override
        public Map<String, Object> getState() {
            log.call("getState");
            return super.getState();
        }


        @Override
        public synchronized void addPropertyChangeListener(String propertyName,
                                                           PropertyChangeListener listener) {
            log.call("addPropertyChangeListener", propertyName, listener);
        }


        @Override
        public synchronized void removePropertyChangeListener(String propertyName,
                                                              PropertyChangeListener listener) {
            log.call("removePropertyChangeListener", propertyName, listener);
        }
    }
}
