/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import net.codjo.mad.gui.request.util.DetailWindowUtil;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class BroadcastFilesDetailWindow extends JInternalFrame implements InternationalizableContainer {
    private JCheckBox autoDistribution = new JCheckBox();
    private JComboBox cftBatchFile = new JComboBox();
    private TextField destinationSystem = new TextField();
    private JComboBox distributionMethod = new JComboBox();
    private JComboBox fileDestinationLocation = new JComboBox();
    private JCheckBox fileHeader = new JCheckBox();
    private JTextArea fileHeaderText = new JTextArea();
    private TextField fileName = new TextField();
    private JCheckBox historiseFile = new JCheckBox();
    private JCheckBox sectionSeparator = new JCheckBox();
    private JLabel batchFileLabel = new JLabel();
    private BorderLayout borderLayout1 = new BorderLayout();
    private ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
    private JLabel destinationSystemLabel = new JLabel();
    private JLabel distributionMethodLabel = new JLabel();
    private JLabel fileNameLabel = new JLabel();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private GuiPreferencesManager guiPrefManager = GuiPreferencesManager.getGuiPreferencesManager();
    private JPanel filePanel = new JPanel();
    private JLabel locationLabel = new JLabel();
    private JPanel headerPanel = new JPanel();
    private JScrollPane scrollPane = new JScrollPane();


    public BroadcastFilesDetailWindow(DetailDataSource dataSource)
          throws RequestException {
        super("Fichier", true, true, true, true);
        declareFields(dataSource);
        fillDiffuserCode();
        fillBatchFile();
        fillDestinationLocation();
        buttonPanelLogic.setMainDataSource(dataSource);
        dataSource.load();
        initGui(dataSource.getGuiContext());
        DetailWindowUtil.manageEditModeFields(dataSource);
    }


    private void declareFields(DetailDataSource dataSource) {
        dataSource.declare("autoDistribution", autoDistribution);
        dataSource.declare("cftBatchFile", cftBatchFile);
        dataSource.declare("destinationSystem", destinationSystem);
        dataSource.declare("distributionMethod", distributionMethod);
        dataSource.declare("fileDestinationLocation", fileDestinationLocation);
        dataSource.declare("fileHeader", fileHeader);
        dataSource.declare("fileHeaderText", fileHeaderText);
        dataSource.declare("fileName", fileName);
        dataSource.declare("historiseFile", historiseFile);
        dataSource.declare("sectionSeparator", sectionSeparator);
    }


    public void addInternationalizableComponents(TranslationNotifier translationNotifier) {
        translationNotifier.addInternationalizableComponent(this, "BroadcastFilesDetailWindow.title");
        translationNotifier.addInternationalizableComponent(headerPanel,
                                                            "BroadcastFilesDetailWindow.headerPanel.title");
        translationNotifier.addInternationalizableComponent(fileNameLabel,
                                                            "BroadcastFilesDetailWindow.fileNameLabel");
        translationNotifier.addInternationalizableComponent(destinationSystemLabel,
                                                            "BroadcastFilesDetailWindow.destinationSystemLabel");
        translationNotifier.addInternationalizableComponent(locationLabel,
                                                            "BroadcastFilesDetailWindow.locationLabel");
        translationNotifier.addInternationalizableComponent(fileHeader,
                                                            "BroadcastFilesDetailWindow.fileHeader",
                                                            null);
        translationNotifier.addInternationalizableComponent(autoDistribution,
                                                            "BroadcastFilesDetailWindow.autoDistribution",
                                                            null);
        translationNotifier.addInternationalizableComponent(historiseFile,
                                                            "BroadcastFilesDetailWindow.historiseFile",
                                                            null);
        translationNotifier.addInternationalizableComponent(distributionMethodLabel,
                                                            "BroadcastFilesDetailWindow.distributionMethodLabel");
        translationNotifier.addInternationalizableComponent(batchFileLabel,
                                                            "BroadcastFilesDetailWindow.batchFileLabel");
        translationNotifier.addInternationalizableComponent(sectionSeparator,
                                                            "BroadcastFilesDetailWindow.sectionSeparator",
                                                            null);
    }


    private void fillBatchFile() {
        cftBatchFile.setModel(new DefaultComboBoxModel(
              guiPrefManager.getVtomBatchFilesNames()));
    }


    private void fillDestinationLocation() {
        fileDestinationLocation.setModel(new DefaultComboBoxModel(
              guiPrefManager.getBroadcastLocations()));
    }


    private void fillDiffuserCode() {
        distributionMethod.setModel(new DefaultComboBoxModel(
              guiPrefManager.getDiffuserCode()));
    }


    private void initGui(GuiContext guiContext) {
        this.setResizable(true);
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        this.setPreferredSize(new Dimension(470, 470));
        this.getContentPane().setLayout(borderLayout1);
        autoDistribution.setEnabled(false);
        historiseFile.setEnabled(false);
        fileName.setColumns(30);
        fileName.setMaxTextLength(30);
        destinationSystem.setMaxTextLength(6);
        headerPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white,
              new Color(134, 134, 134)), ""));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(scrollPane, BorderLayout.CENTER);
        filePanel.setLayout(gridBagLayout1);
        fileDestinationLocation.setEnabled(false);
        distributionMethod.setEnabled(false);
        cftBatchFile.setEnabled(false);
        filePanel.setBorder(BorderFactory.createEtchedBorder());
        filePanel.add(fileNameLabel,
                      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(16, 12, 0, 66), 0, 0));
        filePanel.add(fileName,
                      new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(16, 0, 0, 100),
                                             -52,
                                             0));
        filePanel.add(locationLabel,
                      new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(17, 12, 0, 79), 0, 0));
        filePanel.add(destinationSystemLabel,
                      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(19, 12, 0, 34), 0, 0));
        filePanel.add(fileDestinationLocation,
                      new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(14, 0, 0, 100),
                                             153,
                                             0));
        filePanel.add(distributionMethodLabel,
                      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(17, 12, 0, 44), 0, 0));
        filePanel.add(distributionMethod,
                      new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(14, 0, 0, 131),
                                             78,
                                             0));
        filePanel.add(batchFileLabel,
                      new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(17, 12, 0, 76), 0, 0));
        filePanel.add(cftBatchFile,
                      new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(14, 0, 0, 100),
                                             153,
                                             0));
        filePanel.add(sectionSeparator,
                      new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(14, 12, 0, 0), 0, 0));
        filePanel.add(historiseFile,
                      new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(14, 5, 0, 88), 0, 0));
        filePanel.add(headerPanel,
                      new GridBagConstraints(0, 7, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                             GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 512, 40));
        filePanel.add(fileHeader,
                      new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(0, 12, 0, 0), 0, 0));
        filePanel.add(destinationSystem,
                      new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(16, 0, 0, 100),
                                             274,
                                             0));
        filePanel.add(autoDistribution,
                      new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(0, 5, 0, 23), 0, 0));
        this.getContentPane().add(buttonPanelLogic.getGui(), BorderLayout.SOUTH);
        scrollPane.getViewport().add(fileHeaderText, null);
        this.getContentPane().add(filePanel, BorderLayout.CENTER);
        this.setSize(new Dimension(450, 400));

        TranslationNotifier translationNotifier =
              InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        translationNotifier.addInternationalizableContainer(this);
    }
}
