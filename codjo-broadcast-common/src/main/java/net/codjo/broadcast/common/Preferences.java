/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.broadcast.common.columns.FunctionHolder;
import net.codjo.broadcast.common.computed.ComputedField;
import net.codjo.sql.builder.JoinKey;
import net.codjo.sql.builder.JoinKeyExpression;
import net.codjo.sql.builder.OrderByField;
import net.codjo.sql.builder.QueryConfig;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 */
public abstract class Preferences {
    public static final int RIGHT_JOIN = 1;
    public static final int LEFT_JOIN = 2;
    public static final int INNER_JOIN = 3;
    private String broadcastTableName;
    private ComputedField[] computedFields;
    private String computedTableName;
    private String family;
    private Map<String, JoinKey> joinKeyMap;
    private String selectionTableName;
    private final QueryConfig config;


    protected Preferences(String family, String broadcastTableName,
                          String selectionTableName, String computedTableName) {
        if (family == null
            || broadcastTableName == null
            || selectionTableName == null
            || computedTableName == null) {
            throw new IllegalArgumentException();
        }
        this.family = family;
        this.broadcastTableName = broadcastTableName;
        this.selectionTableName = selectionTableName;
        this.computedTableName = computedTableName;
        this.computedFields = initComputedFields();
        this.joinKeyMap = new HashMap<String, JoinKey>();
        initJoinKeys();
        config =
              new QueryConfig() {
                  public Map<String, JoinKey> getJoinKeyMap() {
                      return Collections.unmodifiableMap(joinKeyMap);
                  }


                  public String getRootTableName() {
                      return Preferences.this.getSelectionTableName();
                  }


                  public JoinKeyExpression getRootExpression() {
                      return null;
                  }


                  public OrderByField[] getOrderByFields() {
                      return Preferences.this.getOrderByFields();
                  }
              };
    }


    /**
     * Retourne la famille de cette interface de preference.
     *
     * @return le nom de la famille de Preference.
     */
    public String getFamily() {
        return family;
    }


    /**
     * Retourne le FunctionHolder de la famille. ATTENTION ce functionHolder doit être sans état.
     *
     * @return le FunctionHolder de la famille de Preference.
     */
    public FunctionHolder getFunctionHolder() {
        return null;
    }


    /**
     * Retourne le FunctionHolder de la famille. ce functionHolder est avec état.
     *
     * @return le FunctionHolder de la famille de Preference.
     */
    public FunctionHolder createFunctionHolder(Context context) {
        return getFunctionHolder();
    }


    /**
     * construit le <code>Selector</code> approprié.
     *
     * @param con         la connection
     * @param contentID   id du contenu de la section
     * @param sectionID   id de la section.
     * @param selectionID id de la selection
     *
     * @return le Selector
     */
    public abstract Selector buildSelector(Connection con, BigDecimal contentID,
                                           BigDecimal sectionID, BigDecimal selectionID)
          throws SQLException;


    /**
     * Construit le <code>PostBroadcaste</code> approprié.
     *
     * @param con       la connection
     * @param sectionId l'id de selection
     *
     * @return le PostBroadcaster
     */
    public PostBroadcaster buildPostBroadcaster(Connection con,
                                                BigDecimal fileId,
                                                BigDecimal contentID,
                                                BigDecimal sectionId) throws SQLException {
        return null;
    }


    /**
     * Retourne la liste des champs calculable.
     *
     * @return Les champs calculés
     */
    public ComputedField[] getComputedFields() {
        return computedFields;
    }


    /**
     * Retourne la liste des champs à placer dans l'order by final
     *
     * @return Les champs à placer dans l'order by
     */
    public OrderByField[] getOrderByFields() {
        return new OrderByField[0];
    }


    /**
     * Retourne le nom de la table des champs calcules.
     *
     * @return Le nom de la table
     */
    public String getComputedTableName() {
        return computedTableName;
    }


    /**
     * Retourne le nom de la table principal de diffusion (eg AP_SHARE_PRICE). La table de selection choisi
     * les enregistrmenents a diffuser parmi cette table.
     *
     * @return le nom de table
     */
    public String getBroadcastTableName() {
        return broadcastTableName;
    }


    /**
     * Retourne le nom de la table de selection des éléments(cours, ordre) a envoyer.
     *
     * @return Le nom de la table de selection.
     */
    public String getSelectionTableName() {
        return selectionTableName;
    }


    /**
     * Initialisation de la liste des champs calculable.
     *
     * @return les champs calculées
     *
     * @since Broadcast version 1.0
     */
    protected abstract ComputedField[] initComputedFields();


    /**
     * Initialisation du tableau <code>joinKeyMap</code> contenant les clefs de jointure avec la table
     * <code>Maître de diffusion</code>.
     *
     * @since Broadcast version 1.0
     */
    protected abstract void initJoinKeys();


    /**
     * Methode utilitaire pour la creation des clef de jointure.
     *
     * @param joinType           Type de jointure (ex.: AbstractPreferences.INNER_JOIN)
     * @param fullLeftTableName  Le nom de la table de gauche (avec alias si necessaire)
     * @param fullRightTableName Le nom de la table de droite (avec alias si necessaire)
     * @param fields             tableau a 3 colonnes dont chaque ligne est constitue du nom de la colonne a
     *                           gauche de la jointure, du nom de la colonne a droite de la jointure et d'un
     *                           operateur(optionnel: par defaut c'est '=').
     *
     * @since Broadcast version 2.04
     */
    protected void addJoinKeys(int joinType,
                               String fullLeftTableName,
                               String fullRightTableName,
                               String[][] fields) {
        addJoinKeys(joinType, fullLeftTableName, fullRightTableName, fields, new JoinExpression());
    }


    protected void addJoinKeys(int joinType,
                               String fullLeftTableName,
                               String fullRightTableName,
                               String[][] fields,
                               JoinExpression queryExpression) {
        JoinKey jk = new JoinKey(fullLeftTableName, convert(joinType), fullRightTableName);
        jk.setJoinKeyExpression(queryExpression.toJoinKeyExpression());

        for (String[] field : fields) {
            jk.addPart(new JoinKey.Part(field[0], field[2], field[1]));
        }

        if (joinKeyMap.containsKey(fullLeftTableName)) {
            throw new IllegalArgumentException("La clef de jointure attaché à la table >"
                                               + fullLeftTableName + "< est déjà définie");
        }
        joinKeyMap.put(fullLeftTableName, jk);
    }


    /**
     * DEPRECATED.
     *
     * @Deprecated Utiliser la version utilisant un {@link JoinExpression}
     * @see #addJoinKeys(int,String,String,String[][],JoinExpression)
     */
    protected void addJoinKeys(int joinType,
                               String fullLeftTableName,
                               String fullRightTableName,
                               String[][] fields,
                               JoinKeyExpression queryExpression) {
        addJoinKeys(joinType, fullLeftTableName, fullRightTableName, fields,
                    new JoinExpression(queryExpression));
    }


    public QueryConfig getConfig() {
        return config;
    }


    private static JoinKey.Type convert(int joinType) {
        switch (joinType) {
            case LEFT_JOIN:
                return JoinKey.Type.LEFT;
            case RIGHT_JOIN:
                return JoinKey.Type.RIGHT;
            case INNER_JOIN:
                return JoinKey.Type.INNER;
            default:
                throw new IllegalArgumentException("Type de jointure inconnu");
        }
    }


    public Collection<String> getTableList() {
        return getConfig().getJoinKeyMap().keySet();
    }
}
