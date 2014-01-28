/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.computed;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * Interface responsable du calcul d'une colonne dans la table <code>#COMPUTED</code> .
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 * @see ComputedContext.getComputedTableName
 */
public interface ComputedField {
    public static final String WARNINGS = "WARNINGS";


    /**
     * Mise a jour du champ.
     *
     * @param ctxt Le contexte d'execution
     * @param con  La connexion
     *
     * @throws SQLException Erreur d'acces a la base de donnees
     */
    public void compute(ComputedContext ctxt, Connection con)
          throws SQLException;


    /**
     * Retourne le nom de la colonne.
     *
     * @return La valeur de name
     */
    public String getName();


    /**
     * Retourne le type SQL
     *
     * @return La valeur de sqlType
     *
     * @see java.sql.Types
     */
    public int getSqlType();


    /**
     * Retourne la definition SQL de la colonne.
     * <pre> "DATA_TYPE varchar(2)" </pre>
     *
     * @return La definition
     */
    public String getSqlDefinition();
}
