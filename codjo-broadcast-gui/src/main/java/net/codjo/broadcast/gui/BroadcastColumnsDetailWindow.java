/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.swing.GenericRenderer;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import net.codjo.mad.gui.request.util.DetailWindowUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class BroadcastColumnsDetailWindow extends JInternalFrame implements InternationalizableContainer {

    private static final String DB_TABLE_NAME = "dbTableName";
    private static final String DB_FIELD_NAME = "dbFieldName";

    private GuiPreference guiPref;

    private final BorderLayout borderLayout1 = new BorderLayout();
    private final BorderLayout borderLayout2 = new BorderLayout();
    private final JLabel columnDateFormatLabel = new JLabel();
    private final GridBagLayout columnGridBagLayout = new GridBagLayout();
    private final JLabel columnLengthLabel = new JLabel();
    private final JLabel columnNumberLabel = new JLabel();
    private final JLabel columnNameLabel = new JLabel();

    private final JPanel columnPanel = new JPanel();
    private final JComboBox fieldNameCombo = new JComboBox();

    private final JComboBox tableNameCombo = new JComboBox();
    private final ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
    private final JLabel fieldNameLabel = new JLabel();
    private final JLabel expressionLabel = new JLabel();
    private final JPanel mainPanel = new JPanel();
    private final JLabel numberFormatLabel = new JLabel();
    private final JLabel paddingCharacterLabel = new JLabel();
    private final JScrollPane scrollPane = new JScrollPane();
    private final GridBagLayout tableGridBagLayout = new GridBagLayout();
    private final JLabel tableNameLabel = new JLabel();
    private final JPanel tablePanel = new JPanel();

    private final DetailDataSource dataSource;

    // Champs de la table
    private final TextField columnDateFormat = new TextField();
    private final NumberField columnLength = new NumberField();
    private final TextField columnName = new TextField();
    private final NumberField columnNumber = new NumberField();
    private final JCheckBox breakColumn = new JCheckBox();
    private final TextField columnNumberFormat = new TextField();
    private final JTextField columnsId = new JTextField();
    private final JTextArea expression = new JTextArea();
    private final TextField paddingCaracter = new TextField();
    private final JCheckBox rightColumnPadding = new JCheckBox();
    private final JTextField sectionId = new JTextField();
    private TranslationManager translationManager;
    private TranslationNotifier translationNotifier;


    public BroadcastColumnsDetailWindow(DetailDataSource dataSource,
                                        Row selectedSectionRow) throws RequestException {
        super("", true, true, true, true);

        GuiContext guiContext = dataSource.getGuiContext();
        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        translationManager = InternationalizationUtil.retrieveTranslationManager(guiContext);

        if (selectedSectionRow == null) {
            throw new IllegalStateException(
                  translationManager.translate("BroadcastError.noFatherRow",
                                               translationNotifier.getLanguage()));
        }

        this.guiPref = GuiPreferencesManager.getGuiPreferencesManager()
              .getPreferenceFor(selectedSectionRow.getFieldValue("family"));

        sectionId.setText(selectedSectionRow.getFieldValue("sectionId"));

        Map<String, String> joinKeyLabel = this.guiPref.getJoinKeyLabels();
        tableNameCombo.setModel(new DefaultComboBoxModel(sortedNameToArray(joinKeyLabel)));
        tableNameCombo.setRenderer(new GenericRenderer(joinKeyLabel));

        this.dataSource = dataSource;

        initGui();
        initDatasource();
        updateGui();

        installListeners();

        expression.setToolTipText("<html><b>Exemple d'expression</b>"
                                  + "<br>iif(Valeur_nulle, \"NA\", outil.format(Valeur * 2) )"
                                  + "<br><b>Astuce:</b>"
                                  + "<br>Utilisez le menu contextuel pour afficher les fonctions disponibles.");
    }


    public void addInternationalizableComponents(TranslationNotifier notifier) {
        notifier.addInternationalizableComponent(this, "BroadcastColumnsDetailWindow.title");
        registerTablePanel();
        registerColumnPanel();
    }


    private void registerTablePanel() {
        translationNotifier.addInternationalizableComponent(tablePanel,
                                                            "BroadcastColumnsDetailWindow.tablePanel.title");
        translationNotifier.addInternationalizableComponent(tableNameLabel,
                                                            "BroadcastColumnsDetailWindow.tableNameLabel");
        translationNotifier.addInternationalizableComponent(fieldNameLabel,
                                                            "BroadcastColumnsDetailWindow.fieldNameLabel");
    }


    private void registerColumnPanel() {
        translationNotifier.addInternationalizableComponent(columnPanel,
                                                            "BroadcastColumnsDetailWindow.columnPanel.title");
        translationNotifier.addInternationalizableComponent(rightColumnPadding,
                                                            "BroadcastColumnsDetailWindow.rightColumnPadding",
                                                            null);
        translationNotifier.addInternationalizableComponent(breakColumn,
                                                            "BroadcastColumnsDetailWindow.breakColumn",
                                                            null);
        translationNotifier.addInternationalizableComponent(columnNameLabel,
                                                            "BroadcastColumnsDetailWindow.columnNameLabel");
        translationNotifier.addInternationalizableComponent(columnNumberLabel,
                                                            "BroadcastColumnsDetailWindow.columnNumberLabel");
        translationNotifier.addInternationalizableComponent(columnLengthLabel,
                                                            "BroadcastColumnsDetailWindow.columnLengthLabel");
        translationNotifier.addInternationalizableComponent(paddingCharacterLabel,
                                                            "BroadcastColumnsDetailWindow.paddingCaracterLabel");
        translationNotifier.addInternationalizableComponent(columnDateFormatLabel,
                                                            "BroadcastColumnsDetailWindow.columnDateFormatLabel");
        translationNotifier.addInternationalizableComponent(numberFormatLabel,
                                                            "BroadcastColumnsDetailWindow.numberFormatLabel");
        translationNotifier.addInternationalizableComponent(expressionLabel,
                                                            "BroadcastColumnsDetailWindow.expressionLabel");
    }


    private void initDatasource() throws RequestException {
        declareDatasourceFields();

        dataSource.load();

        buttonPanelLogic.setMainDataSource(dataSource);

        DetailWindowUtil.manageEditModeFields(dataSource);
    }


    private void installListeners() {
        tableNameCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                onTableNameComboChange();
            }
        });

        fieldNameCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                onFieldNameComboChange();
            }
        });
    }


    private String[] sortedNameToArray(final Map<String, String> joinKeyLabel) {
        String[] joinKeyNames = joinKeyLabel.keySet().toArray(new String[]{});
        Arrays.sort(joinKeyNames, new Comparator<String>() {
            public int compare(String name1, String name2) {
                return joinKeyLabel.get(name1).compareTo(joinKeyLabel.get(name2));
            }
        });
        return joinKeyNames;
    }


    private void declareDatasourceFields() {
        this.dataSource.declare(DB_TABLE_NAME);
        this.dataSource.declare(DB_FIELD_NAME);

        dataSource.declare("columnDateFormat", columnDateFormat);
        dataSource.declare("columnLength", columnLength);
        dataSource.declare("columnName", columnName);
        dataSource.declare("columnNumber", columnNumber);
        dataSource.declare("breakField", breakColumn);
        dataSource.declare("columnNumberFormat", columnNumberFormat);
        dataSource.declare("columnsId", columnsId);
        dataSource.declare("expression", expression);
        dataSource.declare("paddingCaracter", paddingCaracter);
        dataSource.declare("rightColumnPadding", rightColumnPadding);
        dataSource.declare("sectionId", sectionId);
    }


    private void onFieldNameComboChange() {
        GuiField selectedField = getSelectedGuiField();
        if (selectedField != null) {
            this.dataSource.setFieldValue(DB_FIELD_NAME, selectedField.getFieldName());
        }
        else {
            dataSource.setFieldValue(DB_FIELD_NAME, null);
        }
    }


    private void onTableNameComboChange() {
        String selectedDBTableName = getSelectedTableName();
        if (selectedDBTableName != null) {
            this.dataSource.setFieldValue(DB_TABLE_NAME, selectedDBTableName);
            fieldNameCombo.setModel(new DefaultComboBoxModel(guiPref.getGuiFieldsFor(
                  selectedDBTableName)));

            GuiField selectedField = getSelectedGuiField();
            this.dataSource.setFieldValue(DB_FIELD_NAME, selectedField.getFieldName());
        }
        else {
            throw new IllegalStateException(
                  translationManager.translate("BroadcastColumnsDetailWindow.preferenceUnknown",
                                               translationNotifier.getLanguage()));
        }
    }


    private GuiField getSelectedGuiField() {
        GuiField selectedField = null;

        if (fieldNameCombo.getSelectedIndex() != -1) {
            selectedField = (GuiField)fieldNameCombo.getSelectedItem();
        }

        return selectedField;
    }


    private String getSelectedTableName() {
        String selectedDBTableName = null;
        if (tableNameCombo.getSelectedIndex() != -1) {
            selectedDBTableName = tableNameCombo.getSelectedItem().toString();
        }

        return selectedDBTableName;
    }


    private void initGui() {
        this.setResizable(true);
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        this.setMinimumSize(new Dimension(475, 369));
        this.setPreferredSize(new Dimension(475, 369));
        this.getContentPane().setLayout(borderLayout2);
        rightColumnPadding.setHorizontalTextPosition(SwingConstants.LEFT);
        breakColumn.setHorizontalTextPosition(SwingConstants.LEFT);
        tableNameCombo.setName(DB_TABLE_NAME);
        fieldNameCombo.setName(DB_FIELD_NAME);
        columnName.setMaxTextLength(30);
        tablePanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white, new Color(142, 142, 142)), ""));
        tablePanel.setLayout(tableGridBagLayout);
        columnPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white, new Color(142, 142, 142)), ""));
        columnPanel.setLayout(columnGridBagLayout);
        paddingCaracter.setSelectionEnd(1);
        paddingCaracter.setColumns(2);
        paddingCaracter.setMaxTextLength(1);
        columnLength.setColumns(3);
        columnNumber.setColumns(3);
        columnDateFormat.setColumns(10);
        columnNumberFormat.setColumns(10);
        columnNumberFormat.setMaxTextLength(30);
        columnDateFormat.setMaxTextLength(18);
        mainPanel.setLayout(borderLayout1);
        scrollPane.getViewport().add(expression);
        //Table Panel
        //Ligne 1
        tablePanel.add(tableNameLabel,
                       new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                              GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
        tablePanel.add(tableNameCombo,
                       new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                                              GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        //Ligne 2
        tablePanel.add(fieldNameLabel,
                       new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST,
                                              GridBagConstraints.NONE, new Insets(10, 5, 10, 0), 0, 0));
        tablePanel.add(fieldNameCombo,
                       new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                                              GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0));
        // Column Panel
        // ligne 1
        columnPanel.add(columnNameLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        columnPanel.add(columnName,
                        new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        // ligne 2
        columnPanel.add(columnNumberLabel,
                        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        columnPanel.add(columnNumber,
                        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        columnPanel.add(breakColumn,
                        new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        //Ligne 3
        columnPanel.add(columnLengthLabel,
                        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        columnPanel.add(columnLength,
                        new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0));
        //Ligne 4
        columnPanel.add(columnDateFormatLabel,
                        new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        columnPanel.add(columnDateFormat,
                        new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        columnPanel.add(numberFormatLabel,
                        new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(5, 10, 0, 5), 0, 0));
        columnPanel.add(columnNumberFormat,
                        new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0));

        //Ligne 5
        columnPanel.add(paddingCharacterLabel,
                        new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        columnPanel.add(paddingCaracter,
                        new GridBagConstraints(1, 4, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        columnPanel.add(rightColumnPadding,
                        new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
        //Ligne 6
        columnPanel.add(expressionLabel,
                        new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        columnPanel.add(scrollPane,
                        new GridBagConstraints(1, 5, 3, 2, 0.0, 1.0, GridBagConstraints.NORTH,
                                               GridBagConstraints.BOTH, new Insets(5, 5, 10, 5), 0, 0));
        mainPanel.add(tablePanel, BorderLayout.NORTH);
        mainPanel.add(columnPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanelLogic.getGui(), BorderLayout.SOUTH);
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);

        new FunctionsPopupHelper(this.expression, this.guiPref.getAllFunctions(), translationNotifier);

        translationNotifier.addInternationalizableContainer(this);
    }


    private void updateGui() {
        if (dataSource.getLoadFactory() != null) {
            update();
        }
        else {
            insert();
        }
    }


    private void update() {
        tableNameCombo.setSelectedItem(guiPref.determineTableName(dataSource.getFieldValue(DB_TABLE_NAME)));

        String selectedDBTableName = getSelectedTableName();
        this.dataSource.setFieldValue(DB_TABLE_NAME, selectedDBTableName);

        fieldNameCombo.setModel(new DefaultComboBoxModel(guiPref.getGuiFieldsFor(selectedDBTableName)));

        for (int i = 0; i < fieldNameCombo.getItemCount(); i++) {
            GuiField field = (GuiField)fieldNameCombo.getItemAt(i);

            if (field.getJoinKeyName().equals(dataSource.getFieldValue(DB_TABLE_NAME))
                && field.getFieldName().equals(dataSource.getFieldValue(DB_FIELD_NAME))) {
                fieldNameCombo.setSelectedIndex(i);
                return;
            }
        }

        fieldNameCombo.setSelectedIndex(-1);
    }


    private void insert() {
        tableNameCombo.setSelectedItem(0);
        String selectedDBTableName = getSelectedTableName();
        if (selectedDBTableName == null) {
            fieldNameCombo.setModel(new DefaultComboBoxModel());
            throw new IllegalStateException(
                  translationManager.translate("BroadcastColumnsDetailWindow.noTableMatchingSelectedFamily",
                                               translationNotifier.getLanguage()));
        }

        this.dataSource.setFieldValue(DB_TABLE_NAME, selectedDBTableName);
        fieldNameCombo.setModel(new DefaultComboBoxModel(guiPref.getGuiFieldsFor(selectedDBTableName)));

        GuiField selectedField = getSelectedGuiField();
        this.dataSource.setFieldValue(DB_FIELD_NAME, selectedField.getFieldName());

        fieldNameCombo.setSelectedIndex(0);
    }


    private static class FunctionsPopupHelper extends java.awt.event.MouseAdapter
          implements ActionListener {
        private JPopupMenu popupMenu = new JPopupMenu();
        private JTextArea expr = null;


        FunctionsPopupHelper(JTextArea expression,
                             java.util.List functions,
                             TranslationNotifier translationNotifier) {
            JMenu funcs = new JMenu("Fonctions");
            translationNotifier.addInternationalizableComponent(funcs,
                                                                "BroadcastColumnsDetailWindow.functions",
                                                                null);
            popupMenu.add(funcs);
            for (Object function : functions) {
                addItem(funcs, (String)function);
            }
            this.expr = expression;

            JMenu values = new JMenu("Valeurs");
            translationNotifier.addInternationalizableComponent(values,
                                                                "BroadcastColumnsDetailWindow.values",
                                                                null);
            popupMenu.add(values);
            addItem(values, "Valeur");
            addItem(values, "Valeur_nulle");

            expr.addMouseListener(this);
        }


        public void actionPerformed(ActionEvent event) {
            if (expr.getSelectedText() != null) {
                expr.replaceSelection(event.getActionCommand());
            }
            else {
                expr.insert(event.getActionCommand(), expr.getCaretPosition());
            }
        }


        @Override
        public void mousePressed(MouseEvent event) {
            maybeShowPopup(event);
        }


        @Override
        public void mouseReleased(MouseEvent event) {
            maybeShowPopup(event);
        }


        private void addItem(JMenu menu, String functionName) {
            JMenuItem menuItem = new JMenuItem(functionName);
            menuItem.addActionListener(this);
            menu.add(menuItem);
        }


        /**
         * Affiche le popupMenu si necessaire
         *
         * @param event L'événement souris
         */
        private void maybeShowPopup(MouseEvent event) {
            if (event.isPopupTrigger()) {
                popupMenu.show(event.getComponent(), event.getX(), event.getY());
            }
        }
    }
}
