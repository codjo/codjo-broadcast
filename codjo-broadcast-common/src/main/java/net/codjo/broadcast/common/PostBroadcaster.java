/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * Interface permetant de lancer des traitements lorsque la diffusion s'est bien
 * déroulée.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public interface PostBroadcaster {
    /**
     * Execution du traitement.
     *
     * @param ctxt contexte de diffusion
     * @param con la connection
     *
     * @throws SQLException Erreur de BD.
     */
    public void proceed(Context ctxt, Connection con)
            throws SQLException, BroadcastException;


    /**
     * Annule l'execution du traitement Post-diffusion.
     *
     * @param ctxt contexte de diffusion
     * @param con la connection
     */
    public void undoProceed(Context ctxt, Connection con);
}
