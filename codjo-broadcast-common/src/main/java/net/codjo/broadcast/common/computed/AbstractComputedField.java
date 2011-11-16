/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.computed;
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


    @Override
    public String toString() {
        return getName();
    }
}
