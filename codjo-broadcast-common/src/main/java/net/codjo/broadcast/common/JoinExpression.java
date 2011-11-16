/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.sql.builder.JoinKeyExpression;
/**
 * Expression where et having d'une jointure.
 *
 * <p> Cette classe est utile pour cacher l'implantation agf-sql-builder. </p>
 */
public final class JoinExpression {
    private final JoinKeyExpression impl;


    JoinExpression(JoinKeyExpression impl) {
        this.impl = impl;
    }


    public JoinExpression() {
        this(new JoinKeyExpression());
    }


    public JoinExpression(String whereClause) {
        this(new JoinKeyExpression(whereClause));
    }


    public JoinExpression(String whereClause, String havingClause, String extraGroupByField) {
        this(new JoinKeyExpression(whereClause, havingClause, extraGroupByField));
    }


    public JoinExpression(String whereClause, String havingClause, String[] extraGroupByFields) {
        this(new JoinKeyExpression(whereClause, havingClause, extraGroupByFields));
    }


    public String[] getExtraGroupByFields() {
        return impl.getExtraGroupByFields();
    }


    public String getHavingClause() {
        return impl.getHavingClause();
    }


    public String getWhereClause() {
        return impl.getWhereClause();
    }


    public String getExtraOnClause() {
        return impl.getExtraOnClause();
    }


    JoinKeyExpression toJoinKeyExpression() {
        return impl;
    }


    public static JoinExpression create() {
        return new JoinExpression();
    }


    public JoinExpression extraOnClause(String onClause) {
        impl.setExtraOnClause(onClause);
        return this;
    }


    public JoinExpression where(String whereClause) {
        impl.setWhereClause(whereClause);
        return this;
    }
}
