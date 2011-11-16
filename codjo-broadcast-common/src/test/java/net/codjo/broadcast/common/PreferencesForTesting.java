/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.broadcast.common.computed.ComputedField;
import net.codjo.broadcast.common.computed.ConstantField;
import net.codjo.broadcast.common.computed.GeneratedDateField;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
/**
 * Preferences du module Diffusion pour les Tests. Mode de diffusion pour la table PM_BROADCAST_COLUMNS.
 *
 * @author $Author: galaber $
 * @version $Revision: 1.5 $
 */
public class PreferencesForTesting extends Preferences {
    public PreferencesForTesting() {
        super("TestTU", "PM_BROADCAST_COLUMNS", "COLUMNS_LIST", "COMPUTED");
    }


    public PreferencesForTesting(String prefix) {
        super("TestTU", "PM_BROADCAST_COLUMNS", prefix + "COLUMNS_LIST",
              prefix + "COMPUTED");
    }


    public static Preferences buildPreferences() {
        return new PreferencesForTesting();
    }


    public static Preferences buildPreferencesWithSlash() {
        return new PreferencesForTesting("#");
    }


    @Override
    public Selector buildSelector(Connection con, BigDecimal contentID,
                                  BigDecimal sectionID, BigDecimal selectionID)
          throws SQLException {
        return new FakeSelector();
    }


    @Override
    protected ComputedField[] initComputedFields() {
        return new ComputedField[]{
              new GeneratedDateField(),
              new ConstantField("DATA_TYPE_DC", Types.VARCHAR,
                                "DATA_TYPE_DC varchar(3)", "DC")
        };
    }


    @Override
    protected void initJoinKeys() {
        addJoinKeys(INNER_JOIN, getBroadcastTableName(), getSelectionTableName(),
                    new String[][]{
                          {"COLUMNS_ID", "COLUMNS_ID", " = "},
                          {"SECTION_ID", "SECTION_ID", " = "}
                    });

        addJoinKeys(INNER_JOIN, getComputedTableName(), getSelectionTableName(),
                    new String[][]{
                          {"SELECTION_ID", "SELECTION_ID", " = "}
                    });

        addJoinKeys(RIGHT_JOIN, "PM_BROADCAST_SECTION", getBroadcastTableName(),
                    new String[][]{
                          {"SECTION_ID", "SECTION_ID", " = "}
                    });

//--- Test new operator (>;>=;<;<=): since Broadcast version 2.03 --------------
        addJoinKeys(INNER_JOIN, "NEW_OPER", getBroadcastTableName(),
                    new String[][]{
                          {"BRANCH_TABLE_A", "BRANCH_TABLE_B", " = "},
                          {"DATE_TABLE_A", "DATE_DEBUT_TABLE_B", " >= "},
                          {"DATE_TABLE_A", "DATE_FIN_TABLE_B", " <= "}
                    });

//-------------Test table as xxx : since Broadcast version 2.03 --------------
        addJoinKeys(RIGHT_JOIN, "REF as REF_1", "T_A",
                    new String[][]{
                          {"FIELD_F", "FIELD_E", " = "}
                    });

        addJoinKeys(RIGHT_JOIN, "T_A", getBroadcastTableName(),
                    new String[][]{
                          {"FIELD_B", "FIELD_A", " = "}
                    });
        addJoinKeys(RIGHT_JOIN, "REF as REF_2", "T_B",
                    new String[][]{
                          {"FIELD_H", "FIELD_J", " = "}
                    });

        addJoinKeys(RIGHT_JOIN, "REF as REF_3", "T_B",
                    new String[][]{
                          {"FIELD_H", "FIELD_I", " = "}
                    });

        addJoinKeys(RIGHT_JOIN, "T_B", getBroadcastTableName(),
                    new String[][]{
                          {"FIELD_D", "FIELD_C", " = "}
                    });

//---Test max de date dans la table des données: since Broadcast version 2.04---
        addJoinKeys(RIGHT_JOIN, "COB", "COB as COB_1",
                    new String[][]{
                          {"ID_COB", "ID_COB", " = "}
                    });

        addJoinKeys(RIGHT_JOIN, "COB as COB_1", "VL_SELECT",
                    new String[][]{
                          {"ID_COB", "VL", " = "}
                    },
                    new JoinExpression("(COB_1.DATE_COB <= VL_SELECT.DATE)",
                                       "(Max(COB_1.DATE_COB) = COB.DATE_COB)", "COB.DATE_COB"));

        addJoinKeys(RIGHT_JOIN, "VL_SELECT", getBroadcastTableName(),
                    new String[][]{
                          {"VL", "FIELD_A", " = "}
                    });
    }


    public static final class FakeSelector implements Selector {
        private boolean proceedHasBeenCalled ;
        private boolean cleanupHasBeenCalled ;


        public void cleanup(Context ctxt, Connection con, String table, java.sql.Date today) {
            cleanupHasBeenCalled = true;
        }


        public void proceed(Context ctxt, Connection con, String table, java.sql.Date today) {
            proceedHasBeenCalled = true;
        }


        public boolean isProceedHasBeenCalled() {
            return proceedHasBeenCalled;
        }


        public boolean isCleanupHasBeenCalled() {
            return cleanupHasBeenCalled;
        }
    }
}
