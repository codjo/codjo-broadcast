/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import net.codjo.gui.toolkit.swing.GenericRenderer;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizableRequestTable;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
/**
 * Liste des sections d'un fichier de diffusion.
 *
 * @author $Author: galaber $
 * @version $Revision: 1.4 $
 */
public class BroadcastSectionsWindow extends JInternalFrame implements InternationalizableContainer {
    private RequestTable columnsTable = new RequestTable();
    private RequestToolBar columnsToolBar = new RequestToolBar();
    private GuiPreferencesManager guiPrefManager =
          GuiPreferencesManager.getGuiPreferencesManager();
    private RequestTable sectionTable = new RequestTable();
    private RequestToolBar sectionToolBar = new RequestToolBar();
    private TranslationNotifier translationNotifier;
    private TranslationManager translationManager;
    private JPanel sectionPanel;
    private JPanel columnsPanel;


    public BroadcastSectionsWindow(GuiContext ctxt)
          throws Exception {
        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(ctxt);
        translationManager = InternationalizationUtil.retrieveTranslationManager(ctxt);

        jbInit();
        columnsTable.setPreference(PreferenceFactory.getPreference("BroadcastColumnsWindow"));
        sectionTable.setPreference(PreferenceFactory.getPreference("BroadcastSectionsWindow"));

        sectionTable.load();
        sectionToolBar.setHasExcelButton(true);
        sectionToolBar.init(ctxt, sectionTable);
        columnsToolBar.setHasExcelButton(true);
        columnsToolBar.init(ctxt, columnsTable);
        columnsToolBar.setFather(sectionTable, "sectionId", "selectBroadcastColumnsBySectionId", ctxt);

        sectionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }
                setRenderers();
            }
        });

        translationNotifier.addInternationalizableContainer(this);
    }


    private void setRenderers() {
        if (sectionTable.getSelectedRow() == -1) {
            return;
        }
        GuiPreference pref = guiPrefManager.getPreferenceFor(sectionTable.getSelectedFieldValue("family"));

        columnsTable.setCellRenderer("dbTableName", new GenericRenderer(pref.getJoinKeyLabels()));
        columnsTable.setCellRenderer("dbFieldName", new FieldNameRenderer(pref));
    }


    private void jbInit() {
        sectionPanel = new JPanel();
        TitledBorder sectionsTitledBorder = new TitledBorder(
              BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134)), "");
        sectionPanel.setBorder(sectionsTitledBorder);
        sectionPanel.setLayout(new BorderLayout());

        JScrollPane sectionScrollPane = new JScrollPane();
        sectionScrollPane.getViewport().add(sectionTable);
        sectionPanel.add(sectionScrollPane, BorderLayout.CENTER);
        sectionPanel.add(sectionToolBar, BorderLayout.SOUTH);

        columnsPanel = new JPanel();
        TitledBorder columnsTitledBorder = new TitledBorder(
              BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134)), "");
        columnsPanel.setBorder(columnsTitledBorder);
        columnsPanel.setLayout(new BorderLayout());

        JScrollPane columnsScrollPane = new JScrollPane();
        columnsScrollPane.getViewport().add(columnsTable);
        columnsPanel.add(columnsScrollPane, BorderLayout.CENTER);
        columnsPanel.add(columnsToolBar, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, sectionPanel, columnsPanel);
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerLocation(250);

        setTitle("Sections / Colonnes");
        getContentPane().setLayout(new BorderLayout());
        setMinimumSize(new Dimension(500, 200));
        setClosable(true);
        setResizable(true);
        getContentPane().setBackground(UIManager.getColor("Panel.background"));
        getContentPane().add(splitPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(750, 550));
    }


    public void addInternationalizableComponents(TranslationNotifier notifier) {
        notifier.addInternationalizableComponent(this, "BroadcastSectionsWindow.title");
        notifier.addInternationalizableComponent(sectionPanel, "BroadcastSectionsWindow.sectionPanel.title");
        notifier.addInternationalizableComponent(columnsPanel, "BroadcastSectionsWindow.columnsPanel.title");
        notifier.addInternationalizableComponent(
              new InternationalizableRequestTable(PreferenceFactory.getPreference("BroadcastColumnsWindow"),
                                                  columnsTable));
        notifier.addInternationalizableComponent(
              new InternationalizableRequestTable(
                    PreferenceFactory.getPreference("BroadcastSectionsWindow"),
                    sectionTable));
    }


    private static class FieldNameRenderer extends DefaultTableCellRenderer {
        GuiPreference guiPref = null;


        FieldNameRenderer(GuiPreference guiPref) {
            this.guiPref = guiPref;
        }


        @Override
        public Component getTableCellRendererComponent(JTable ta,
                                                       Object fieldName,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            RequestTable table = (RequestTable)ta;
            String jkName = table.getColumnValue(row, "dbTableName");
            String label = translateValue(jkName, fieldName);
            super.getTableCellRendererComponent(ta, label, isSelected, hasFocus, row, column);
            manageNoTranslation(fieldName, label);
            return this;
        }


        private void manageNoTranslation(Object value, String translated) {
            if (translated == value) {
                setForeground(Color.red);
            }
            else {
                setForeground(UIManager.getColor("TextField.foreground"));
            }
        }


        private String translateValue(String jkName, Object fieldName) {
            if ("null".equals(fieldName)) {
                return "";
            }

            GuiField[] guiFields = guiPref.getGuiFieldsFor(jkName);

            for (GuiField guiField : guiFields) {
                if (fieldName.equals(guiField.getFieldName())) {
                    return guiField.getLabel();
                }
            }

            return fieldName.toString();
        }
    }
}
