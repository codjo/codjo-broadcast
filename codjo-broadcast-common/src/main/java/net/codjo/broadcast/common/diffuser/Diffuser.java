/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.diffuser;
import java.io.File;
/**
 * Interface pour la diffusion d'un fichier par un protocole (CFT...).
 */
public interface Diffuser {
    /**
     * Constructeur
     *
     * @param context contexte de diffusion
     * @param file    fichier à diffuser
     *
     * @throws DiffuserException Echec de la diffusion.
     */
    public void diffuse(DiffuserContext context, File file) throws DiffuserException;
}
