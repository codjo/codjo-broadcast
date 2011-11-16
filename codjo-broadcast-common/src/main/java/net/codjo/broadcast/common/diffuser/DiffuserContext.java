/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.diffuser;
import java.io.File;
/**
 * Interface decrivant le contexte de diffusion.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public interface DiffuserContext {
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
     * Remplace les variables du fichier <code>templateFile</code> par leurs valeurs.
     *
     * @param templateFile
     *
     * @return le fichier converti
     */
    public File replaceVariables(File templateFile);


    /**
     * Remplace les variables de la String <code>template</code> par leurs valeurs.
     *
     * @param template
     *
     * @return le template instancie
     */
    public String replaceVariables(String template);
}
