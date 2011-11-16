package net.codjo.broadcast.gui.selector;
import net.codjo.mad.gui.request.util.ButtonPanelGui;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 *
 */
public class BroadcastSelectorDetailWindow extends JInternalFrame {
    private JTextField selectorName;
    private JTextArea selectorQuery;
    private JComboBox selectorFamily;
    private ButtonPanelGui buttonPanelGui;
    private JPanel mainPanel;
    private JTextArea selectorColumns;


    public BroadcastSelectorDetailWindow() {
        super("Requète de sélection pour l'export", true, true, true, true);

        selectorColumns.setBackground(new JPanel().getBackground());
        selectorColumns.setName("selectorColumns");
        getContentPane().add(mainPanel);
        setPreferredSize(new Dimension(500, 300));
    }


    public JTextField getSelectorName() {
        return selectorName;
    }


    public JTextArea getSelectorQuery() {
        return selectorQuery;
    }


    public JComboBox getSelectorFamily() {
        return selectorFamily;
    }


    public ButtonPanelGui getButtonPanelGui() {
        return buttonPanelGui;
    }


    public JTextArea getSelectorColumns() {
        return selectorColumns;
    }


    public void switchToUpdateMode() {
        selectorFamily.setEnabled(false);
    }
}
