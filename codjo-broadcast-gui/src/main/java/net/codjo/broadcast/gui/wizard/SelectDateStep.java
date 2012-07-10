/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui.wizard;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.gui.toolkit.wizard.StepPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/**
 */
public class SelectDateStep extends StepPanel {
    public static final String SELECTION_DATE = "broadcast.date";
    private DateField broadcastDateField = new DateField();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();


    public SelectDateStep() {
        setName("SelectDateStep.title");
        jbInit();
        initDateField();
    }


    private void initDateField() {
        broadcastDateField.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (broadcastDateField.getDate() != null) {
                    setFulfilled(true);
                    setValue(SELECTION_DATE, broadcastDateField.getDate());
                }
                else {
                    setFulfilled(false);
                    setValue(SELECTION_DATE, null);
                }
            }
        });

        broadcastDateField.setDate(new java.util.Date());
    }


    private void jbInit() {
        this.setLayout(gridBagLayout1);
        this.add(broadcastDateField,
                 new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                        GridBagConstraints.NONE, new Insets(10, 10, 10, 0), 70, 0));
    }
}
