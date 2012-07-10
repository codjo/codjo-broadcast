/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.FieldType;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import net.codjo.mad.gui.request.util.DetailWindowUtil;

import static net.codjo.mad.gui.i18n.InternationalizationUtil.translate;
/**
 */
public class BroadcastSectionsDetailWindow extends JInternalFrame implements InternationalizableContainer {
    private TextField decimalSeparator = new TextField();
    private JComboBox family = new JComboBox();
    private JCheckBox fixedLength = new JCheckBox();
    private NumberField recordLength = new NumberField();
    private JTextField sectionId = new JTextField();
    private TextField sectionName = new TextField();
    private JComboBox selectionId = new JComboBox();

    private JPanel columnPanel = new JPanel();
    private JLabel decimalSeparatorLabel = new JLabel();
    private ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
    private JLabel familyLabel = new JLabel();
    private GuiPreferencesManager guiPrefManager = GuiPreferencesManager.getGuiPreferencesManager();
    private JLabel lengthLabel = new JLabel();
    private JLabel sectionNameLabel = new JLabel();
    private JPanel sectionPanel = new JPanel();
    private JLabel selectionNameLabel = new JLabel();
    private DetailDataSource dataSource;
    private TranslationNotifier translationNotifier;
    private JTabbedPane sectionTabPanel;
    private GuiContext guiContext;


    public BroadcastSectionsDetailWindow(DetailDataSource dataSource) throws RequestException {
        super("", true, true, true, true);
        this.dataSource = dataSource;
        guiContext = dataSource.getGuiContext();
        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        declareFields(dataSource);
        initFamily();
        recordLength.setEnabled(false);
        fixedLength.addItemListener(new ActionListenerForFixedLength());
        family.putClientProperty(DetailDataSource.UPDATE_PRIORITY,
                                 DetailDataSource.HIGH_PRIORITY);
        buttonPanelLogic.setMainDataSource(dataSource);
        dataSource.load();
        initGui();
        family.putClientProperty(FieldType.EDIT_MODE, FieldType.NOT_UPDATABLE);
        DetailWindowUtil.manageEditModeFields(dataSource);

        translationNotifier.addInternationalizableContainer(this);
    }


    public void addInternationalizableComponents(TranslationNotifier notifier) {
        notifier.addInternationalizableComponent(this, "BroadcastSectionsDetailWindow.title");
        notifier.addInternationalizableComponent(columnPanel, "BroadcastSectionsDetailWindow.columnPanel.title");
        notifier.addInternationalizableComponent(fixedLength,
                                                 "BroadcastSectionsDetailWindow.fixedLength",
                                                 null);
        notifier.addInternationalizableComponent(lengthLabel, "BroadcastSectionsDetailWindow.lengthLabel");
        notifier.addInternationalizableComponent(sectionNameLabel, "BroadcastSectionsDetailWindow.sectionNameLabel");
        notifier.addInternationalizableComponent(familyLabel, "BroadcastSectionsDetailWindow.familyLabel");
        notifier.addInternationalizableComponent(selectionNameLabel,
                                                 "BroadcastSectionsDetailWindow.selectionNameLabel");
        notifier.addInternationalizableComponent(decimalSeparatorLabel,
                                                 "BroadcastSectionsDetailWindow.decimalSeparatorLabel");
        notifier.addInternationalizableComponent(sectionTabPanel,
                                                 "BroadcastSectionsDetailWindow.sectionTabPanel",
                                                 new String[]{
                                                       "BroadcastSectionsDetailWindow.sectionTabPanel.section"});
    }


    private void declareFields(DetailDataSource detailDataSource) {
        detailDataSource.declare("decimalSeparator", decimalSeparator);
        detailDataSource.declare("family", family);
        detailDataSource.declare("fixedLength", fixedLength);
        detailDataSource.declare("recordLength", recordLength);
        detailDataSource.declare("sectionId", sectionId);
        detailDataSource.declare("sectionName", sectionName);
        detailDataSource.declare("selectionId", selectionId);
    }


