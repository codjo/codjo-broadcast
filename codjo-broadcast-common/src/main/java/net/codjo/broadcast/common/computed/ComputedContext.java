/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.computed;
/**
 * C'est le contexte d'execution des champs calculées.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public interface ComputedContext {
    /**
     * Retourne la valeur du parametre. Si le parametre est indefini une exception est
     * levee.
     *
     * @param parameterName
     *
     * @return la valeur du parametre
     */
    public Object getParameter(String parameterName);


    /**
     * DOCUMENT ME!
     *
     * @return La date du jour.
     */
    public java.sql.Date getToday();


    /**
     * Teste la presence d'un parametre.
     *
     * @param parameterName Le parametre a tester
     *
     * @return <code>true</code> si la variable est definie.
     */
    public boolean hasParameter(String parameterName);


    /**
     * Ajoute un nouveau parametre.
     *
     * @param parameterName
     * @param value
     */
    public void putParameter(String parameterName, Object value);


    /**
     * Remplace les variables de la String <code>template</code> par leurs valeurs.
     *
     * @param template
     *
     * @return le template instancie
     */
    public String replaceVariables(String template);


    /**
     * Retourne le nom de la table des champs calcules.
     *
     * @return Le nom de la table
     */
    public String getComputedTableName();


    /**
     * Retourne le nom de la table principal de diffusion (eg AP_SHARE_PRICE). La table
     * de selection choisi les enregistrmenents a diffuser parmi cette table.
     *
     * @return le nom de table
     */
    public String getBroadcastTableName();


    /**
     * Retourne le nom de la table de selection des éléments(cours, ordre) a envoyer.
     *
     * @return Le nom de la table de selection.
     */
    public String getSelectionTableName();


    /**
     * Construction de la jointure de la table calcule vers la table de diffusion maitre.
     *
     * @return
     */
    public String joinToBroadcastTable();
}
