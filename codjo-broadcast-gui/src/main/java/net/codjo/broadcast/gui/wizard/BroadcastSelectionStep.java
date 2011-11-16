/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui.wizard;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.toolkit.wizard.Step;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.workflow.gui.wizard.AbstractSelectionStep;
import net.codjo.workflow.gui.wizard.WizardConstants;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
/**
 *
 */
public class BroadcastSelectionStep extends AbstractSelectionStep {
    private RequestComboBox broadcastTypeCombo;
    private BroadcastSelector selector;


    public BroadcastSelectionStep(BroadcastSelector selector) {
        this(null, selector);
    }


    public BroadcastSelectionStep(Step subStep, BroadcastSelector selector) {
        super(subStep, "Selection du type d'export:");
        this.selector = selector;
        setValue(WizardConstants.BROADCAST_DATE, new Date());
    }


    @Override
    public void initGui() {
        broadcastTypeCombo.setModelFieldName("fileName");
        try {
            Result result = selector.selectBroadcastItems(new String[]{"fileId", "fileName"});
            broadcastTypeCombo.getDataSource().setLoadResult(result);
        }
        catch (RequestException ex) {
            ErrorDialog.show(this, "Impossible de charger la liste des fichier de diffusion.", ex);
        }

        broadcastTypeCombo.getDataSource().addPropertyChangeListener(
              DataSource.SELECTED_ROW_PROPERTY,
              new PropertyChangeListener() {
                  public void propertyChange(PropertyChangeEvent evt) {
                      if (broadcastTypeCombo.getSelectedIndex() != -1) {
                          setFulfilled(true);
                          setValue(WizardConstants.BROADCAST_FILE_NAME,
                                   broadcastTypeCombo.getSelectedValue("fileName"));
                      }
                      else {
                          setFulfilled(false);
                          setValue(WizardConstants.BROADCAST_FILE_NAME, null);
                      }
                  }
              });
    }


    @Override
    public void buildLayout() {
        broadcastTypeCombo = new RequestComboBox();
        setLayout(new GridBagLayout());
        add(broadcastTypeCombo,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                   GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        broadcastTypeCombo.setName("wizard.typeComboBox");
    }
}
