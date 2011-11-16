package net.codjo.broadcast.gui.wizard;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.workflow.gui.wizard.FinalStep;
import net.codjo.workflow.gui.wizard.WizardConstants;
import static net.codjo.workflow.gui.wizard.WizardConstants.BROADCAST_DATE;
import static net.codjo.workflow.gui.wizard.WizardConstants.BROADCAST_FILE_NAME;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BroadcastWizardSummaryGui implements FinalStep.WizardSummaryGui, InternationalizableContainer {

    private JPanel guiPanel;
    private JTextField file;
    private JTextField date;
    private JTextField startTime;
    private JTextField extraArguments;
    private JLabel extraArgumentsLabel;
    private JLabel fileLabel;
    private JLabel dateLabel;
    private JLabel hourLabel;


    public BroadcastWizardSummaryGui(MutableGuiContext guiContext) {
        TranslationNotifier translationNotifier =
              InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        translationNotifier.addInternationalizableContainer(this);
        setFieldNames();
    }


    private void setFieldNames() {
        file.setName(("file"));
        date.setName(("date"));
        startTime.setName(("startTime"));
        extraArguments.setName(("extraArguments"));
    }


    public JComponent getGui() {
        return guiPanel;
    }


    public void addInternationalizableComponents(TranslationNotifier translationNotifier) {
        translationNotifier.addInternationalizableComponent(fileLabel, "BroadcastWizardSummaryGui.fileLabel");
        translationNotifier.addInternationalizableComponent(dateLabel, "BroadcastWizardSummaryGui.dateLabel");
        translationNotifier.addInternationalizableComponent(hourLabel, "BroadcastWizardSummaryGui.hourLabel");
        translationNotifier.addInternationalizableComponent(extraArgumentsLabel,
                                                            "BroadcastWizardSummaryGui.extraArgumentsLabel");
    }


    public void display(Map requestState) {
        final String broadcastFileName = (String)requestState.get(WizardConstants.BROADCAST_FILE_NAME);
        final Date broadcastDate = (Date)requestState.get(WizardConstants.BROADCAST_DATE);

        String formattedBroadcastDate = "";
        if (broadcastDate != null) {
            formattedBroadcastDate = new SimpleDateFormat("dd/MM/yyyy").format(broadcastDate);
        }

        file.setText(broadcastFileName);
        date.setText(formattedBroadcastDate);
        startTime.setText(new SimpleDateFormat("HH:mm").format(new Date()));

        addExtraArguments(requestState);
    }


    private void addExtraArguments(Map state) {
        setExtraArgumentsVisible(false);

        final StringBuilder extraArgumentsBuilder = new StringBuilder();
        for (Object key : state.keySet()) {
            Object value = state.get(key);
            if (!key.equals(BROADCAST_FILE_NAME) && !key.equals(BROADCAST_DATE) && isNotNull(value)) {
                setExtraArgumentsVisible(true);
                extraArgumentsBuilder.append(key).append("=");
                extraArgumentsBuilder.append(value.toString()).append(", ");
            }
        }

        if (extraArgumentsBuilder.length() > 0) {
            extraArguments.setText(extraArgumentsBuilder.substring(0, extraArgumentsBuilder.length() - 2));
        }
        else {
            extraArguments.setText("");
        }
    }


    private static boolean isNotNull(Object value) {
        return value != null && "null" != value;
    }


    private void setExtraArgumentsVisible(boolean visible) {
        extraArgumentsLabel.setVisible(visible);
        extraArguments.setVisible(visible);
    }
}
