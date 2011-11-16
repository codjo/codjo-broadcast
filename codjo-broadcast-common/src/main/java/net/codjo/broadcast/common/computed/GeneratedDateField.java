/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.computed;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
/**
 * Description of the Class
 *
 * @author $Author: galaber $
 * @version $Revision: 1.2 $
 */
public class GeneratedDateField extends AbstractComputedField {
    /**
     * Constructeur de GeneratedDateField
     */
    public GeneratedDateField() {
        super("DATE_HEURE", Types.TIMESTAMP, "DATE_HEURE DATETIME");
    }

    /**
     * Remplissage de la colonne avec la date et l'heure de generation
     *
     * @param ctxt context de la colonne calculee (contient les noms des tables
     *        utilisees...)
     * @param con
     *
     * @exception SQLException
     */
    public void compute(ComputedContext ctxt, Connection con)
            throws SQLException {
        Statement stmt = con.createStatement();
        try {
            stmt.executeUpdate("update " + ctxt.getComputedTableName() + " set "
                + getName() + " = getDate()");
        }
        finally {
            stmt.close();
        }
    }
}