    private void fillSelectionComboBox() {
        try {
            String selectedSelection = (String)selectionId.getSelectedItem();
            GuiPreference pref =
                  guiPrefManager.getPreferenceFor(dataSource.getFieldValue("family"));
            JComboBox newCombo = pref.buildSelectionComboBox();
            selectionId.setModel(newCombo.getModel());
            selectionId.setRenderer(newCombo.getRenderer());
            selectionId.setSelectedItem(selectedSelection);
        }
        catch (RequestException ex) {
            ErrorDialog.show(this, translate("BroadcastSectionsDetailWindow.loadErrorMessage", guiContext), ex);
        }
    }


    private void initFamily() {
        family.setModel(new DefaultComboBoxModel(getFamiliesSortedByLabel()));
        family.setRenderer(new FamilyListCellRenderer());
        dataSource.addPropertyChangeListener("family", new FamilyChangeListener());
        family.setSelectedItem(null);
    }


    private String[] getFamiliesSortedByLabel() {
        String[] familyIds = guiPrefManager.getFamilies();
        SortedMap<String, String> familyMap = new TreeMap<String, String>();
        for (String id : familyIds) {
            GuiPreference guiPreference = guiPrefManager.getPreferenceFor(id);
            String label = guiPreference.getFamilyLabel();
            familyMap.put(label, id);
        }

        String[] sortedFamilyIds = new String[familyIds.length];
        int index = 0;
        for (Map.Entry<String, String> entry : familyMap.entrySet()) {
            sortedFamilyIds[index++] = entry.getValue();
        }
        return sortedFamilyIds;
    }


    private void initGui() {
        this.setResizable(true);
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        this.setPreferredSize(new Dimension(400, 320));
        this.getContentPane().setLayout(new GridBagLayout());

        columnPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white,
              new Color(142, 142, 142)), ""));
        columnPanel.setLayout(new GridBagLayout());

        recordLength.setBackground(UIManager.getColor("Panel.background"));
        recordLength.setColumns(0);
        fixedLength.setHorizontalTextPosition(SwingConstants.LEFT);
        sectionName.setMaxTextLength(30);
        decimalSeparator.setColumns(2);
        decimalSeparator.setMaxTextLength(1);

        sectionPanel.setLayout(new GridBagLayout());
        sectionPanel.add(sectionNameLabel,
                         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 8, 0));
        sectionPanel.add(sectionName,
                         new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 310,
                                                0));
        sectionPanel.add(selectionId,
                         new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 5), 0,
                                                0));
        sectionPanel.add(selectionNameLabel,
                         new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
        sectionPanel.add(fixedLength,
                         new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE, new Insets(6, 5, 5, 0), 0, 0));
        sectionPanel.add(lengthLabel,
                         new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
        sectionPanel.add(recordLength,
                         new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 30, 0));
        sectionPanel.add(family,
                         new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 5), 0,
                                                0));
        sectionPanel.add(familyLabel,
                         new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

        columnPanel.add(decimalSeparatorLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        columnPanel.add(decimalSeparator,
                        new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                               GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(sectionPanel,
                      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL, new Insets(15, 0, 0, 0), 0, 0));
        mainPanel.add(columnPanel,
                      new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
                                             GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        sectionTabPanel = new JTabbedPane();
        sectionTabPanel.add(mainPanel, "Section");

        this.getContentPane().add(sectionTabPanel,
                                  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
                                                         GridBagConstraints.BOTH, new Insets(10, 5, 0, 5), 0,
                                                         0));
        this.getContentPane().add(buttonPanelLogic.getGui(),
                                  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(0, 0, 0, 0), 0, 0));
    }


    private class ActionListenerForFixedLength implements java.awt.event.ItemListener {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            if (fixedLength.isSelected()) {
                recordLength.setEnabled(true);
                recordLength.setBackground(UIManager.getColor("TextField.background"));
            }
            else {
                recordLength.setText(null);
                recordLength.setEnabled(false);
                recordLength.setBackground(UIManager.getColor("Panel.background"));
            }
        }
    }

    private class FamilyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent pe) {
            fillSelectionComboBox();
        }
    }

    private class FamilyListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int idx,
                                                      boolean selected,
                                                      boolean focus) {

            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, idx, selected, focus);

            if (value != null) {
                GuiPreference guiPreference = guiPrefManager.getPreferenceFor((String)value);
                String rendererLabel = guiPreference.getFamilyLabel();
                label.setText(rendererLabel);
            }
            return label;
        }
    }
}
