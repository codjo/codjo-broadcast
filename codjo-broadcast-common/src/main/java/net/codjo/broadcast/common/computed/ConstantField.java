/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.computed;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Champ Calculé a valeur constante.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class ConstantField extends AbstractComputedField {
    private Object value;

    /**
     * Constructeur de ConstantField
     *
     * @param name
     * @param sqlType
     * @param sqlDefinition
     * @param value Valeur constante du champ
     */
    public ConstantField(String name, int sqlType, String sqlDefinition, Object value) {
        super(name, sqlType, sqlDefinition);
        this.value = value;
    }

    /**
     * Remplissage de la colonne avec la constante.
     *
     * @param ctxt Le contexte d'execution
     * @param con
     *
     * @exception SQLException
     */
    public void compute(ComputedContext ctxt, Connection con)
            throws SQLException {
        PreparedStatement stmt =
            con.prepareStatement("update " + ctxt.getComputedTableName() + " set "
                + getName() + " = ?");
        try {
            if (value == null) {
                stmt.setNull(1, getSqlType());
            }
            else {
                stmt.setObject(1, value, getSqlType());
            }
            stmt.executeUpdate();
        }
        finally {
            stmt.close();
        }
    }
}
