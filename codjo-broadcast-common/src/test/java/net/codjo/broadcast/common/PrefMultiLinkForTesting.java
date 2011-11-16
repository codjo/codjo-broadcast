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
 */
class PrefMultiLinkForTesting extends Preferences {
    PrefMultiLinkForTesting() {
        super("TestTU", "AP_FUND_PRICE", "#COLUMNS_LIST", "#COMPUTED");
    }


    public static Preferences buildPreferences() {
        return new PrefMultiLinkForTesting();
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
        addJoinKeys(INNER_JOIN, getSelectionTableName(), getBroadcastTableName(),
                    new String[][]{
                          {"PORTFOLIO_BRANCH", "PORTFOLIO_BRANCH", " = "},
                          {"VALUATION_DATE", "VALUATION_DATE", " = "}
                    });

        addJoinKeys(INNER_JOIN, getComputedTableName(), getSelectionTableName(),
                    new String[][]{
                          {"SELECTION_ID", "SELECTION_ID", " = "}
                    });

        // Prendre le dernier enregistrement de AP_COMMERCIAL correspondant au code Ptf.
        addJoinKeys(INNER_JOIN, "AP_COMMERCIAL as AP_COMMERCIAL_1", "AP_FUND_PRICE",
                    new String[][]{
                          {"PORTFOLIO_CODE", "PORTFOLIO_CODE", " = "}
                    },
                    new JoinExpression(null,
                                       "(Max(AP_COMMERCIAL_1.DATE_BEGIN) = AP_COMMERCIAL.DATE_BEGIN)",
                                       "AP_COMMERCIAL.DATE_BEGIN"));

        addJoinKeys(INNER_JOIN, "AP_COMMERCIAL", "AP_COMMERCIAL as AP_COMMERCIAL_1",
                    new String[][]{
                          {"PORTFOLIO_CODE", "PORTFOLIO_CODE", " = "}
                    });

        // Lien vers le ref pour le champ risk
        addJoinKeys(INNER_JOIN, "REF_REFERENTIAL as REF_RISK", "AP_COMMERCIAL",
                    new String[][]{
                          {"RISK_LEVEL", "ID", " = "}
                    });

        // Lien vers le ref pour le champ fund
        addJoinKeys(INNER_JOIN, "REF_REFERENTIAL as REF_FUND", "AP_COMMERCIAL",
                    new String[][]{
                          {"FUND_TYPE", "ID", " = "}
                    });
    }


    public static final class FakeSelector implements Selector {
        public void cleanup(Context ctxt, Connection con, String table, java.sql.Date today) {
        }


        public void proceed(Context ctxt, Connection con, String table, java.sql.Date today) {
        }
    }
}
