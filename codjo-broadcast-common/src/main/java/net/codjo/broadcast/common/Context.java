/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.variable.TemplateInterpreter;
import net.codjo.variable.UnknownVariableException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
/**
 * Contexte d'une diffusion.
 *
 * <p> Un contexte contient la definition de variable. </p>
 *
 * <p> Les variables definit lors de la construction du contexte sont considéré comme immutable (la methode
 * put refusera leurs redefinition). </p>
 */
public class Context {
    private Map<String, Object> immutableParameters = new HashMap<String, Object>();
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private java.sql.Date forceToday = null;
    private Context subContext = null;


    public Context() {
    }


    public Context(java.sql.Date forceDate) {
        this(null, forceDate);
    }


    public Context(Map<String, Object> params) {
        this(params, null);
    }


    public Context(Map<String, Object> params, java.sql.Date forceDate) {
        if (params != null) {
            immutableParameters = new HashMap<String, Object>(params);
        }
        forceToday(forceDate);
    }


    /**
     * Connecte ce contexte a un sous-contexte. Ce contexte utilisera aussi les variables du sous-contexte.
     *
     * @param ctxt un sous-contexte.
     */
    public void connectTo(Context ctxt) {
        subContext = ctxt;
    }


    /**
     * Force la date du jour.
     *
     * @param newToday Une date
     */
    public void forceToday(java.sql.Date newToday) {
        this.forceToday = newToday;
    }


    /**
     * Retourne la valeur du parametre. Si le parametre est indefini une exception est levee.
     *
     * @param parameterName
     *
     * @return la valeur du parametre
     *
     * @throws NoSuchElementException Parametre inconnu
     */
    public Object getParameter(String parameterName) {
        Map params = getParameters();
        Object val = params.get(parameterName);
        if (val == null && !params.containsKey(parameterName)) {
            throw new NoSuchElementException("Le parametre " + parameterName + " n'est pas definit");
        }
        return val;
    }


    /**
     * Retourne les parametres de ce contexte (+ les parametres du sous-contexte).
     *
     * @return une map de parametres : clef = nom du parametres / valeur = valeur du parametre (pas forcement
     *         une string)
     */
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<String, Object>();
        if (subContext != null) {
            params.putAll(subContext.getParameters());
        }
        params.putAll(parameters);
        params.putAll(immutableParameters);
        return params;
    }


    /**
     * Renvoie la date du jour.
     *
     * @return La date du jour ou la date forcée par forceToday().
     */
    public java.sql.Date getToday() {
        if (this.forceToday == null) {
            return new java.sql.Date(System.currentTimeMillis());
        }
        else {
            return this.forceToday;
        }
    }


    /**
     * Teste la presence d'un parametre.
     *
     * @param parameterName Le parametre a tester
     *
     * @return <code>true</code> si la variable est definie.
     */
    public boolean hasParameter(String parameterName) {
        return getParameters().containsKey(parameterName);
    }


    public void putParameter(String parameterName, Object value) {
        if (immutableParameters.containsKey(parameterName)) {
            throw new IllegalArgumentException("La variable " + parameterName
                                               + "ne peut etre redefinit.");
        }
        parameters.put(parameterName, value);
    }


    /**
     * Remplace les variables du fichier <code>templateFile</code> par leurs valeurs.
     *
     * @param templateFile
     *
     * @return le fichier converti
     */
    public File replaceVariables(File templateFile) {
        return new File(replaceVariables(templateFile.getPath()));
    }


    /**
     * Remplace les variables de la String <code>template</code> par leurs valeurs.
     *
     * @param template
     *
     * @return le template instancie
     *
     * @throws IllegalArgumentException si le templateFile contient une variable inconnue.
     */
    public String replaceVariables(String template) {
        TemplateInterpreter interpreter = new TemplateInterpreter();
        interpreter.addAsVariable(getParameters());
        try {
            String before = template;
            String after = interpreter.evaluate(template);

            while(!after.equals(before)) {
                before = after;
                after = interpreter.evaluate(before);
            }
            return after;
        }
        catch (UnknownVariableException ex) {
            throw new IllegalArgumentException("La string >" + template
                                               + "< contient des variables inconnues : " + ex.getMessage());
        }
    }
}
