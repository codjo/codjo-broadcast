/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * Interface responsable de la creation et du remplissage de la table temporaire contenat
 * les champs calcules.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public interface ComputedFieldGenerator {
    /**
     * Remarque : la table de selection <code>Preferences.getSelectionTableName()</code>
     * doit imperativement etre cree et remplie avec la meme connexion avant d'appeler
     * cette methode.
     *
     * @param ctxt Le contexte d'execution
     * @param fileColumnGenerator Liste d'objets <code>FileColumnGenerator</code>
     * @param con une connexion
     *
     * @exception SQLException Erreur d'acces a la base de données
     */
    public void generateComputedTable(Context ctxt,
        FileColumnGenerator[] fileColumnGenerator, Connection con)
            throws SQLException;
}
