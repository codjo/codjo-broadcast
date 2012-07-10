/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.FieldType;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import net.codjo.mad.gui.request.util.DetailWindowUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class BroadcastFileContentsDetailWindow extends JInternalFrame implements InternationalizableContainer {
    private JCheckBox columnHeader = new JCheckBox();
    private TextField columnSeparator = new TextField();
    private JTextField contentId = new JTextField();
    private JTextField fileId = new JTextField();
    private JCheckBox sectionHeader = new JCheckBox();
    private JTextArea sectionHeaderText = new JTextArea();
    private RequestComboBox sectionId = new RequestComboBox();
    private NumberField sectionPosition = new NumberField();

    private BorderLayout borderLayout1 = new BorderLayout();
    private JPanel columnPanel = new JPanel();
    private ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private GridBagLayout gridBagLayout2 = new GridBagLayout();
    private GridBagLayout gridBagLayout3 = new GridBagLayout();
    private JScrollPane headerScrollPane = new JScrollPane();
    private JPanel headerPanel = new JPanel();
    private JPanel mainPanel = new JPanel();
    private JLabel positionLabel = new JLabel();
    private JLabel sectionNameLabel = new JLabel();
    private JPanel sectionPanel = new JPanel();
    private JTabbedPane sectionTabPanel = new JTabbedPane();
    private JLabel separatorLabel = new JLabel();
    private GuiPreferencesManager guiPrefManager = GuiPreferencesManager.getGuiPreferencesManager();
    private GuiPreference pref = null;
    private DetailDataSource dataSource;
    private TranslationNotifier translationNotifier;
    private TranslationManager translationManager;


    public BroadcastFileContentsDetailWindow(DetailDataSource dataSource,
                                             Row selectedFileRow) throws Exception {
        super("", true, true, true, true);
        this.dataSource = dataSource;

        GuiContext guiContext = dataSource.getGuiContext();
        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        translationManager = InternationalizationUtil.retrieveTranslationManager(guiContext);

        if (selectedFileRow == null) {
            throw new IllegalStateException(
                  translationManager.translate("BroadcastError.noFatherRow",
                                               translationNotifier.getLanguage()));
        }
        fileId.setText(selectedFileRow.getFieldValue("fileId"));

        fillSectionComboBox();
        declareFields(dataSource);

        sectionId.putClientProperty(DetailDataSource.UPDATE_PRIORITY,
                                    DetailDataSource.LOW_PRIORITY);
        buttonPanelLogic.setMainDataSource(dataSource);
        dataSource.load();

        initGui();
        sectionId.putClientProperty(FieldType.EDIT_MODE, FieldType.NOT_UPDATABLE);
        DetailWindowUtil.manageEditModeFields(dataSource);

        sectionId.getDataSource().addPropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY,
                                                            new PropertyChangeListener() {
                                                                public void propertyChange(PropertyChangeEvent evt) {
                                                                    try {
                                                                        manageOptionPanel();
                                                                    }
                                                                    catch (RequestException ex) {
                                                                        ErrorDialog.show(
                                                                              BroadcastFileContentsDetailWindow.this,
                                                                              translationManager.translate(
                                                                                    "BroadcastFileContentsDetailWindow.unableToDisplayParameters",
                                                                                    translationNotifier.getLanguage()),
                                                                              ex);
                                                                    }
                                                                }
                                                            });
        manageOptionPanel();

        translationNotifier.addInternationalizableContainer(this);
    }


    private void declareFields(DetailDataSource detailDataSource) {
        detailDataSource.declare("columnHeader", columnHeader);
        detailDataSource.declare("columnSeparator", columnSeparator);
        detailDataSource.declare("contentId", contentId);
        detailDataSource.declare("fileId", fileId);
        detailDataSource.declare("sectionId", sectionId);
        detailDataSource.declare("sectionHeader", sectionHeader);
        detailDataSource.declare("sectionHeaderText", sectionHeaderText);
        detailDataSource.declare("sectionPosition", sectionPosition);
    }


    void dataSourceSaveEvent() {
        if (pref != null && sectionTabPanel.getComponentCount() > 1) {
            try {
                pref.saveContentOptionPanel(dataSource,
                                            (JPanel)sectionTabPanel.getComponentAt(1));
            }
            catch (RequestException ex) {
                ErrorDialog.show(this,
                                 translationManager.translate(
                                       "BroadcastFileContentsDetailWindow.unableToSaveOptions",
                                       translationNotifier.getLanguage()),
                                 ex);
            }
        }
    }


    private void manageOptionPanel() throws RequestException {
        if (sectionId.getSelectedIndex() == -1) {
            removeOptionalTab();
            pref = null;
        }
        else {
            String family = sectionId.getSelectedValue("family");

            if (pref == null || !family.equals(pref.getFamily())) {
                removeOptionalTab();
                pref = guiPrefManager.getPreferenceFor(family);
                JPanel optionPanel = pref.buildContentOptionPanel(dataSource);
                if (optionPanel != null) {
                    this.sectionTabPanel.addTab("Options", optionPanel);
                }
            }
        }
    }


    private void removeOptionalTab() {
        if (sectionTabPanel.getTabCount() > 1) {
            sectionTabPanel.removeTabAt(1);
        }
    }


    private void fillSectionComboBox() throws RequestException {
        sectionId.setColumns(new String[]{"family", "sectionId", "sectionName"});
        sectionId.setRendererFieldName("sectionName");
        sectionId.setModelFieldName("sectionId");
        sectionId.setSelectFactoryId("selectAllBroadcastSections");
        sectionId.load();
    }


    private void initGui() throws Exception {
        this.setResizable(true);
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        this.setPreferredSize(new Dimension(430, 430));
        this.getContentPane().setLayout(borderLayout1);
        mainPanel.setLayout(gridBagLayout3);
        columnPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white,
              new Color(142, 142, 142)), ""));
        columnPanel.setLayout(gridBagLayout2);
        columnSeparator.setMaxTextLength(2);
        sectionPanel.setLayout(gridBagLayout1);
        sectionPosition.setColumns(0);
        headerPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white,
              new Color(134, 134, 134)), ""));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(headerScrollPane, BorderLayout.CENTER);
        dataSource.addDataSourceListener(new net.codjo.mad.gui.request.event.DataSourceAdapter() {
            @Override
            public void saveEvent(DataSourceEvent event) {
                dataSourceSaveEvent();
            }
        });
        sectionTabPanel.setName("tabPanel");
        this.getContentPane().add(sectionTabPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanelLogic.getGui(), BorderLayout.SOUTH);
        sectionTabPanel.add(mainPanel, "Section");
        columnPanel.add(separatorLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                               GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
        columnPanel.add(columnSeparator,
                        new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                               GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 51, 0));
        columnPanel.add(columnHeader,
                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                               GridBagConstraints.NONE, new Insets(0, 18, 5, 165), 0, 0));
        sectionPanel.add(sectionNameLabel,
                         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.NONE, new Insets(10, 5, 0, 12), 8, 0));
        sectionPanel.add(sectionId,
                         new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(10, 5, 0, 10),
                                                181,
                                                0));
        sectionPanel.add(positionLabel,
                         new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
        sectionPanel.add(sectionPosition,
                         new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(10, 5, 0, 0),
                                                30,
                                                0));
        sectionPanel.add(sectionHeader,
                         new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                                GridBagConstraints.NONE, new Insets(10, 92, 0, 10), 0, 0));
        sectionPanel.add(headerPanel,
                         new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                GridBagConstraints.BOTH, new Insets(10, 5, 5, 5), 0, 0));
        mainPanel.add(columnPanel,
                      new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                             GridBagConstraints.BOTH, new Insets(0, 0, 0, 9), -5, 0));
        headerScrollPane.getViewport().add(sectionHeaderText, null);
        mainPanel.add(sectionPanel,
                      new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                             GridBagConstraints.BOTH, new Insets(0, 0, 0, 9), 4, 54));
    }


    public void addInternationalizableComponents(TranslationNotifier notifier) {
        notifier.addInternationalizableComponent(this, "BroadcastFileContentsDetailWindow.title");
        notifier.addInternationalizableComponent(columnPanel,
                                                 "BroadcastFileContentsDetailWindow.columnPanel.title");
        notifier.addInternationalizableComponent(separatorLabel,
                                                 "BroadcastFileContentsDetailWindow.separatorLabel");
        notifier.addInternationalizableComponent(columnHeader,
                                                 "BroadcastFileContentsDetailWindow.columnHeader",
                                                 null);
        notifier.addInternationalizableComponent(positionLabel,
                                                 "BroadcastFileContentsDetailWindow.positionLabel");
        notifier.addInternationalizableComponent(sectionHeader,
                                                 "BroadcastFileContentsDetailWindow.sectionHeader",
                                                 null);
        notifier.addInternationalizableComponent(sectionNameLabel,
                                                 "BroadcastFileContentsDetailWindow.sectionNameLabel");
        notifier.addInternationalizableComponent(headerPanel,
                                                 "BroadcastFileContentsDetailWindow.headerPanel.title");
        notifier.addInternationalizableComponent(sectionTabPanel,
                                                 "BroadcastFileContentsDetailWindow.sectionTabPanel",
                                                 new String[]{
                                                       "BroadcastFileContentsDetailWindow.sectionTabPanel.section",
                                                       "BroadcastFileContentsDetailWindow.sectionTabPanel.options"});
    }
}
