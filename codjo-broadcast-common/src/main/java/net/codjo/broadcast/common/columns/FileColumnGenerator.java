/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Interface decrivant un generateur de colonne en sortie d'export.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.2 $
 */
public interface FileColumnGenerator {
    /**
     * Retourne l'en-tete de cette colonne.
     *
     * @return Le column header
     */
    public String buildColumnHeader();


    /**
     * Extrait et formate le champ.
     *
     * @param rs La ligne contenant le champ a extraire
     *
     * @return Le champ formate
     *
     * @throws SQLException Erreur d'acces a la base de donnees
     */
    public String proceedField(ResultSet rs) throws SQLException, GenerationException;


    /**
     * Retourne les info du champ en base.
     *
     * @return FieldInfo
     */
    public FieldInfo getFieldInfo();


    public boolean isBreakField();
}
