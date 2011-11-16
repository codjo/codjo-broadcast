/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
/**
 * Cette classe permet d'ajouter <code>n</code> caracteres, a droite ou a gauche d'une
 * chaine.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
class Padder {
    private int columnLength;
    private String padder;
    private boolean rightPadding;

    /**
     * Initialisation du <code>Padder</code>.
     *
     * @param padder Caractere a ajouter
     * @param columnLength Largeur de la colonne
     * @param rightPadding Specifie si le caractere <code>padder</code> doit etre ajoute
     *        a droit ou a gauche
     *
     * @throws IllegalArgumentException si un des paramètres n'est pas valide
     */
    Padder(String padder, int columnLength, boolean rightPadding) {
        if (padder == null || padder.length() != 1) {
            throw new IllegalArgumentException("Caractere de remplissage invalide");
        }
        if (columnLength < 1) {
            throw new IllegalArgumentException("Largeur de colonne invalide : "
                + columnLength);
        }
        this.padder = padder;
        this.columnLength = columnLength;
        this.rightPadding = rightPadding;
    }

    /**
     * Ajoute le caractere <code>padder</code> a <code>fieldToPadding</code>.
     *
     * @param fieldToPadding La chaine a formater
     *
     * @return La chaine formate
     *
     * @exception IllegalArgumentException Champs trops grand
     */
    public String doPadding(String fieldToPadding) {
        if (fieldToPadding == null) {
            fieldToPadding = "";
        }
        if (fieldToPadding.length() > this.columnLength) {
            throw new IllegalArgumentException(
                "Nombre de caracteres superieur a la largeur de la colonne ("
                + fieldToPadding.length() + " > " + this.columnLength + ") " + " : "
                + fieldToPadding);
        }

        StringBuffer buffer = new StringBuffer(fieldToPadding);
        for (int i = buffer.length(); i < columnLength; i++) {
            if (rightPadding) {
                buffer.append(padder);
            }
            else {
                buffer.insert(0, padder);
            }
        }

        return buffer.toString();
    }
}
