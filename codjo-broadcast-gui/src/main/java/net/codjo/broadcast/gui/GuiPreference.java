/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.DetailDataSource;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JPanel;
/**
 * Interface decrivant les preferences d'export pour une famille.
 */
public interface GuiPreference {
    /**
     * Retourne la famille de cet objet preference.
     *
     * @return La valeur de family
     */
    public String getFamily();


    public String getFamilyLabel();


    /**
     * Retourne Un JPanel contenant les champs optionnels specifiques a la famille se trouvant dans une table
     * liée a la table des contents.
     *
     * @param contentDataSource le datasource du content en edition
     */
    public JPanel buildContentOptionPanel(DetailDataSource contentDataSource)
          throws RequestException;


    /**
     * Retourne la liste des fonctions utilisables dans les expressions.
     */
    public List<String> getAllFunctions();


    /**
     * Enregistre les données du pannel optionnel des Contents.
     *
     * <p> N.B. Cette methode est appele apres que le content soit enregistre </p>
     *
     * @param contentDataSource le datasource du content en edition
     * @param panel             le panel optionnel construit par buildContentOptionPanel
     */
    public void saveContentOptionPanel(DetailDataSource contentDataSource, JPanel panel)
          throws RequestException;


    /**
     * @param joinKeyName le nom de la jointure
     *
     * @return le nom de la table maitre de la jointure
     */
    public String determineTableName(String joinKeyName);


    /**
     * Retourne la JCombobox des selecteurs de selection pour cette famille.
     *
     * @return une JComboBox
     *
     * @throws RequestException pb
     */
    public JComboBox buildSelectionComboBox() throws RequestException;


    /**
     * Retourne les GuiField pour une entité exportable.
     *
     * @param joinKeyName
     *
     * @return Tableau de GuiField.
     */
    public GuiField[] getGuiFieldsFor(String joinKeyName);


    /**
     * Retourne les labels de toutes les entités exportable (tables + clefs de jointure).
     *
     * @return Retourne les labels (clef = joinKeyName / value = label).
     */
    public Map<String, String> getJoinKeyLabels();
}
