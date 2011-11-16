/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.columns.FileColumnGenerator;
import net.codjo.sql.builder.DefaultFieldInfoList;
import net.codjo.sql.builder.QueryBuilderFactory;
/**
 * Cette classe permet de generer un ordre SQL de selection a partir d'une liste de
 * <code>FileColumnGenerator</code>.
 */
class DefaultQueryBuilder implements QueryBuilder {
    private net.codjo.sql.builder.QueryBuilder builder;

    DefaultQueryBuilder(Preferences preferences) {
        builder = QueryBuilderFactory.newSelectQueryBuilder(preferences.getConfig());
    }

    public String buildQuery(FileColumnGenerator[] columns) {
        DefaultFieldInfoList list = new DefaultFieldInfoList();
        for (FileColumnGenerator column : columns) {
            list.add(column.getFieldInfo());
        }
        return builder.buildQuery(list);
    }
}
