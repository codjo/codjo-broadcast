/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 * Description of the Class
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class BroadcastSectionsAction extends AbstractGuiAction {
    CleanUpListener cleanUpListener = new CleanUpListener();
    JInternalFrame frame;

    public BroadcastSectionsAction(GuiContext ctxt) {
        super(ctxt, "Sections/Colonnes", "Paramétrage des sections et colonnes", null,
            "ParamExportSectionsColumns");
    }

    public void actionPerformed(ActionEvent event) {
        if (frame == null) {
            displayNewWindow();
        }
        else {
            try {
                frame.setSelected(true);
            }
            catch (PropertyVetoException ex) {
                System.err.println("[PropertyVeto] " + ex.getMessage());
            }
        }
    }


    private void displayNewWindow() {
        try {
            frame = new BroadcastSectionsWindow(getGuiContext());
            frame.addInternalFrameListener(cleanUpListener);
            getDesktopPane().add(frame);
            frame.pack();
            frame.setVisible(true);
            frame.setSelected(true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ErrorDialog.show(getDesktopPane(), "Impossible d'afficher la liste", ex);
        }
    }

    private class CleanUpListener extends InternalFrameAdapter {
        @Override
        public void internalFrameActivated(InternalFrameEvent event) {}


        @Override
        public void internalFrameClosed(InternalFrameEvent event) {
            event.getInternalFrame().removeInternalFrameListener(this);
            frame = null;
        }


        @Override
        public void internalFrameClosing(InternalFrameEvent event) {
            event.getInternalFrame().removeInternalFrameListener(this);
            frame = null;
        }
    }
}
