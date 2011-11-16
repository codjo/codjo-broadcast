/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Classe de base pour les FileColumnGenerator.
 *
 * <p> Cette classe permet d'extraire d'un resultSet un champs et de le formater. </p>
 *
 * @author $Author: galaber $
 * @version $Revision: 1.3 $
 */
abstract class AbstractFileColumnGenerator implements FileColumnGenerator {
    private String destColumnName;
    private GeneratorExpression expression;
    private FieldInfo field;
    private Padder padder;
    private boolean isBreakField;


    /**
     * Constructeur de AbstractFileColumnGenerator
     *
     * @param destColumnName Le nom de la colonne destination
     * @param padder         Le <code>Padder</code> responsable du padding
     *
     * @throws IllegalArgumentException field null
     */
    protected AbstractFileColumnGenerator(FieldInfo field, String destColumnName, Padder padder,
                                          GeneratorExpression expression, boolean isBreakField) {
        if (field == null) {
            throw new IllegalArgumentException("Parametres invalides");
        }
        this.expression = expression;
        this.field = field;
        this.destColumnName = destColumnName;
        this.padder = padder;
        this.isBreakField = isBreakField;
        if (expression != null) {
            expression.init(this);
        }
    }


    public final String proceedField(ResultSet rs)
          throws SQLException, GenerationException {
        if (expression == null) {
            return doPadding(format(rs.getObject(getFieldAlias())));
        } else {
            return doPadding(expression.computeToString(rs.getObject(getFieldAlias())));
        }
    }


    /**
     * Retourne le nom de la colonne destination. Si l'objet <code>padder</code> n'est pas null le nom est
     * formate.
     *
     * @return Le nom de la colonne destination
     */
    public String buildColumnHeader() {
        if (padder == null) {
            return this.destColumnName;
        }
        return padder.doPadding(this.destColumnName);
    }


    public String getFieldAlias() {
        return this.field.getAlias();
    }


    public FieldInfo getFieldInfo() {
        return this.field;
    }


    public boolean isBreakField() {
        return isBreakField;
    }


    @Override
    public String toString() {
        return "Column(" + getFieldInfo().getFullDBTableName() + "," + getClass() + ")";
    }


    protected abstract String format(Object value);


    /**
     * Cette methode utilise l'objet <code>Padder</code> pour ajouter les caracteres de remplissage du champ a
     * formater.
     *
     * @param stringToPadding Le champ a padder
     *
     * @return Le champ formate
     *
     * @throws IllegalArgumentException la chaine de caracteres apres remplissage est trop grande par rapport
     *                                  a la taille du champs en destination
     */
    String doPadding(String stringToPadding) {
        if (padder == null) {
            return stringToPadding;
        }
        try {
            return padder.doPadding(stringToPadding);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Padding du champs >" + destColumnName
                                               + "< (" + field.getAlias() + ")" + " en erreur : "
                                               + ex.getMessage());
        }
    }
}
