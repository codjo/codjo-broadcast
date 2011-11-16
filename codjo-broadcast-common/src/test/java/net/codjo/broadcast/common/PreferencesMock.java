package net.codjo.broadcast.common;
import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;
import net.codjo.broadcast.common.computed.ComputedField;
/**
 *
 */
public class PreferencesMock extends Preferences {
    public PreferencesMock(String family,
                           String broadcastTableName,
                           String selectionTableName,
                           String computedTableName) {
        super(family, broadcastTableName, selectionTableName, computedTableName);
    }


    @Override
    public Selector buildSelector(Connection con,
                                  BigDecimal contentID,
                                  BigDecimal sectionID,
                                  BigDecimal selectionID) throws SQLException {
        return null;
    }


    @Override
    protected ComputedField[] initComputedFields() {
        return new ComputedField[0];
    }


    @Override
    protected void initJoinKeys() {
    }
}
