/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui.wizard;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.toolkit.wizard.StepPanel;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
/**
 */
public class SelectTypeStep extends StepPanel {
    public static final String SELECTION_FILE_NAME = "broadcast.id";
    private RequestComboBox broadcastTypeCombo = new RequestComboBox();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private BroadcastSelector selector;


    public SelectTypeStep(BroadcastSelector selector) {
        this.selector = selector;
        setName("Selection du type d'export:");
        jbInit();
    }


    @Override
    public void start(Map previousStepState) {
        initCombo();
    }


    private void initCombo() {
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
                          setValue(SELECTION_FILE_NAME,
                                   broadcastTypeCombo.getSelectedValue("fileName"));
                      }
                      else {
                          setFulfilled(false);
                          setValue(SELECTION_FILE_NAME, null);
                      }
                  }
              });
    }


    private void jbInit() {
        this.setLayout(gridBagLayout1);
        this.add(broadcastTypeCombo,
                 new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                                        GridBagConstraints.CENTER,
                                        GridBagConstraints.HORIZONTAL,
                                        new Insets(10, 10, 10, 10), 0, 0));
        broadcastTypeCombo.setName("wizard.typeComboBox");
    }
}
