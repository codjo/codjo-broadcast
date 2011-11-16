/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
/**
 * Classe responsable de l'extraction et du formatage de donnees de type <code>boolean</code> .
 *
 * @author $Author: galaber $
 * @version $Revision: 1.3 $
 */
class BooleanColumnGenerator extends AbstractFileColumnGenerator {
    /**
     * Constructeur de BooleanColumnGenerator
     *
     * @param field          l'id du champs en BD
     * @param destColumnName Le nom de la colonne destrination
     * @param padder         L'objet responsable du formatage
     * @param expression     l'expression.
     * @param isBreakField   si true , ce champ est un champ de rupture.
     */
    BooleanColumnGenerator(FieldInfo field, String destColumnName, Padder padder,
                           GeneratorExpression expression, boolean isBreakField) {
        super(field, destColumnName, padder, expression, isBreakField);
    }


    BooleanColumnGenerator(FieldInfo field, String destColumnName, Padder padder) {
        this(field, destColumnName, padder, null, false);
    }


    /**
     * Extraction du champ source.
     *
     * @param value la valeur a formatter
     *
     * @return 'Vrai' si true ou 'Faux' si false
     */
    @Override
    protected String format(Object value) {
        if (Boolean.TRUE.equals(value)) {
            return "Vrai";
        }
        return "Faux";
    }
}
