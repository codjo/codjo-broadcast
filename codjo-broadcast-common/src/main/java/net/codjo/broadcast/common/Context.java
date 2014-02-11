/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import net.codjo.variable.TemplateInterpreter;
import net.codjo.variable.UnknownVariableException;
/**
 * Contexte d'une diffusion.
 *
 * <p> Un contexte contient la definition de variable. </p>
 *
 * <p> Les variables definit lors de la construction du contexte sont considéré comme immutable (la methode put refusera
 * leurs redefinition). </p>
 */
public class Context {
    private Map<String, Object> immutableParameters = new HashMap<String, Object>();
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private java.sql.Date forceToday = null;
    private Context subContext = null;
    private List<String> warnings;
    private boolean computedTableWasCreated;


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
     * @return une map de parametres : clef = nom du parametres / valeur = valeur du parametre (pas forcement une
     *         string)
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
     * @return le fichier converti
     */
    public File replaceVariables(File templateFile) {
        return new File(replaceVariables(templateFile.getPath()));
    }


    /**
     * Remplace les variables de la String <code>template</code> par leurs valeurs.
     *
     * @return le template instancie
     *
     * @throws IllegalArgumentException si le templateFile contient une variable inconnue.
     */
    public String replaceVariables(String template) {
        TemplateInterpreter interpreter = new TemplateInterpreter();
        interpreter.addAsVariable(getParameters());
        try {
            return interpreter.evaluate(template);
        }
        catch (UnknownVariableException ex) {
            throw new IllegalArgumentException("La string >" + template
                                               + "< contient des variables inconnues : " + ex.getMessage());
        }
    }


    public void addWarning(String warning) {
        if (warnings == null) {
            warnings = new ArrayList<String>();
        }

        // a given computed field might report the same warning multiple times
        // if it's used by multiple sections of the same exported file
        // => we must avoid reporting warning more than once
        if (!warnings.contains(warning)) {
            warnings.add(warning);
        }
    }


    public String getWarnings() {
        if (warnings == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");
        boolean first = true;
        for (String warning : warnings) {
            if (!first) {
                result.append(lineSeparator);
            }
            first = false;

            result.append(warning);
        }

        return result.toString();
    }


    public void setComputedTableWasCreated(boolean computedTableWasCreated) {
        this.computedTableWasCreated = computedTableWasCreated;
    }


    public boolean getComputedTableWasCreated() {
        return computedTableWasCreated;
    }
}
