/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * Interface pour le chargement d'une sélection.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public interface Selector {
    /**
     * Effectue le chargement de la selection
     *
     * @param context Le contexte de diffusion
     * @param connection
     * @param table nom de la table temporaire
     * @param today date du jour
     *
     * @exception SQLException
     *
     * @since 1.00
     */
    public void proceed(Context context, Connection connection, String table, java.sql.Date today)
            throws SQLException;


    /**
     * Effectue les clean-up des tables temporaires utilisées par le selecteur. Cette
     * methode est appelée a la fin de la section ou en cas d'echec (même si c'est le
     * proceed qui echoue).
     *
     * @param context Le contexte de diffusion
     * @param connection
     * @param table nom de la table temporaire
     * @param today date du jour
     *
     * @throws SQLException TODO
     *
     * @since 2.01
     */
    public void cleanup(Context context, Connection connection, String table, java.sql.Date today)
            throws SQLException;
}
