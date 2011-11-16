/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
/**
 * Classe responsable de l'extraction et du formatage de donnees de type <code>String</code> .
 *
 * @author $Author: galaber $
 * @version $Revision: 1.3 $
 */
class StringColumnGenerator extends AbstractFileColumnGenerator {
    /**
     * Constructeur de StringColumnGenerator
     *
     * @param fieldInfo      le champs en BD
     * @param destColumnName Le nom de la colonne destination
     * @param padder         L'obje responsable du formatage
     * @param expression     l'expression.
     * @param isBreakField   si true , ce champ est un champ de rupture.
     */
    StringColumnGenerator(FieldInfo fieldInfo, String destColumnName, Padder padder,
                          GeneratorExpression expression, boolean isBreakField) {
        super(fieldInfo, destColumnName, padder, expression, isBreakField);
    }


    StringColumnGenerator(FieldInfo fieldInfo, String destColumnName, Padder padder) {
        super(fieldInfo, destColumnName, padder, null, false);
    }


    @Override
    protected String format(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
