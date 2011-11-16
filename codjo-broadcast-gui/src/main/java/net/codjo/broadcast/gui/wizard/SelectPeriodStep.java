/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui.wizard;
import net.codjo.gui.toolkit.wizard.StepPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 * Step de sélection d'une période.
 */
public class SelectPeriodStep extends StepPanel {
    public static final String BROADCAST_DATE = "broadcast.date";
    private JTextField periodField = new JTextField();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    public SelectPeriodStep() {
        setName("Selection de la période d'export:");
        jbInit();
        initPeriodField();
        setValue(BROADCAST_DATE, new Date());
    }

    private void initPeriodField() {
        periodField.setText(simpleDateFormat.format(new Date()).substring(0, 6));
        setFulfilled(true);

        periodField.getDocument().addDocumentListener(new PeriodDocumentListener());
    }


    private void controlPeriod() {
        String period = periodField.getText();
        if (period.length() != 6) {
            setFulfilled(false);
            return;
        }
        try {
            Long.parseLong(period);
        }
        catch (NumberFormatException e) {
            setFulfilled(false);
            return;
        }
        long month = Long.parseLong(period.substring(4));
        if (month < 1 || month > 12) {
            setFulfilled(false);
            return;
        }
        try {
            setValue(BROADCAST_DATE, simpleDateFormat.parse(periodField.getText() + "01"));
            setFulfilled(true);
        }
        catch (ParseException exception) {
            exception.printStackTrace();
            setFulfilled(false);
        }
    }


    private void jbInit() {
        this.setLayout(gridBagLayout1);
        periodField.setName("wizard.periodField");
        this.add(periodField,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(10, 10, 10, 0), 70, 0));
    }

    private class PeriodDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            controlPeriod();
        }


        public void removeUpdate(DocumentEvent e) {
            controlPeriod();
        }


        public void changedUpdate(DocumentEvent e) {
            controlPeriod();
        }
    }
}
