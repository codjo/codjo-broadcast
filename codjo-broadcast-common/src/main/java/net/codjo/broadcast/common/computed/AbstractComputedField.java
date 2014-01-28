/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.computed;
import net.codjo.variable.TemplateInterpreter;
import net.codjo.variable.UnknownVariableException;
/**
 * Classe de base pour les objets <code>ComputedField</code>.
 */
public abstract class AbstractComputedField implements ComputedField {
    private String name;
    private String sqlDefinition;
    private int sqlType;


    /**
     * @param name          Le nom de la colonne
     * @param sqlType       Le type SQL de la colonne
     * @param sqlDefinition La definition SQL de la colonne
     */
    protected AbstractComputedField(String name, int sqlType, String sqlDefinition) {
        if (name == null || sqlDefinition == null) {
            throw new IllegalArgumentException("Parametres invalides");
        }
        this.name = name;
        this.sqlType = sqlType;
        this.sqlDefinition = sqlDefinition;
    }


    public String getName() {
        return name;
    }


    public String getSqlDefinition() {
        return sqlDefinition;
    }


    public int getSqlType() {
        return sqlType;
    }


    protected final String appendWarning(String normalCondition, String warningMessage) {
        return appendWarning(normalCondition, warningMessage, null);
    }


    protected final String appendWarning(String normalCondition, String warningMessage, String messagePrefix) {
        String template = ", $warningsField$ ="
                          + " case when NOT ($normalCondition$)"
                          + " then NVL($warningsField$, $messagePrefix$) || '    - $warningMessage$'"
                          + " else null"
                          + " end ";

        TemplateInterpreter interpreter = new TemplateInterpreter();
        interpreter.add("warningsField", "comp." + WARNINGS);
        interpreter.add("normalCondition", normalCondition);
        interpreter.add("warningMessage", warningMessage);
        interpreter.add("messagePrefix", (messagePrefix == null) ? "null" : "'" + messagePrefix + "'");
        try {
            return interpreter.evaluate(template);
        }
        catch (UnknownVariableException ex) {
            throw new IllegalArgumentException("The sql template >" + template
                                               + "< contains unknown variables : " + ex.getMessage());
        }
    }


    @Override
    public String toString() {
        return getName();
    }
}
