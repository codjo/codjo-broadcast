/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
/**
 * Classe responsable de l'extraction et du formatage de donnees de type <code>numeric</code> .
 *
 * @author $Author: galaber $
 * @version $Revision: 1.3 $
 */
class NumberColumnGenerator extends AbstractFileColumnGenerator {
    private DecimalFormat formatFloatOUT;


    /**
     * Initialisation de <code>NumberColumnGenerator</code>.
     *
     * @param field            information sur le champs
     * @param destColumnName   Le nom physique du champ destination
     * @param decimalSeparator Le separateur de decimales
     * @param decimalPattern   format
     * @param padder           Le <code>Padder</code> responsable du formatage du champ
     * @param expression       expression * @param isBreakField   si true , ce champ est un champ de rupture.
     *
     * @throws IllegalArgumentException separateur de decimal ou format nul ou vide
     */
    NumberColumnGenerator(FieldInfo field, String destColumnName, String decimalSeparator,
                          String decimalPattern,
                          Padder padder,
                          GeneratorExpression expression,
                          boolean isBreakField) {
        super(field, destColumnName, padder, expression, isBreakField);
        if (decimalSeparator == null || "".equals(decimalSeparator)) {
            throw new IllegalArgumentException("Séparateur de decimal interdit : "
                                               + decimalSeparator + " pour " + field.toString());
        }
        if (decimalPattern == null || "".equals(decimalPattern)) {
            throw new IllegalArgumentException("Format de nombre interdit : "
                                               + decimalPattern + " pour " + field.toString());
        }
        initFormat(decimalSeparator, decimalPattern);
    }


    NumberColumnGenerator(FieldInfo field, String destColumnName,
                          String decimalSeparator, String decimalPattern, Padder padder) {
        this(field, destColumnName, decimalSeparator, decimalPattern, padder, null, false);
    }


    @Override
    protected String format(Object number) {
        if (number == null) {
            return "";
        }
        return formatFloatOUT.format(number);
    }


    /**
     * Initialisation du separateur de decimales pour le format de sortie.
     *
     * @param decimalSeparator Le separateur de decimales a utiliser
     * @param decimalPattern   Description of the Parameter
     */
    private void initFormat(String decimalSeparator, String decimalPattern) {
        if (".".equals(decimalSeparator)) {
            formatFloatOUT =
                  new DecimalFormat(decimalPattern, new DecimalFormatSymbols(Locale.ENGLISH));
        } else if (",".equals(decimalSeparator)) {
            formatFloatOUT =
                  new java.text.DecimalFormat(decimalPattern,
                                              new DecimalFormatSymbols(Locale.FRENCH));
        } else {
            throw new IllegalArgumentException("Separateur de décimal non supporté : "
                                               + decimalSeparator);
        }
    }
}
