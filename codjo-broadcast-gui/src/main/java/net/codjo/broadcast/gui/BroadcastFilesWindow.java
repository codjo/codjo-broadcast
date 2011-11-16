/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizableRequestTable;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class BroadcastFilesWindow extends JInternalFrame implements InternationalizableContainer {
    private RequestTable contentsTable = new RequestTable();
    private RequestToolBar contentsToolBar = new RequestToolBar();
    private RequestTable filesTable = new RequestTable();
    private RequestToolBar filesToolBar = new RequestToolBar();
    private TranslationNotifier translationNotifier;
    private TranslationManager translationManager;
    private JPanel filesPanel;
    private JPanel sectionsPanel;


    public BroadcastFilesWindow(GuiContext ctxt) throws Exception {
        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(ctxt);
        translationManager = InternationalizationUtil.retrieveTranslationManager(ctxt);

        jbInit();

        filesTable.setPreference(PreferenceFactory.getPreference("BroadcastFilesWindow"));
        filesTable.load();
        filesToolBar.setHasExcelButton(true);
        filesToolBar.init(ctxt, filesTable);

        contentsTable.setPreference(PreferenceFactory.getPreference("BroadcastFileContentsWindow"));

        contentsToolBar.setHasExcelButton(true);
        contentsToolBar.init(ctxt, contentsTable);
        contentsToolBar.setFather(filesTable, "fileId", "selectNamesBroadcastFileContentsByFileId", ctxt);

        translationNotifier.addInternationalizableContainer(this);
    }


    private void jbInit() throws Exception {
        TitledBorder filesTitledBorder =
              new TitledBorder(BorderFactory.createEtchedBorder(Color.white,
                                                                new Color(134, 134, 134)), "");
        JScrollPane filesScrollPane = new JScrollPane();
        filesScrollPane.getViewport().add(filesTable, null);

        filesPanel = new JPanel();
        filesPanel.setLayout(new BorderLayout());
        filesPanel.setBorder(filesTitledBorder);
        filesPanel.add(filesScrollPane, BorderLayout.CENTER);
        filesPanel.add(filesToolBar, BorderLayout.SOUTH);

        Border sectionsBorder =
              BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134));
        TitledBorder sectionsTitledBorder = new TitledBorder(sectionsBorder, "");
        JScrollPane sectionsScrollPane = new JScrollPane();
        sectionsScrollPane.getViewport().add(contentsTable, null);

        sectionsPanel = new JPanel();
        sectionsPanel.setLayout(new BorderLayout());
        sectionsPanel.setBorder(sectionsTitledBorder);
        sectionsPanel.add(sectionsScrollPane, BorderLayout.CENTER);
        sectionsPanel.add(contentsToolBar, BorderLayout.SOUTH);

        JSplitPane splitPane =
              new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, filesPanel, sectionsPanel);
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerLocation(300);

        getContentPane().setLayout(new BorderLayout());
        setClosable(true);
        setResizable(true);
        setIconifiable(true);
        setTitle("Fichiers / Sections");
        setPreferredSize(new Dimension(500, 500));
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }


    public void addInternationalizableComponents(TranslationNotifier notifier) {
        notifier.addInternationalizableComponent(this, "BroadcastFilesWindow.title");
        notifier.addInternationalizableComponent(filesPanel, "BroadcastFilesWindow.filesPanel.title");
        notifier.addInternationalizableComponent(sectionsPanel, "BroadcastFilesWindow.sectionsPanel.title");
        notifier.addInternationalizableComponent(
              new InternationalizableRequestTable(PreferenceFactory.getPreference("BroadcastFilesWindow"),
                                                  filesTable));
        notifier.addInternationalizableComponent(
              new InternationalizableRequestTable(
                    PreferenceFactory.getPreference("BroadcastFileContentsWindow"),
                    contentsTable));
    }
}
