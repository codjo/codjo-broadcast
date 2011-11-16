/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.broadcast.common.computed.ComputedContext;
import net.codjo.sql.builder.JoinKey;
/**
 * Classe d'adaptation du contexte ComputedContext.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.2 $
 */
public class ComputedContextAdapter extends ContextAdapter implements ComputedContext {
    private Preferences pref;

    public ComputedContextAdapter(Preferences preferences, Context context) {
        super(context);
        pref = preferences;
    }

    public String getBroadcastTableName() {
        return pref.getBroadcastTableName();
    }


    public String getComputedTableName() {
        return pref.getComputedTableName();
    }


    public String getSelectionTableName() {
        return pref.getSelectionTableName();
    }


    public String joinToBroadcastTable() {
        JoinKey ajoinkey =
            (JoinKey)pref.getConfig().getJoinKeyMap().get(pref.getBroadcastTableName());

        String clause = ajoinkey.buildJoinClause();

        return pref.getComputedTableName() + " inner join "
        + pref.getSelectionTableName() + " on " + pref.getComputedTableName()
        + ".SELECTION_ID" + " = " + pref.getSelectionTableName() + ".SELECTION_ID"
        + " inner join " + pref.getBroadcastTableName() + " on " + clause;
    }
}
