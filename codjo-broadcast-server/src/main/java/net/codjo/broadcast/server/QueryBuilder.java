/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
/**
 * Interface pour la construction de la requête de selection des cours de la section.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
interface QueryBuilder {
    /**
     * Construit la requête de selection pour les cours de la section.
     *
     * @param columns Les générateurs de colonnes
     *
     * @return La requête de sélection
     */
    public String buildQuery(FileColumnGenerator[] columns);
}
