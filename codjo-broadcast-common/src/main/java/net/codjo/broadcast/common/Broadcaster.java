/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.broadcast.common.diffuser.Diffuser;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
/**
 * Interface pour la diffusion d'un fichier.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.2 $
 */
public interface Broadcaster {
    /**
     * Retourne le fichier de destination de cette diffusion en fonction du context.
     *
     * @param currentContext Le contexte courant
     */
    public File getDestinationFile(Context currentContext);


    /**
     * Positionne le fichier destination théorique (avec variable).
     *
     * @param destFile Le fichier destination
     */
    public void setDestinationFile(File destFile);


    /**
     * Lance la génération du fichier et sa diffusion.
     *
     * @param currentContext contexte courant de la diffusion
     */
    public void broadcast(Context currentContext) throws IOException, SQLException, BroadcastException;


    public void setDiffuser(Diffuser diffuser);


    public Diffuser getDiffuser();
}
