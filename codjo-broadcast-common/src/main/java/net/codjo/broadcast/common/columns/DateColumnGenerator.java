/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/**
 * Classe responsable de l'extraction et du formatage de donnees de type
 * <code>Date</code> .
 *
 * @author $Author: galaber $
 * @version $Revision: 1.3 $
 */
class DateColumnGenerator extends AbstractFileColumnGenerator {
    private DateFormat formatOUT;

    /**
     * Initialisation de l'objet.
     *
     * @param fieldInfo Le nom physique de la table + Le nom physique du champ a extraire
     * @param destColumnName Description of the Parameter
     * @param formatOUT Le format de sortie du champ extrait
     * @param padder L'objet responsable du padding
     * @param expression
     * @param isBreakField   si true , ce champ est un champ de rupture.
     *
     * @throws IllegalArgumentException dans le cas de parametres invalides
     */
    DateColumnGenerator(FieldInfo fieldInfo, String destColumnName, String formatOUT,
        Padder padder, GeneratorExpression expression, boolean isBreakField) {
        super(fieldInfo, destColumnName, padder, expression, isBreakField);
        if (formatOUT == null) {
            throw new IllegalArgumentException("Parametres invalides");
        }
        this.formatOUT = new SimpleDateFormat(formatOUT);
    }


    DateColumnGenerator(FieldInfo fieldInfo, String destColumnName, String formatOUT,
        Padder padder) {
        this(fieldInfo, destColumnName, formatOUT, padder, null, false);
    }

    /**
     * Formatage de la date <code>date</code>.
     *
     * @param date La date a formater
     *
     * @return La date formate
     */
    @Override
    protected String format(Object date) {
        if (date == null) {
            return "";
        }
        return formatOUT.format(date);
    }
}
