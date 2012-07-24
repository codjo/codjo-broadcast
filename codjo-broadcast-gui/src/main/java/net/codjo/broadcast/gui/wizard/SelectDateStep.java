/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui.wizard;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.gui.toolkit.date.InternationalizableDateField;
import net.codjo.gui.toolkit.wizard.StepPanel;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
/**
 */
public class SelectDateStep extends StepPanel implements InternationalizableContainer {
    public static final String SELECTION_DATE = "broadcast.date";
    private DateField broadcastDateField = new DateField();
    private GridBagLayout gridBagLayout = new GridBagLayout();
    private TranslationManager translationManager;


    public SelectDateStep(GuiContext guiContext) {
        setName("SelectDateStep.title");
        jbInit();
        initDateField();

        TranslationNotifier notifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        translationManager = InternationalizationUtil.retrieveTranslationManager(guiContext);
        notifier.addInternationalizableContainer(this);
    }


    public void addInternationalizableComponents(TranslationNotifier translationNotifier) {
        translationNotifier.addInternationalizableComponent(new InternationalizableDateField(broadcastDateField,
                                                                                             translationNotifier,
                                                                                             translationManager));
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
        this.setLayout(gridBagLayout);
        this.add(broadcastDateField,
                 new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                        GridBagConstraints.NONE, new Insets(10, 10, 10, 0), 70, 0));
    }
}
